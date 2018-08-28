package com.bootdo.common.service;

import com.bootdo.common.domain.Attachment;
import com.bootdo.common.vo.AttachmentVO;

import java.util.List;

public interface AttachmentService {
    AttachmentVO addAttachment(String newPath, String attachPath, String oriFileName, String extName, Long size, String md5);

    Long add(AttachmentVO vo);

    void deleteById(Long id);

    AttachmentVO getById(Long id);

    List<AttachmentVO> findByIds(String ids) throws Exception;

    List<Attachment> getAttachmentByMd5(String wholeMd5);

    List<Attachment> findByFileMd5(String wholeMd5);
}
