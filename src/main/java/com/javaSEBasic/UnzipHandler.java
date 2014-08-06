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
    private static ArrayList<String> tempZipContent = new ArrayList<String>();
    public static Deque<String> archivesStructure = new ArrayDeque<String>();
    public static Map<String, ArrayList<String>> zipWithChildren = new HashMap<String, ArrayList<String>>();

    public void unzip(final String currentPath,final String rootPath) {
        Enumeration entries;
        ZipFile zipFile = null;
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
                    copyInputStream(zipFile.getInputStream(entry),
                            new BufferedOutputStream(new FileOutputStream(pathToFile)));
                }
                else {
                    fileHandler.copyFile(zipFile.getInputStream(entry),pathToFile);
                }
            }
            zipWithChildren.put(currentPath, innerZipContent);
            zipFile.close();
            if(!rootPath.equals(currentPath)){
                deleteExtractedArchive(currentPath);
            }
        } catch (IOException e) {
            LOGGER.error("Error While unzipping file : {}",currentPath);
        }catch (NullPointerException e){
            LOGGER.error("Error getting file extension");
        }
        if (!tempZipContent.isEmpty()) {
            runUnzipRecursively(rootPath);
        }
    }

    private void runUnzipRecursively(final String rootPath){
        for (Iterator<String> innerZipFilePathArray = tempZipContent.iterator(); innerZipFilePathArray.hasNext(); ) {
            final String innerPath = innerZipFilePathArray.next();
            try {
                innerZipFilePathArray.remove();
                if (("application/x-zip-compressed").equals(getFileExtension(innerPath))) {
                    unzip(innerPath,rootPath);
                } else {
                    unGzip(innerPath);
                }
            } catch (NullPointerException e) {
                LOGGER.error("Error getting file extension : {}",innerPath);
            }
        }
    }

    private void unGzip(final String pathToGzip){
        archivesStructure.addFirst(pathToGzip);
        final String pathToFile = pathToGzip.substring(0,pathToGzip.length()-3);
        try {
            fileHandler.copyFile(new GZIPInputStream(new FileInputStream(pathToGzip)),pathToFile);
            deleteExtractedArchive(pathToGzip);
        } catch (IOException e) {
            LOGGER.error("Error creating FileInputStream from Gzip. File : {}",pathToGzip);
        }
    }

    private String getPathToFile(final String path){
        return path.substring(0,path.lastIndexOf(Paths.get(path).getFileName().toString()));
    }

    public String getFileExtension (final String pathToFile) {
        try {
            final Path path = Paths.get(pathToFile);
            return Files.probeContentType(path);
        } catch (IOException e) {
            LOGGER.error("Couldn't get file's type : {}",pathToFile);
        }
        return null;
    }

    private void copyInputStream(final InputStream in, final OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);
        in.close();
        out.close();
    }

    private void deleteExtractedArchive(final String path){
        final Path target = Paths.get(path);
        try {
            Files.delete(target);
        } catch (IOException e) {
            LOGGER.error("Can't delete file : {}",path);
        }
    }
}

