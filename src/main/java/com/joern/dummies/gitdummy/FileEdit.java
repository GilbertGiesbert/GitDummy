package com.joern.dummies.gitdummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by jsattler on 28.04.2017.
 */
public class FileEdit {

    private static final Logger l = LoggerFactory.getLogger(FileEdit.class);

    public static boolean createFile(String filePath){

        boolean fileCreated = false;

        File f = new File(filePath);
        if(f.getParentFile().exists() || f.getParentFile().mkdirs()){

            try {
                fileCreated = f.createNewFile();
            } catch (IOException e) {
                l.error("Failed to create file, check path="+filePath, e);
            }
        }else{
            l.error("Failed to create file, check path="+filePath);
        }
        return fileCreated;
    }

    public static void editFileContent(String filePath, String fileContent, boolean appendContent){

        File f = new File(filePath);
        if(f.exists()){

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, appendContent))) {

                bw.write(fileContent);

            } catch (IOException e) {
                l.error("Failed to edit file, check path="+filePath, e);
            }
        }else{
            l.error("Failed to edit file, check path="+filePath);
        }
    }

    public static boolean deleteFile(String filePath){

        File f = new File(filePath);
        boolean success = f.delete();
        l.debug("Did "+(success? "":"not ")+"delete file with path="+filePath);
        return success;
    }
}