package com.bootdo.common.service;

import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.vo.AttachmentVO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AttachmentService {
    List<SysAttachment> listFlie(Map<String, Object> queryParamMap) throws Exception;

    AttachmentVO addAttachment(String newPath, AttachmentVO parentAtt, String oriFileName, String extName, Long size, String md5) throws IOException;

    Long add(AttachmentVO vo);

    void deleteById(Long id);

    AttachmentVO getById(Long id);

    List<AttachmentVO> findByIds(String ids) throws Exception;

    List<SysAttachment> getAttachmentByMd5(String wholeMd5);

    List<SysAttachment> findByFileMd5(String wholeMd5);

    void initFile(Map<String, Object> queryParamMap) throws Exception;
}
