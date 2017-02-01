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
    }

    @Test
    public void doubleSplitIntoArray() throws IOException {

        File file = new File(getClass().getClassLoader().getResource("sample-double-004010.837").getFile());
        InputStream inputStream = new FileInputStream(file);

        X12Splitter x12Splitter = new X12Splitter(inputStream);
        List<X12File> edi = x12Splitter.split();
        edi.forEach((e) -> System.out.println(e));

        inputStream.close();

        System.out.println(edi.size());

        assertTrue(edi.size() == 2);
    }
}
