package com.javaSEBasic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipHandler.class);
    final UnzipHandler unzipHandler = new UnzipHandler();

    final DeletingFileVisitor delFileVisitor = new DeletingFileVisitor();

    public void createZipArchive(Deque<String> archivesStructure, Map<String, ArrayList<String>> zipWithChildren, String pathToCut) {
        for (String currentArchive : archivesStructure) {
//            final String archiveName = currentArchive.substring(currentArchive.lastIndexOf("/") + 1);
//            final String archiveName = currentArchive.substring(0,currentArchive.lastIndexOf("/"));
            FileOutputStream outputStream = null;
            ZipOutputStream zos = null;
            final boolean isLastElement = currentArchive.equals(archivesStructure.getLast());
            boolean isGzipArchive = false;
            try {
                outputStream = new FileOutputStream(currentArchive);
                zos = new ZipOutputStream(outputStream);
                if(isGzipArchive=unzipHandler.getFileExtension(currentArchive).equals("application/x-gzip")){
                    gzipFile(currentArchive);
                }else {
                    for (String archiveContent : addInnerZipContent(zipWithChildren.get(currentArchive))) {
                        final File innerFile = new File(archiveContent);
                        if (innerFile.isDirectory()) {
//                        zipDir(innerFile, zos, pathToCut);
                            zipDir(innerFile, zos, currentArchive.substring(0,currentArchive.lastIndexOf("/")+1));
                        } else {
//                        zipFile(innerFile, zos, pathToCut);
                            zipFile(innerFile, zos, archiveContent.substring(0,archiveContent.lastIndexOf("/")));
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("Error creating outputStream. File: {} was not found", currentArchive);
            } catch (IOException e) {
                LOGGER.error("Can't get File extension : {}",currentArchive);
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

            moveFileToRealPath(currentArchive,isGzipArchive,isLastElement);
        }
    }

    private ArrayList<String> addInnerZipContent(final ArrayList<String> innerContent){
//        final File currentFile = new File(pathToFile);
        String tempPath="";
        ArrayList<String> filesToZip = new ArrayList<String>();
        for(String currentPath: innerContent){
            boolean isSameDirectory = !"".equals(tempPath);
//            if(currentPath.length() >= tempPath.length()
//                    && !tempPath.equals(currentPath.substring(0,tempPath.length()))){
//                isSameDirectory = false;
//            }
            if(currentPath.length() <= tempPath.length()){
                isSameDirectory = false;
            }else if (!tempPath.equals(currentPath.substring(0,tempPath.length()))){
                isSameDirectory = false;
            }
            if(!isSameDirectory){
                filesToZip.add(currentPath);
                tempPath = currentPath;
            }

//            if("".equals(tempPath) || !tempPath.equals(currentPath.substring(0,tempPath.length()))){
//                filesToZip.add(currentPath);
//                tempPath = currentPath;
//            }
        }
//        if(currentFile.isDirectory()){

        return filesToZip;
//        }else {
//            innerContent.add(pathToFile);
//        }

    }

    private void zipDir(File dir, ZipOutputStream zos, String pathToCut){
        File[] files = dir.listFiles();
        byte[] tmpBuf = new byte[1024];
        zipEmptyDir(dir,zos,pathToCut);
        for (File currentFile: files) {
            if (currentFile.isDirectory()) {
                zipDir(currentFile, zos, pathToCut);
                continue;
            }
                zipFile(currentFile,zos, pathToCut);
        }
//        if(dir.list().length == 0){
//            zipEmptyDir(dir, zos, pathToCut);
//        }
    }

    private void zipFile(File file, ZipOutputStream zos, String pathToCut) {
        FileInputStream fileInputStream = null;
        final byte[] buffer = new byte[1024];
        try {
            fileInputStream = new FileInputStream(file);
//            zos.putNextEntry(new ZipEntry(file.getAbsolutePath().replace("D:\\testData\\testToZip\\","")));
//            zos.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(pathToCut.replace("/","\\"),"")));
            zos.putNextEntry(new ZipEntry((file.getAbsolutePath().replace("\\","/")).replace(pathToCut,"")));

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

    private void zipEmptyDir(File file, ZipOutputStream zos, String pathToCut){
        try {
//            zos.putNextEntry(new ZipEntry(file.getAbsolutePath().replace(pathToCut.replace("/","\\"),"") + "/"));
            zos.putNextEntry(new ZipEntry((file.getAbsolutePath().replace("\\","/")).replace(pathToCut,"")+"/"));
            zos.closeEntry();
        } catch (IOException e) {
            LOGGER.error("Error Adding file : {} to ZipEntry", file.getAbsolutePath());
        }
    }

    private void moveFileToRealPath(final String fromPath, boolean isGzip, boolean isLastElement){

        final Path source = Paths.get(fromPath);
        Path fileToDelete;

        if(isLastElement){
            fileToDelete = Paths.get(fromPath.substring(0,fromPath.lastIndexOf(".")));
        }else {
            fileToDelete = source.getParent();
        }
        final Path target = Paths.get(fileToDelete.getParent().toString() + "\\" + source.getFileName().toString());//Paths.get(toPath);


//        final String toPath = fromPath.replace(currentArchive.substring(currentArchive.lastIndexOf("/"))+".temp","");

        try {
            if(!isGzip && !isLastElement){
                Files.move(source, target);
            }
            Files.walkFileTree(fileToDelete,delFileVisitor);
        }
        catch (IOException e) {
            LOGGER.error("Could not move file : {}",fromPath);
        }
    } 
    private void gzipFile (String path){
        try {
            final Path source = Paths.get(path);
            final String fileName = source.getParent().getParent().toString()+"\\" + source.getFileName();
            FileInputStream fis = new FileInputStream(path);
            FileOutputStream fos = new FileOutputStream(fileName);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzos.write(buffer, 0, len);
            }
            gzos.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

//    public static void copyFile(File sourceFile, File destFile) throws IOException {
//        if(!destFile.exists()) {
//            destFile.createNewFile();
//        }
//
//        FileChannel source = null;
//        FileChannel destination = null;
//        try {
//            source = new FileInputStream(sourceFile).getChannel();
//            destination = new FileOutputStream(destFile).getChannel();
//
//            long count = 0;
//            long size = source.size();
//            while((count += destination.transferFrom(source, count, size-count))<size);
//        }
//        finally {
//            if(source != null) {
//                source.close();
//            }
//            if(destination != null) {
//                destination.close();
//            }
//        }
//    }
}
