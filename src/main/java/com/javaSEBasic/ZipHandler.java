package com.javaSEBasic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipHandler.class);

//    public static ArrayList<String> tempZipStorage = new ArrayList<String>();
    public static ArrayList<String> zipContent = new ArrayList<String>();
    public static ArrayList<String> folderZipStructure = new ArrayList<String>();
//    public static Deque<String> zipContent = new ArrayDeque<String>();

    private String getPathToFile(String path){
        return path.replace(Paths.get(path).getFileName().toString(), "");
    }

    public void unzip(String path) {
        Enumeration entries;
        ZipFile zipFile;

        try {
            zipFile = new ZipFile(path);

            entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String pathToFile = getPathToFile(path)+entry.getName();
                final String extension = getFileExtension(entry.getName());

                if (entry.isDirectory()) {
                    (new File(pathToFile)).mkdir();
                    continue;
                }

                if (extension.equals("application/x-zip-compressed") || extension.equals("application/x-zip-compressed")){
                    (new File(pathToFile)).mkdir();
                    pathToFile = pathToFile + "/" + Paths.get(pathToFile).getFileName().toString();
                    zipContent.add(pathToFile);
                }

                copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(pathToFile)));
            }

            zipFile.close();
            if(!zipContent.isEmpty()){
                for (Iterator<String> innerZipFilePathArray = zipContent.iterator(); innerZipFilePathArray.hasNext();){
                    String innerPath = innerZipFilePathArray.next();
                    innerZipFilePathArray.remove();
                    unzip(innerPath);
                }
            }
        } catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return;
        }
    }

    private void addZipPath (String zipPath, String innerZipPath ){
        final String pathToMainZip = zipPath.substring(0,zipPath.indexOf(Paths.get(zipPath).getFileName().toString()));
        String formatPath = pathToMainZip+innerZipPath;
        zipContent.add(formatPath);
    }

    private String getFileExtension (String pathToFile) throws IOException {
        final Path path = Paths.get(pathToFile);
        return Files.probeContentType(path);
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
                final String path = currentFile.getAbsolutePath();
                in = new FileInputStream(path);
                zos.putNextEntry(new ZipEntry(path));
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
