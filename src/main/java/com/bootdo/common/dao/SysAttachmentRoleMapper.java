package com.bootdo.common.dao;

import java.util.List;
import java.util.Map;

import com.bootdo.common.domain.SysAttachmentRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-09-12 14:52:29
 */
@Mapper
public interface SysAttachmentRoleMapper {

	SysAttachmentRole get(Long id);
	
	List<SysAttachmentRole> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(SysAttachmentRole attachmentRole);
	
	int update(SysAttachmentRole attachmentRole);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);
}
