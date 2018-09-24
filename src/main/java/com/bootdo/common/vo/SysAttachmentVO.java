package com.bootdo.common.vo;


import com.bootdo.common.domain.SysAttachment;

/**
 * Created by wangfei on 2016/4/25.
 * 附件VO
 */
public class SysAttachmentVO extends SysAttachment {

    private String permission;

    private Integer level;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
