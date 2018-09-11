package com.bootdo.common.vo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BootStrapTreeViewVo {

    private String id; // 节点的 id，它对于加载远程数据很重要。
    private String parentId;
    private String text; // 列表树节点上的文本，通常是节点右边的小图标。
    private String icon;//图标
    private Boolean lazyLoad;//图标
    private Map<String, Object> attributes; // 给一个节点添加的自定义属性。
    private List<BootStrapTreeViewVo> nodes; // 定义了一些子节点的节点数组。
    private BootStrapTreeViewVoState state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public List<BootStrapTreeViewVo> getNodes() {
        return nodes;
    }

    public void setNodes(List<BootStrapTreeViewVo> nodes) {
        this.nodes = nodes;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getLazyLoad() {
        return lazyLoad;
    }

    public void setLazyLoad(Boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    /**
     * 增加属性
     *
     * @param key
     * @param value
     */
    public void addAttribut(String key, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>();
        }
        this.attributes.put(key, value);
    }

    /**
     * 增加子节点
     *
     * @param child
     */
    public void addChild(BootStrapTreeViewVo child) {
        if (this.nodes == null) {
            this.nodes = new ArrayList<>();
        }
        this.nodes.add(child);
    }

    public BootStrapTreeViewVoState getState() {
        return state;
    }

    public void setState(BootStrapTreeViewVoState state) {
        this.state = state;
    }

    public static List<BootStrapTreeViewVo> listToTree(List<BootStrapTreeViewVo> list) {
        List<BootStrapTreeViewVo> tree = new ArrayList<BootStrapTreeViewVo>();
        for (BootStrapTreeViewVo vo1 : list) {
            int k = 0;
            for (BootStrapTreeViewVo vo2 : list) {
                if (vo1.getId().equals(vo2.getParentId())) {
                    vo1.addChild(vo2);
                    k++;
                }
            }
            if (k == 0) {
//                vo1.setIsFolder(Boolean.FALSE);
//                vo1.setState("open");
            }
            if (StringUtils.isBlank(vo1.getParentId())) {
                tree.add(vo1);
            }
        }
        return tree;
    }
}
