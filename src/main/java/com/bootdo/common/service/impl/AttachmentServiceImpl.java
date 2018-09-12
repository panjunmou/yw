package com.bootdo.common.service.impl;

import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.dao.SysAttachmentDao;
import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.service.AttachmentService;
import com.bootdo.common.utils.BeanMapper;
import com.bootdo.common.utils.StringUtil;
import com.bootdo.common.vo.AttachmentVO;
import com.bootdo.common.vo.BootStrapTreeViewVo;
import com.bootdo.common.vo.BootStrapTreeViewVoState;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public AttachmentVO addAttachment(String newPath, AttachmentVO parentAtt, String oriFileName, String extName, Long size, String md5) throws IOException {
        File f1 = new File(newPath);
//        File f2 = new File(attachPath);
        AttachmentVO vo = new AttachmentVO();
        String fullName = f1.getName();
        String fileName = fullName;
        if (fullName.indexOf(".") > 0) {
            fileName = fullName.substring(0, fullName.lastIndexOf("."));
        }
        boolean directory = f1.isDirectory();
        vo.setOriginalFileName(fileName);
        vo.setOriginalFullName(fullName);
        String canonicalPath = f1.getCanonicalPath().replaceAll("\\\\", "/");
        vo.setPersistedFileName(canonicalPath.replace(bootdoConfig.getAttachBasePath(), ""));
        vo.setFileSize(size);
        vo.setCreateDate(new Date());
        vo.setFileExt(extName);
        vo.setFileMd5(md5);
        vo.setIsDirectory(directory ? 1 : 0);

        SysAttachment attachment = new SysAttachment();
        BeanUtils.copyProperties(vo, attachment);
        attachment = attachmentDao.save(attachment);
        attachment.setPath(parentAtt == null ? attachment.getId().toString() : (parentAtt.getPath() + "." + attachment.getId()));
        attachment.setParentId(parentAtt == null ? 0l : parentAtt.getId());
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
                    sysAttachment.setPersistedFileName(canonicalPath.replace(bootdoConfig.getAttachBasePath(), ""));
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

    @Override
    public List<BootStrapTreeViewVo> getAttachmentTree(Map<String, Object> queryParamMap) {
        String parentId = queryParamMap.get("parentId") == null ? "0" : (String) queryParamMap.get("parentId");
        List<BootStrapTreeViewVo> list = getByParentId(queryParamMap);
        if (parentId.equals("0")) {
            BootStrapTreeViewVo bootStrapTreeViewVo = new BootStrapTreeViewVo();
            bootStrapTreeViewVo.setId("0");
            bootStrapTreeViewVo.setText("上海尧伟建设");
//        bootStrapTreeViewVo.setIcon("glyphicon glyphicon-folder-open");
            BootStrapTreeViewVoState state = new BootStrapTreeViewVoState();
            bootStrapTreeViewVo.setParentId(null);
            state.setExpanded(true);
            bootStrapTreeViewVo.setState(state);
            list.add(bootStrapTreeViewVo);
        }

        List<BootStrapTreeViewVo> bootStrapTreeViewVos = BootStrapTreeViewVo.listToTree(list);
        return bootStrapTreeViewVos;
    }

    @Override
    public List<BootStrapTreeViewVo> getByParentId(Map<String, Object> queryParamMap) {
        String parentId = queryParamMap.get("parentId") == null ? "0" : (String) queryParamMap.get("parentId");
        String containsFile = queryParamMap.get("containsFile") == null ? "0" : (String) queryParamMap.get("containsFile");
        List<SysAttachment> sysAttachmentList = attachmentDao.findByParentId(Long.parseLong(parentId));
        List<BootStrapTreeViewVo> list = new ArrayList<BootStrapTreeViewVo>();
        if (sysAttachmentList != null && !sysAttachmentList.isEmpty()) {
            for (int i = 0; i < sysAttachmentList.size(); i++) {
                SysAttachment sysAttachment = sysAttachmentList.get(i);
                Integer isDirectory = sysAttachment.getIsDirectory();
                BootStrapTreeViewVo bootStrapTreeViewVo = new BootStrapTreeViewVo();
                bootStrapTreeViewVo.setId(sysAttachment.getId().toString());
                bootStrapTreeViewVo.setText(sysAttachment.getOriginalFullName());
                if (isDirectory == 1) {
                    //是文件夹
//                    bootStrapTreeViewVo.setIcon("glyphicon glyphicon-folder-close");
                    bootStrapTreeViewVo.setLazyLoad(true);
                } else {
//                    bootStrapTreeViewVo.setIcon("glyphicon glyphicon-file");
                    //是文件
                    if (containsFile.equals("0")) {
                        //不要包含文件,跳过
                        continue;
                    }
                    bootStrapTreeViewVo.setLazyLoad(false);
                }
                BootStrapTreeViewVoState state = new BootStrapTreeViewVoState();
                bootStrapTreeViewVo.setParentId(sysAttachment.getParentId().toString());
                state.setExpanded(false);
                bootStrapTreeViewVo.setState(state);
                list.add(bootStrapTreeViewVo);
            }
        }
        return list;
    }

    @Override
    public List<SysAttachment> getNavList(Map<String, Object> queryParamMap) {
        String parentId = (String) queryParamMap.get("parentId");
        List<SysAttachment> list = new ArrayList<>();
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setOriginalFileName("主目录");
        sysAttachment.setId(0l);
        list.add(sysAttachment);
        if (!StringUtil.isEmpty(parentId)) {
            SysAttachment parent = attachmentDao.findById(Long.parseLong(parentId)).get();
            String path = parent.getPath();
            String[] split = path.split("\\.");
            Long[] idList = new Long[split.length];
            for (int i = 0; i < split.length; i++) {
                String id = split[i];
                idList[i] = Long.parseLong(id);
            }
            List<SysAttachment> attachmentList = attachmentDao.findByIdIn(idList);
            Collections.sort(attachmentList, new Comparator<SysAttachment>() {
                @Override
                public int compare(SysAttachment o1, SysAttachment o2) {
                    int i = o1.getPath().length() - o2.getPath().length();
                    return i;
                }
            });
            list.addAll(attachmentList);
        }
        return list;
    }
}
