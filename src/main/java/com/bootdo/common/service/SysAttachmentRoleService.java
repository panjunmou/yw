package com.bootdo.common.service;

import com.bootdo.common.domain.SysAttachmentRole;

import java.util.List;
import java.util.Map;

/**
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-09-12 14:52:29
 */
public interface SysAttachmentRoleService {

    SysAttachmentRole get(Long id);

    List<SysAttachmentRole> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    SysAttachmentRole save(SysAttachmentRole sysAttachmentRole);

    int update(SysAttachmentRole sysAttachmentRole);

    int remove(Long id);

    int batchRemove(Long[] ids);
}
