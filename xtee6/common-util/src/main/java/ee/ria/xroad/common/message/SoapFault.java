package ee.ria.xroad.common.message;

import javax.xml.soap.SOAPFault;

import org.apache.commons.lang3.StringEscapeUtils;

import ee.ria.xroad.common.CodedException;

/**
 * Soap interface implementation representing an error message.
 */
public class SoapFault implements Soap {

    static final String SOAP_NS_SOAP_ENV =
            "http://schemas.xmlsoap.org/soap/envelope/";

    private final String faultCode;
    private final String faultString;
    private final String faultActor;
    private final String faultDetail;

    /**
     * Creates a new SOAP fault from a SOAPFault DOM element.
     * @param soapFault the DOM element used in created of the fault
     */
    public SoapFault(SOAPFault soapFault) {
        this.faultCode = soapFault.getFaultCode();
        this.faultString = soapFault.getFaultString();
        this.faultActor = soapFault.getFaultActor();
        this.faultDetail = getFaultDetail(soapFault);
    }

    /**
     * Gets the fault code of the SOAP fault.
     * @return a String with the fault code
     */
    public String getCode() {
        return faultCode;
    }

    /**
     * Gets the fault string of the SOAP fault.
     * @return a String giving an explanation of the fault
     */
    public String getString() {
        return faultString;
    }

    /**
     * Gets the fault actor of the SOAP fault.
     * @return a String giving the actor in the message path that caused this fault
     */
    public String getActor() {
        return faultActor;
    }

    /**
     * Gets the fault detail of the SOAP fault.
     * @return a String with application-specific error information
     */
    public String getDetail() {
        return faultDetail;
    }

    /**
     * Converts this SOAP fault into a coded exception.
     * @return CodedException
     */
    public CodedException toCodedException() {
        return CodedException.fromFault(faultCode, faultString, faultActor,
                faultDetail);
    }

    @Override
    public String getXml() {
        return createFaultXml(faultCode, faultString, faultActor, faultDetail);
    }

    /**
     * Creates SOAP fault message from exception.
     * @param ex exception representing a SOAP fault
     * @return a String containing XML of the SOAP fault represented by the given coded exception
     */
    public static String createFaultXml(CodedException ex) {
        return createFaultXml(ex.getFaultCode(), ex.getFaultString(),
                ex.getFaultActor(), ex.getFaultDetail());
    }

    /**
     * Creates a SOAP fault message.
     * @param faultCode code of the new SOAP fault
     * @param faultString string of the new SOAP fault
     * @param faultActor actor of the new SOAP fault
     * @param detail detail of the new SOAP fault
     * @return a String containing XML of the SOAP fault represented by the given parameters
     */
    public static String createFaultXml(String faultCode, String faultString,
            String faultActor, String detail) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<SOAP-ENV:Envelope "
                    + "xmlns:SOAP-ENV=\"" + SOAP_NS_SOAP_ENV + "\" "
                    + "xmlns:xroad=\"" + SoapHeader.NS_XROAD + "\">"
                    + "<SOAP-ENV:Body>"
                        + "<SOAP-ENV:Fault>"
                            + "<faultcode>" + faultCode + "</faultcode>"
                            + "<faultstring>"
                            + StringEscapeUtils.escapeXml(faultString)
                            + "</faultstring>"
                            + (faultActor != null
                             ? "<faultactor>"
                                     + StringEscapeUtils.escapeXml(faultActor)
                                     + "</faultactor>" : "")
                                     + (detail != null
                             ? "<detail><xroad:faultDetail>"
                                     + StringEscapeUtils.escapeXml(detail)
                                 + "</xroad:faultDetail>" + "</detail>" : "")
                         + "</SOAP-ENV:Fault>"
                     + "</SOAP-ENV:Body>"
                 + "</SOAP-ENV:Envelope>";
    }

    private static String getFaultDetail(SOAPFault soapFault) {
        if (soapFault.getDetail() != null
                && soapFault.getDetail().getFirstChild() != null) {
            return soapFault.getDetail().getFirstChild().getTextContent();
        }

        return null;
    }

}