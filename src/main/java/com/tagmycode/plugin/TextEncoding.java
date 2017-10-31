package com.tagmycode.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TextEncoding {

    public boolean isBinaryFile(File f) throws IOException {

        try (FileInputStream in = new FileInputStream(f)) {
            int size = in.available();
            if (size > 1024) size = 1024;
            byte[] data = new byte[size];
            in.read(data);

            int ascii = 0;
            int other = 0;

            for (byte b : data) {
                if (b < 0x09) return true;
                if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D) ascii++;
                else if (b >= 0x20 && b <= 0x7E) ascii++;
                else other++;
            }

            return (other != 0) && (((100 * other) / (ascii + other)) > 95);

        }
    }

}
