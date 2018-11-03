package com.bootdo.common.service;

import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.vo.SysAttachmentVO;
import com.bootdo.common.vo.BootStrapTreeViewVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SysAttachmentService {
    List<SysAttachmentVO> listFlie(Map<String, Object> queryParamMap) throws Exception;

    List<BootStrapTreeViewVo> listTree(Map<String, Object> queryParamMap) throws Exception;

    boolean isSysManager();

    SysAttachmentVO addAttachment(String newPath, SysAttachmentVO parentAtt, String oriFileName, String extName, Long size, String md5) throws IOException;

    Long add(SysAttachmentVO vo);

    void deleteById(Long id);

    SysAttachmentVO getById(Long id);

    List<SysAttachmentVO> findByIds(String ids) throws Exception;

    List<SysAttachment> getAttachmentByMd5(String wholeMd5);

    List<SysAttachment> findByFileMd5(String wholeMd5);

    void initFile(Map<String, Object> queryParamMap) throws Exception;

    List<SysAttachment> getNavList(Map<String, Object> queryParamMap);

    SysAttachment getByPersistedFileName(String persistedFileName);

    void mkDir(Map<String, Object> paraMap);

    void downFile(HttpServletRequest request, HttpServletResponse response) throws Exception;

    void del(Map<String, Object> paraMap) throws IOException;

    String convertFile(Map<String, Object> queryParamMap) throws IOException;

    void move(Map<String, Object> paraMap) throws Exception;

    void changeName(Map<String, Object> paraMap) throws Exception;
}
