package com.javaSEBasic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
    private final UnzipHandler unzipHandler = new UnzipHandler();
    private final DeletingFileVisitor delFileVisitor = new DeletingFileVisitor();


    public void createZipArchive(final Deque<String> archivesStructure, final Map<String, ArrayList<String>> zipWithChildren, final String rootPath) {
        for (String currentArchive : archivesStructure) {
            FileOutputStream outputStream = null;
            ZipOutputStream zos = null;
            final boolean isLastElement = currentArchive.equals(archivesStructure.getLast());
            boolean isGzipArchive = false;
            try {
                if(rootPath.equals(currentArchive)){
                    outputStream = new FileOutputStream(currentArchive.substring(0, rootPath.lastIndexOf("."))+"2.zip");
                }else {
                    outputStream = new FileOutputStream(currentArchive);
                }
                zos = new ZipOutputStream(outputStream);
                if(isGzipArchive = unzipHandler.getFileExtension(currentArchive).equals("application/x-gzip")){
                    gzipFile(currentArchive);
                }else {
                    for (String archiveContent : addInnerZipContent(zipWithChildren.get(currentArchive))) {
                        final File innerFile = new File(archiveContent);
                        if (innerFile.isDirectory()) {
                            zipDir(innerFile, zos, currentArchive.substring(0,currentArchive.lastIndexOf("/")+1));
                        } else {
                            zipFile(innerFile, zos, archiveContent.substring(0,archiveContent.lastIndexOf("/")));
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("Error creating outputStream. File: {} was not found", currentArchive);
            } catch (IOException e) {
                LOGGER.error("Can't get File extension : {}",currentArchive);
            } catch (NullPointerException e){
                LOGGER.error("Error getting file Extension : {}",currentArchive);
            }
            finally {
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
        String tempPath="";
        ArrayList<String> filesToZip = new ArrayList<String>();
        for(String currentPath: innerContent){
            boolean isSameDirectory = !"".equals(tempPath);
            if(currentPath.length() <= tempPath.length() || !tempPath.equals(currentPath.substring(0,tempPath.length()))){
                isSameDirectory = false;
            }
            if(!isSameDirectory){
                filesToZip.add(currentPath);
                tempPath = currentPath;
            }
        }
        return filesToZip;
    }

    private void zipDir(final File dir, final ZipOutputStream zos, final String pathToCut){
        File[] files = dir.listFiles();
        zipEmptyDir(dir,zos,pathToCut);
        for (File currentFile: files) {
            if (currentFile.isDirectory()) {
                zipDir(currentFile, zos, pathToCut);
                continue;
            }
                zipFile(currentFile,zos, pathToCut);
        }
    }

    private void zipFile(final File file,final ZipOutputStream zos,final String pathToCut) {
        FileInputStream fileInputStream = null;
        final byte[] buffer = new byte[1024];
        try {
            fileInputStream = new FileInputStream(file);
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

    private void zipEmptyDir(final File file,final ZipOutputStream zos,final String pathToCut){
        try {
            zos.putNextEntry(new ZipEntry((file.getAbsolutePath().replace("\\","/")).replace(pathToCut,"")+"/"));
        } catch (IOException e) {
            LOGGER.error("Error Adding file : {} to ZipEntry", file.getAbsolutePath());
        }finally {
            try {
                zos.closeEntry();
            } catch (IOException e) {
                LOGGER.error("Couldn't close ZipOutputStream : {}",zos);
            }
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
        final Path target = Paths.get(fileToDelete.getParent().toString() + "\\" + source.getFileName().toString());
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

    private void gzipFile (final String path){
        FileInputStream fis = null;
        FileOutputStream fos = null;
        GZIPOutputStream gzos = null;
        try {
            final Path source = Paths.get(path);
            fis = new FileInputStream(path.substring(0,path.lastIndexOf(".gz")));
            fos = new FileOutputStream(source.getParent().getParent().toString()+"\\" + source.getFileName());
            gzos = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            LOGGER.error("Error adding file {} to Gzip", path);
        }finally {
            if(gzos != null){
                try {
                    gzos.close();
                } catch (IOException e) {
                    LOGGER.error("Couldn't close GZIPOutputStream");
                }
            }
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error("Couldn't close FileOutputStream");
                }
            }
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error("Couldn't close FileInputStream");
                }
            }
        }
    }
}
