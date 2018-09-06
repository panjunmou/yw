package com.bootdo.common.service.impl;

import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.dao.SysAttachmentDao;
import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.service.AttachmentService;
import com.bootdo.common.utils.BeanMapper;
import com.bootdo.common.utils.StringUtil;
import com.bootdo.common.vo.AttachmentVO;
import com.bootdo.common.vo.FileVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2016/4/25.
 * 附件服务类
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

    /**
     * 附件dao
     */
    @Resource
    private SysAttachmentDao attachmentDao;
    @Resource
    private BootdoConfig bootdoConfig;
    @Resource
    private SysAttachmentDao sysAttachmentDao;

    /**
     * 获取文件列表
     *
     * @param queryParamMap
     * @return
     */
    @Override
    public List<SysAttachment> listFlie(Map<String, Object> queryParamMap) throws Exception {
        String parentId = queryParamMap.get("parentId") == null ? "0" : (String) queryParamMap.get("parentId");
        List<SysAttachment> sysAttachmentList = attachmentDao.findByParentId(Long.parseLong(parentId));
        return sysAttachmentList;
    }

    @Override
    public AttachmentVO addAttachment(String newPath, String attachPath, String oriFileName, String extName, Long size, String md5) {
        File f1 = new File(newPath);
        File f2 = new File(attachPath);
        AttachmentVO vo = new AttachmentVO();
        vo.setOriginalFileName(oriFileName);
        vo.setPersistedFileName(
                f1.getAbsolutePath().replace(f2.getAbsolutePath() + File.separator, "").replaceAll("\\\\", "/"));
        vo.setFileSize(size);
        vo.setCreateDate(new Date());
        vo.setFileExt(extName);
        vo.setFileMd5(md5);
        SysAttachment attachment = new SysAttachment();
        BeanUtils.copyProperties(vo, attachment);
        attachment = attachmentDao.save(attachment);
        AttachmentVO rvo = new AttachmentVO();
        BeanUtils.copyProperties(attachment, rvo);
        return rvo;
    }

    /**
     * 添加附件
     */
    @Override
    public Long add(AttachmentVO vo) {
        SysAttachment attachment = new SysAttachment();
        BeanUtils.copyProperties(vo, attachment);
        SysAttachment att = attachmentDao.save(attachment);
        return att.getId();
    }

    /**
     * 根据id删除
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        attachmentDao.deleteById(id);
    }

    /**
     * 根据id查询附件
     *
     * @param id
     */
    @Override
    public AttachmentVO getById(Long id) {
        AttachmentVO vo = new AttachmentVO();
        SysAttachment attachment = attachmentDao.findById(id).get();
        BeanUtils.copyProperties(attachment, vo);
        return vo;
    }

    /**
     * 根据ids查询附件列表
     *
     * @param ids
     */
    @Override
    public List<AttachmentVO> findByIds(String ids) throws Exception {
        List<AttachmentVO> voList = new ArrayList<AttachmentVO>();
        String[] idsArr = ids.split("\\,");
        for (int i = 0; i < idsArr.length; i++) {
            if (StringUtil.isNotEmpty(idsArr[i])) {
                SysAttachment po = attachmentDao.findById(Long.valueOf(idsArr[i])).get();
                AttachmentVO vo = new AttachmentVO();
                BeanMapper.copy(po, vo);
                //if (vo != null) {
                voList.add(vo);
                //}
            }
        }
        return voList;
    }

    /**
     * 根据整个文件的md5值查询附件表中是否存在该附件
     */
    @Override
    public List<SysAttachment> getAttachmentByMd5(String wholeMd5) {
        return attachmentDao.findByPersistedFileNameLike(wholeMd5);
    }

    @Override
    public List<SysAttachment> findByFileMd5(String wholeMd5) {
        if (!StringUtil.isEmpty(wholeMd5)) {
            return attachmentDao.findByFileMd5(wholeMd5);
        } else
            return new ArrayList();
    }

    /**
     * 初始化数据
     *
     * @param queryParamMap
     */
    @Override
    public void initFile(Map<String, Object> queryParamMap) throws Exception {
        String attachBasePath = bootdoConfig.getAttachBasePath();
        File fileDir = new File(attachBasePath);
        regFile(fileDir, null);
    }

    /**
     * 递归生成数据
     *
     * @param fileDir
     * @param parent
     * @throws Exception
     */
    private void regFile(File fileDir, SysAttachment parent) throws Exception {
        if (fileDir.exists()) {
            File[] listFiles = fileDir.listFiles();
            for (File file : listFiles) {
                boolean directory = file.isDirectory();
                boolean hidden = file.isHidden();
                if (!hidden) {
                    String fullName = file.getName();
                    String fileName = fullName;
                    String prefix = "";
                    if (fullName.indexOf(".") > 0) {
                        prefix = fullName.substring(fullName.lastIndexOf(".") + 1);
                        fileName = fullName.substring(0, fullName.lastIndexOf("."));
                    }
                    String canonicalPath = file.getCanonicalPath().replaceAll("\\\\", "/");
                    SysAttachment sysAttachment = new SysAttachment();
                    sysAttachment.setOriginalFullName(fullName);
                    sysAttachment.setOriginalFileName(fileName);
                    sysAttachment.setFileExt(prefix);
                    sysAttachment.setIsDirectory(directory ? 1 : 0);
                    sysAttachment.setPersistedFileName(canonicalPath.replace(bootdoConfig.getAttachBasePath(),""));
                    sysAttachment.setParentId(parent == null ? 0l : parent.getId());
                    sysAttachment.setFileSize(directory ? null : file.length());
                    sysAttachmentDao.save(sysAttachment);
                    sysAttachment.setPath(parent == null ? sysAttachment.getId().toString() : (parent.getPath() + "." + sysAttachment.getId()));
                    sysAttachmentDao.save(sysAttachment);
                    if (directory) {
                        regFile(new File(canonicalPath), sysAttachment);
                    }
                }
            }
        }
    }
}
