package com.javaSEBasic;

//import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

public class App
{
    private static final String rootPath = "D:/testData/inputs.zip";
//    private static final String directory = "D:/projects/simpleSE/inputs2";
//    private static final String file = "D:/projects/simpleSE/logstest.zip";


    public static void main( String[] args )
    {
//        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        final ZipHandler zipHandler = new ZipHandler();
        final UnzipHandler unzipHandler = new UnzipHandler();
        final FileHandler fileHandler = new FileHandler();



//        try {
//            fileHandler.copyFile();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

//        try {
//            fileHandler.readFile1111();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }


        unzipHandler.unzip(rootPath,rootPath);
        fileHandler.addFileToArchive(rootPath,FileHandler.uniquePhones,"phones.txt");
        fileHandler.addFileToArchive(rootPath,FileHandler.uniqueEmails,"emails.txt");
        zipHandler.createZipArchive(UnzipHandler.archivesStructure,UnzipHandler.zipWithChildren,rootPath);


//        zipHandler.testMethodToCallZip();
//        zipHandler.zipDirectory("testDirectory.zip",directory);
//        final File myDir = new File(directory);
//        final File myFile = new File(file);
//        archiveHandler.zipFile(myDir,myFile);




    }
}
