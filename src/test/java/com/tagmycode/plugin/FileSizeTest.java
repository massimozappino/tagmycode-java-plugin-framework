package com.tagmycode.plugin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileSizeTest {
    @Test
    public void toHumanReadable() {
        assertEquals("1024 Bytes", FileSize.toHumanReadable(1024L));
        assertEquals("4.88 MB", FileSize.toHumanReadable(1024 * 5000L));
        assertEquals("7.07 GB", FileSize.toHumanReadable(7_594_884_397d));
        assertEquals("90.95 TB", FileSize.toHumanReadable(100_000_000_000_000d));
    }
}