
package eu.europa.esig.dss.wsclient.signature;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for wsParameters complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wsParameters">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="asicMimeType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="asicSignatureForm" type="{http://ws.dss.esig.europa.eu/}signatureForm" minOccurs="0"/>
 *         &lt;element name="asicZipComment" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="certifiedSignerRoles" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="chainCertificateList" type="{http://ws.dss.esig.europa.eu/}wsChainCertificate" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="claimedSignerRole" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="commitmentTypeIndication" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="contentIdentifierPrefix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contentIdentifierSuffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="digestAlgorithm" type="{http://ws.dss.esig.europa.eu/}digestAlgorithm" minOccurs="0"/>
 *         &lt;element name="encryptionAlgorithm" type="{http://ws.dss.esig.europa.eu/}encryptionAlgorithm" minOccurs="0"/>
 *         &lt;element name="pdfFontColor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pdfFontName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pdfFontSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pdfFontStyle" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pdfMark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pdfPage" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pdfX" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pdfY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="references" type="{http://ws.dss.esig.europa.eu/}wsdssReference" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="signWithExpiredCertificate" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="signatureLevel" type="{http://ws.dss.esig.europa.eu/}signatureLevel" minOccurs="0"/>
 *         &lt;element name="signaturePackaging" type="{http://ws.dss.esig.europa.eu/}signaturePackaging" minOccurs="0"/>
 *         &lt;element name="signaturePolicy" type="{http://ws.dss.esig.europa.eu/}policy" minOccurs="0"/>
 *         &lt;element name="signedInfoCanonicalizationMethod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="signerLocation" type="{http://ws.dss.esig.europa.eu/}signerLocation" minOccurs="0"/>
 *         &lt;element name="signingCertificateBytes" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="signingCertificateDigestAlgorithm" type="{http://ws.dss.esig.europa.eu/}digestAlgorithm" minOccurs="0"/>
 *         &lt;element name="signingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="timestampDigestAlgorithm" type="{http://ws.dss.esig.europa.eu/}digestAlgorithm" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wsParameters", propOrder = {
    "asicMimeType",
    "asicSignatureForm",
    "asicZipComment",
    "certifiedSignerRoles",
    "chainCertificateList",
    "claimedSignerRole",
    "commitmentTypeIndication",
    "contentIdentifierPrefix",
    "contentIdentifierSuffix",
    "digestAlgorithm",
    "encryptionAlgorithm",
    "pdfFontColor",
    "pdfFontName",
    "pdfFontSize",
    "pdfFontStyle",
    "pdfMark",
    "pdfPage",
    "pdfX",
    "pdfY",
    "references",
    "signWithExpiredCertificate",
    "signatureLevel",
    "signaturePackaging",
    "signaturePolicy",
    "signedInfoCanonicalizationMethod",
    "signerLocation",
    "signingCertificateBytes",
    "signingCertificateDigestAlgorithm",
    "signingDate",
    "timestampDigestAlgorithm"
})
public class WsParameters {

    protected String asicMimeType;
    protected SignatureForm asicSignatureForm;
    protected boolean asicZipComment;
    @XmlElement(nillable = true)
    protected List<String> certifiedSignerRoles;
    @XmlElement(nillable = true)
    protected List<WsChainCertificate> chainCertificateList;
    @XmlElement(nillable = true)
    protected List<String> claimedSignerRole;
    @XmlElement(nillable = true)
    protected List<String> commitmentTypeIndication;
    protected String contentIdentifierPrefix;
    protected String contentIdentifierSuffix;
    protected DigestAlgorithm digestAlgorithm;
    protected EncryptionAlgorithm encryptionAlgorithm;
    protected int pdfFontColor;
    protected String pdfFontName;
    protected int pdfFontSize;
    protected int pdfFontStyle;
    protected String pdfMark;
    protected int pdfPage;
    protected int pdfX;
    protected int pdfY;
    @XmlElement(nillable = true)
    protected List<WsdssReference> references;
    protected boolean signWithExpiredCertificate;
    protected SignatureLevel signatureLevel;
    protected SignaturePackaging signaturePackaging;
    protected Policy signaturePolicy;
    protected String signedInfoCanonicalizationMethod;
    protected SignerLocation signerLocation;
    protected byte[] signingCertificateBytes;
    protected DigestAlgorithm signingCertificateDigestAlgorithm;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar signingDate;
    protected DigestAlgorithm timestampDigestAlgorithm;

    /**
     * Gets the value of the asicMimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsicMimeType() {
        return asicMimeType;
    }

    /**
     * Sets the value of the asicMimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsicMimeType(String value) {
        this.asicMimeType = value;
    }

    /**
     * Gets the value of the asicSignatureForm property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureForm }
     *     
     */
    public SignatureForm getAsicSignatureForm() {
        return asicSignatureForm;
    }

    /**
     * Sets the value of the asicSignatureForm property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureForm }
     *     
     */
    public void setAsicSignatureForm(SignatureForm value) {
        this.asicSignatureForm = value;
    }

    /**
     * Gets the value of the asicZipComment property.
     * 
     */
    public boolean isAsicZipComment() {
        return asicZipComment;
    }

    /**
     * Sets the value of the asicZipComment property.
     * 
     */
    public void setAsicZipComment(boolean value) {
        this.asicZipComment = value;
    }

    /**
     * Gets the value of the certifiedSignerRoles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the certifiedSignerRoles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCertifiedSignerRoles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCertifiedSignerRoles() {
        if (certifiedSignerRoles == null) {
            certifiedSignerRoles = new ArrayList<String>();
        }
        return this.certifiedSignerRoles;
    }

    /**
     * Gets the value of the chainCertificateList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chainCertificateList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChainCertificateList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WsChainCertificate }
     * 
     * 
     */
    public List<WsChainCertificate> getChainCertificateList() {
        if (chainCertificateList == null) {
            chainCertificateList = new ArrayList<WsChainCertificate>();
        }
        return this.chainCertificateList;
    }

    /**
     * Gets the value of the claimedSignerRole property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the claimedSignerRole property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClaimedSignerRole().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getClaimedSignerRole() {
        if (claimedSignerRole == null) {
            claimedSignerRole = new ArrayList<String>();
        }
        return this.claimedSignerRole;
    }

    /**
     * Gets the value of the commitmentTypeIndication property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the commitmentTypeIndication property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommitmentTypeIndication().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCommitmentTypeIndication() {
        if (commitmentTypeIndication == null) {
            commitmentTypeIndication = new ArrayList<String>();
        }
        return this.commitmentTypeIndication;
    }

    /**
     * Gets the value of the contentIdentifierPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentIdentifierPrefix() {
        return contentIdentifierPrefix;
    }

    /**
     * Sets the value of the contentIdentifierPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentIdentifierPrefix(String value) {
        this.contentIdentifierPrefix = value;
    }

    /**
     * Gets the value of the contentIdentifierSuffix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContentIdentifierSuffix() {
        return contentIdentifierSuffix;
    }

    /**
     * Sets the value of the contentIdentifierSuffix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContentIdentifierSuffix(String value) {
        this.contentIdentifierSuffix = value;
    }

    /**
     * Gets the value of the digestAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link DigestAlgorithm }
     *     
     */
    public DigestAlgorithm getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * Sets the value of the digestAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigestAlgorithm }
     *     
     */
    public void setDigestAlgorithm(DigestAlgorithm value) {
        this.digestAlgorithm = value;
    }

    /**
     * Gets the value of the encryptionAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link EncryptionAlgorithm }
     *     
     */
    public EncryptionAlgorithm getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    /**
     * Sets the value of the encryptionAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link EncryptionAlgorithm }
     *     
     */
    public void setEncryptionAlgorithm(EncryptionAlgorithm value) {
        this.encryptionAlgorithm = value;
    }

    /**
     * Gets the value of the pdfFontColor property.
     * 
     */
    public int getPdfFontColor() {
        return pdfFontColor;
    }

    /**
     * Sets the value of the pdfFontColor property.
     * 
     */
    public void setPdfFontColor(int value) {
        this.pdfFontColor = value;
    }

    /**
     * Gets the value of the pdfFontName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdfFontName() {
        return pdfFontName;
    }

    /**
     * Sets the value of the pdfFontName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdfFontName(String value) {
        this.pdfFontName = value;
    }

    /**
     * Gets the value of the pdfFontSize property.
     * 
     */
    public int getPdfFontSize() {
        return pdfFontSize;
    }

    /**
     * Sets the value of the pdfFontSize property.
     * 
     */
    public void setPdfFontSize(int value) {
        this.pdfFontSize = value;
    }

    /**
     * Gets the value of the pdfFontStyle property.
     * 
     */
    public int getPdfFontStyle() {
        return pdfFontStyle;
    }

    /**
     * Sets the value of the pdfFontStyle property.
     * 
     */
    public void setPdfFontStyle(int value) {
        this.pdfFontStyle = value;
    }

    /**
     * Gets the value of the pdfMark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdfMark() {
        return pdfMark;
    }

    /**
     * Sets the value of the pdfMark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdfMark(String value) {
        this.pdfMark = value;
    }

    /**
     * Gets the value of the pdfPage property.
     * 
     */
    public int getPdfPage() {
        return pdfPage;
    }

    /**
     * Sets the value of the pdfPage property.
     * 
     */
    public void setPdfPage(int value) {
        this.pdfPage = value;
    }

    /**
     * Gets the value of the pdfX property.
     * 
     */
    public int getPdfX() {
        return pdfX;
    }

    /**
     * Sets the value of the pdfX property.
     * 
     */
    public void setPdfX(int value) {
        this.pdfX = value;
    }

    /**
     * Gets the value of the pdfY property.
     * 
     */
    public int getPdfY() {
        return pdfY;
    }

    /**
     * Sets the value of the pdfY property.
     * 
     */
    public void setPdfY(int value) {
        this.pdfY = value;
    }

    /**
     * Gets the value of the references property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the references property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferences().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WsdssReference }
     * 
     * 
     */
    public List<WsdssReference> getReferences() {
        if (references == null) {
            references = new ArrayList<WsdssReference>();
        }
        return this.references;
    }

    /**
     * Gets the value of the signWithExpiredCertificate property.
     * 
     */
    public boolean isSignWithExpiredCertificate() {
        return signWithExpiredCertificate;
    }

    /**
     * Sets the value of the signWithExpiredCertificate property.
     * 
     */
    public void setSignWithExpiredCertificate(boolean value) {
        this.signWithExpiredCertificate = value;
    }

    /**
     * Gets the value of the signatureLevel property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureLevel }
     *     
     */
    public SignatureLevel getSignatureLevel() {
        return signatureLevel;
    }

    /**
     * Sets the value of the signatureLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureLevel }
     *     
     */
    public void setSignatureLevel(SignatureLevel value) {
        this.signatureLevel = value;
    }

    /**
     * Gets the value of the signaturePackaging property.
     * 
     * @return
     *     possible object is
     *     {@link SignaturePackaging }
     *     
     */
    public SignaturePackaging getSignaturePackaging() {
        return signaturePackaging;
    }

    /**
     * Sets the value of the signaturePackaging property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignaturePackaging }
     *     
     */
    public void setSignaturePackaging(SignaturePackaging value) {
        this.signaturePackaging = value;
    }

    /**
     * Gets the value of the signaturePolicy property.
     * 
     * @return
     *     possible object is
     *     {@link Policy }
     *     
     */
    public Policy getSignaturePolicy() {
        return signaturePolicy;
    }

    /**
     * Sets the value of the signaturePolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link Policy }
     *     
     */
    public void setSignaturePolicy(Policy value) {
        this.signaturePolicy = value;
    }

    /**
     * Gets the value of the signedInfoCanonicalizationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignedInfoCanonicalizationMethod() {
        return signedInfoCanonicalizationMethod;
    }

    /**
     * Sets the value of the signedInfoCanonicalizationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignedInfoCanonicalizationMethod(String value) {
        this.signedInfoCanonicalizationMethod = value;
    }

    /**
     * Gets the value of the signerLocation property.
     * 
     * @return
     *     possible object is
     *     {@link SignerLocation }
     *     
     */
    public SignerLocation getSignerLocation() {
        return signerLocation;
    }

    /**
     * Sets the value of the signerLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignerLocation }
     *     
     */
    public void setSignerLocation(SignerLocation value) {
        this.signerLocation = value;
    }

    /**
     * Gets the value of the signingCertificateBytes property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSigningCertificateBytes() {
        return signingCertificateBytes;
    }

    /**
     * Sets the value of the signingCertificateBytes property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSigningCertificateBytes(byte[] value) {
        this.signingCertificateBytes = ((byte[]) value);
    }

    /**
     * Gets the value of the signingCertificateDigestAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link DigestAlgorithm }
     *     
     */
    public DigestAlgorithm getSigningCertificateDigestAlgorithm() {
        return signingCertificateDigestAlgorithm;
    }

    /**
     * Sets the value of the signingCertificateDigestAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigestAlgorithm }
     *     
     */
    public void setSigningCertificateDigestAlgorithm(DigestAlgorithm value) {
        this.signingCertificateDigestAlgorithm = value;
    }

    /**
     * Gets the value of the signingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSigningDate() {
        return signingDate;
    }

    /**
     * Sets the value of the signingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSigningDate(XMLGregorianCalendar value) {
        this.signingDate = value;
    }

    /**
     * Gets the value of the timestampDigestAlgorithm property.
     * 
     * @return
     *     possible object is
     *     {@link DigestAlgorithm }
     *     
     */
    public DigestAlgorithm getTimestampDigestAlgorithm() {
        return timestampDigestAlgorithm;
    }

    /**
     * Sets the value of the timestampDigestAlgorithm property.
     * 
     * @param value
     *     allowed object is
     *     {@link DigestAlgorithm }
     *     
     */
    public void setTimestampDigestAlgorithm(DigestAlgorithm value) {
        this.timestampDigestAlgorithm = value;
    }

}
