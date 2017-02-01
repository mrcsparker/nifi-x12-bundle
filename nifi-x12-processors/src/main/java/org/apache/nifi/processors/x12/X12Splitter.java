package org.apache.nifi.processors.x12;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * EDI X12 file splitter
 *
 * @author <a href="mailto:mrcsparker@gmail.com">mrcsparker@gmail.com</a>
 */
public class X12Splitter {

    private static final int HEADER_LENGTH = 106;
    private static final int SEGMENT_POSITION = 105;
    private static final int ELEMENT_POSITION = 3;
    private static final int COMPOSITE_ELEMENT_POSITION = 104;

    private Character segmentSeparator;
    private Character elementSeparator;
    private Character compositeElementSeparator;

    private final Reader inputReader;
    private List<String> baseList = new ArrayList<>();
    private List<String> ediList = new ArrayList<>();

    private String isaSegment;
    private String gsSegment;
    private String geSegment;
    private String ieaSegment;

    /**
     * @param inputStream Input Stream
     */
    public X12Splitter(InputStream inputStream) {
        this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public List<String> split() throws IOException {

        parseHeader();

        Scanner scanner = new Scanner(inputReader);
        scanner.useDelimiter(Pattern.quote(getSegmentSeparator().toString()));

        while(scanner.hasNext()) {
            String s = scanSegment(scanner.next());
            if (!s.equals("")) {
                ediList.add(s);
            }
        }
        if (ediList.size() > 0) {
            Collections.reverse(ediList);
            ediList.add(gsSegment);
            ediList.add(isaSegment);
            Collections.reverse(ediList);
            ediList.add(geSegment);
            ediList.add(ieaSegment);
            baseList.add(ediList.stream().collect(Collectors.joining("~")));
        }
        return baseList;
    }

    String scanSegment(String segment) {

        List<String> l = new ArrayList<>();

        Scanner scanner = new Scanner(segment);
        scanner.useDelimiter(Pattern.quote(getElementSeparator().toString()));
        while (scanner.hasNext()) {
            l.add(scanner.next().trim());
        }

        switch (l.get(0)) {
            case "ST":
                if (ediList.size() > 0) {
                    Collections.reverse(ediList);
                    ediList.add(gsSegment);
                    ediList.add(isaSegment);
                    Collections.reverse(ediList);
                    ediList.add(geSegment);
                    ediList.add(ieaSegment);
                    baseList.add(ediList.stream().collect(Collectors.joining("~")));
                    ediList = new ArrayList<>();
                }
                break;
            case "GS":
                gsSegment = scanHeaderSegment(segment);
                return "";
            case "GE":
                geSegment = scanHeaderSegment(segment);
                return "";
            case "IEA":
                ieaSegment = scanHeaderSegment(segment);
                return "";
        }

        return l.stream().collect(Collectors.joining("*"));
    }

    String scanHeaderSegment(String segment) {
        List<String> l = new ArrayList<>();
        Scanner scanner = new Scanner(segment);
        scanner.useDelimiter(Pattern.quote(getElementSeparator().toString()));
        while (scanner.hasNext()) {
            String element = scanner.next().trim();
            l.add(scanElement(element));
        }
        return l.stream().collect(Collectors.joining("*"));
    }

    String scanElement(String element) {
        List<String> l = new ArrayList<>();
        Scanner scanner = new Scanner(element);
        scanner.useDelimiter(Pattern.quote(getCompositeElementSeparator().toString()));
        while (scanner.hasNext()) {
            l.add(scanner.next());
        }
        return l.stream().collect(Collectors.joining(":"));
    }

    public void parseHeader() throws IOException {
        char[] buf = new char[HEADER_LENGTH];

        int size = inputReader.read(buf);
        if (size != HEADER_LENGTH) {
            throw new IOException("Size is not " + HEADER_LENGTH + " but " + size);
        }

        segmentSeparator = buf[SEGMENT_POSITION];
        elementSeparator = buf[ELEMENT_POSITION];
        compositeElementSeparator = buf[COMPOSITE_ELEMENT_POSITION];

        isaSegment = scanHeaderSegment(testIsaSegment(buf));

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
        Scanner scanner = new Scanner(new String(buf));
        scanner.useDelimiter(Pattern.quote(getElementSeparator().toString()));
        String result = scanner.next();
        scanner.close();
        if (!result.equals("ISA")) {
            throw new IOException("Not valid file format. Got " + result + " instead of ISA. " + getElementSeparator().toString());
        }
        return new String(buf);
    }

}
