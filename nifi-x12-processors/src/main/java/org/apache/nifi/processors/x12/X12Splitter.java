package org.apache.nifi.processors.x12;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * EDI X12 file splitter
 * [INFO] Ugly code alert.  Just getting it working right now.
 *
 * @author <a href="mailto:mrcsparker@gmail.com">mrcsparker@gmail.com</a>
 */
class X12Splitter {

    private static final int HEADER_LENGTH = 106;
    private static final int SEGMENT_POSITION = 105;
    private static final int ELEMENT_POSITION = 3;
    private static final int COMPOSITE_ELEMENT_POSITION = 104;

    private Character segmentSeparator;
    private Character elementSeparator;
    private Character compositeElementSeparator;

    private final Reader inputReader;
    private List<X12File> baseList = new ArrayList<>();
    private X12File x12File = new X12File();

    private List<String> isaSegments = new ArrayList<>();
    private List<String> gsSegments = new ArrayList<>();
    private List<List<String>> stSegments = new ArrayList<>();
    private List<String> geSegments = new ArrayList<>();
    private List<String> ieaSegments = new ArrayList<>();

    /**
     * @param inputStream Input Stream
     */
    public X12Splitter(InputStream inputStream) {
        this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public List<X12File> split() throws IOException {

        parseHeader();

        Scanner scanner = new Scanner(inputReader);
        scanner.useDelimiter(Pattern.quote(getSegmentSeparator().toString()));

        while(scanner.hasNext()) {
            List<String> s = scanSegment(scanner.next());
            if (s != null) {
                stSegments.add(s);
            }
        }
        if (stSegments.size() > 0) {
            baseList.add(x12File.withIsaSegments(isaSegments)
                                .withGsSegments(gsSegments)
                                .withStSegments(stSegments)
                                .withIeaSegments(ieaSegments)
                                .withGeSegments(geSegments));
        }
        return baseList;
    }

    private List<String> scanSegment(String segment) {

        List<String> l = new ArrayList<>();

        Scanner scanner = new Scanner(segment);
        scanner.useDelimiter(Pattern.quote(getElementSeparator().toString()));
        while (scanner.hasNext()) {
            l.add(scanElement(scanner.next().trim()));
        }

        switch (l.get(0)) {
            case "ST":
                if (stSegments.size() > 0) {
                    baseList.add(x12File.withIsaSegments(isaSegments)
                                        .withGsSegments(gsSegments)
                                        .withStSegments(stSegments)
                                        .withIeaSegments(ieaSegments)
                                        .withGeSegments(geSegments));
                    x12File = new X12File();
                }
                break;
            case "GS":
                gsSegments = scanHeaderSegment(segment);
                return null;
            case "GE":
                geSegments = scanHeaderSegment(segment);
                return null;
            case "IEA":
                ieaSegments = scanHeaderSegment(segment);
                return null;
        }

        return l;
    }

    private List<String> scanHeaderSegment(String segment) {
        List<String> l = new ArrayList<>();
        Scanner scanner = new Scanner(segment);
        scanner.useDelimiter(Pattern.quote(getElementSeparator().toString()));
        while (scanner.hasNext()) {
            String element = scanner.next().trim();
            l.add(element);
        }
        return l;
    }

    private String scanElement(String element) {
        List<String> l = new ArrayList<>();
        Scanner scanner = new Scanner(element);
        scanner.useDelimiter(Pattern.quote(getCompositeElementSeparator().toString()));
        while (scanner.hasNext()) {
            l.add(scanner.next());
        }
        return l.stream().collect(Collectors.joining(":"));
    }

    private void parseHeader() throws IOException {
        char[] buf = new char[HEADER_LENGTH];

        int size = inputReader.read(buf);
        if (size != HEADER_LENGTH) {
            throw new IOException("Size is not " + HEADER_LENGTH + " but " + size);
        }

        segmentSeparator = buf[SEGMENT_POSITION];
        elementSeparator = buf[ELEMENT_POSITION];
        compositeElementSeparator = buf[COMPOSITE_ELEMENT_POSITION];

        isaSegments = scanHeaderSegment(testIsaSegment(buf));

    }

    public Character getSegmentSeparator() {
        return segmentSeparator;
    }

    public Character getElementSeparator() {
        return elementSeparator;
    }

    public Character getCompositeElementSeparator() {
        return compositeElementSeparator;
    }

    private String testIsaSegment(char[] buf) throws IOException {
        List<String> l = new ArrayList<>();

        Scanner scanner = new Scanner(new String(buf));
        scanner.useDelimiter(Pattern.quote(getElementSeparator().toString()));
        while (scanner.hasNext()) {
            l.add(scanner.next().trim());
        }
        scanner.close();
        if (!l.get(0).equals("ISA")) {
            throw new IOException("Not valid file format. Got " + l.get(0) + " instead of ISA. " + getElementSeparator().toString());
        }
        return l.stream().collect(Collectors.joining(getElementSeparator().toString()));
    }

}
