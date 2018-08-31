package com.bootdo.common.vo;

public class FileVo {

    //文件全名
    private String fullName;

    //文件名
    private String fileName;

    //后缀
    private String prefix;

    //是否文件夹
    private Boolean isDictory;

    //是否隐藏
    private Boolean isHidden;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Boolean getDictory() {
        return isDictory;
    }

    public void setDictory(Boolean dictory) {
        isDictory = dictory;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }
}
