package com.bootdo.common.task;

import com.bootdo.common.config.BootdoConfig;
import org.apache.commons.io.FileUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@Component
public class DelTempFileJon implements Job {

    @Resource
    private BootdoConfig bootdoConfig;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("DelTempFileJon.execute");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        for (int i = 0; i < 3; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String path = bootdoConfig.getAttachTempPath() + year + "/" + month + "/" + day;
            System.out.println("path = " + path);
            try {
                FileUtils.deleteDirectory(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}