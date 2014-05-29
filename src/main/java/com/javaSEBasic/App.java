package com.javaSEBasic;

import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class App 
{
    private static final String path = "D:/projects/simpleSE/inputs.zip";

    private static final String directory = "D:/projects/simpleSE/inputs2";
    private static final String file = "D:/projects/simpleSE/logstest.zip";


    public static void main( String[] args )
    {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        final ArchiveHandler archiveHandler = new ArchiveHandler();
        final ZipHandler zipHandler = new ZipHandler();
        zipHandler.unzip(path);
//        zipHandler.zipDirectory("testDirectory.zip",directory);
//        final File myDir = new File(directory);
//        final File myFile = new File(file);
//        archiveHandler.zipFile(myDir,myFile);
//        archiveHandler.readArchive(path);




    }
}
