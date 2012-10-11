package btwmods;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ModLoader {
	
	private ModLoader() {}

	private static boolean hasInit = false;
	private static ClassLoader classLoader = null;
	private static Set<URL> classLoaderUrls = new HashSet<URL>();
	private static Method classLoaderAddURLMethod = null;
	
	/**
	 * Initialize the {@link ModLoader} and mods. Should only be called from 
	 */
	public static void init() {
		if (!hasInit) {
			
			// Attempt to get the URLClassLoader and its private addURL() method.
			if (classLoader == null) {
				classLoader = ModLoader.class.getClassLoader();
				
				if (classLoader instanceof URLClassLoader) {
					try {
						classLoaderUrls.addAll(Arrays.asList(((URLClassLoader)classLoader).getURLs()));
						classLoaderAddURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
						classLoaderAddURLMethod.setAccessible(true);
					} catch (NoSuchMethodException e) {
					}
				}
				
				// TODO: Can we use our own URLClassLoader instead?
			}
			
			loadModsFromClassPath();
			loadModsFromFolder(new File(".", "btwmods"));
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
		// TODO: Unhandled exceptions in addClassLoaderURL()
		
		if (url == null)
			throw new IllegalArgumentException("url argument cannot be null");
		
		if (classLoaderUrls.contains(url))
			return true;
		
		// Add the URL only if the private URLClassLoader#addURL() method has been set.
		if (supportsAddClassLoaderURL()) {
			try {
				classLoaderAddURLMethod.invoke((URLClassLoader)classLoader, new Object[] { url });
				return true;
			} catch (IllegalAccessException e) {
				// TODO: IllegalAccessException in ModLoader#addClassLoaderURL()
			} catch (InvocationTargetException e) {
				// TODO: InvocationTargetException in ModLoader#addClassLoaderURL()
			}
		}
		
		return false;
	}
	
	private static void loadModsFromFolder(File modsDir) {
		if (!modsDir.exists()) {
			modsDir.mkdir();
		}
		
		if (modsDir.isDirectory()) {
			
			// Get a list of mod folders/files. The contents of these folders/files should be in package format.
			File[] file = modsDir.listFiles();
			if (file != null) {
				for (int i = 0; i < file.length; i++) {
					String name = file[i].getName();
					
					// Load mod from a regular directory.
					if (file[i].isDirectory()) {
						String[] binaryNames = getModBinaryNamesFromDirectory(file[i]);
						try {
							if (binaryNames.length > 0) {
								addClassLoaderURL(file[i].toURI().toURL());
								loadMods(binaryNames);
							}
						} catch (MalformedURLException e) {
							// TODO: MalformedURLException in ModLoader#loadModsFromFolder()
						}
					}
					
					// Load mod from a zip or jar.
					else if (file[i].isFile() && name.startsWith("BTWMod") && (name.endsWith(".jar") || name.endsWith(".zip"))) {
						// TODO
						throw new UnsupportedOperationException();
					}
				}
			}
		}
	}
	
	private static void loadModsFromClassPath() {
		for (URL url : classLoaderUrls) {
			try {
				File path = new File(url.toURI());
				if (path.isDirectory()) {
					String[] binaryNames = getModBinaryNamesFromDirectory(path);
				}
			} catch (URISyntaxException e) {
				// if this URL is not formatted strictly according to to RFC2396 and cannot be converted to a URI.
			} catch (IllegalArgumentException e) {
				// if the URI cannot be parsed by File(URI).
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
								if (classNames[c].isFile() && classNames[c].getName().startsWith("BTWMod") && classNames[c].getName().startsWith(".class")) {
									names.add("btwmod." + modPackages[p].getName() + "." + classNames[c].getName());
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
	 */
	private static String[] getModBinaryNamesFromZip(File zip) {
		ArrayList<String> names = new ArrayList<String>();
		
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zip);
			
			for (Enumeration<? extends ZipEntry> list = zipFile.entries(); list.hasMoreElements(); ) {
				ZipEntry entry = list.nextElement();
				if (!entry.isDirectory() && entry.getName().matches("^btwmod/[^/]+/BTWMod.+\\.class$")) {
					names.add(entry.getName().substring(0, entry.getName().length() - ".class".length() - 1).replace('/',  '.'));
				}
			}
			
			return names.toArray(new String[names.size()]);
			
		} catch (IllegalStateException e) {
			// If action was taken but zip is closed.
			// TODO
		} catch (ZipException e1) {
			// TODO
		} catch (IOException e1) {
			// TODO
		}
		finally {
			if (zipFile != null)
				try { zipFile.close(); } catch (Exception e) { }
		}
		
		// Do not return binary names if an exception was caught.
		return new String[0];
	}
	
	private static void loadMod(String binaryName) {
		System.out.println("Loading mod " + binaryName + "...");
	}
	
	private static void loadMods(String[] binaryNames) {
		for (int n = 0; n < binaryNames.length; n++) {
			loadMod(binaryNames[n]);
		}
	}
	
	public class Mods {
		
		private HashSet<IMod> mods;
		
		private Mods(HashSet<IMod> mods) {
			this.mods = mods;
		}
		
		public void initMods() {
			for (IMod mod : mods) {
				mod.init();
			}
		}

		public void unloadMods() {
			for (IMod mod : mods) {
				mod.unload();
			}
			mods.clear();
		}
	}
}
