package com.javaSEBasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipHandler{

    final FileHandler fileHandler = new FileHandler();
    private static final Logger LOGGER = LoggerFactory.getLogger(UnzipHandler.class);
    public static ArrayList<String> tempZipContent = new ArrayList<String>();
    public static Deque<String> archivesStructure = new ArrayDeque<String>();
    public static Map<String, ArrayList<String>> zipWithChildren = new HashMap<String, ArrayList<String>>();

//    public static Deque<ZipEntry> test = new ArrayDeque<ZipEntry>();


    public void unzip(final String currentPath,final String rootPath) {
        Enumeration entries;
        ZipFile zipFile;
        InputStream fileToWrite;
        try {
            zipFile = new ZipFile(currentPath);
            entries = zipFile.entries();
            archivesStructure.addFirst(currentPath);
            ArrayList<String> innerZipContent = new ArrayList<String>();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String pathToFile = getPathToFile(currentPath) + entry.getName();
                final String extension = getFileExtension(entry.getName());
                innerZipContent.add(pathToFile);

                if (entry.isDirectory()) {
                    (new File(pathToFile)).mkdir();
                    continue;
                }

                if (extension.equals("application/x-zip-compressed") || extension.equals("application/x-gzip")) {
                    (new File(pathToFile + ".temp")).mkdir();
                    pathToFile = pathToFile + ".temp" + "/" + Paths.get(pathToFile).getFileName().toString();
                    tempZipContent.add(pathToFile);
//                    fileToWrite = zipFile.getInputStream(entry);
                    copyInputStream(zipFile.getInputStream(entry),
                            new BufferedOutputStream(new FileOutputStream(pathToFile)));
                }
                else {
//                    fileToWrite = fileHandler.readFile(zipFile.getInputStream(entry));
                    fileHandler.copyFile(zipFile.getInputStream(entry),pathToFile);
                }

//                copyInputStream(fileToWrite,
//                        new BufferedOutputStream(new FileOutputStream(pathToFile)));
            }
            zipWithChildren.put(currentPath, innerZipContent);
            zipFile.close();
            if(!rootPath.equals(currentPath)){
                deleteExtractedArchive(currentPath);
            }

            if (!tempZipContent.isEmpty()) {
                for (Iterator<String> innerZipFilePathArray = tempZipContent.iterator(); innerZipFilePathArray.hasNext(); ) {
                    String innerPath = innerZipFilePathArray.next();
                    innerZipFilePathArray.remove();
                    if (("application/x-zip-compressed").equals(getFileExtension(innerPath))) {
                        unzip(innerPath,rootPath);
                    } else {
                        unGzip(innerPath);
                    }

                }
            }
        } catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return;
        }
    }

    private void unGzip(String pathToGzip){
        archivesStructure.addFirst(pathToGzip);
        final String pathToFile = pathToGzip.substring(0,pathToGzip.length()-3);
        try {
//            final InputStream fileToWrite = fileHandler.readFile(new GZIPInputStream(new FileInputStream(pathToGzip)));
//            final InputStream fileToWrite = fileHandler.readFile(new GZIPInputStream(new FileInputStream(pathToGzip)));
//            copyInputStream(fileHandler.readFile(new GZIPInputStream(new FileInputStream(pathToGzip))),
//                    new BufferedOutputStream(new FileOutputStream(pathToFile)));
            fileHandler.copyFile(new GZIPInputStream(new FileInputStream(pathToGzip)),pathToFile);
            deleteExtractedArchive(pathToGzip);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getPathToFile(String path){
//        return path.replace(Paths.get(path).getFileName().toString(), "");
        return path.substring(0,path.lastIndexOf(Paths.get(path).getFileName().toString()));
    }

    public String getFileExtension (String pathToFile) throws IOException {
        final Path path = Paths.get(pathToFile);
        return Files.probeContentType(path);
    }

    private void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    private void deleteExtractedArchive(String path){
        final Path target = Paths.get(path);
        try {
            Files.delete(target);
        } catch (IOException e) {
            LOGGER.error("Can't delete file : {}",path);
        }
    }
}

