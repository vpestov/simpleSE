package com.javaSEBasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);
    public static Set uniqueEmails = new TreeSet();
    public static Set uniqueTelephones = new TreeSet();

    public InputStream readFile(InputStream in){

        String line;
        String input = "";
//        StringBuilder input = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((line = bufferedReader.readLine())!= null){
//                input.append(line).append("/n"); //+= line + '\n';
                input += line + '\n';
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        input = input.replace("(101)","(201)");
        input = input.replace("(202)","(802)");
        input = input.replace("(301)","(321)");



        return new ByteArrayInputStream(input.getBytes());
    }


    public InputStream readFile(){
        BufferedReader bufferedReader = null;
        try {
            String path = "D:/testData/new data from ISP provide 2012.05.11 CODE 2342423535345 Ext. +G245 -N2435 ++d7867";
//            String path = "D:/testData/data1.txt";
//            String input = "";
            RandomAccessFile aFile = new RandomAccessFile
                    (path, "r");
            FileChannel inChannel = aFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
//            inChannel.read(buffer);
//            buffer.rewind();
//            for (int i = 0; i < 2048; i++){
//                System.out.println((char)buffer.get());
//            }
            while(inChannel.read(buffer) > 0)
            {
//                buffer.rewind();
                buffer.flip();
//                input += new String(buffer.array(),Charset.defaultCharset());
                for (int i = 0; i < buffer.limit(); i++)
                {
                    System.out.println((char)buffer.get());
//                    input += (char) buffer.get()+'\n';
                }
//                new String(buffer.array(),Charset.defaultCharset)
                buffer.clear(); // do something with the data and clear/compact it.
            }
            inChannel.close();
            aFile.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public void readTest(){
        try {
//            String path = "D:/testData/new data from ISP provide 2012.05.11 CODE 2342423535345 Ext. +G245 -N2435 ++d7867";
            String path = "D:/testData/data1.txt";
            String input = "";
            InputStream in = new FileInputStream(path);
            final FileChannel inChannel = (FileChannel) Channels.newChannel(in);
//            RandomAccessFile aFile = new RandomAccessFile
//                    (path, "r");
//            FileChannel inChannel = aFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while(inChannel.read(buffer) > 0)
            {

//                buffer.rewind();
                buffer.flip();
                for (int i = 0; i < buffer.limit(); i++)
                {
//                    input += Charset.defaultCharset().decode(buffer);
                    System.out.print(Charset.defaultCharset().decode(buffer));
//                    LOGGER.info(String.valueOf((char) buffer.get()));
//                    System.out.print((char) buffer.get());
                }
                System.out.println("");
                buffer.clear(); // do something with the data and clear/compact it.
            }
            inChannel.close();
//            aFile.close();
            in.close();
        }catch (Exception r) {
            r.printStackTrace();
        }

    }

    public void readHugeFile() throws IOException {

        String path = "D:/testData/new data from ISP provide 2012.05.11 CODE 2342423535345 Ext. +G245 -N2435 ++d7867";
        FileInputStream in = new FileInputStream(path);
        FileChannel fileChannel = in.getChannel();

        MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,0,fileChannel.size());
        int i = 0;
        while (i < fileChannel.size())
            System.out.print((char) buffer.get(i++));
//        System.out.println((char) buffer.get(i++));
//        buffer.load();
//        for (int i = 0; i < buffer.limit(); i++)
//        {
//            System.out.print(Charset.defaultCharset().decode(buffer));
//        }
        buffer.clear();
        fileChannel.close();
        in.close();

        System.out.println(buffer.isLoaded());
        System.out.println(buffer.capacity());

    }


    public void readFile1111() throws FileNotFoundException {
        String path = "D:/testData/new data from ISP provide 2012.05.11 CODE 2342423535345 Ext. +G245 -N2435 ++d7867";
//        String path = "D:/testData/data1.txt";
        File file = new File(path);
        InputStream in = new FileInputStream(path);
        ArrayList<String> ss = new ArrayList<String>();

        String line;
        String input = "";
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        try {
            Scanner scanner = new Scanner(in);
            while (scanner.hasNextLine()){
                System.out.println(scanner.nextLine());
                input += scanner.nextLine()+ '\n';
            }
//            while ((line = bufferedReader.readLine())!= null){
//                ss.add(line+'\n');
//                System.out.println(line);
//                input.append(line).append("/n"); //+= line + '\n';
//                input += line + '\n';
//            }

            System.out.println("ccc");
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
//        return new ByteArrayInputStream(input.getBytes());
    }


    public void copyFile (){
        FileChannel source = null;
        FileChannel destination = null;
        String fromPath = "D:/testData/new data from ISP provide 2012.05.11 CODE 2342423535345 Ext. +G245 -N2435 ++d7867";
//        String fromPath = "D:/testData/data1.txt";
        String toPath = "D:/testData/testTest.txt";
        int DEFAULT_BUFFER_SIZE = 1024 * 8;
        StringBuilder builder = new StringBuilder();

        try {
            source = new FileInputStream(new File(fromPath)).getChannel();
            destination = new FileOutputStream(new File(toPath)).getChannel();


            ByteBuffer buf = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
            while ((source.read(buf))!= -1){
                buf.flip();
//                String dd = String.valueOf(Charset.defaultCharset().decode(buf));
                String dd = replaceCodes(String.valueOf(Charset.defaultCharset().decode(buf)));
                findEmails(dd);
//                builder.append(dd);
                destination.write(ByteBuffer.wrap(dd.getBytes()));
//                System.out.println(Charset.defaultCharset().decode(buf));
//                buf.flip();
//                destination.write(buf);
                buf.clear();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        finally {
            if(source != null){
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if (destination!=null){
                try {
                    destination.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

    }

    private String replaceCodes (String line){
        line = line.replace("(101)","(201)");
        line = line.replace("(202)","(802)");
        line = line.replace("(301)","(321)");
        return line;
    }

    private void findEmails(String line){
        final String RE_MAIL = "([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+org)";
        Pattern p = Pattern.compile(RE_MAIL);
        Matcher m = p.matcher(line);
        while(m.find()) {
            uniqueEmails.add(m.group(1));
        }
    }

    private void findTelephones(){

    }

}
