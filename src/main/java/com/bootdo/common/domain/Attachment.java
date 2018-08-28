package com.bootdo.common.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangfei on 2016/4/25.
 * 附件
 */
@Entity
@Table(name = "sys_attachment")
public class Attachment implements Serializable {
    /**
     * 序列
     */
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @GeneratedValue
    @Id
    private Long id;

    /**
     * 附件名称
     */
    @Column(name = "original_file_name")
    private String originalFileName;
    /**
     * 附件保存地址
     */
    @Column(name = "persisted_file_name")
    private String persistedFileName;
    /**
     * 附件拥有人
     */
    @Column(name = "owner")
    private String owner;
    /**
     * 大小
     */
    @Column(name = "file_size")
    private Long fileSize;
    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createDate;
    /**
     * 附件类型
     */
    @Column(name = "file_ext")
    private String fileExt;
    /**
     * 拥有者id
     */
    @Column(name = "owner_id")
    private String ownerId;
    /**
     * 删除标志
     */
    @Column(name = "del_flag")
    private Integer delFlag;
    /**
     * 删除人
     */
    @Column(name = "del_user")
    private String delUser;

    @Column(name = "file_md5")
    private String fileMd5;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getPersistedFileName() {
        return persistedFileName;
    }

    public void setPersistedFileName(String persistedFileName) {
        this.persistedFileName = persistedFileName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelUser() {
        return delUser;
    }

    public void setDelUser(String delUser) {
        this.delUser = delUser;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }


}
