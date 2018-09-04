package com.bootdo.common.domain;

import ch.qos.logback.core.util.ContextUtil;
import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.system.domain.UserDO;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangfei on 2016/4/25.
 * 附件
 */
@Entity
@Table(name = "sys_attachment")
public class SysAttachment implements Serializable {
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
     * 附件全名
     */
    @Column(name = "original_full_name")
    private String originalFullName;

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
     * 附件类型
     */
    @Column(name = "file_ext")
    private String fileExt;

    /**
     * 大小
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * Md5
     */
    @Column(name = "file_md5")
    private String fileMd5;

    /**
     * 父节点
     */
    @Column(name = "paretn_id")
    private Long parentId;

    /**
     * 是否文件夹
     */
    @Column(name = "is_directory")
    private Integer isDirectory;

    /**
     * 拥有者id
     */
    @Column(name = "create_id")
    private String createId;
    /**
     * 附件拥有人
     */
    @Column(name = "create_name")
    private String createName;

    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createDate;

    /**
     * 跟新人id
     */
    @Column(name = "update_id")
    private String updateId;
    /**
     * 附件跟新人
     */
    @Column(name = "update_name")
    private String updateName;

    /**
     * 跟新时间
     */
    @Column(name = "update_date")
    private Date updateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFullName() {
        return originalFullName;
    }

    public void setOriginalFullName(String originalFullName) {
        this.originalFullName = originalFullName;
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

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Integer isDirectory) {
        this.isDirectory = isDirectory;
    }

    /**
     * prePersist
     */
    @PrePersist
    public void prePersist() {
        UserDO user = ShiroUtils.getUser();
        this.createDate = new Date();
        if (user != null) {
            this.createId = user.getUserId().toString();
            this.createName = user.getUsername();
            if (updateId == null) {
                this.updateId = user.getUserId().toString();
                this.updateName = user.getUsername();
                this.updateDate = new Date();
            }
        }
    }

    /**
     * preUpdate
     */
    @PreUpdate
    public void preUpdate() {
        UserDO user = ShiroUtils.getUser();
        if (user != null) {
            this.updateId = user.getUserId().toString();
            this.updateName = user.getUsername();
        }
        this.updateDate = new Date();
    }
}
