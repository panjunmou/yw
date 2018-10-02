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

    @Column(name = "permission")
    private String permission;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

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
}
