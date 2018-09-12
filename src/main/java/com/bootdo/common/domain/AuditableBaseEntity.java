package com.bootdo.common.domain;

import com.bootdo.common.utils.ShiroUtils;
import com.bootdo.system.domain.UserDO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@MappedSuperclass
public abstract class AuditableBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @GeneratedValue
    @Id
    private Long id;

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

