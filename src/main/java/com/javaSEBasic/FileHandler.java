package com.javaSEBasic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);
    public static Set uniqueEmails = new TreeSet();
    public static Set uniquePhones = new TreeSet();

    public void copyFile(final InputStream in,final String toPath){
        ReadableByteChannel source = null;
        FileChannel destination = null;
        final int DEFAULT_BUFFER_SIZE = 1024 * 8;
        try {
            source = Channels.newChannel(in);
            destination = new FileOutputStream(new File(toPath)).getChannel();

            ByteBuffer buf = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
            while ((source.read(buf))!= -1){
                buf.flip();
                    final String filePart = replaceCodes(String.valueOf(Charset.defaultCharset().decode(buf)));
                    findEmails(filePart);
                    findPhones(filePart);
                destination.write(ByteBuffer.wrap(filePart.getBytes()));
                buf.clear();
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find file : {}",toPath);
        } catch (IOException e) {
            LOGGER.error("Could not read content to ByteBuffer");
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error("Could not close InputStream : {}", in);
            }
            if(source != null){
                try {
                    source.close();
                } catch (IOException e) {
                    LOGGER.error("Could not close ReadableByteChannel : {}", source);
                }
            }
            if (destination!=null){
                try {
                    destination.close();
                } catch (IOException e) {
                    LOGGER.error("Could not close FileChannel : {}", destination);
                }
            }
        }
    }

    private String replaceCodes (String filePart){
        filePart = filePart.replace("(101)","(201)");
        filePart = filePart.replace("(202)","(802)");
        filePart = filePart.replace("(301)","(321)");
        return filePart;
    }

    private void findEmails(String line){
        final String regex = "([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+org)";
        final Pattern p = Pattern.compile(regex);
        final Matcher m = p.matcher(line);
        while(m.find()) {
            uniqueEmails.add(m.group(1));
        }
    }

    private void findPhones(String line){
        final String regex = "(\\+\\d*)(\\s*\\(\\d*\\))(\\s?-?\\d*\\s?-?\\d*\\s?-?\\d*)";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(line);
        while (matcher.find()){
            uniquePhones.add(matcher.group(1).replace(" ", "") + " " + matcher.group(2).replace(" ", "") + " " + matcher.group(3).replaceAll("[^\\d]", ""));
        }
    }

    public void createFile(final String path, final Set<String> fileContent){
        final Path pathToFile = Paths.get(path);
        try {
            Files.write(pathToFile,fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Error creating file : {}", path);
        }
    }

    public void addFileToArchive(final String rootPath, final Set<String> fileContent, final String fileName){
        final StringBuilder path = new StringBuilder().append(rootPath.substring(0,rootPath.length()-4)).append("/").append(fileName);
        createFile(path.toString(),fileContent);
        UnzipHandler.zipWithChildren.get(rootPath).add(path.toString());
    }

    public boolean isZIpFileExists(String path){
        final Path pathToFile = Paths.get(path);
        if(Files.exists(pathToFile)){
            try {
                return "application/x-zip-compressed".equals(Files.probeContentType(pathToFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
