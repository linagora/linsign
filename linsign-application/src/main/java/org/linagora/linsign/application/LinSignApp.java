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
package org.linagora.linsign.application;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.linagora.linsign.application.controller.CertificatController;
import org.linagora.linsign.application.controller.KeyStoreController;
import org.linagora.linsign.application.langue.Language;
import org.linagora.linsign.application.langue.SelectorLanguage;
import org.linagora.linsign.application.langue.Text;
import org.linagora.linsign.application.model.dao.Document;
import org.linagora.linsign.application.model.dao.Status;
import org.linagora.linsign.application.model.dao.StatusCert;
import org.linagora.linsign.application.orchestrator.IssOrchestrator;
import org.linagora.linsign.application.service.ServiceIss;
import org.linagora.linsign.application.signature.SigningUtils;
import org.linagora.linsign.application.utility.ConfigGenerator;
import org.linagora.linsign.application.utility.UtilityLinSignAPP;
import org.linagora.linsign.application.utility.UtilityLinSignAPP.NoSpaceException;
import org.linagora.linsign.application.view.MyJPanelBackground;
import org.linagora.linsign.application.view.SkinLoader;
import org.linagora.linsign.application.view.TableUtility;
import org.linagora.linsign.application.view.certificat.CertificatManager;
import org.linagora.linsign.application.view.certificat.CertificatRadioButtonCellEditorRenderer;
import org.linagora.linsign.application.view.certificat.CertificatTableModel;
import org.linagora.linsign.application.view.certificat.CertificatView;
import org.linagora.linsign.application.view.document.DocumentManager;
import org.linagora.linsign.application.view.document.DocumentTableModel;
import org.linagora.linsign.application.view.document.DocumentView;
import org.linagora.linsign.sddss.dao.PDFVisibleProperties;

import eu.europa.esig.dss.wsclient.signature.SignatureService_Service;
import eu.europa.esig.dss.wsclient.signature.WsChainCertificate;

@SuppressWarnings("serial")
public class LinSignApp extends Applet {

	private final static Logger LOGGER = Logger.getLogger(LinSignApp.class.getName());

	/* Variable value */
	private List<KeyStore> keystore;
	private ServiceIss serviceIss;
	private SelectorLanguage selectorLangue;
	private List<Document> documentList;
	private List<PDFVisibleProperties> pdfVisibleProperties;
	private KeyStoreController keyStoreSelector;

	/* Applet Param */
	private static ConfigGenerator config = new ConfigGenerator();

	/* Default Value */
	private int tableRowSelect = 0;
	private boolean hasSelect = false, validCertificat = false;

	/* Graphic content */
	private JFrame frame;
	private JTable tableFile, tableCert;
	private JScrollPane scrollFile, scrollCert;
	private JLabel lblSouth, lblNorth, myCertificatValidationText;
	private JButton buttonSign, buttonRevert;
	private JRadioButton rdbtnNewRadioButton;
	private MyJPanelBackground center_panel;

	/* Graphic constant */
	private static final boolean RESIZABLE = false;
	private static final int G_TABLE_ROW_HEIGHT = 20, DIFFERENCE_WINDOWS = 6;
	private static final int RESIZE_INSETS_SIDE = 30, RESIZE_INSETS_TOP = 5;
	private static final int SIZE_LABEL = 25, SIZE_BUTTON = 25;
	private static final int NUMBER_LABEL = 3;
	private static final int ADDITIONAL_SIZE_DOCUMENT = 150, NUMBER_ROW_TABLE_DOCUMENT_INCREASED = 10;

	/* Variable application size */
	private int numberRowCertificat = 1, numberRowDocumentIss = 1;
	private static final int MAX_NUMBER_ROW_TABLE_VIEW_CERT = 10, MAX_NUMBER_ROW_TABLE_VIEW_DOCUMENT = 6;
	private static final int MIN_SIZE_TABLE_DOCUMENT_VIEW = 121, MIN_SIZE_TABLE_CERT_VIEW = 80;

	// iImage name
	private static final String HEADER_IMG = "header.png", FOOTER_IMG = "footer.png", BACKGROUND_PNG = "background.png";

	private final Font FONT_TABLE = new Font("Dialog", 0, 11);

	private boolean isNotInit = true;

