package com.javaSEBasic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipHandler.class);

    public void createZipArchive (final String zipName,final ArrayList<String> inner, String path){
        FileOutputStream outputStream = null;
        ZipOutputStream zos = null;
        try {
            outputStream = new FileOutputStream(zipName);
            zos = new ZipOutputStream(outputStream);
            for (String innerPath: inner){
                final File innerFile = new File(innerPath);
                if(innerFile.isDirectory()){
                    zipDir(innerFile,zos, path);
                }else {
                    zipFile(innerFile,zos,path);
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

    public void createZipArchive(Deque<String> archivesStructure, Map<String, ArrayList<String>> zipWithChildren, String pathToCut) {
        for (String currentArchive : archivesStructure) {
            final String archiveName = currentArchive.substring(currentArchive.lastIndexOf("/") + 1);
            FileOutputStream outputStream = null;
            ZipOutputStream zos = null;
            try {
                outputStream = new FileOutputStream(archiveName);
                zos = new ZipOutputStream(outputStream);
                for (String archiveContent : addInnerZipContent(zipWithChildren.get(currentArchive))) {
                    final File innerFile = new File(archiveContent);
                    if (innerFile.isDirectory()) {
                        zipDir(innerFile, zos, pathToCut);
                    } else {
                        zipFile(innerFile, zos, pathToCut);
                    }
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("Error creating outputStream. File: {} was not found", archiveName);
            } finally {
                if (zos != null) {
                    try {
                        zos.close();
                    } catch (IOException e) {
                        LOGGER.error("Can't close zipOutputStream: {}", zos);
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("Can't close FileOutputStream: {}", outputStream);
                    }

                }
            }
        }
    }

    public void testMethodToCallZip(){
        final String path = "D:/testData/testToZip/firstZip.zip";
        final ArrayList<String> testData = new ArrayList<String>(10);
        testData.add("D:/testData/testToZip/data2");
        testData.add("D:/testData/testToZip/data2/data");
        testData.add("D:/testData/testToZip/data2/data/town4.txt");
        testData.add("D:/testData/testToZip/data2/data/town6.txt");
        testData.add("D:/testData/testToZip/data2/data/town6(2).txt");
        testData.add("D:/testData/testToZip/data1.txt");
        final ArrayList<String> filesToZip = addInnerZipContent(testData);
        createZipArchive(path, testData, path);
    }
//    public static ArrayList<String> tempZipStorage = new ArrayList<String>();

//    private void addZipPath (String zipPath, String innerZipPath ){
//        final String pathToMainZip = zipPath.substring(0,zipPath.indexOf(Paths.get(zipPath).getFileName().toString()));
//        String formatPath = pathToMainZip+innerZipPath;
//        tempZipContent.add(formatPath);
//    }

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



    private ArrayList<String> addInnerZipContent(final ArrayList<String> innerContent){
//        final File currentFile = new File(pathToFile);
        String tempPath="";
        ArrayList<String> filesToZip = new ArrayList<String>();
        for(String currentPath: innerContent){
            if("".equals(tempPath) || !tempPath.equals(currentPath.substring(0,tempPath.length()))){
                filesToZip.add(currentPath);
                tempPath = currentPath;
            }
        }
//        if(currentFile.isDirectory()){

        return filesToZip;
//        }else {
//            innerContent.add(pathToFile);
//        }

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

    private void zipDir(File dir, ZipOutputStream zos, String pathToCut){
        File[] files = dir.listFiles();
        byte[] tmpBuf = new byte[1024];
        for (File currentFile: files) {
            if (currentFile.isDirectory()) {
                zipDir(currentFile, zos, pathToCut);
                continue;
            }
                zipFile(currentFile,zos, pathToCut);


        }
    }

    private void zipFile(File file, ZipOutputStream zos, String pathToCut) {
        FileInputStream fileInputStream = null;
        final byte[] buffer = new byte[1024];
        try {
            fileInputStream = new FileInputStream(file);
            zos.putNextEntry(new ZipEntry(file.getAbsolutePath().replace("D:\\testData\\testToZip\\","")));
//            zos.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(pathToCut.replace("/","\\"),"")));
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

}
