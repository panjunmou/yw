package com.bootdo.common.service;

import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.vo.SysAttachmentVO;
import com.bootdo.common.vo.BootStrapTreeViewVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SysAttachmentService {
    List<SysAttachmentVO> listFlie(Map<String, Object> queryParamMap) throws Exception;

    SysAttachmentVO addAttachment(String newPath, SysAttachmentVO parentAtt, String oriFileName, String extName, Long size, String md5) throws IOException;

    Long add(SysAttachmentVO vo);

    void deleteById(Long id);

    SysAttachmentVO getById(Long id);

    List<SysAttachmentVO> findByIds(String ids) throws Exception;

    List<SysAttachment> getAttachmentByMd5(String wholeMd5);

    List<SysAttachment> findByFileMd5(String wholeMd5);

    void initFile(Map<String, Object> queryParamMap) throws Exception;

    List<BootStrapTreeViewVo> getAttachmentTree(Map<String, Object> queryParamMap);

    List<BootStrapTreeViewVo> getByPersionParentId(Map<String, Object> queryParamMap);

    List<BootStrapTreeViewVo> getByParentId(Map<String, Object> queryParamMap);

    List<SysAttachment> getNavList(Map<String, Object> queryParamMap);

    SysAttachment getByPersistedFileName(String persistedFileName);

    List<BootStrapTreeViewVo> getPersonTree(Map<String, Object> queryParamMap);

    void mkDir(Map<String, Object> paraMap);
}
