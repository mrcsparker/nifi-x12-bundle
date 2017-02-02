package org.apache.nifi.processors.x12;


import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class X12Parser {
    private static final int HEADER_LENGTH = 106;
    private static final int SEGMENT_POSITION = 105;
    private static final int ELEMENT_POSITION = 3;
    private static final int COMPOSITE_ELEMENT_POSITION = 104;

    private String isa;
    private String gs;
    private String ge;
    private String iea;

    private Character segmentSeparator;
    private Character elementSeparator;
    private Character compositeElementSeparator;

    public void parse(InputStream inputStream) throws IOException {
        char[] buf = new char[HEADER_LENGTH];

        Reader reader = new BufferedReader(new InputStreamReader(inputStream));
        int size = reader.read(buf);
        if (size != HEADER_LENGTH) {
            throw new IOException("Size is not " + HEADER_LENGTH + " but " + size);
        }

        segmentSeparator = buf[SEGMENT_POSITION];
        elementSeparator = buf[ELEMENT_POSITION];
        compositeElementSeparator = buf[COMPOSITE_ELEMENT_POSITION];

        testISA(buf);
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

    private void testISA(char[] buf) throws IOException {
        Scanner scanner = new Scanner(new String(buf));
        scanner.useDelimiter(Pattern.quote(getElementSeparator().toString()));
        isa = scanner.next();
        scanner.close();
        if (!isa.equals("ISA")) {
            throw new IOException("Not valid file format. Got " + isa + " instead of ISA. " + getElementSeparator().toString());
        }
    }
}
