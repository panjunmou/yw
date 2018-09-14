package com.bootdo.common.vo;

/**
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-09-12 14:52:29
 */
public class SysAttachmentRoleVO {
    private static final long serialVersionUID = 1L;

    private Long attactmentId;

    private Long relationId;
    private String relationName;

    private String type;

    private Integer canRead;

    private Integer canUpload;

    private Integer canDel;

    private Integer canDownload;

    public Long getAttactmentId() {
        return attactmentId;
    }

    public void setAttactmentId(Long attactmentId) {
        this.attactmentId = attactmentId;
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCanRead() {
        return canRead;
    }

    public void setCanRead(Integer canRead) {
        this.canRead = canRead;
    }

    public Integer getCanUpload() {
        return canUpload;
    }

    public void setCanUpload(Integer canUpload) {
        this.canUpload = canUpload;
    }

    public Integer getCanDel() {
        return canDel;
    }

    public void setCanDel(Integer canDel) {
        this.canDel = canDel;
    }

    public Integer getCanDownload() {
        return canDownload;
    }

    public void setCanDownload(Integer canDownload) {
        this.canDownload = canDownload;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
}
