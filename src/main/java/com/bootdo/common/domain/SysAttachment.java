package com.bootdo.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by wangfei on 2016/4/25.
 * 附件
 */
@Entity
@Table(name = "sys_attachment")
public class SysAttachment extends AuditableBaseEntity {
    /**
     * 序列
     */
    private static final long serialVersionUID = 1L;

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
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 是否文件夹
     */
    @Column(name = "is_directory")
    private Integer isDirectory;

    @Column(name = "path")
    private String path;

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

    public Integer getIsDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(Integer isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
