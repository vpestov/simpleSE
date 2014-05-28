package com.javaSEBasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ArchiveHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveHandler.class);

    public InputStream readArchive(String pathToFile){
        InputStream inputStream = null;
        FileOutputStream fos = null;
        Map<String,Object>entryMap = new LinkedHashMap<String, Object>();
        byte [] buffer = new byte[1024];

        try {
            final ZipFile zip = new ZipFile(pathToFile);
            final Enumeration<? extends ZipEntry> entries = zip.entries();


            while (entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                inputStream = zip.getInputStream(entry);


                entryMap.put(entry.getName(),entry);
                if(!entry.isDirectory() && entry.getName().endsWith(".zip")){

                }

                if(entry.getName().equals("inputs/data1.txt")){
                    readTxtFile(inputStream);
                }
            }
            LOGGER.info("asdasdsd");
        }catch (FileNotFoundException e){
            LOGGER.error("File Not Found. Path : {}", pathToFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("Can not close inputStream");
            }

        }
        return inputStream;
    }

    private void readTxtFile(InputStream txtFile) throws IOException {
        String str;
        StringBuffer buf = new StringBuffer();
        BufferedReader r = new BufferedReader(new InputStreamReader(txtFile));
        if(txtFile != null ){
            while ((str = r.readLine())!=null){
                buf.append(str + '\n');
            }
        }
    }


    public void zipFile(File dir, File zipFile) {
        try {
            final FileOutputStream fos = new FileOutputStream(zipFile);
            final ZipOutputStream zos = new ZipOutputStream(fos);
            final byte [] bytes = new byte[1024];
            final File [] files = dir.listFiles();

            for (File file : files){
                LOGGER.info("Adding file: {}", file.getName());
                final FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int length;
                while ((length = fis.read(bytes))>0){
                    zos.write(bytes,0,length);
                }

                zos.closeEntry();
                fis.close();
            }
            zos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private final void copyInputStream(InputStream in, OutputStream out)
            throws IOException
    {
        byte[] buffer = new byte[1024];
        int len;

        while((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);

        in.close();
        out.close();
    }

    public void unzip (String path) {
        Enumeration entries;
        ZipFile zipFile;

        try {
            zipFile = new ZipFile(path);

            entries = zipFile.entries();

            while(entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)entries.nextElement();

                if(entry.isDirectory()) {
                    // Assume directories are stored parents first then children.
//                    System.err.println("Extracting directory: " + entry.getName());
                    // This is not robust, just for demonstration purposes.
                    (new File(entry.getName())).mkdir();
                    continue;
                }

                System.err.println("Extracting file: " + entry.getName());
                copyInputStream(zipFile.getInputStream(entry),
                        new BufferedOutputStream(new FileOutputStream(entry.getName())));
            }

            zipFile.close();
        } catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return;
        }
    }



//    public void addToZip(File directory, File zipfile) throws IOException {
//        final URI base = directory.toURI();
//        Deque<File> queue = new LinkedList<File>();
//        queue.push(directory);
//        OutputStream out = new FileOutputStream(zipfile);
//        Closeable res = out;
//        try {
//            ZipOutputStream zout = new ZipOutputStream(out);
//            res = zout;
//            while (!queue.isEmpty()) {
//                directory = queue.pop();
//                for (File kid : directory.listFiles()) {
//                    String name = base.relativize(kid.toURI()).getPath();
//                    if (kid.isDirectory()) {
//                        queue.push(kid);
//                        name = name.endsWith("/") ? name : name + "/";
//                        zout.putNextEntry(new ZipEntry(name));
//                    } else {
//                        zout.putNextEntry(new ZipEntry(name));
//                        copy(kid, zout);
//                        zout.closeEntry();
//                    }
//                }
//            }
//        } finally {
//            res.close();
//        }
//    }
//
//    private static void copy(InputStream in, OutputStream out) throws IOException {
//        byte[] buffer = new byte[10000];
//        while (true) {
//            int readCount = in.read(buffer);
//            if (readCount < 0) {
//                break;
//            }
//            out.write(buffer, 0, readCount);
//        }
//    }
//
//    private static void copy(File file, OutputStream out) throws IOException {
//        InputStream in = new FileInputStream(file);
//        try {
//            copy(in, file);
//        } finally {
//            in.close();
//        }
//    }
//
//    private static void copy(InputStream in, File file) throws IOException {
//        OutputStream out = new FileOutputStream(file);
//        try {
//            copy(in, out);
//        } finally {
//            out.close();
//        }
//    }
}
