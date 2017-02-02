package org.apache.nifi.processors.x12;

import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.StringResult;
import org.milyn.payload.StringSource;
import org.milyn.smooks.edi.EDIReaderConfigurator;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class X12File {
    private String controlVersion;
    private String identifierCode;

    private List<String> isaSegments;
    private List<String> gsSegments;
    private List<List<String>> stSegments;
    private List<String> geSegments;
    private List<String> ieaSegments;

    public String getControlVersion() {
        return controlVersion;
    }

    void setControlVersion(String controlVersion) {
        this.controlVersion = controlVersion;
    }

    String getIdentifierCode() {
        return identifierCode;
    }

    void setIdentifierCode(String identifierCode) {
        this.identifierCode = identifierCode;
    }

    void setIsaSegments(List<String> isaSegments) {
        this.isaSegments = isaSegments;
    }

    X12File withIsaSegments(List<String> isaSegments) {
        this.setIsaSegments(isaSegments);
        return this;
    }

    void setGsSegments(List<String> gsSegments) {
        setControlVersion(gsSegments.get(8));
        this.gsSegments = gsSegments;
    }

    X12File withGsSegments(List<String> gsSegments) {
        setGsSegments(gsSegments);
        return this;
    }

    List<List<String>> getStSegments() {
        return stSegments;
    }

    void setStSegments(List<List<String>> stSegments) {
        setIdentifierCode(stSegments.get(0).get(1));
        this.stSegments = stSegments;
    }

    X12File withStSegments(List<List<String>> stSegments) {
        setStSegments(stSegments);
        return this;
    }

    void setGeSegments(List<String> geSegments) {
        this.geSegments = geSegments;
    }

    X12File withGeSegments(List<String> geSegments) {
        setGeSegments(geSegments);
        return this;
    }

    void setIeaSegments(List<String> ieaSegments) {
        this.ieaSegments = ieaSegments;
    }

    X12File withIeaSegments(List<String> ieaSegments) {
        setIeaSegments(ieaSegments);
        return this;
    }

    String getMappingModelFileName() {
        return String.format("x12/%s-%s.xml", getControlVersion(), getIdentifierCode());
    }

    File getMappingModelFile() {
        URL resourceURL = getClass().getClassLoader().getResource(getMappingModelFileName());
        assert resourceURL != null;
        return new File(resourceURL.getFile());
    }

    public String toXML() throws IOException, SAXException {

        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("en", "IE"));

        EDIReaderConfigurator ediReaderConfigurator = new EDIReaderConfigurator(getMappingModelFileName());

        Smooks smooks = new Smooks("smooks-config.xml");
        smooks.setReaderConfig(ediReaderConfigurator);

        try {

            // Create an exec context - no profiles
            ExecutionContext executionContext = smooks.createExecutionContext();

            StringResult result = new StringResult();

            System.out.println(getMappingModelFileName());
            System.out.println(toString());

            // Filter the output message to the output writer, using the execution context
            smooks.filterSource(executionContext, new StringSource(toString()), result);

            Locale.setDefault(defaultLocale);

            return result.getResult();
        } finally {
            smooks.close();
        }

    }

    public String toString() {

        String header =
                isaSegments.stream().collect(Collectors.joining("*")) +
                        gsSegments.stream().collect(Collectors.joining("*")) + "~";

        String footer =
                geSegments.stream().collect(Collectors.joining("*")) + "~" +
                        ieaSegments.stream().collect(Collectors.joining("*")) + "~";


        String body = stSegments.stream()
                .map((s) -> s.stream().collect(Collectors.joining("*")))
                .collect(Collectors.joining("~"));

        return header + body + footer;
    }
}
