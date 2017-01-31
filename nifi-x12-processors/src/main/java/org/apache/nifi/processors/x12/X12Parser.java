package org.apache.nifi.processors.x12;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class X12Parser {
    private static final int HEADER_LENGTH = 106;
    private static final int SEGMENT_POSITION = 105;
    private static final int ELEMENT_POSITION = 3;
    private static final int COMPOSITE_ELEMENT_POSITION = 104;

    private Character segmentSeparator;
    private Character elementSeparator;
    private Character compositeElementSeparator;

    void parse(File f) throws IOException {
        char[] buf = new char[HEADER_LENGTH];
        FileReader fileReader = new FileReader(f);
        int size = fileReader.read(buf);
        fileReader.close();
        if (size != HEADER_LENGTH) {
            throw new IOException("Size is not " + HEADER_LENGTH + " but " + size);
        }

        segmentSeparator = buf[SEGMENT_POSITION];
        elementSeparator = buf[ELEMENT_POSITION];
        compositeElementSeparator = buf[COMPOSITE_ELEMENT_POSITION];

        test(f);
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

    private void test(File f) throws IOException {
        Scanner scanner = new Scanner(f);
        scanner.useDelimiter(Pattern.quote(getElementSeparator().toString()));
        String result = scanner.next();
        scanner.close();
        if (!result.equals("ISA")) {
            throw new IOException("Not valid file format");
        }
    }
}