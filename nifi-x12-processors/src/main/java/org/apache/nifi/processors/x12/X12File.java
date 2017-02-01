package org.apache.nifi.processors.x12;

import java.util.List;

public class X12File {
    private String controlVersion;
    private String identifierCode;


    private List<String> isaSegments;
    private List<String> gsSegments;
    private List<List<String>> stSegments;
    private List<String> geSegments;
    private List<String> ieaSegments;

    // ediList.stream().collect(Collectors.joining("~")

    public String getControlVersion() {
        return controlVersion;
    }

    public void setControlVersion(String controlVersion) {
        this.controlVersion = controlVersion;
    }

    public String getIdentifierCode() {
        return identifierCode;
    }

    public void setIdentifierCode(String identifierCode) {
        this.identifierCode = identifierCode;
    }

    public List<String> getIsaSegments() {
        return isaSegments;
    }

    public void setIsaSegments(List<String> isaSegments) {
        this.isaSegments = isaSegments;
    }

    public List<String> getGsSegments() {
        return gsSegments;
    }

    public void setGsSegments(List<String> gsSegments) {
        this.gsSegments = gsSegments;
    }

    public List<List<String>> getStSegments() {
        return stSegments;
    }

    public void setStSegments(List<List<String>> stSegments) {
        this.stSegments = stSegments;
    }

    public void addStSegment(List s) {
        stSegments.add(s);
    }

    public List<String> getGeSegments() {
        return geSegments;
    }

    public void setGeSegments(List<String> geSegments) {
        this.geSegments = geSegments;
    }

    public List<String> getIeaSegments() {
        return ieaSegments;
    }

    public void setIeaSegments(List<String> ieaSegments) {
        this.ieaSegments = ieaSegments;
    }
}
