package com.javaSEBasic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipHandler.class);

    public void unzip(String path) {
        Enumeration entries;
        ZipFile zipFile;

        try {
            zipFile = new ZipFile(path);

            entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                if (entry.isDirectory()) {
                    // Assume directories are stored parents first then children.
//                    System.err.println("Extracting directory: " + entry.getName());
                    // This is not robust, just for demonstration purposes.
                    (new File(entry.getName())).mkdir();
                    continue;
                }

                System.err.println("Extracting file: " + entry.getName());
                copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(entry.getName())));
            }

            zipFile.close();
        } catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return;
        }
    }

    public void zipDirectory(String zipFileName, String dir){
        final File file = new File(dir);
        FileOutputStream outputStream = null;
        ZipOutputStream zos = null;
        try {
            outputStream = new FileOutputStream(zipFileName);
            zos = new ZipOutputStream(outputStream);
            LOGGER.info("creating : {}", zipFileName);
            addDirectory(file, zos);
        } catch (FileNotFoundException e) {
            LOGGER.error("Can't file file : {}", zipFileName);
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                LOGGER.error("Can't close OutputStream");
            }
        }
    }

    private void addDirectory(File dir, ZipOutputStream zos){
        File[] files = dir.listFiles();
        byte[] tmpBuf = new byte[1024];
        FileInputStream in = null;

        for (File currentFile: files) {

            if (currentFile.isDirectory()) {
                addDirectory(currentFile, zos);
                continue;
            }
            try {
//                final String path = currentFile.getAbsolutePath();
                in = new FileInputStream(currentFile);
                zos.putNextEntry(new ZipEntry(currentFile.getName()));
                int len;
                while ((len = in.read(tmpBuf)) > 0) {
                    zos.write(tmpBuf, 0, len);
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("File : {} was not found ", currentFile.getAbsolutePath());
            } catch (IOException e) {
                LOGGER.error("Can't create Zip Entry : {}",currentFile.getAbsolutePath());
            } finally {
                try {
                    zos.closeEntry();
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("can't close Entry/inputStream");
                }
            }
        }
    }

    private void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }
}
