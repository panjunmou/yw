package com.bootdo.common.service;

import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.vo.AttachmentVO;
import com.bootdo.common.vo.FileVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AttachmentService {
    List<FileVo> listFlie(Map<String, Object> queryParamMap) throws Exception;

    AttachmentVO addAttachment(String newPath, String attachPath, String oriFileName, String extName, Long size, String md5);

    Long add(AttachmentVO vo);

    void deleteById(Long id);

    AttachmentVO getById(Long id);

    List<AttachmentVO> findByIds(String ids) throws Exception;

    List<SysAttachment> getAttachmentByMd5(String wholeMd5);

    List<SysAttachment> findByFileMd5(String wholeMd5);

    void initFile(Map<String, Object> queryParamMap) throws Exception;
}
