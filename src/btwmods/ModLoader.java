package btwmods;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ModLoader {
	
	private ModLoader() {}

	private static boolean hasInit = false;
	private static ClassLoader classLoader = null;
	private static Set<URL> classLoaderUrls = new HashSet<URL>();
	private static Method classLoaderAddURLMethod = null;
	
	public static void init() {
		if (!hasInit) {
			String[] binaryNames = getModBinaryNamesFromClassPath();
		}
		
		hasInit = true;
	}
	
	/**
	 * Attempt to add URL to ModLoader's class loader.
	 * 
	 * @see <a href="http://www.javafaq.nu/java-example-code-895.html">http://www.javafaq.nu/java-example-code-895.html</a>
	 * @param url
	 * @return true if the URL was loaded. false if the class loader is not a {@link URLClassLoader} or the URL could not be added.
	 * @throws IllegalArgumentException
	 */
	public static boolean addClassLoaderURL(URL url) throws IllegalArgumentException {
		if (url == null)
			throw new IllegalArgumentException("addClassLoaderURL's url argument cannot be null");
		
		if (classLoaderUrls.contains(url))
			return true;
		
		// TODO: Handle addClassLoaderURL exceptions.
		
		if (classLoaderAddURLMethod == null) {
			classLoader = ModLoader.class.getClassLoader();
			
			if (classLoader instanceof URLClassLoader) {
				try {
					classLoaderUrls.addAll(Arrays.asList(((URLClassLoader)classLoader).getURLs()));
					classLoaderAddURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
					classLoaderAddURLMethod.setAccessible(true);
				} catch (NoSuchMethodException e) {
				}
			}
			
			// TODO: Use our own URLClassLoader instead?
		}
		
		// Only attempt to add the URL if the method was set.
		if (classLoaderAddURLMethod != null) {
			try {
				classLoaderAddURLMethod.invoke((URLClassLoader)classLoader, new Object[] { url });
				return true;
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		
		return false;
	}
	
	public static void loadModsFromFolder() {
		File modsDir = new File(".", "btwmods");
		
		if (!modsDir.exists() && !modsDir.isDirectory()) {
			modsDir.mkdir();
		}
		
		if (modsDir.isDirectory()) {
			File[] file = modsDir.listFiles();
			
			for (int i = 0; i < file.length; i++) {
				String name = file[i].getName();
				
				if (file[i].isDirectory()) {
					String[] binaryNames = getModBinaryNamesFromDirectory(file[i]);
					try {
						if (binaryNames.length > 0 && addClassLoaderURL(file[i].toURI().toURL())) {
							for (int n = 0; n < binaryNames.length; n++) {
								loadMod(binaryNames[n]);
							}
						}
					} catch (MalformedURLException e) {
						// TODO: handle MalformedURLException in loadModsFromFolder()
					}
				}
				else if (file[i].isFile() && name.startsWith("btwmod_") && (name.endsWith(".jar") || name.endsWith(".zip"))) {
					// TODO: Load from zip/jar.
				}
			}
		}
	}
	
	/**
	 * Searches a directory for a 'btwmod' package dir, sub package dirs under it (i.e. 'mymod'), and then for btwmod_*.class files.
	 * 
	 * @param directory The directory to search.
	 * @return An array of binary names (see {@link ClassLoader}.
	 * @throws IllegalArgumentException
	 */
	private static String[] getModBinaryNamesFromDirectory(File directory) throws IllegalArgumentException {
		if (directory == null)
			throw new IllegalArgumentException("getModBinaryNamesFromDirectory's directory argument cannot be null");
		
		ArrayList<String> names = new ArrayList<String>();
		
		File btwmodDir = new File(directory, "btwmod");
		
		if (btwmodDir.isDirectory()) {
			File[] packageNames = btwmodDir.listFiles();
			
			for (int p = 0; p < packageNames.length; p++) {
				if (packageNames[p].isDirectory()) {
					File[] classNames = packageNames[p].listFiles();
					for (int c = 0; c < classNames.length; c++) {
						if (classNames[c].isFile() && classNames[c].getName().startsWith("btwmod_") && classNames[c].getName().startsWith(".class")) {
							names.add("btwmod." + packageNames[p].getName() + "." + classNames[c].getName());
						}
					}
				}
			}
		}
		
		return names.toArray(new String[names.size()]);
	}
	
	private static String[] getModBinaryNamesFromClassPath() {
		ClassLoader checkLoader = ModLoader.class.getClassLoader();
		if (checkLoader instanceof URLClassLoader) {
			URLClassLoader loader = (URLClassLoader)checkLoader;
			URL[] urls = loader.getURLs();
			System.out.println(urls.length);
		}
		
		return null;
	}
	
	private static void loadMod(String binaryName) {
		
	}
	
	/*public HashSet<Class> loadModClasses(String modList) {
		HashSet<Class> classes;
		
		if ((classes = ModClassCache.get(this.parentClass)) == null) {
			classes = new HashSet<Class>();
			
			if (modList != null && !modList.isEmpty()) {
				String[] modNames = modList.split("[:,;]+");
				for (int i = 0; i < modNames.length; i++) {
					modNames[i] = modNames[i].trim();
					
					if (!modNames[i].isEmpty()) {
						try {
							Class modClass = Class.forName(modNames[i]);
							if (requiredClass.isAssignableFrom(modClass)) {
								classes.add(modClass);
							}
							else {
								MinecraftServer.logger.warning("BTWMod's " + this.parentClass.getSimpleName() + " failed to load the '" + modNames[i] + "' mod class, as it does not extend " + requiredClass.getName() + ".");
							}
						} catch (ClassNotFoundException e) {
							MinecraftServer.logger.warning("BTWMod's " + this.parentClass.getSimpleName() + " failed to find the class for the '" + modNames[i] + "' mod.");
						}
					}
				}
			}
			
			ModClassCache.put(this.parentClass, classes);
		}
		
		return classes;
	}
	
	public Mods createMods(String modList) {
		HashSet<Class> classes = this.loadModClasses(modList);
		HashSet<T> mods = new HashSet<T>();
		
		for (Class modClass : classes) {
			try {
				mods.add((T)modClass.getDeclaredConstructor().newInstance());
			} catch (Exception e) {
				MinecraftServer.logger.warning("BTWMod's " + this.parentClass.getSimpleName() + " failed to create an instance of the '" + modClass.getName() + "' mod (" + e.getClass().getName() + "): " + e.getMessage());
			}
		}
		
		return new Mods(mods);
	}*/
	
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
