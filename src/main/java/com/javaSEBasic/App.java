package com.javaSEBasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class App
{
    private final static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        final ZipHandler zipHandler = new ZipHandler();
        final UnzipHandler unzipHandler = new UnzipHandler();
        final FileHandler fileHandler = new FileHandler();

        final String rootPath = args[0];
//        final String rootPath = "D:/projects/simpleSE/inputs.zip";
        if(fileHandler.isZIpFileExists(rootPath)){
            LOGGER.info("Starting application");
            LOGGER.info("Please wait application is in progress");
            unzipHandler.unzip(rootPath,rootPath);
            fileHandler.addFileToArchive(rootPath,FileHandler.uniquePhones,"phones.txt");
            fileHandler.addFileToArchive(rootPath,FileHandler.uniqueEmails,"emails.txt");
            zipHandler.createZipArchive(UnzipHandler.archivesStructure,UnzipHandler.zipWithChildren,rootPath);
            LOGGER.info("Work is done");
        }else {
            LOGGER.error("Incorrect path to file or file extension.");
        }
    }
}
