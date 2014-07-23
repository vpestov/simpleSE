package com.javaSEBasic;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

public class FileHandler {

    public InputStream readFile(InputStream in){
        String line;
        String input = "";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((line = bufferedReader.readLine())!= null){
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


//    public InputStream readFile(InputStream in, String asd){
////        RandomAccessFile randomAccessFile = new RandomAccessFile() ;
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        try {
//            while(inChannel.read(buffer) > 0)
//            {
//                buffer.flip();
//                for (int i = 0; i < buffer.limit(); i++)
//                {
//                    System.out.print((char) buffer.get());
//                }
//                buffer.clear(); // do something with the data and clear/compact it.
//            }
//            inChannel.close();
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        return null;
//    }

}
