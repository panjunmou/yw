package com.bootdo.testDemo;

import org.junit.Test;

import java.io.File;

public class TestFile {

    @Test
    public void testFileList() {
        File dirFile = new File("G://aaaa","asdf.xml");
    }

    @Test
    public void testasdfasdf() {
        String str = "D:/temp//2018/10/23/1540279908317.swf";
        str = str.replace("D:/temp/", "");
        System.out.println("str = " + str);
    }
}
