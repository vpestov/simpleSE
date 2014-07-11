package com.javaSEBasic;

import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App 
{
    private static final String path = "D:/testData/inputs.zip";
//    private static final String path = "D:/testData/myTestDir.zip";

    private static final String directory = "D:/projects/simpleSE/inputs2";
    private static final String file = "D:/projects/simpleSE/logstest.zip";


    public static void main( String[] args )
    {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        final ArchiveHandler archiveHandler = new ArchiveHandler();
        final ZipHandler zipHandler = new ZipHandler();
        final UnzipHandler unzipHandler = new UnzipHandler();
        final String pathToCut = path.substring(0,path.lastIndexOf("/")+1);
        unzipHandler.unzip(path);
        zipHandler.createZipArchive(UnzipHandler.archivesStructure,UnzipHandler.zipWithChildren,pathToCut);
//        zipHandler.testMethodToCallZip();
//        zipHandler.zipDirectory("testDirectory.zip",directory);
//        final File myDir = new File(directory);
//        final File myFile = new File(file);
//        archiveHandler.zipFile(myDir,myFile);
//        archiveHandler.readArchive(path);




    }
}
