package com.bootdo.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-09-12 14:52:29
 */
@Entity
@Table(name = "sys_attachment_role")
public class SysAttachmentRole extends AuditableBaseEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "attactment_id")
    private Long attactmentId;

    @Column(name = "relation_id")
    private Long relationId;

    @Column(name = "type")
    private String type;

    @Column(name = "can_read")
    private Integer canRead;

    @Column(name = "can_upload")
    private Integer canUpload;

    @Column(name = "can_del")
    private Integer canDel;

    @Column(name = "can_download")
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
}
