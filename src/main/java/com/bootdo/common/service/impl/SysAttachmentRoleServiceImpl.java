package com.bootdo.common.service.impl;

import com.bootdo.common.dao.SysAttachmentRoleDao;
import com.bootdo.common.dao.SysAttachmentRoleMapper;
import com.bootdo.common.domain.SysAttachmentRole;
import com.bootdo.common.service.SysAttachmentRoleService;
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
        List<SysAttachmentRole> sysAttachmentRoleList = sysAttachmentRoleMapper.list(map);
        List<SysAttachmentRoleVO> voList = new ArrayList<>();
        if (sysAttachmentRoleList != null && !sysAttachmentRoleList.isEmpty()) {
            for (int i = 0; i < sysAttachmentRoleList.size(); i++) {
                SysAttachmentRole sysAttachmentRole = sysAttachmentRoleList.get(i);
                SysAttachmentRoleVO sysAttachmentRoleVO = new SysAttachmentRoleVO();
                BeanUtils.copyProperties(sysAttachmentRole, sysAttachmentRoleVO);
                String type = sysAttachmentRole.getType();
                Long relationId = sysAttachmentRole.getRelationId();
                if (type.equals("dept")) {
                    DeptDO deptDO = deptDao.get(relationId);
                    sysAttachmentRoleVO.setRelationName(deptDO.getName());
                } else {
                    UserDO userDO = userDao.get(relationId);
                    sysAttachmentRoleVO.setRelationName(userDO.getName());
                }
                voList.add(sysAttachmentRoleVO);
            }
        }
        return voList;
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
