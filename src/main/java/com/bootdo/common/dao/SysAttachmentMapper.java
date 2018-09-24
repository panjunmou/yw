package com.bootdo.common.dao;

import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.vo.SysAttachmentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-09-12 14:52:29
 */
@Mapper
public interface SysAttachmentMapper {

    SysAttachment get(Long id);

    List<SysAttachment> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(SysAttachment attachment);

    int update(SysAttachment attachment);

    int remove(Long id);

    int batchRemove(Long[] ids);

    List<SysAttachmentVO> getByPersonParentId(Map<String, Object> map);
}
