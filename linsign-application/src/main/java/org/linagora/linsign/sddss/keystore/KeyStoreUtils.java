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
package org.linagora.linsign.sddss.keystore;

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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.linagora.linsign.application.langue.Language;
import org.linagora.linsign.application.langue.SelectorLanguage;
import org.linagora.linsign.application.langue.Text;
import org.linagora.linsign.sddss.GUI.GUIPasswordDialog;
import org.linagora.linsign.sddss.GUI.MessageConstants;
import org.linagora.linsign.sddss.exception.KeystoreAccessBadPasswordException;
import org.linagora.linsign.sddss.exception.KeystoreAccessException;
import org.linagora.linsign.sddss.keystore.filter.KeystoreFilters;

import sun.security.pkcs11.Secmod;
import sun.security.pkcs11.Secmod.Module;
import sun.security.pkcs11.Secmod.ModuleType;
import sun.security.pkcs11.SunPKCS11;

public class KeyStoreUtils {

	public static final String PKCS11_TOKEN_NAME = "LINSIGN";
	public static final String PKCS11_FIREFOX_NSS_NAME = "NSScrypto";

	private static final String PROG_FILE_x86 = "Program Files (x86)";
	private static final String WIN_LIB_EXTENSION = ".dll";
	private static final String NSS_LIB_NAME = "library = ";

	public static Provider pkcs11prov = null;
	private static SelectorLanguage selectLangue = null;;
	private static final String PATERN_PATH = "[ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ]+";
	private static final String TEMP_LIB_NAME = "securityFirefoxLib";

	/**
	 * Creates a new instance of KeyStoreUtils
	 * 
	 * @param selectLangueInit
	 */
	public KeyStoreUtils(SelectorLanguage selectLangueInit) {
		if (selectLangueInit == null)
			selectLangue = new SelectorLanguage(Language.EN);
		else
			selectLangue = selectLangueInit;
	}

	public KeyStoreUtils() {
		selectLangue = new SelectorLanguage(Language.EN);
	}

	public static SelectorLanguage getSelectLangue() {
		return selectLangue;
	}

	/**
	 * return a list of all keystore entries
	 * 
	 * @param ks
	 * @return
	 * @throws java.security.KeyStoreException
	 */

	public static List<KeyStoreEntry> getTableEntries(KeyStore ks) throws KeystoreAccessException {
		return getTableEntries(ks, null);
	}

	/**
	 * return a list of all keystore entries
	 * 
	 * @param ks
	 * @param filters
	 *            for the entries (null if no filtering)
	 * @return
	 * @throws KeyStoreException
	 */
	public static List<KeyStoreEntry> getTableEntries(KeyStore ks, KeystoreFilters filters)
			throws KeystoreAccessException {

		try {
			if (ks == null) {
				throw new IllegalArgumentException("keystore is null");
			}
			List<KeyStoreEntry> entries = new ArrayList<KeyStoreEntry>();
			for (Enumeration<String> it = ks.aliases(); it.hasMoreElements();) {
				String alias = it.nextElement();

				if (ks.isKeyEntry(alias)) {

					KeyStoreEntry ke = new KeyStoreEntry(ks, alias);

					if (filters != null) {
						if (filters.acceptMyEntry(ke))
							entries.add(ke);
					} else {
						// no filter
						entries.add(ke);
					}
				}
			}
			return entries;
		} catch (KeyStoreException e) {
			throw new KeystoreAccessException(e);
		} catch (CertificateException e) {
			throw new KeystoreAccessException(e);
		}
	}

	/**
	 * get and load JKS Store. returns null if bad password or invalid format.
	 * 
	 * @throws KeyStoreException
	 *             for other exceptions.
	 */
	public static KeyStore getJKSStore(File JKSFile, char[] password) throws KeystoreAccessException {

		FileInputStream fis = null;
		KeyStore ks = null;

		try {
			ks = KeyStore.getInstance("JKS");
			fis = new FileInputStream(JKSFile);
			ks.load(fis, password);

		} catch (IOException e) {
			if (e.getCause() instanceof UnrecoverableKeyException) {
				throw new KeystoreAccessBadPasswordException(e, "JKS");
			} else
				throw new KeystoreAccessException(e, "JKS");
		} catch (NoSuchAlgorithmException e) {
			throw new KeystoreAccessException(e, "JKS");
		} catch (CertificateException e) {
			throw new KeystoreAccessException(e, "JKS");
		} catch (KeyStoreException e) {
			throw new KeystoreAccessException(e, "JKS");
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
			}
		}

