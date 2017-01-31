package org.apache.nifi.processors.x12;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class X12ParserTest {
    @Test
    public void testDelimiters() throws IOException {

        File file = new File(getClass().getClassLoader().getResource("sample-004010.837").getFile());

        X12Parser x12Parser = new X12Parser();
        x12Parser.parse(file);

        assertTrue(x12Parser.getSegmentSeparator() == '~');
        assertTrue(x12Parser.getElementSeparator() == '*');
        assertTrue(x12Parser.getCompositeElementSeparator() == ':');
    }
}
