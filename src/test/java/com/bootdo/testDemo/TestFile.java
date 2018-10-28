package com.bootdo.testDemo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestFile {

    @Test
    public void testFileList() {
        File dirFile = new File("G://aaaa", "asdf.xml");
    }

    @Test
    public void testasdfasdf() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        for (int i = 0; i < 3; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String path = year + "/" + month + "/" + day;
            System.out.println("path = " + path);
        }
    }

    @Test
    public void testDelDir() {
        String path = "D:/temp/2018/10/25";
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