	public String printData() {
		return "LinSignApp [config=" + config + "]";
	}

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		System.setProperty("java.security.debug", "sunpkcs11");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LinSignApp window = null;
					if (args.length != 0 && args.length > 1) {
						for (String argument : args) {
							if (!argument.contains("clientId") && !argument.contains("clientPassword"))
								LOGGER.info("Param : " + argument);
							defineArgument(argument);
						}
						window = new LinSignApp();
					} else {
						window = new LinSignApp(new ConfigGenerator(args[0]));
					}
					window.frame.setVisible(true);
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});
	}

	public static void defineArgument(String argument) {
		String[] argSplit = argument.split("=", 2);
		String key = argSplit[0];
		String value = argSplit[1];

		if (key.equals("userAgent")) {
			config.setUserAgent(value);
		} else if (key.equals("urlSkin")) {
			config.setSkinURL(value);
		} else if (key.equals("wsLinSignURL")) {
			config.setWsLinSignURL(value);
		} else if (key.equals("issService")) {
			config.setIssServiceURL(value);
		} else if (key.equals("idSignature")) {
			config.setIdSignature(value);
		} else if (key.equals("language")) {
			config.setLangue(value);
		} else if (key.equals("pdf")) {
			config.setPdfVisiblePropertiesBase64(value.split(","));
		} else if (key.equals("clientId")) {
			config.setClientId(value);
		} else if (key.equals("clientPassword")) {
			config.setClientPassword(value);
		} else if (key.equals("policyReference")) {
			config.setPolicyReference(value);
		} else if (key.equals("signerNameConstraints")) {
			config.setSignerNameConstraints(value);
		}
	}

	/**
	 * Create the application for JNLP (param : userAgent, urlSkin,
	 * wsLinSignURL, issService, language)
	 */
	public LinSignApp() {
		initApplication();
	}

	// Test Launch
	public LinSignApp(ConfigGenerator confExecutable) {
		config = confExecutable;

		initApplication();
	}

	private void initApplication() {
		if (isNotInit) {
			if (config.getIdSignature() == null)
				LOGGER.info("No ID signature");

			if (config.getIssServiceURL() == null) {
				LOGGER.info("Service Error");
			} else {
				serviceIss = new ServiceIss(config.getIssServiceURL(), config.getIdSignature(), config.getClientId(),
						config.getClientPassword());
				serviceIss.sendStatus(config.getIdSignature(), Status.PROCESS_START);
				documentList = serviceIss.getFiles(config.getIdSignature());
			}

			if (config.getLangue() == null) {
				LOGGER.info("Error langue selector - default FR");
				selectorLangue = new SelectorLanguage(Language.FR);
			} else
				selectorLangue = new SelectorLanguage(Language.getLangue(config.getLangue()));

			keyStoreSelector = new KeyStoreController(selectorLangue, config.getSignerNameConstraints());

			try {
				if (config.getUserAgent() == null)
					LOGGER.info("No user agent");
				else {
					keystore = keyStoreSelector.selectKeyStore(config.getUserAgent());
					serviceIss.sendStatus(config.getIdSignature(), Status.KEYSTORE_INIT);
				}
			} catch (Exception e) {
				serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_KEYSTORE_INIT);
				LOGGER.log(Level.SEVERE, "KeyStore Error", e);
			}

			if (config.getWsLinSignURL() == null) {
				LOGGER.info("No server LinSign address");
				serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_APPLICATION_INIT);
			}

			if (config.getSkinURL() == null) {
				LOGGER.info("No skin address");
				serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_APPLICATION_INIT);
			}

			if (config.getPolicyReference() == null) {
				LOGGER.info("No policy");
				serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_APPLICATION_INIT);
			}

			if (config.getPdfVisiblePropertiesBase64() == null) {
				LOGGER.info("No pdf properties");
				serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_APPLICATION_INIT);
			} else {
				pdfVisibleProperties = UtilityLinSignAPP
						.createPdfMarkProperties(config.getPdfVisiblePropertiesBase64());
			}

			try {
				initializeSwing();
			} catch (KeyStoreException e) {
				serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_APPLICATION_INIT);
				LOGGER.log(Level.SEVERE, "Init Application error", e);
			}

			serviceIss.sendStatus(config.getIdSignature(), Status.APPLICATION_INIT);
			isNotInit = false;
		} else {
			LOGGER.log(Level.SEVERE, "The application is already init");
		}
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws KeyStoreException
	 */
	private void initializeSwing() throws KeyStoreException {
		SignatureService_Service.setROOT_SERVICE_URL(config.getWsLinSignURL());
		initializeView();

		if (numberRowCertificat == 1) {
			checkCertificatValidity();
		} else if (numberRowCertificat == 0)
			setTextLabelCertError(selectorLangue.printText(Text.NO_CERTIFICAT_FOUND));
	}

	private void initializeView() {
		frame = new JFrame();
		frame.setResizable(RESIZABLE);
		frame.setName(selectorLangue.printText(Text.TITLE_APPLET));
		frame.setTitle(selectorLangue.printText(Text.TITLE_APPLET));

		center_panel = new MyJPanelBackground();
		center_panel.setLayout(new GridBagLayout());
		center_panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		/* NORTH PANEL */
		initGraphicNorth();

		/* CENTER PANEL */
		initGraphicCenter();

		/* SOUTH PANEL */
		initGraphicSouth();

		/* SKIN INIT */
		initSkinGraphicPanel();

		addActionListener();
		frame.getContentPane().add(center_panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// generateGridBag(positionX, positionY, cellTakeInX, cellTakeInY,
	// defineNumberCellX, defineNumberCellY)
	public GridBagConstraints generateGridBag(int gridBagConstraint, int gridx, int gridy, int gridwidth,
			int gridheight, double weightx, double weighty, Insets insets) {
		GridBagConstraints gridBag = new GridBagConstraints();

		gridBag.fill = gridBagConstraint;

		gridBag.gridx = gridx;
		gridBag.gridy = gridy;

		gridBag.gridwidth = gridwidth;
		gridBag.gridheight = gridheight;

		gridBag.weightx = weightx;
		gridBag.weighty = weighty;

		if (insets != null)
			gridBag.insets = insets;

		return gridBag;

	}

	private void initSkinGraphicPanel() {
		try {
			String skinURL = config.getSkinURL();

			String pathHeader = skinURL + HEADER_IMG;
			String pathFooter = skinURL + FOOTER_IMG;
			String pathBg = skinURL + BACKGROUND_PNG;

			SkinLoader skinLoad = new SkinLoader(config.getIdSignature(), serviceIss);

			BufferedImage imageHeader = skinLoad.getURLFile(pathHeader, "img/defaultHeader.png");
			BufferedImage imageFooter = skinLoad.getURLFile(pathFooter, "img/defaultFooter.png");
			BufferedImage imageBg = skinLoad.getURLFile(pathBg, "img/defaultBackground.png");

			lblSouth = new JLabel(new ImageIcon(imageFooter));
			frame.getContentPane().add(lblSouth, BorderLayout.SOUTH);

			lblNorth = new JLabel(new ImageIcon(imageHeader));
			frame.getContentPane().add(lblNorth, BorderLayout.NORTH);

			center_panel.setImage(new ImageIcon(imageBg).getImage());

			int sizeHeightCert = Math.min(numberRowCertificat, MAX_NUMBER_ROW_TABLE_VIEW_CERT) * G_TABLE_ROW_HEIGHT;
			int sizeHeightDocument = Math.min(numberRowDocumentIss, MAX_NUMBER_ROW_TABLE_VIEW_DOCUMENT)
					* G_TABLE_ROW_HEIGHT;
			int otherObjectHeight = SIZE_BUTTON + (SIZE_LABEL * NUMBER_LABEL);

			if (sizeHeightCert < MIN_SIZE_TABLE_CERT_VIEW)
				sizeHeightCert = MIN_SIZE_TABLE_CERT_VIEW;

			if (sizeHeightDocument < MIN_SIZE_TABLE_DOCUMENT_VIEW)
				sizeHeightDocument = MIN_SIZE_TABLE_DOCUMENT_VIEW;

			int totalSizeObjectHeight = sizeHeightCert + sizeHeightDocument + otherObjectHeight;
			if (documentList.size() > NUMBER_ROW_TABLE_DOCUMENT_INCREASED)
				totalSizeObjectHeight += ADDITIONAL_SIZE_DOCUMENT;

			String OS = System.getProperty("os.name").toUpperCase().trim();
			if (OS.startsWith("WINDOWS")) {
				frame.setBounds(100, 100,
						Math.max(imageHeader.getWidth(this), imageFooter.getWidth(this) + DIFFERENCE_WINDOWS),
						totalSizeObjectHeight + imageHeader.getHeight(this) + imageFooter.getHeight(this));
			} else {
				frame.setBounds(100, 100, Math.max(imageHeader.getWidth(this), imageFooter.getWidth(this)),
						totalSizeObjectHeight + imageHeader.getHeight(this) + imageFooter.getHeight(this));
			}
		} catch (Exception e) {
			serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_SKIN);
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}

	private void initGraphicNorth() {

		JLabel myLabel = new JLabel(selectorLangue.printText(Text.DOCUMENT_TO_SIGN));
		center_panel.add(myLabel, generateGridBag(GridBagConstraints.BOTH, 0, 0, 1, 1, 0.0, 0.0,
				new Insets(RESIZE_INSETS_TOP, RESIZE_INSETS_SIDE, 0, 0)));

		try {
			DocumentManager manager = new DocumentManager();
			numberRowDocumentIss = documentList.size();

			for (Document issDoc : documentList) {
				manager.addObject(new DocumentView(issDoc));
			}
			tableFile = new JTable(new DocumentTableModel(manager, selectorLangue));

			tableFile.setRowHeight(G_TABLE_ROW_HEIGHT);
			tableFile.setOpaque(false);
			tableFile.setFont(FONT_TABLE);

			tableFile = TableUtility.defineColumnSize(tableFile);
			tableFile = TableUtility.colorColumn(tableFile, (tableFile.getColumnCount() - 1));

			tableFile.setRowSelectionAllowed(false);
			tableFile.setColumnSelectionAllowed(false);
			tableFile.setCellSelectionEnabled(true);
			tableFile.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tableFile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			Dimension d = tableFile.getPreferredSize();
			scrollFile = new JScrollPane(tableFile);
			scrollFile.setPreferredSize(new Dimension(d.width, tableFile.getRowHeight() * G_TABLE_ROW_HEIGHT + 1));

			double calcDocViewFactor = Math.min(Math.max((documentList.size() / 5), 1.25), 4.5);
			center_panel.add(scrollFile, generateGridBag(GridBagConstraints.BOTH, 0, 1, 3, 1, 0.5, calcDocViewFactor,
					new Insets(0, RESIZE_INSETS_SIDE, 0, RESIZE_INSETS_SIDE)));

		} catch (Exception e) {
			serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_CERTIFICAT_LIST);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void initGraphicCenter() {
		JLabel myLabel = new JLabel(selectorLangue.printText(Text.SIGNING_CERTIFICATE));
		center_panel.add(myLabel, generateGridBag(GridBagConstraints.BOTH, 0, 4, 1, 1, 0.0, 0.0,
				new Insets(RESIZE_INSETS_TOP, RESIZE_INSETS_SIDE, 0, 0)));

		try {
			CertificatManager manager = new CertificatManager();
			List<CertificatView> listCert;

			listCert = keyStoreSelector.generateListCertView(keystore);

			numberRowCertificat = listCert.size();

			if (numberRowCertificat == 1) {
				listCert.get(0).isAlone();
				tableRowSelect = 0;
				hasSelect = true;
			}

			for (CertificatView certView : listCert)
				manager.addObject(certView);

			tableCert = new JTable(new CertificatTableModel(manager, selectorLangue));
			tableCert.setRowHeight(G_TABLE_ROW_HEIGHT);
			tableCert.setFont(FONT_TABLE);

			tableCert = TableUtility.defineColumnSize(tableCert);

			tableCert.setOpaque(false);

			Dimension d = tableCert.getPreferredSize();
			scrollCert = new JScrollPane(tableCert);
			scrollCert.setPreferredSize(new Dimension(d.width, tableCert.getRowHeight() * G_TABLE_ROW_HEIGHT + 1));

		} catch (KeyStoreException e) {
			serviceIss.sendStatus(config.getIdSignature(), Status.ERROR_CERTIFICAT_LIST);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		TableColumn column = tableCert.getColumnModel().getColumn(0);
		column.setCellEditor(new CertificatRadioButtonCellEditorRenderer());
		column.setCellRenderer(new CertificatRadioButtonCellEditorRenderer());

		center_panel.add(scrollCert, generateGridBag(GridBagConstraints.BOTH, 0, 5, 3, 2, 3.0, 1.5,
				new Insets(0, RESIZE_INSETS_SIDE, 0, RESIZE_INSETS_SIDE)));
	}

	private void initGraphicSouth() {
		myCertificatValidationText = new JLabel();
		Font f = myCertificatValidationText.getFont();
		myCertificatValidationText.setFont(new Font(f.getFontName(), f.getStyle(), f.getSize() - 2));

		center_panel.add(myCertificatValidationText, generateGridBag(GridBagConstraints.BOTH, 0, 7, 1, 1, 0.0, 0.0,
				new Insets(RESIZE_INSETS_TOP, RESIZE_INSETS_SIDE, 0, 0)));

		JLabel myLabel = new JLabel(selectorLangue.printText(Text.SIGNING));
		center_panel.add(myLabel, generateGridBag(GridBagConstraints.BOTH, 0, 8, 1, 1, 0.0, 0.0,
				new Insets(RESIZE_INSETS_TOP, RESIZE_INSETS_SIDE, 0, 0)));

		rdbtnNewRadioButton = new JRadioButton(selectorLangue.printText(Text.CERTIFY_READ_DOCUMENT));
		center_panel.add(rdbtnNewRadioButton, generateGridBag(GridBagConstraints.HORIZONTAL, 0, 9, 1, 1, 0.5, 0.0,
				new Insets(0, RESIZE_INSETS_SIDE, 0, 0)));

		buttonSign = new JButton(selectorLangue.printText(Text.SIGN_BUTTON));
		buttonSign.setEnabled(false);
		center_panel.add(buttonSign, generateGridBag(GridBagConstraints.HORIZONTAL, 1, 9, 1, 1, 0.5, 0.0, null));

		buttonRevert = new JButton(selectorLangue.printText(Text.QUIT_BUTTON));
		center_panel.add(buttonRevert, generateGridBag(GridBagConstraints.HORIZONTAL, 2, 9, 1, 1, 0.5, 0.0,
				new Insets(0, 0, 0, RESIZE_INSETS_SIDE)));
	}

	private boolean previewFile(Document doc) {
		try {
			UtilityLinSignAPP.memoryDiskSpace(doc.getFileContent().length);
			File file = UtilityLinSignAPP.writeFilePreview(doc);

			try {
				URL fileURL = new URL("file://" + file.getAbsolutePath());
				BasicService bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
				if (bs.showDocument(fileURL) == false) {
					if (System.getProperty("os.name").toUpperCase().trim().startsWith("WINDOWS")) {
						Process p = Runtime.getRuntime().exec("cmd /c start " + fileURL);
						if (p != null)
							return true;
					}
					if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
						Desktop.getDesktop().open(file);
						return true;
					}
					return false;
				}
				return true;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.ERROR_PREVIEW));
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				return false;
			}
		} catch (NoSpaceException e) {
			JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.ERROR_SPACE_PREVIEW));
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return false;
		} catch (MalformedURLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return false;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
	}

	private void checkCertificatValidity() {
		/* SELECT CERTIFICAT */
		hasSelect = true;

		CertificatController certificatSelector = new CertificatController();
		X509Certificate userCertificateSelected = certificatSelector.setSelectedKey(keystore, getTableRowSelect(),
				config.getSignerNameConstraints());
		serviceIss.sendStatus(config.getIdSignature(), Status.CERTIFICAT_SELECT);

		String alias = (String) tableCert.getValueAt(getTableRowSelect(), 1);

		/* VALIDATE CERTIFICAT */
		WsChainCertificate chainCertificate = certificatSelector.prepareCertificatToService(userCertificateSelected);

		String resultCertificat = SigningUtils.checkCertificate(chainCertificate);
		LOGGER.info("Certificat result : " + resultCertificat);
		serviceIss.sendStatus(config.getIdSignature(), Status.CERTIFICAT_VALIDITY);

		if (resultCertificat.equals(StatusCert.TRUSTED_CERT.getStatus())) {
			validCertificat = true;
			setTextLabelCertOk(selectorLangue.printText(Text.TRUSTED_CERT) + " : " + alias);
		} else {
			validCertificat = false;
			tableCert.setValueAt(false, getTableRowSelect(), 0);

			String errMsg = null;
			Text txt = Text.selectTextWithCertificatError(resultCertificat);
			if (txt.equals(Text.ERROR_NOT_TRUSTED_CERT)) {
				String certInfos = "";
				try {
					X500Name x500name = new JcaX509CertificateHolder(userCertificateSelected).getSubject();
					RDN cn = x500name.getRDNs(BCStyle.CN)[0];
					certInfos = IETFUtils.valueToString(cn.getFirst().getValue());
				} catch (CertificateEncodingException e) {
					e.printStackTrace();
				}

				errMsg = selectorLangue.printText(Text.selectTextWithCertificatError(resultCertificat)) + " : " + alias
						+ " " + selectorLangue.printText(Text.ISSUED_BY) + certInfos;
			} else
				errMsg = selectorLangue.printText(Text.selectTextWithCertificatError(resultCertificat)) + " : " + alias;

			setTextLabelCertError(errMsg);
			LOGGER.log(Level.SEVERE, "Result certificat : " + resultCertificat);
		}
	}

	private void canSignFile() {
		if (rdbtnNewRadioButton.isSelected() && validCertificat && hasSelect) {
			buttonSign.setEnabled(true);
		} else {
			buttonSign.setEnabled(false);
		}
	}

	private void addActionListener() {
		tableCert.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				tableCert.setValueAt(false, getTableRowSelect(), 0);
				setTableRowSelect(tableCert.getSelectedRow());
				tableCert.setValueAt(true, getTableRowSelect(), 0);
				checkCertificatValidity();

				canSignFile();
			}
		});

		tableFile.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					Document docPreview = new Document(documentList.get(tableFile.getSelectedRow()));
					docPreview.setTitle(docPreview.getTitle().replaceAll("[^a-zA-Z0-9\\._]+", "_"));
					try {
						if (!previewFile(docPreview))
							JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.ERROR_PREVIEW));
					} catch (Exception e) {
						LOGGER.info("Can't preview file");
						LOGGER.log(Level.SEVERE, e.getMessage(), e);

						JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.ERROR_PREVIEW));
					}
					tableFile.clearSelection();
				}

			}
		});

		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				canSignFile();
			}
		});

		ActionListener buttonSignListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				signFile();
			}
		};
		buttonSign.addActionListener(buttonSignListener);

		ActionListener buttonRevertListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				closeApplication();
			}
		};
		buttonRevert.addActionListener(buttonRevertListener);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				closeApplication();
			}
		});

	}

	public void closeApplication() {
		try {
			serviceIss.sendStatus(config.getIdSignature(), Status.USER_END_PROCESS);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.ERROR_WITH_SOAP_SERVICE),
					selectorLangue.printText(Text.POP_UP_TITLE_SIGN), JOptionPane.ERROR_MESSAGE);
		}
		System.exit(0);
	}

	private void signFile() {
		if (!validCertificat) {
			JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.SELECT_VALID_CERTIFICAT));
		} else if (!hasSelect) {
			JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.NO_CERTIFICAT_SELECTED));
		} else if (pdfVisibleProperties.size() != documentList.size()) {
			JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.NO_MATCHING_NUMBER));
		} else if (!isDocumentHasPdfProperties()) {
			JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.NO_MATCHING_CONFIG));
		} else {
			boolean resSign = IssOrchestrator.signatureOrchestrator(config.getIdSignature(), config.getUserAgent(),
					documentList, keystore, getTableRowSelect(), pdfVisibleProperties, config.getPolicyReference(),
					serviceIss, config.getSignerNameConstraints());
			endProcess(resSign);
		}
	}

	private boolean isDocumentHasPdfProperties() {
		for (PDFVisibleProperties pdf : pdfVisibleProperties) {
			boolean inLoop = false;
			for (Document doc : documentList)
				if (doc.getTitle().equals(pdf.getReference()))
					inLoop = true;

			if (inLoop == false)
				return false;
		}
		return true;
	}

	private void endProcess(boolean resultSign) {
		int endProcess = JOptionPane.CLOSED_OPTION;
		if (resultSign)
			JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.SUCCES_MESSAGE),
					selectorLangue.printText(Text.POP_UP_TITLE_SIGN), endProcess);
		else
			JOptionPane.showMessageDialog(frame, selectorLangue.printText(Text.FAIL_MESSAGE),
					selectorLangue.printText(Text.POP_UP_TITLE_SIGN), JOptionPane.ERROR_MESSAGE);

		System.exit(0);
	}

	/*
	 * Getter and Setter
	 */

	public int getTableRowSelect() {
		return tableRowSelect;
	}

	public void setTableRowSelect(int tableRowSelect) {
		this.tableRowSelect = tableRowSelect;
	}

	private void setTextLabelCertError(String txt) {
		myCertificatValidationText.setText(txt);
		myCertificatValidationText.setForeground(Color.RED);
	}

	private void setTextLabelCertOk(String txt) {
		myCertificatValidationText.setText(txt);
		myCertificatValidationText.setForeground(Color.decode("#7ac650"));
	}

}
