package com.bootdo.common.enums;

public enum FileRoleEnums {
    /**
     * 查看
     */
    VIEW("view", "查看"),

    /**
     * 下载
     */
    DOWN("down", "下载"),

    READONLINE("readOnLine", "在线预览"),

    /**
     * 移动
     */
    MOVE("move", "移动"),

    /**
     * 复制
     */
    COPY("copy", "复制"),

    /**
     * 删除
     */
    DELETE("delete", "删除"),

    /**
     * 上传
     */
    CHANGENAME("changeName", "重命名"),

    /**
     * 上传
     */
    UPLOAD("upload", "上传"),

    /**
     * 新建文件夹
     */
    MKDIR("mkdir", "新建文件夹");

    private String value;

    private String lable;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    private FileRoleEnums(String value, String lable) {
        this.value = value;
        this.lable = lable;
    }

    /**
     * 获取值
     *
     * @param lable
     * @return
     */
    public static String getValue(String lable) {
        for (FileRoleEnums examType : FileRoleEnums.values()) {
            String examTypeLable = examType.getLable();
            if (lable.equals(examTypeLable)) {
                return examType.getValue();
            }
        }
        return "";
    }

    /**
     * 获取lable
     *
     * @param value
     * @return
     */
    public static String getLable(String value) {
        for (FileRoleEnums examType : FileRoleEnums.values()) {
            String examTypeValue = examType.getValue();
            if (value.equals(examTypeValue)) {
                return examType.getLable();
            }
        }
        return "";
    }

    public static String getValueStr() {
        String value = "";
        for (FileRoleEnums examType : FileRoleEnums.values()) {
            value += examType.getValue() + ",";
        }
        return value;
    }
}
