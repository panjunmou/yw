package com.bootdo.testDemo;

import com.bootdo.common.utils.Office2Swf;
import com.bootdo.common.utils.WaterMarkUtil;
import org.junit.Test;


public class TestwaterMark {

    private static int interval = -5;

    @Test
    public void testMark() throws Exception {
        WaterMarkUtil.setPdfWatermark("D:/temp/test.pdf", "D:/temp/bbb.pdf", "潘骏谋", true);
    }

    @Test
    public void testPdf2Swf() {
        String aa = "D:/temp/bbb.pdf";
        String outputSwfPath = "D:/temp/aaa.swf";
        Office2Swf.pdf2Swf(aa, outputSwfPath);
    }
}