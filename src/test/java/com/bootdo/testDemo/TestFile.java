package com.bootdo.testDemo;

import org.junit.Test;

import java.io.File;

public class TestFile {

    @Test
    public void testFileList() {
        File dirFile = new File("G:/");
        File[] files = dirFile.listFiles();
        for (File file : files) {
            String fullName = file.getName();
            String fileName = fullName;
            String prefix = "";
            if (fullName.indexOf(".") > 0) {
                prefix = fullName.substring(fullName.lastIndexOf(".") + 1);
                fileName = fullName.substring(0,fullName.lastIndexOf(".") );
            }
            System.out.println("fullName = " + fullName);
            System.out.println("fileName = " + fileName);
            System.out.println("prefix = " + prefix);
        }
    }
}
