package com.bootdo.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.dao.SysAttachmentRoleDao;
import com.bootdo.common.dao.SysAttachmentRoleMapper;
import com.bootdo.common.domain.SysAttachmentRole;
import com.bootdo.common.service.SysAttachmentRoleService;
import com.bootdo.common.utils.StringUtils;
import com.bootdo.common.vo.SysAttachmentRoleVO;
import com.bootdo.system.dao.DeptDao;
import com.bootdo.system.dao.UserDao;
import com.bootdo.system.domain.DeptDO;
import com.bootdo.system.domain.UserDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
public class SysAttachmentRoleServiceImpl implements SysAttachmentRoleService {

    @Autowired
    private SysAttachmentRoleMapper sysAttachmentRoleMapper;
    @Resource
    private SysAttachmentRoleDao sysAttachmentRoleDao;
    @Resource
    private UserDao userDao;
    @Resource
    private DeptDao deptDao;

    @Override
    public SysAttachmentRole get(Long id) {
        return sysAttachmentRoleMapper.get(id);
    }

    @Override
    public List<SysAttachmentRoleVO> list(Map<String, Object> map) {
        List<SysAttachmentRoleVO> sysAttachmentRoleList = sysAttachmentRoleMapper.list(map);
        return sysAttachmentRoleList;
    }

    @Override
    public int count(Map<String, Object> map) {
        return sysAttachmentRoleMapper.count(map);
    }

    @Override
    public SysAttachmentRole save(Map<String, Object> paraMap) {
        String jsonString = JSON.toJSONString(paraMap);
        SysAttachmentRole sysAttachmentRole = JSON.parseObject(jsonString, SysAttachmentRole.class);
        JSONObject jsonObject = JSON.parseObject(jsonString);
        String permissionStr = (String) jsonObject.get("permission[]");
        if (!StringUtils.isEmpty(permissionStr)) {
            sysAttachmentRole.setPermission(permissionStr);
        }
        return sysAttachmentRoleDao.save(sysAttachmentRole);
    }

    @Override
    public int update(SysAttachmentRole attachmentRole) {
        return sysAttachmentRoleMapper.update(attachmentRole);
    }

    @Override
    public int remove(Long id) {
        return sysAttachmentRoleMapper.remove(id);
    }

    @Override
    public int batchRemove(Long[] ids) {
        return sysAttachmentRoleMapper.batchRemove(ids);
    }

    @Override
    public void delById(Long[] ids) {
        Iterable<Long> iterable = Arrays.asList(ids);
        Iterable<SysAttachmentRole> roleIterable = this.sysAttachmentRoleDao.findAllById(iterable);
        this.sysAttachmentRoleDao.deleteAll(roleIterable);
    }
}
