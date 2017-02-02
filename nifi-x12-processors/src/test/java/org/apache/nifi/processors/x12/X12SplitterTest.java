package org.apache.nifi.processors.x12;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class X12SplitterTest {

    @Test
    public void simpleSplitIntoArray() throws IOException {

        File file = new File(getClass().getClassLoader().getResource("sample-004010.837").getFile());
        InputStream inputStream = new FileInputStream(file);

        X12Splitter x12Splitter = new X12Splitter(inputStream);
        List<X12File> edi = x12Splitter.split();

        inputStream.close();

        assertTrue(edi.size() == 1);
        assertTrue(edi.get(0).toString().substring(0, 3).equals("ISA"));
        assertTrue(edi.get(0).getControlVersion().equals("00401"));
        assertTrue(edi.get(0).getIdentifierCode().equals("837"));
    }

    @Test
    public void doubleSplitIntoArray() throws IOException {

        File file = new File(getClass().getClassLoader().getResource("sample-double-004010.837").getFile());
        InputStream inputStream = new FileInputStream(file);

        X12Splitter x12Splitter = new X12Splitter(inputStream);
        List<X12File> edi = x12Splitter.split();

        edi.forEach(System.out::println);

        inputStream.close();

        assertTrue(edi.size() == 2);
        assertTrue(edi.get(0).toString().substring(0, 3).equals("ISA"));
        assertTrue(edi.get(0).getControlVersion().equals("00401"));
        assertTrue(edi.get(0).getIdentifierCode().equals("837"));

        assertTrue(edi.get(1).toString().substring(0, 3).equals("ISA"));
        assertTrue(edi.get(1).getControlVersion().equals("00401"));
        assertTrue(edi.get(1).getIdentifierCode().equals("837"));
    }
}
