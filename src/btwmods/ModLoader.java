package btwmods;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ModLoader {
	
	private ModLoader() {}

	private static boolean hasInit = false;
	private static ClassLoader classLoader = null;
	private static Set<URL> classLoaderUrls = new HashSet<URL>();
	private static Method classLoaderAddURLMethod = null;
	
	public static final String VERSION = "1.0 (vMC 1.3.2 BTW 4.21)";
	
	/**
	 * Initialize the {@link ModLoader} and mods. Should only be called from 
	 */
	public static void init() {
		if (!hasInit) {
			net.minecraft.server.MinecraftServer.logger.info("BTWMods " + VERSION + " Initializing...");
			
			// Attempt to get the URLClassLoader and its private addURL() method.
			if (classLoader == null) {
				classLoader = ModLoader.class.getClassLoader();
				
				if (classLoader instanceof URLClassLoader) {
					try {
						classLoaderUrls.addAll(Arrays.asList(((URLClassLoader)classLoader).getURLs()));
						classLoaderAddURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
						classLoaderAddURLMethod.setAccessible(true);
					} catch (Throwable e) {
						outputError(e, "BTWMods could not load mods from the class path (i.e. any mods directly in your minecraft_server.jar).");
					}
				}
				
				// TODO: Can we use our own URLClassLoader instead?
			}
			
			findModsInClassPath();
			findModsInFolder(new File(".", "btwmods"));

			net.minecraft.server.MinecraftServer.logger.info("BTWMods Initialization Complete.");
		}
		
		hasInit = true;
	}
	
	/**
	 * Returns if {@link URLClassLoader} was successfully set.
	 * 
	 * @return <code>true</code> if it was set; <code>false</code> otherwise.
	 */
	public static boolean hasURLClassLoader() {
		return classLoader != null && classLoader instanceof URLClassLoader;
	}
	
	/**
	 * Returns if {@link URLClassLoader} was successfully set and we can add URLs to it.
	 * 
	 * @return <code>true</code> if we can add URLs; <code>false</code> otherwise.
	 */
	public static boolean supportsAddClassLoaderURL() {
		return hasURLClassLoader() && classLoaderAddURLMethod != null;
	}
	
	/**
	 * Add a URL to {@link ModLoader}'s class loader.
	 * 
	 * @see <a href="http://www.javafaq.nu/java-example-code-895.html">http://www.javafaq.nu/java-example-code-895.html</a>
	 * @param url
	 * @return <code>true</code> if the URL was added (or is already set); <code>false</code> if {@link #supportsAddClassLoaderURL()} returns <code>false</code> or the URL could not be added.
	 * @throws IllegalArgumentException
	 */
	public static boolean addClassLoaderURL(URL url) throws IllegalArgumentException {
		if (url == null)
			throw new IllegalArgumentException("url argument cannot be null");
		
		if (classLoaderUrls.contains(url))
			return true;
		
		// Add the URL only if the private URLClassLoader#addURL() method has been set.
		if (supportsAddClassLoaderURL()) {
			try {
				classLoaderAddURLMethod.invoke((URLClassLoader)classLoader, new Object[] { url });
				return true;
			} catch (Throwable e) {
				outputError(e, "BTWMods failed (" + e.getClass().getSimpleName() + ") to add the following mod path: " + url.toString());
			}
		}
		
		return false;
	}
	
	private static void findModsInFolder(File modsDir) {
		if (!modsDir.exists()) {
			modsDir.mkdir();
		}
		
		if (modsDir.isDirectory()) {
			
			// Get a list of mod folders/files. The contents of these folders/files should be in package format.
			File[] file = modsDir.listFiles();
			if (file != null) {
				for (int i = 0; i < file.length; i++) {
					try {
						String name = file[i].getName();
						
						// Load mod from a regular directory.
						if (file[i].isDirectory()) {
							String[] binaryNames = getModBinaryNamesFromDirectory(file[i]);
							if (binaryNames.length > 0 && addClassLoaderURL(file[i].toURI().toURL())) {
								loadMods(binaryNames);
							}
						}
						
						// Load mod from a zip or jar.
						else if (file[i].isFile() && (name.endsWith(".jar") || name.endsWith(".zip"))) {
							String[] binaryNames = getModBinaryNamesFromZip(file[i]);
							if (binaryNames.length > 0 && addClassLoaderURL(file[i].toURI().toURL())) {
								loadMods(binaryNames);
							}
						}
					} catch (Throwable e) {
						outputError(e, "BTWMods failed (" + e.getClass().getSimpleName() + ") to load mods from: " + file[i].getPath());
					}
				}
			}
		}
	}
	
	private static void findModsInClassPath() {
		for (URL url : classLoaderUrls) {
			try {
				File path = new File(url.toURI());
				String name = path.getName();
				
				// Load mod from a regular directory.
				if (path.isDirectory()) {
					loadMods(getModBinaryNamesFromDirectory(path));
				}
				
				// Load mod from a zip or jar.
				else if (path.isFile() && (name.endsWith(".jar") || name.endsWith(".zip"))) {
					loadMods(getModBinaryNamesFromZip(path));
				}
			} catch (Throwable e) {
				outputError(e, "BTWMods failed (" + e.getClass().getSimpleName() + ") to search the following classpath for mods: " + url.toString());
			}
		}
	}
	
	/**
	 * Searches a directory for a 'btwmod' package dir, sub package dirs under it (i.e. 'mymod'), and then for BTWMod*.class files.
	 * 
	 * @param directory The directory to search.
	 * @return An array of binary names (see {@link ClassLoader}.
	 */
	private static String[] getModBinaryNamesFromDirectory(File directory) {
		ArrayList<String> names = new ArrayList<String>();
		
		// Make sure the 'btwmod' package folder exists.
		File btwmodPackage = new File(directory, "btwmod");
		if (btwmodPackage.isDirectory()) {
			
			// Loop through the second level of package names, which should be for mods.
			File[] modPackages = btwmodPackage.listFiles();
			if (modPackages != null) {
				for (int p = 0; p < modPackages.length; p++) {
					if (modPackages[p].isDirectory()) {
						
						// Check for mod_*.class files.
						File[] classNames = modPackages[p].listFiles();
						if (classNames != null) {
							for (int c = 0; c < classNames.length; c++) {
								if (classNames[c].isFile() && classNames[c].getName().startsWith("BTWMod") && classNames[c].getName().endsWith(".class")) {
									names.add("btwmod." + modPackages[p].getName() + "." + classNames[c].getName().substring(0, classNames[c].getName().length() - ".class".length()));
								}
							}
						}
					}
				}
			}
		}
		
		return names.toArray(new String[names.size()]);
	}
	
	/**
	 * Searches a zip file (or jar) for files that match the package pattern btwmod.{somename}.BTWMod{somename}.class.
	 * 
	 * @see <a href="http://www.javamex.com/tutorials/compression/zip_individual_entries.shtml">http://www.javamex.com/tutorials/compression/zip_individual_entries.shtml</a>
	 * @param zip The zip (or jar) file to search.
	 * @return An array of binary names (see {@link ClassLoader}.
	 * @throws IllegalStateException If an action is taken on a closed zip file.
	 * @throws ZipException
	 * @throws IOException If the zip file could not be loaded.
	 */
	private static String[] getModBinaryNamesFromZip(File zip) throws IllegalStateException, ZipException, IOException {
		ArrayList<String> names = new ArrayList<String>();
		
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zip);
			
			for (Enumeration<? extends ZipEntry> list = zipFile.entries(); list.hasMoreElements(); ) {
				ZipEntry entry = list.nextElement();
				if (!entry.isDirectory() && entry.getName().matches("^btwmod/[^/]+/BTWMod.+\\.class$")) {
					names.add(entry.getName().substring(0, entry.getName().length() - ".class".length()).replace('/',  '.'));
				}
			}
			
			return names.toArray(new String[names.size()]);
			
		} finally {
			if (zipFile != null)
				try { zipFile.close(); } catch (Throwable e) { }
		}
	}
	
	private static void loadMod(String binaryName) {
		try {
			Class mod = classLoader.loadClass(binaryName);
			if (IMod.class.isAssignableFrom(mod)) {
				IMod modInstance = (IMod)mod.newInstance();
				
				try {
				modInstance.init();
				}
				catch (Throwable e) {
					outputError(e, "BTWMods failed (" + e.getClass().getSimpleName() + ") while running init for: " + binaryName);
				}
				
				net.minecraft.server.MinecraftServer.logger.info("BTWMod loaded: " + binaryName);
			}
		} catch (Throwable e) {
			outputError(e, "BTWMods failed (" + e.getClass().getSimpleName() + ") to create an instance of: " + binaryName);
		}
	}
	
	private static void loadMods(String[] binaryNames) {
		for (int n = 0; n < binaryNames.length; n++) {
			loadMod(binaryNames[n]);
		}
	}
	
	private static void outputError(Throwable throwable, String message) {
		outputError(throwable, message, Level.WARNING);
	}
	
	private static void outputError(Throwable throwable, String message, Level level) {
		net.minecraft.server.MinecraftServer.logger.log(level, message);
		throwable.printStackTrace(new PrintStream(System.out));
	}
	
	public static void reportListenerFailure(Throwable t, IAPIListener listener) {
		ServerAPI.removeListener(listener);
		WorldAPI.removeListener(listener);
		NetworkAPI.unregisterCustomChannels(listener);
		PlayerAPI.removeListener(listener);
		outputError(t, "BTWMod " + listener.getMod().getName() + " (" + listener.getClass().getName() + ") threw a " + t.getClass().getSimpleName() + ": " + t.getMessage(), Level.SEVERE);
	}
}
