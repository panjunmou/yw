package com.bootdo.common.vo;

/**
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-09-12 14:52:29
 */
public class SysAttachmentRoleVO {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long attactmentId;

    private Long relationId;
    private String relationName;

    private String type;

    private Integer view;

    private Integer down;

    private Integer move;

    private Integer copy;
    private Integer del;
    private Integer changeName;
    private Integer upload;
    private Integer mkDir;

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

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public Integer getDown() {
        return down;
    }

    public void setDown(Integer down) {
        this.down = down;
    }

    public Integer getMove() {
        return move;
    }

    public void setMove(Integer move) {
        this.move = move;
    }

    public Integer getCopy() {
        return copy;
    }

    public void setCopy(Integer copy) {
        this.copy = copy;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    public Integer getChangeName() {
        return changeName;
    }

    public void setChangeName(Integer changeName) {
        this.changeName = changeName;
    }

    public Integer getUpload() {
        return upload;
    }

    public void setUpload(Integer upload) {
        this.upload = upload;
    }

    public Integer getMkDir() {
        return mkDir;
    }

    public void setMkDir(Integer mkDir) {
        this.mkDir = mkDir;
    }
}
