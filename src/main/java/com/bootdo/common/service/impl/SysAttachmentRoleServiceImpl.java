package com.bootdo.common.service.impl;

import com.bootdo.common.dao.SysAttachmentRoleDao;
import com.bootdo.common.dao.SysAttachmentRoleMapper;
import com.bootdo.common.domain.SysAttachmentRole;
import com.bootdo.common.service.SysAttachmentRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class SysAttachmentRoleServiceImpl implements SysAttachmentRoleService {

    @Autowired
    private SysAttachmentRoleMapper sysAttachmentRoleMapper;
    @Resource
    private SysAttachmentRoleDao sysAttachmentRoleDao;

    @Override
    public SysAttachmentRole get(Long id) {
        return sysAttachmentRoleMapper.get(id);
    }

    @Override
    public List<SysAttachmentRole> list(Map<String, Object> map) {
        return sysAttachmentRoleMapper.list(map);
    }

    @Override
    public int count(Map<String, Object> map) {
        return sysAttachmentRoleMapper.count(map);
    }

    @Override
    public SysAttachmentRole save(SysAttachmentRole attachmentRole) {
        SysAttachmentRole sysAttachmentRole = sysAttachmentRoleDao.save(attachmentRole);
        return sysAttachmentRole;
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

}
