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
import java.util.zip.ZipOutputStream;

public class ZipHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipHandler.class);

//    public static ArrayList<String> tempZipStorage = new ArrayList<String>();
    public static ArrayList<String> zipContent = new ArrayList<String>();
//    public static ArrayList<String> zipStructure = new ArrayList<String>();
    public static Deque<String> zipStructure = new ArrayDeque<String>();
    public static Map<String, ArrayList<String>> zipWithChildren = new HashMap<String, ArrayList<String>>();

    private String getPathToFile(String path){
//        return path.replace(Paths.get(path).getFileName().toString(), "");
        return path.substring(0,path.lastIndexOf(Paths.get(path).getFileName().toString()));
    }

    public void unzip(String path) {
        Enumeration entries;
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(path);
            entries = zipFile.entries();
            zipStructure.addFirst(path);
            ArrayList<String> innerZipContent = new ArrayList<String>();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String pathToFile = getPathToFile(path)+entry.getName();
                final String extension = getFileExtension(entry.getName());
                innerZipContent.add(pathToFile);

                if (entry.isDirectory()) {
                    (new File(pathToFile)).mkdir();
                    continue;
                }

                if (extension.equals("application/x-zip-compressed") || extension.equals("application/x-gzip")){
                    (new File(pathToFile)).mkdir();
                    pathToFile = pathToFile + "/" + Paths.get(pathToFile).getFileName().toString();
                    zipContent.add(pathToFile);
                }

                copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(pathToFile)));
            }
            zipWithChildren.put(path,innerZipContent);
            zipFile.close();

            deleteExtractedArchive(path);

            if(!zipContent.isEmpty()){
                for (Iterator<String> innerZipFilePathArray = zipContent.iterator(); innerZipFilePathArray.hasNext();){
                    String innerPath = innerZipFilePathArray.next();
                    innerZipFilePathArray.remove();
                    if(("application/x-zip-compressed").equals(getFileExtension(innerPath))){
                        unzip(innerPath);
                    }else {
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

    private void deleteExtractedArchive(String path){
        final Path target = Paths.get(path);
        try {
            Files.delete(target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unGzip(String pathToGzip){
        final String pathToFile = pathToGzip.substring(0,pathToGzip.length()-3);
        try {
            copyInputStream(new GZIPInputStream(new FileInputStream(pathToGzip)),
                    new BufferedOutputStream(new FileOutputStream(pathToFile)));
            deleteExtractedArchive(pathToGzip);
        } catch (IOException e) {
            e.printStackTrace();
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

//    public void zipDirectory(String zipFileName, String dir){
//        final File file = new File(dir);
//        FileOutputStream outputStream = null;
//        ZipOutputStream zos = null;
//        try {
//            outputStream = new FileOutputStream(zipFileName);
//            zos = new ZipOutputStream(outputStream);
//            LOGGER.info("creating : {}", zipFileName);
//            addDirectory(file, zos);
//        } catch (FileNotFoundException e) {
//            LOGGER.error("Can't file file : {}", zipFileName);
//        } finally {
//            try {
//                zos.close();
//            } catch (IOException e) {
//                LOGGER.error("Can't close OutputStream");
//            }
//        }
//    }


    public void createZipArchive (final String zipName,final ArrayList<String> inner){
        FileOutputStream outputStream = null;
        ZipOutputStream zos = null;
        try {
            outputStream = new FileOutputStream(zipName);
            zos = new ZipOutputStream(outputStream);
            for (String innerPath: inner){
                final File innerFile = new File(innerPath);
                if(innerFile.isDirectory()){
                    zipDir(innerFile,zos);
                }else {
                    zipFile(innerFile,zos);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Error creating outputStream. File: {} was not found",zipName);
        } finally {
            if (zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    LOGGER.error("Can't close zipOutputStream: {}",zos);
                }
            }
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Can't close FileOutputStream: {}",outputStream);
                }

            }
        }
    }
    public void testMethodToCallZip(){
        final String path = "D:/testData/testToZip/firstZip.zip";
        final ArrayList<String> testData = new ArrayList<String>(10);
        testData.add("D:/testData/testToZip/data2");
        testData.add("D:/testData/testToZip/data1.txt");
        createZipArchive(path, testData);
    }

//    public void zipFiles(final String zipName, final ArrayList<String> inner){
//
//        try {
//            final FileOutputStream outputStream = new FileOutputStream(zipName);
//            final ZipOutputStream zos = new ZipOutputStream(outputStream);
//            final byte [] buffer = new byte[1024];
//            FileInputStream fileInputStream = null;
//
//            for (String innerPath: inner){
//                final File innerFile = new File(innerPath);
//                try {
//                    fileInputStream = new FileInputStream(innerFile);
//                    zos.putNextEntry(new ZipEntry(innerFile.getName()));
//                    final int length = fileInputStream.read(buffer);
//                    while (length > 0){
//                        zos.write(buffer,0,length);
//                    }
//                    zos.closeEntry();
//                }catch (FileNotFoundException e) {
//                    LOGGER.error("File : {} was not found", innerPath);
//                }catch (IOException e) {
//                    LOGGER.error("Error Adding zipEntry: {}", innerFile.getName());
//                }finally {
//                    try {
//                        if (fileInputStream!=null){
//                            fileInputStream.close();
//                        }
//                        zos.close();
//                    } catch (IOException e) {
//                        LOGGER.error("Can't close ZipOutputStream");
//                    }
//                }
//
//            }
//        } catch (FileNotFoundException e) {
//            LOGGER.error("Error creating outputStream. File: {} was not found",zipName);
//        }
//    }

    private void zipDir(File dir, ZipOutputStream zos){
        File[] files = dir.listFiles();
        byte[] tmpBuf = new byte[1024];
        for (File currentFile: files) {
            if (currentFile.isDirectory()) {
                zipDir(currentFile, zos);
                continue;
            }
                zipFile(currentFile,zos);


        }
    }

    private void zipFile(File file, ZipOutputStream zos) {
        FileInputStream fileInputStream = null;
        final byte[] buffer = new byte[1024];
        try {
            fileInputStream = new FileInputStream(file);
            zos.putNextEntry(new ZipEntry(file.getAbsolutePath().replace("D:\\testData\\testToZip\\","")));
            int length;
            while ((length = fileInputStream.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        } catch (FileNotFoundException e) {
            LOGGER.error("File : {} was not found", file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Error Adding zipEntry: {}", file.getName());
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("Can't close ZipOutputStream");
            }
        }
    }

//    private void addDirectory(File dir, ZipOutputStream zos){
//        File[] files = dir.listFiles();
//        byte[] tmpBuf = new byte[1024];
//        FileInputStream in = null;
//
//        for (File currentFile: files) {
//
//            if (currentFile.isDirectory()) {
//                addDirectory(currentFile, zos);
//                continue;
//            }
//            try {
//                final String path = currentFile.getAbsolutePath();
//                in = new FileInputStream(path);
//                zos.putNextEntry(new ZipEntry(path));
//                int len;
//                while ((len = in.read(tmpBuf)) > 0) {
//                    zos.write(tmpBuf, 0, len);
//                }
//                zos.closeEntry();
//            } catch (FileNotFoundException e) {
//                LOGGER.error("File : {} was not found ", currentFile.getAbsolutePath());
//            } catch (IOException e) {
//                LOGGER.error("Can't create Zip Entry : {}",currentFile.getAbsolutePath());
//            } finally {
//                try {
//                    if(in!=null){
//                        in.close();
//                    }
//                } catch (IOException e) {
//                    LOGGER.error("can't close inputStream");
//                }
//            }
//        }
//    }

    private void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }
}
