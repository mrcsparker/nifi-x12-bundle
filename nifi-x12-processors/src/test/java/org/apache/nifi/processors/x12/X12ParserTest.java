package org.apache.nifi.processors.x12;

import org.junit.Test;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class X12ParserTest {
    @Test
    public void testDelimitersOnInputSource() throws IOException {

        File file = new File(getClass().getClassLoader().getResource("sample-004010.837").getFile());
        InputStream inputStream = new FileInputStream(file);

        X12Parser x12Parser = new X12Parser();
        x12Parser.parse(inputStream);

        assertTrue(x12Parser.getSegmentSeparator() == '~');
        assertTrue(x12Parser.getElementSeparator() == '*');
        assertTrue(x12Parser.getCompositeElementSeparator() == ':');
    }
}
