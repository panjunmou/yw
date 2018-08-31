package com.bootdo.common.service.impl;

import com.bootdo.common.dao.AttachmentDao;
import com.bootdo.common.domain.Attachment;
import com.bootdo.common.service.AttachmentService;
import com.bootdo.common.utils.BeanMapper;
import com.bootdo.common.utils.ShiroUtils;
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
    private AttachmentDao attachmentDao;

    /**
     * 获取文件列表
     * @return
     */
    @Override
    public List<FileVo> listFlie() {
        File fileDir = new File("G:/");
        List<FileVo> fileVoList = new ArrayList<>();
        if (fileDir.exists()) {
            File[] listFiles = fileDir.listFiles();
            for (File file : listFiles) {
                String fullName = file.getName();
                String fileName = fullName;
                String prefix = "";
                if (fullName.indexOf(".") > 0) {
                    prefix = fullName.substring(fullName.lastIndexOf(".") + 1);
                    fileName = fullName.substring(0,fullName.lastIndexOf(".") );
                }
                boolean directory = file.isDirectory();
                boolean hidden = file.isHidden();
                FileVo fileVo = new FileVo();
                fileVo.setFullName(fullName);
                fileVo.setFileName(fileName);
                fileVo.setPrefix(prefix);
                fileVo.setDictory(directory);
                fileVo.setHidden(hidden);
                fileVoList.add(fileVo);
            }
        }
        return fileVoList;
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
        vo.setOwnerId(ShiroUtils.getUser().getUserId().toString());
        vo.setOwner(ShiroUtils.getUser().getUsername());
        vo.setCreateDate(new Date());
        vo.setFileExt(extName);
        vo.setDelFlag(0);
        vo.setFileMd5(md5);
        Attachment attachment = new Attachment();
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
        Attachment attachment = new Attachment();
        BeanUtils.copyProperties(vo, attachment);
        Attachment att = attachmentDao.save(attachment);
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
        Attachment attachment = attachmentDao.findById(id).get();
        BeanUtils.copyProperties(attachment,vo);
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
                Attachment po = attachmentDao.findById(Long.valueOf(idsArr[i])).get();
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
    public List<Attachment> getAttachmentByMd5(String wholeMd5) {
        return attachmentDao.findByPersistedFileNameLike(wholeMd5);
    }

    @Override
    public List<Attachment> findByFileMd5(String wholeMd5) {
        if (!StringUtil.isEmpty(wholeMd5)) {
            return attachmentDao.findByFileMd5(wholeMd5);
        } else
            return new ArrayList();
    }
}
