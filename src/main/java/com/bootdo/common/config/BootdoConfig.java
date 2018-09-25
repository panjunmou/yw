package com.bootdo.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bootdo")
public class BootdoConfig {
    //上传路径
    private String uploadPath;
    private String attachBasePath;
    private String attachTempPath;
    private String sysManager;

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getAttachBasePath() {
        return attachBasePath;
    }

    public void setAttachBasePath(String attachBasePath) {
        this.attachBasePath = attachBasePath;
    }

    public String getAttachTempPath() {
        return attachTempPath;
    }

    public void setAttachTempPath(String attachTempPath) {
        this.attachTempPath = attachTempPath;
    }

    public String getSysManager() {
        return sysManager;
    }

    public void setSysManager(String sysManager) {
        this.sysManager = sysManager;
    }
}
