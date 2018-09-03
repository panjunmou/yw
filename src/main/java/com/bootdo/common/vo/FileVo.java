package com.bootdo.common.vo;

public class FileVo {

    private String id;

    //文件全名
    private String fullName;

    //文件名
    private String fileName;

    //后缀
    private String prefix;

    //路径
    private String path;

    //路径
    private String absolutePath;

    //路径
    private String canonicalPath;

    //是否文件夹
    private Boolean isDictory;

    //是否隐藏
    private Boolean isHidden;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getCanonicalPath() {
        return canonicalPath;
    }

    public void setCanonicalPath(String canonicalPath) {
        this.canonicalPath = canonicalPath;
    }
}
