package com.svalero.psaa2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFile {

    public static File zip(File inputFile) throws IOException {

        String zipFilePath = inputFile.getAbsolutePath().replace(".csv", ".zip");
        File zipFile = new File(zipFilePath);

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut  = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(inputFile)) {

            ZipEntry zipEntry = new ZipEntry(inputFile.getName());
            zipOut .putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zipOut .write(buffer, 0, length);
            }

            zipOut.closeEntry();
        }

        return zipFile;
    }
}
