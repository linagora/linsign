/**
 *  LinSign - Electronic signature application
 *  
 *  Copyright © 2008--2017 LINAGORA, www.linagora.com
 *  
 *  SPDX-License-Identifier: AGPL-3.0
 *  
 *  This file is part of LinSign.
 *  
 *  LinSign is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of
 *  the License, or (at your option) any later version.
 *  
 *  LinSign is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with LinSign.  If not, see <http://www.gnu.org/licenses/agpl.html>.
 */
package org.linagora.linsign.client.keystore;

/*
 * #%L
 * signature-client
 * %%
 * Copyright (C) 2013 - 2015 gSafe
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


//import com.google.common.io.Files;

public class FirefoxPkcs11 {

	
	private static final String WIN_RESOURCE_PATH = "lib/";
	private static final String MAIN_RESSOURCES = "src/main/resources/";

	private static final String WIN_LIBRARY_SUFFIX = ".dll";
	private static final String LINUX_LIBRARY_PATH = "/usr/lib;/usr/lib/iceweasel;/usr/lib/nss";
	private static final String LINUX_LIBRARY_PREFIX = "libsoftokn3";
	private static final String LINUX_LIBRARY_SUFFIX = ".so";
	private static final String MAC_LIBRARY_PATH = "/Applications/Firefox.app/Contents/MacOS";
	private static final String MAC_LIBRARY_PREFIX = "libsoftokn3";
	private static final String MAC_LIBRARY_SUFFIX = ".dylib";

	//profile firefox
	private static final String WIN_PROFILE = System.getenv("APPDATA")+"\\Mozilla\\Firefox\\Profiles";
	private static final String LINUX_PROFILE = "/.mozilla/firefox";
	private static final String MAC_PROFILE = "/Library/Application Support/Firefox/Profiles";

	//executable (simple check to see if firefox is installed on the platform at the default place)
	private static final String WIN_EXECUTABLE_FIREFOX = "/Program Files/Mozilla Firefox/firefox.exe";
	private static final String LIN_EXECUTABLE_FIREFOX = "/usr/bin/firefox";
	private static final String MAC_EXECUTABLE_FIREFOX = "/Applications/Firefox.app/Contents/MacOS/firefox-bin";

	//pkcs11 configuration
	private static final String NAME = "NSS";
	private static final String DESCRIPTION = "NSSPKCS11";
	private static final String NSSARGS_PREFIX = "nssArgs=\"configdir='";
	private static final String NSSARGS_SUFFIX = "' certPrefix='' keyPrefix='' secmod='secmod.db' flags=readOnly\"";
	private static final String SLOT = "slot=2";
	private static final String SEP = System.getProperty("line.separator");

	//firefox lock profile when running
	private static final String LOCKFILE = "lock";

	private FirefoxPkcs11(){
		//getSoftokn3();
	}

	/**
	 * Get the current user profile being used.
	 * this function check the execution of firefox with the "lock" file
	 * if no lock file is found, we use the default profile and we read dependencies for softokn3
	 * @return the current profile or default profile
	 */
	public static File getCurrentFirefoxProfileDir()
	{
		File res = null;
		File[] profiles = getAllFirefoxProfiles();
		File defaultProfil= null;

		for (File profil : profiles) {
			if (profil.isDirectory()){
				FilenameFilter filter = new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return (name.endsWith(LOCKFILE));
					}
				};
				File[] checklock = profil.listFiles(filter);
				if (checklock.length > 0) {
					res = profil;
					break;
				}
				if(profil.toString().endsWith(".default"))
					defaultProfil =  profil;
			}						
		}

		if (res==null) {
			//unable to find the profil which is used (firefox is NOT running so no lock)
			if(defaultProfil!=null)
				res = defaultProfil; //so we need to use the default profil instead
			else
				throw new RuntimeException("Unable to get your profiles in firefox.");
		};
		return res;
	}

	/**
	 * Get All profile dirs that user has
	 * @return all profiles directory for the firefox
	 */
	private static File[] getAllFirefoxProfiles()
	{
		File[] profiles = null;

		// recherche du profil firefox par defaut
		if (getOS().startsWith("WINDOWS")) {
			File f = new File(WIN_PROFILE);
			profiles = f.listFiles();
		}

		if (getOS().startsWith("LINUX")) {
			String homeUser = System.getProperty("user.home");
			File f = new File(homeUser + LINUX_PROFILE);
			profiles = f.listFiles();
		}

		if (getOS().startsWith("MAC")) {
			String homeUser = System.getProperty("user.home");
			File f = new File(homeUser + MAC_PROFILE);
			profiles = f.listFiles();
		}
		return profiles;
	}

	private static String getPathToLibSoftokn3() throws IOException {
		String pathToLibsoftokn3 = null;
		if (getOS().startsWith("LINUX")) {
			pathToLibsoftokn3 = getLinuxLibsoftokn3();
		} else if (getOS().startsWith("MAC")) {
			pathToLibsoftokn3 = getMacLibsoftokn3();
		}

		return pathToLibsoftokn3;
	}

	/*Load dependencies libraries for nss*/
	public static File loadLib() throws IOException {

		//This is the dependencies that require nss to load the user keystore
		List<String> ddls = new ArrayList<String>(
				//Arrays.asList("nspr4", "nss3", "nssutil3", "plc4", "plds4", "softokn3", "sqlite3"));
				Arrays.asList("freebl3", "mozglue", "msvcp120", "msvcr120", "nss3", "nssckbi", "nssdbm3", "softokn3"));

		String basePath;
		if(checkJavaArch64())
			basePath = "v323_64";
		else
			basePath = "v323_32";

		//Create the folder who contains libraries
		File tmpDir = File.createTempFile("nss_"+basePath+"_", null);
		tmpDir.delete();
		tmpDir.mkdirs();
		tmpDir.deleteOnExit();

		for(String dll : ddls){
			loadDependenciesDll(basePath, dll, tmpDir);
		}

		return tmpDir;
	}

	/*Create libraries file into the temporary folder*/
	private static void loadDependenciesDll(String basePath, String dllName, File tmpDir) throws IOException {
		/*InputStream dataToSign = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(WIN_RESOURCE_PATH + basePath +"/"+ dllName + WIN_LIBRARY_SUFFIX);*/
		InputStream dataToSign = FirefoxPkcs11.class.getClassLoader().getResourceAsStream(WIN_RESOURCE_PATH + basePath +"/"+ dllName + WIN_LIBRARY_SUFFIX);
		try {
			File fileToLoad = new File(tmpDir, dllName + WIN_LIBRARY_SUFFIX);
			FileOutputStream output = new FileOutputStream(fileToLoad);
			try {
				IOUtils.copy(dataToSign, output);
			} finally {
				IOUtils.closeQuietly(output);
			}

			fileToLoad.deleteOnExit();
		} finally {
			IOUtils.closeQuietly(dataToSign);
		}
	}
	/*Load profile user who contains keystore database (secmod)*/
	private static String getPathToSecmodDirectory() throws IOException {

		String pathToSecodDirectory = null;

		if (getOS().startsWith("WINDOWS")) {
			pathToSecodDirectory = getCurrentFirefoxProfileDir().getCanonicalPath();
		} else {
			pathToSecodDirectory = getLinuxPathToSecmodDirectory();
		}

		return pathToSecodDirectory;
	}


	public static String configPKCS11() throws IOException {

		StringBuilder sbf = new StringBuilder();
		sbf.append("name=");
		sbf.append(NAME);
		sbf.append(SEP);
		sbf.append("attributes=compatibility");
		sbf.append(SEP);
		if (!(getOS().startsWith("WINDOWS"))) {
			String pathLib = getPathToLibSoftokn3();
			if (pathLib == null) {
				sbf.append("nssSecmodDirectory=");
				sbf.append(getPathToSecmodDirectory());
				sbf.append(SEP);
				sbf.append("nssModule=keystore");
				sbf.append(SEP);
				sbf.append("nssDbMode=readOnly");
				sbf.append(SEP);
			} else {
				sbf.append("library=");
				sbf.append(pathLib);	
				sbf.append(SEP);
				sbf.append("description=");
				sbf.append(DESCRIPTION);
				sbf.append(SEP);
				sbf.append(NSSARGS_PREFIX);
				sbf.append(getPathToSecmodDirectory());
				sbf.append(NSSARGS_SUFFIX);
				sbf.append(SEP);
				sbf.append(SLOT);
			}
		} else {

			/*
			 * Load dependencies libraries for nss, libDir contains temporary
			 * folder with all libraries
			 */
			File libDir = loadLib();
			// If this lib isn't loaded first, sunpkcs11 fails to load keystore
			// (mozglue isn't loaded)
			try{
				System.load(libDir.getCanonicalPath() + "\\mozglue.dll");
			}catch(UnsatisfiedLinkError e){
				//needed if firefox version is under 36
				System.load(libDir.getCanonicalPath() + "\\msvcr120.dll");
				System.load(libDir.getCanonicalPath() + "\\msvcp120.dll");
				System.load(libDir.getCanonicalPath() + "\\mozglue.dll");
			}
			/*
			 * Add libraries into the library path The lib need to be load
			 * first, the order is important because he load the first lib found
			 */
			System.setProperty("java.library.path",
					libDir.getCanonicalPath() + ";" + System.getProperty("java.library.path"));

			// nssLibraryDirectory contains dependencies path
			sbf.append("nssLibraryDirectory=");
			sbf.append(libDir.getCanonicalPath());
			sbf.append(SEP);

			// nssSecmodDirectory contains the directory profile user
			sbf.append("nssSecmodDirectory=");
			sbf.append(getPathToSecmodDirectory());
			sbf.append(SEP);

			// nssModule choose to load the keystore
			sbf.append("nssModule=keystore");
			sbf.append(SEP);
			sbf.append("nssDbMode=readOnly");
			sbf.append(SEP);
		}

		return sbf.toString();
	}

	private static String getLibsoftokn3(final String libraryPath, final String libraryPrefix, final String librarySuffix) {
		File f = null;

		// First we loop over ALL LINUX_LIBRARY_PATH
		List<String> listLinuxLibPath = Arrays.asList(libraryPath
				.split(";"));

		for (String currentLinuxLibPath : listLinuxLibPath) {
			// Here we check if a lib exist with the exact name
			// LINUX_LIBRARY_PREFIX+LINUX_LIBRARY_SUFFIX
			// ex: libsoftokn3.so
			f = new File(currentLinuxLibPath + "/" + libraryPrefix
					+ librarySuffix);
			if (f.exists())
				return f.toString();

			// Here we check if a lib exist with a name containing
			// LINUX_LIBRARY_PREFIX AND LINUX_LIBRARY_SUFFIX
			// ex: libsoftokn3.so.3b libsoftokn3.aa.so
			f = new File(currentLinuxLibPath);

			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.startsWith(libraryPrefix) && name.contains((librarySuffix)));
				}
			};
			File[] libsoftokn3Files = f.listFiles(filter);

			if (libsoftokn3Files!=null && libsoftokn3Files.length > 0) {
				// default Firefox Profile
				if (libsoftokn3Files[0].exists())
					return libsoftokn3Files[0].toString();
			}
		}

		// If no lib found we return null value
		return null;
	}


	private static String getLinuxLibsoftokn3() {
		return getLibsoftokn3(LINUX_LIBRARY_PATH,LINUX_LIBRARY_PREFIX,LINUX_LIBRARY_SUFFIX);
	}

	private static String getLinuxPathToSecmodDirectory() {
		return getCurrentFirefoxProfileDir().getAbsolutePath();
	}


	private static String getMacLibsoftokn3() {
		return getLibsoftokn3(MAC_LIBRARY_PATH,MAC_LIBRARY_PREFIX,MAC_LIBRARY_SUFFIX);
	}


	private static String getFirefoxCurrentProfile() {

		File f = null;
		String res =null;

		if (getOS().startsWith("WINDOWS")) {
			f = getCurrentFirefoxProfileDir();
			res = formatQuoteWindows(f.toString());

		} else if (getOS().startsWith("LINUX")) {
			f = getCurrentFirefoxProfileDir();
			res = f.toString();

		} else if (getOS().startsWith("MAC")) {
			f = getCurrentFirefoxProfileDir();
			res = f.toString();
		}

		return res;
	}


	/**
	 * Tranformation of the window user home in the "unix like" way
	 * 
	 * @return
	 */
	private static String formatWinUserHome() {
		String s = System.getProperty("user.home").substring(2);
		return formatQuoteWindows(s);
	}

	/**
	 * convert backslash to slash for windows (mandatory for NSS and configuration pkcs 11)
	 * @param source
	 * @return
	 */
	private static String formatQuoteWindows(String source) {
		return source.replace('\\', '/');
	}

	/**
	 * simple check to see if firefox is installed on the platform at the default place
	 * @return true if ok
	 */
	private static boolean isFirefoxInstalled() {
		boolean res = false;
		if (getOS().startsWith("WINDOWS")) {
			if (new File(WIN_EXECUTABLE_FIREFOX).exists())	res = true;
		} else if (getOS().startsWith("LINUX")) {
			if (new File(LIN_EXECUTABLE_FIREFOX).exists())	res = true;
		}
		else if (getOS().startsWith("MAC")) {
			if (new File(MAC_EXECUTABLE_FIREFOX).exists())	res = true;
		}
		return res;
	}



	private static String getOS(){
		return System.getProperty("os.name").toUpperCase().trim();
	}

	private static boolean checkJavaArch64(){		
		return System.getProperty("sun.arch.data.model").equals("64");
	}
}

