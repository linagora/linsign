
package eu.europa.esig.dss.wsclient.signature;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getSignaturePolicyResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getSignaturePolicyResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="response" type="{http://ws.dss.esig.europa.eu/}wsSignaturePolicy" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getSignaturePolicyResponse", propOrder = {
    "response"
})
public class GetSignaturePolicyResponse {

    protected WsSignaturePolicy response;

    /**
     * Gets the value of the response property.
     * 
     * @return
     *     possible object is
     *     {@link WsSignaturePolicy }
     *     
     */
    public WsSignaturePolicy getResponse() {
        return response;
    }

    /**
     * Sets the value of the response property.
     * 
     * @param value
     *     allowed object is
     *     {@link WsSignaturePolicy }
     *     
     */
    public void setResponse(WsSignaturePolicy value) {
        this.response = value;
    }

}
