package com.tagmycode.plugin;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextEncodingTest {
    @Test
    public void isBinaryFile() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileText = new File(classLoader.getResource("textEncoding/text").getFile());
        File fileBinary = new File(classLoader.getResource("textEncoding/binary").getFile());

        assertFalse(new TextEncoding().isBinaryFile(fileText));
        assertTrue(new TextEncoding().isBinaryFile(fileBinary));
    }

}