		return ks;
	}

	public static KeyStore getPKCS12Store(File PKCS12File, char[] password) throws KeystoreAccessException {

		FileInputStream fis = null;
		KeyStore ks = null;

		try {
			ks = KeyStore.getInstance("PKCS12");
			fis = new FileInputStream(PKCS12File);
			ks.load(fis, password);
		} catch (IOException e) {
			if (e.getCause() instanceof UnrecoverableKeyException) {
				throw new KeystoreAccessBadPasswordException(e, "PKCS 12");
			} else
				throw new KeystoreAccessException(e, "PKCS 12");
		} catch (NoSuchAlgorithmException e) {
			throw new KeystoreAccessException(e, "PKCS 12");
		} catch (CertificateException e) {
			throw new KeystoreAccessException(e, "PKCS 12");
		} catch (KeyStoreException e) {
			throw new KeystoreAccessException(e, "PKCS 12");
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
			}
		}

		return ks;
	}

	public static List<KeyStore> getMacKeychainStore() throws KeystoreAccessException {

		List<KeyStore> ksl = new ArrayList<KeyStore>();
		KeyStore ks = null;

		try {
			ks = KeyStore.getInstance("KeychainStore", "Apple");
			char[] password = {};
			ks.load(null, password);

		} catch (IOException e) {
			if (e.getCause() instanceof UnrecoverableKeyException) {
				throw new KeystoreAccessBadPasswordException(e, "Keychain");
			} else
				throw new KeystoreAccessException(e, "Keychain");
		} catch (NoSuchAlgorithmException e) {
			throw new KeystoreAccessException(e, "Keychain");
		} catch (CertificateException e) {
			throw new KeystoreAccessException(e, "Keychain");
		} catch (NoSuchProviderException e) {
			throw new KeystoreAccessException(e, "Keychain");
		} catch (KeyStoreException e) {
			throw new KeystoreAccessException(e, "Keychain");
		}
		ksl.add(ks);
		return ksl;
	}

	public static List<KeyStore> getMSKeyStore() throws KeystoreAccessException {
		return getMSKeyStoreWithSunMSCAPI();
	}

	/**
	 * LinagoraMSCAPI replace SunMSCAPI SunMSCAPI has limitation and can not
	 * sign hash or encrypt data with private key
	 * 
	 * @return KeyStore
	 * @throws KeyStoreException
	 */
	private static KeyStore getMSKeyStoreWithLinagoraMSCAPI() throws KeystoreAccessException {

		String OS = System.getProperty("os.name").toUpperCase().trim();

		if (!OS.startsWith("WINDOWS")) {
			throw new RuntimeException("Microsoft KeyStore is only available on Windows platform.");
		}

		try {

			Provider sunMs = (Provider) Class.forName("linagora.sun.security.mscapi.LinagoraSunMSCAPI").newInstance();
			Provider old = Security.getProvider("LinagoraSunMSCAPI");

			if (old != null) {
				Security.removeProvider(old.getName());
			}

			Security.addProvider(sunMs);

			KeyStore ks = KeyStore.getInstance("Windows-MY", "LinagoraSunMSCAPI");
			ks.load(null, null);
			return ks;

		} catch (Exception e) {
			throw new KeystoreAccessException(e, "Windows (LinagoraSunMSCAPI)");
		}

	}

	/**
	 * SunMSCAPI has limitations and can not directly sign hash or encrypt data
	 * with private key do not use it until JDK bugs are resolved. need JDK>=6
	 * 
	 * @return KeyStore
	 * @throws KeyStoreException
	 */
	private static List<KeyStore> getMSKeyStoreWithSunMSCAPI() throws KeystoreAccessException {

		String OS = System.getProperty("os.name").toUpperCase().trim();

		if (!OS.startsWith("WINDOWS")) {
			throw new RuntimeException("Microsoft KeyStore is only available on Windows platform.");
		}

		// if(JVM.indexOf("1.6.")!=-1){
		// throw new RuntimeException("must be java JRE >= 1.6");
		// }
		//
		try {

			Provider sunMs = (Provider) Class.forName("sun.security.mscapi.SunMSCAPI").newInstance();
			Provider old = Security.getProvider("SunMSCAPI");

			if (old != null) {
				Security.removeProvider(old.getName());
			}

			Security.addProvider(sunMs);

			KeyStore ks = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
			ks.load(null, null);
			List<KeyStore> ksl = new ArrayList<KeyStore>();
			ksl.add(ks);
			return ksl;

		} catch (Exception e) {
			throw new KeystoreAccessException(e, "Windows (SunMSCAPI)");
		}

	}

	public static KeyStore getPKCS11Store(File PKCS11LibFile, char[] password, String tokenName)
			throws KeystoreAccessException {
		return getPKCS11Store(PKCS11LibFile, password, tokenName, null);
	}

	/**
	 * 
	 * PKSC11, token name is only used to give a name to the provider( in this
	 * case pkcs11-LINSIN) MUST BE ONE WORD.
	 * 
	 */
	public static KeyStore getPKCS11Store(File PKCS11LibFile, char[] password, String tokenName, String configp11)
			throws KeystoreAccessException {

		KeyStore ks = null;

		try {
			if (!PKCS11LibFile.canRead()) {
				throw new KeystoreAccessException(
						new IOException("Cannot read the file " + PKCS11LibFile.getAbsolutePath()), "PKCS 11");
			}

			StringBuffer config = new StringBuffer();
			config.append(String.format("name = %s\n", tokenName));
			config.append(String.format("library = %s\n", PKCS11LibFile.getPath()));

			if (configp11 != null && !configp11.equals(""))
				config.append(configp11);

			ByteArrayInputStream configStream = new ByteArrayInputStream(config.toString().getBytes());

			Provider pkcs11prov = new SunPKCS11(configStream);

			// clean pkcs11
			releasePKCS11Store(tokenName);

			Security.addProvider(pkcs11prov);

			// search the pkcs11 provider NSS (do not forget the prefix
			// SunPKCS11)
			ks = KeyStore.getInstance("PKCS11", "SunPKCS11-" + tokenName);

			ks.load(null, password);

		} catch (IOException e) {
			ks = null;
			if (e.getCause() instanceof FailedLoginException)
				throw new KeystoreAccessBadPasswordException(e, "PKCS 11");
			else
				throw new KeystoreAccessException(e, "PKCS 11");

		} catch (KeyStoreException e) {
			ks = null;
			throw new KeystoreAccessException(e, "PKCS 11");
		} catch (NoSuchProviderException e) {
			ks = null;
			throw new KeystoreAccessException(e, "PKCS 11");
		} catch (NoSuchAlgorithmException e) {
			ks = null;
			throw new KeystoreAccessException(e, "PKCS 11");
		} catch (CertificateException e) {
			ks = null;
			throw new KeystoreAccessException(e, "PKCS 11");
		} catch (ProviderException e) {
			ks = null;
			throw new KeystoreAccessException(e, "PKCS 11");
		}

		return ks;
	}

	public static void releasePKCS11Store(String tokenName) {

		SunPKCS11 old = (SunPKCS11) Security.getProvider("SunPKCS11-" + tokenName);

		if (old != null) {
			Security.removeProvider(old.getName());
		}

	}

	/**
	 * get keystore for the given browser
	 * 
	 * @param useragent
	 *            of the browser or part of it (Firefox,MSIE...)
	 * @return java keystore or null (for example if the user cancel the
	 *         operation for firefox with a password)
	 * @throws KeystoreAccessException
	 *             if troubles to access the keystore (probleme of
	 *             configuration) or subclass KeystoreAccessBadPasswordException
	 *             if bad password to access the keystore.
	 * @throws IOException
	 */
	public static List<KeyStore> getBrowserKeyStore(String useragent) throws KeystoreAccessException, IOException {

		List<KeyStore> myKeystoreList = null;

		if (UserAgent.isFirefox(useragent)) {
			myKeystoreList = getFirefoxPKCS11KeyStore();
		} else if (UserAgent.isInternetExplorer(useragent)) {
			myKeystoreList = getMSKeyStore();
		} else if (UserAgent.isSafariMac(useragent)) {
			myKeystoreList = getMacKeychainStore();
		} else {
			// unable to access this browser
			throw new KeystoreAccessException("browser " + useragent);
		}

		if (myKeystoreList == null)
			throw new KeystoreAccessException("browser " + useragent);

		for (KeyStore keystore : myKeystoreList)
			fixAliases(keystore);

		return myKeystoreList;
	}

	/**
	 * get keystore for firefox with popup for the password
	 * 
	 * @return java keystore or null if the user cancel the operation
	 * @throws KeystoreAccessException
	 *             if troubles to access
	 * @throws IOException
	 */
	public static List<KeyStore> getFirefoxPKCS11KeyStore() throws KeystoreAccessException, IOException {

		List<KeyStore> ksl = null;

		String configPKCS11 = FirefoxPkcs11.configPKCS11();

		boolean firefoxHasPassword = false;

		try {
			ksl = KeyStoreUtils.getFirefoxKeyStore(configPKCS11, null);
		} catch (KeystoreAccessBadPasswordException e) {
			firefoxHasPassword = true;
		}

		if (firefoxHasPassword) { // firefox has password so open dialog

			boolean firstAsk = true;
			boolean chooseOK = true; // chooseOK in the popup

			while (ksl == null && chooseOK) {

				GUIPasswordDialog g;

				if (firstAsk)
					g = new GUIPasswordDialog(MessageConstants.getmessage("GUIPasswordDialog.firefox"));
				else
					g = new GUIPasswordDialog(MessageConstants.getmessage("GUIPasswordDialog.firefox"),
							MessageConstants.getmessage("GUIPasswordDialog.firefox.badPassword"));

				chooseOK = g.run();

				if (chooseOK) {
					char[] pass = g.getPassword();
					firstAsk = false;
					try {
						ksl = KeyStoreUtils.getFirefoxKeyStore(configPKCS11, pass);
					} catch (KeystoreAccessBadPasswordException e) {
						// bad password
						ksl = null;
					}
				}
			}
		}
		// warning: ks can be null if user choose cancel
		return ksl;
	}

	private static List<KeyStore> getFirefoxKeyStoreUnix(String pkcs11config)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException {
		System.out.println(pkcs11config);

		List<KeyStore> ksl = new ArrayList<KeyStore>();
		InputStream configStream = new ByteArrayInputStream(pkcs11config.getBytes());

		// sun.security.pkcs11.SunPKCS11 is not define in JRE 7 64 bit
		if (pkcs11prov == null)
			pkcs11prov = new SunPKCS11(configStream);
		Security.addProvider(pkcs11prov); // if exist there is no add anymore
											// ...
		// search the pkcs11 provider NSS (do not forget the prefix SunPKCS11)
		KeyStore ks = KeyStore.getInstance("PKCS11", pkcs11prov);
		try {
			ks.load(null, null);
		} catch (IOException io) {
			ks = loadKeystoreWithPassword(ks);
		}

		if (ks == null)
			return null;

		ksl.add(ks);
		return ksl;
	}

	@SuppressWarnings({ "restriction", "deprecation" })
	private static List<KeyStore> getFirefoxKeyStoreWindows(String pkcs11config, String firefoxProfil)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		List<KeyStore> ksl = new ArrayList<KeyStore>();
		KeyStore ks = null;
		Secmod secmod = initSecmod(firefoxProfil);

		for (Module mod : secmod.getModules()) {
			if (mod.getType().equals(ModuleType.EXTERNAL)) {
				if (StringUtils.countMatches(mod.getConfiguration(), ":") > 1) {
					String storageDevices = StringUtils.substringBetween(mod.getConfiguration(), NSS_LIB_NAME, ":");
					String newConf = NSS_LIB_NAME + storageDevices + ":" + mod.getConfiguration().split(":")[2];
					mod.setConfiguration(newConf);
				}
			}

			try {

				if (mod.getConfiguration().contains(PROG_FILE_x86)) {
					String pathLib = StringUtils.substringBetween(mod.getConfiguration(), NSS_LIB_NAME,
							WIN_LIB_EXTENSION) + WIN_LIB_EXTENSION;
					File file = new File(pathLib);
					File tmpFile = File.createTempFile(TEMP_LIB_NAME, WIN_LIB_EXTENSION);
					tmpFile.deleteOnExit();
					FileUtils.copyFile(file, tmpFile);
					mod.setConfiguration(mod.getConfiguration().replace(pathLib, tmpFile.getAbsolutePath()));
				}
				// CAN'T ACCES TO CRYPTO KEYSTORE

				if (mod.getType() != ModuleType.CRYPTO) {
					System.out.println(mod.getConfiguration());

					String pathLib = StringUtils.substringBetween(mod.getConfiguration(), NSS_LIB_NAME,
							WIN_LIB_EXTENSION);
					File f = new File(pathLib);
					f.deleteOnExit();
					mod.setConfiguration(mod.getConfiguration().replace(pathLib, f.getCanonicalPath()));

					Provider p = secmod.getModule(mod.getType()).getProvider();

					Security.addProvider(p);
					ks = KeyStore.getInstance("PKCS11", p);

					ks.load(null, null);
					if (ks != null)
						ksl.add(ks);
				}
			} catch (IOException e) {
				if (e.getCause() instanceof FailedLoginException) {
					JOptionPane.showMessageDialog(null, getSelectLangue().printText(Text.ERROR_LOGIN_ERROR));
				} else if (e.getCause() instanceof LoginException) {
					if (e.getMessage().equals("load failed")) {
						ks = loadKeystoreWithPassword(ks);
						if (ks != null)
							ksl.add(ks);
					} else if (e.getCause().getCause().getMessage().equals("CKR_PIN_LOCKED")) {
						JOptionPane.showMessageDialog(null, getSelectLangue().printText(Text.ERROR_LOCK_CARD));
					} else {
						JOptionPane.showMessageDialog(null, getSelectLangue().printText(Text.ERROR_TIME_OUT));
					}
				} else
					e.printStackTrace();

			} catch (Exception e) {
				if (!e.getMessage().equals("PKCS11 not found"))
					JOptionPane.showMessageDialog(null,
							getSelectLangue().printText(Text.ERROR_PKCS11) + "" + e.getMessage());
				e.printStackTrace();
			}
		}

		return ksl;
	}

	@SuppressWarnings("restriction")
	private static Secmod initSecmod(String firefoxProfil) throws IOException {
		Secmod secmod = sun.security.pkcs11.Secmod.getInstance();
		if (secmod.isInitialized() == false) {
			secmod.initialize(sun.security.pkcs11.Secmod.DbMode.READ_ONLY, firefoxProfil,
					FirefoxPkcs11.loadLib().getAbsolutePath() + "\\");
		}

		return secmod;

	}

	private static List<KeyStore> getFirefoxKeyStore(String pkcs11config, char[] password)
			throws KeystoreAccessException {
		try {
			String firefoxProfil = FirefoxPkcs11.getCurrentFirefoxProfileDir().getAbsolutePath() + "\\";
			if (firefoxProfil == null || Pattern.compile(PATERN_PATH).matcher(firefoxProfil).find()) {
				JOptionPane.showMessageDialog(null, "Un charactère spécial a été trouvé dans le chemin du profile");
				return null;
			}

			if (!System.getProperty("os.name").toUpperCase().trim().startsWith("WINDOWS")) {

				return getFirefoxKeyStoreUnix(pkcs11config);
			} else {
				return getFirefoxKeyStoreWindows(pkcs11config, firefoxProfil);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new KeystoreAccessException(e, "Firefox");
		}
	}

	private static void fixAliases(KeyStore keyStore) {
		Field field;
		KeyStoreSpi keyStoreVeritable;

		try {
			field = keyStore.getClass().getDeclaredField("keyStoreSpi");
			field.setAccessible(true);
			keyStoreVeritable = (KeyStoreSpi) field.get(keyStore);

			if ("sun.security.mscapi.KeyStore$MY".equals(keyStoreVeritable.getClass().getName())) {
				Collection entries;
				String alias, hashCode;
				X509Certificate[] certificates;

				field = keyStoreVeritable.getClass().getEnclosingClass().getDeclaredField("entries");
				field.setAccessible(true);
				entries = ((HashMap) field.get(keyStoreVeritable)).values();

				for (Object entry : entries) {
					field = entry.getClass().getDeclaredField("certChain");
					field.setAccessible(true);
					certificates = (X509Certificate[]) field.get(entry);

					hashCode = Integer.toString(certificates[0].hashCode());

					field = entry.getClass().getDeclaredField("alias");
					field.setAccessible(true);
					alias = (String) field.get(entry);

					if (!alias.equals(hashCode)) {
						field.set(entry, alias.concat(" - ").concat(hashCode));
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static KeyStore loadKeystoreWithPassword(KeyStore ks) {
		try {
			JPasswordField pwd = new JPasswordField(10);
			int action = JOptionPane.showConfirmDialog(null, pwd, getSelectLangue().printText(Text.PASSWORD_USER),
					JOptionPane.OK_CANCEL_OPTION);

			if (action < 0) {
				JOptionPane.showMessageDialog(null, getSelectLangue().printText(Text.CANCEL_MAIN_PASSWORD));
				return null;
			} else {
				char[] pass = pwd.getPassword();
				ks.load(null, pass);
				return ks;
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, getSelectLangue().printText(Text.ERROR_LOAD_PKCS11));
			return null;
		}
	}

}
