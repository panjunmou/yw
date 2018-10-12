package com.bootdo.common.service.impl;

import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.dao.SysAttachmentDao;
import com.bootdo.common.dao.SysAttachmentMapper;
import com.bootdo.common.dao.SysAttachmentRoleDao;
import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.service.SysAttachmentService;
import com.bootdo.common.utils.*;
import com.bootdo.common.vo.BootStrapTreeViewVo;
import com.bootdo.common.vo.BootStrapTreeViewVoState;
import com.bootdo.common.vo.SysAttachmentVO;
import com.bootdo.system.dao.RoleDao;
import com.bootdo.system.domain.RoleDO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by wangfei on 2016/4/25.
 * 附件服务类
 */
@Service
public class SysAttachmentServiceImpl implements SysAttachmentService {

    /**
     * 附件dao
     */
    @Resource
    private SysAttachmentDao sysAttachmentDao;
    @Resource
    private BootdoConfig bootdoConfig;
    @Resource
    private SysAttachmentMapper sysAttachmentMapper;
    @Resource
    private RoleDao roleDao;
    @Resource
    private SysAttachmentRoleDao sysAttachmentRoleDao;

    /**
     * 获取文件列表
     *
     * @param queryParamMap
     * @return
     */
    @Override
    public List<SysAttachmentVO> listFlie(Map<String, Object> queryParamMap) throws Exception {
        String parentId = queryParamMap.get("parentId") == null ? "0" : (String) queryParamMap.get("parentId");
        boolean isSysManager = isSysManager();
        if (isSysManager) {
            List<SysAttachment> sysAttachmentList = sysAttachmentDao.findByParentId(Long.parseLong(parentId));
            List<SysAttachmentVO> sysAttachmenVotList = new ArrayList<>();
            if (sysAttachmentList != null) {
                for (int i = 0; i < sysAttachmentList.size(); i++) {
                    SysAttachment sysAttachment = sysAttachmentList.get(i);
                    SysAttachmentVO sysAttachmentVO = new SysAttachmentVO();
                    BeanUtils.copyProperties(sysAttachment, sysAttachmentVO);
                    sysAttachmenVotList.add(sysAttachmentVO);
                }
                return sysAttachmenVotList;
            }
            return sysAttachmenVotList;

        }
        String containsFile = queryParamMap.get("containsFile") == null ? "0" : (String) queryParamMap.get("containsFile");
        Long userId = ShiroUtils.getUserId();
        //获取当前父节点下的所有文件
        //1.权限就在parentId下的文件,此类文件权限最高
        Map<String, Object> selectMap = new HashMap<>();
        selectMap.put("userId", userId);
        selectMap.put("parentId", parentId);
        List<SysAttachmentVO> voList = sysAttachmentMapper.getAttByParentId(selectMap);
        Map<Long, SysAttachmentVO> voMap = new HashMap<>();
        if (voList != null) {
            for (int i = 0; i < voList.size(); i++) {
                SysAttachmentVO sysAttachmentVO = voList.get(i);
                voMap.put(sysAttachmentVO.getId(), sysAttachmentVO);
            }
        }
        //2.权限在父节点上,沿用父节点的权限
        //找到所有父节点
        String lastParetnPermission = sysAttachmentMapper.getLastParetnPermission(selectMap);
        if (StringUtil.isNotEmpty(lastParetnPermission)) {
            List<SysAttachment> sysAttachmentList = sysAttachmentDao.findByParentId(Long.parseLong(parentId));
            if (sysAttachmentList != null) {
                for (int i = 0; i < sysAttachmentList.size(); i++) {
                    SysAttachment attachment = sysAttachmentList.get(i);
                    SysAttachmentVO attachmentVO = voMap.get(attachment.getId());
                    if (attachmentVO == null) {
                        SysAttachmentVO sysAttachmentVO = new SysAttachmentVO();
                        BeanUtils.copyProperties(attachment, sysAttachmentVO);
                        sysAttachmentVO.setPermission(lastParetnPermission);
                        voMap.put(sysAttachmentVO.getId(), sysAttachmentVO);
                    }
                }
            }
        }

        //3.权限设在子节点上,子节点在此parentId下,此类文件权限最低,仅仅只读
        List<SysAttachmentVO> attFromChild = sysAttachmentMapper.getAttFromChild(selectMap);
        if (attFromChild != null) {
            for (int i = 0; i < attFromChild.size(); i++) {
                SysAttachmentVO sysAttachmentVO = attFromChild.get(i);
                SysAttachmentVO childVo = voMap.get(sysAttachmentVO.getId());
                if (childVo == null) {
                    voMap.put(sysAttachmentVO.getId(), sysAttachmentVO);
                }
            }
        }
        Collection<SysAttachmentVO> voCollection = voMap.values();
        return new ArrayList<>(voCollection);
    }

    @Override
    public boolean isSysManager() {
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("userId", ShiroUtils.getUserId());
        List<RoleDO> roleDOList = roleDao.getByUserId(roleMap);
        boolean isSysManager = false;
        for (int i = 0; i < roleDOList.size(); i++) {
            RoleDO roleDO = roleDOList.get(i);
            String roleSign = roleDO.getRoleSign();
            if (roleSign.equalsIgnoreCase(bootdoConfig.getSysManager())) {
                isSysManager = true;
                break;
            }
        }
        return isSysManager;
    }

    @Override
    public SysAttachmentVO addAttachment(String newPath, SysAttachmentVO parentAtt, String oriFileName, String extName, Long size, String md5) throws IOException {
        File f1 = new File(newPath);
//        File f2 = new File(attachPath);
        SysAttachmentVO vo = new SysAttachmentVO();
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
        attachment = sysAttachmentDao.save(attachment);
        attachment.setPath(parentAtt == null ? attachment.getId().toString() : (parentAtt.getPath() + "." + attachment.getId()));
        attachment.setParentId(parentAtt == null ? 0l : parentAtt.getId());
        attachment = sysAttachmentDao.save(attachment);

        SysAttachmentVO rvo = new SysAttachmentVO();
        BeanUtils.copyProperties(attachment, rvo);
        return rvo;
    }

    /**
     * 添加附件
     */
    @Override
    public Long add(SysAttachmentVO vo) {
        SysAttachment attachment = new SysAttachment();
        BeanUtils.copyProperties(vo, attachment);
        SysAttachment att = sysAttachmentDao.save(attachment);
        return att.getId();
    }

    /**
     * 根据id删除
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        sysAttachmentDao.deleteById(id);
    }

    /**
     * 根据id查询附件
     *
     * @param id
     */
    @Override
    public SysAttachmentVO getById(Long id) {
        SysAttachmentVO vo = new SysAttachmentVO();
        Optional<SysAttachment> attachmentOptional = sysAttachmentDao.findById(id);
        if (attachmentOptional.isPresent()) {
            SysAttachment attachment = attachmentOptional.get();
            BeanUtils.copyProperties(attachment, vo);
        }
        return vo;
    }

    /**
     * 根据ids查询附件列表
     *
     * @param ids
     */
    @Override
    public List<SysAttachmentVO> findByIds(String ids) throws Exception {
        List<SysAttachmentVO> voList = new ArrayList<SysAttachmentVO>();
        String[] idsArr = ids.split("\\,");
        for (int i = 0; i < idsArr.length; i++) {
            if (StringUtil.isNotEmpty(idsArr[i])) {
                SysAttachment po = sysAttachmentDao.findById(Long.valueOf(idsArr[i])).get();
                SysAttachmentVO vo = new SysAttachmentVO();
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
        return sysAttachmentDao.findByPersistedFileNameLike(wholeMd5);
    }

    @Override
    public List<SysAttachment> findByFileMd5(String wholeMd5) {
        if (!StringUtil.isEmpty(wholeMd5)) {
            return sysAttachmentDao.findByFileMd5(wholeMd5);
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
    public List<BootStrapTreeViewVo> getPersonTree(Map<String, Object> queryParamMap) {
        String parentId = queryParamMap.get("parentId") == null ? "0" : (String) queryParamMap.get("parentId");
        boolean sysManager = isSysManager();
        List<BootStrapTreeViewVo> list;
        if (sysManager) {
            return this.getAttachmentTree(queryParamMap);
        } else {
            list = getByPersionParentId(queryParamMap);
        }
        if (parentId.equals("0")) {
            BootStrapTreeViewVo bootStrapTreeViewVo = new BootStrapTreeViewVo();
            bootStrapTreeViewVo.setId("0");
            bootStrapTreeViewVo.setText("上海尧伟建设");
            BootStrapTreeViewVoState state = new BootStrapTreeViewVoState();
            state.setExpanded(true);
            bootStrapTreeViewVo.setParentId(null);
            bootStrapTreeViewVo.setState(state);
            list.add(bootStrapTreeViewVo);
        }
        List<BootStrapTreeViewVo> bootStrapTreeViewVos = BootStrapTreeViewVo.listToTree(list);
        return bootStrapTreeViewVos;
    }

    @Override
    public List<BootStrapTreeViewVo> getByPersionParentId(Map<String, Object> queryParamMap) {
        String parentId = queryParamMap.get("parentId") == null ? "0" : (String) queryParamMap.get("parentId");
        String containsFile = queryParamMap.get("containsFile") == null ? "0" : (String) queryParamMap.get("containsFile");
        queryParamMap.put("userId", ShiroUtils.getUserId());
        queryParamMap.put("parentId", parentId);
        boolean sysManager = isSysManager();
        List<SysAttachmentVO> sysAttachmentList;
        if (sysManager) {
            return this.getByParentId(queryParamMap);
        } else {
            sysAttachmentList = sysAttachmentMapper.getByPersonParentId(queryParamMap);
        }
        List<BootStrapTreeViewVo> list = new ArrayList<BootStrapTreeViewVo>();
        if (sysAttachmentList != null && !sysAttachmentList.isEmpty()) {
            Map<Long, SysAttachmentVO> sysAttachmentVOMap = new HashMap<>();
            for (int i = 0; i < sysAttachmentList.size(); i++) {
                SysAttachmentVO sysAttachmentVO = sysAttachmentList.get(i);
                Long id = sysAttachmentVO.getId();
                SysAttachmentVO attachmentVO = sysAttachmentVOMap.get(id);
                if (attachmentVO == null) {
                    sysAttachmentVOMap.put(id, sysAttachmentVO);
                } else {
                    Integer level = attachmentVO.getLevel();
                    Integer voLevel = sysAttachmentVO.getLevel();
                    if (voLevel > level) {
                        sysAttachmentVOMap.put(id, sysAttachmentVO);
                    } else {
                        String permission = attachmentVO.getPermission();
                        String voPermission = sysAttachmentVO.getPermission();
                        if (voPermission.length() > permission.length()) {
                            sysAttachmentVOMap.put(id, sysAttachmentVO);
                        }
                    }
                }
            }
            for (SysAttachmentVO sysAttachmentVO : sysAttachmentVOMap.values()) {
                Integer isDirectory = sysAttachmentVO.getIsDirectory();
                BootStrapTreeViewVo bootStrapTreeViewVo = new BootStrapTreeViewVo();
                bootStrapTreeViewVo.setText(sysAttachmentVO.getOriginalFullName());
                bootStrapTreeViewVo.setId(sysAttachmentVO.getId().toString());
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
                bootStrapTreeViewVo.setParentId(sysAttachmentVO.getParentId().toString());
                state.setExpanded(false);
                bootStrapTreeViewVo.setState(state);
                list.add(bootStrapTreeViewVo);
            }
        }
        return list;
    }

    @Override
    public List<BootStrapTreeViewVo> getByParentId(Map<String, Object> queryParamMap) {
        String parentId = queryParamMap.get("parentId") == null ? "0" : (String) queryParamMap.get("parentId");
        String containsFile = queryParamMap.get("containsFile") == null ? "0" : (String) queryParamMap.get("containsFile");
        List<SysAttachment> sysAttachmentList = sysAttachmentDao.findByParentId(Long.parseLong(parentId));
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
        if (!StringUtil.isEmpty(parentId) && !parentId.equals("0")) {
            SysAttachment parent = sysAttachmentDao.findById(Long.parseLong(parentId)).get();
            String path = parent.getPath();
            String[] split = path.split("\\.");
            Long[] idList = new Long[split.length];
            for (int i = 0; i < split.length; i++) {
                String id = split[i];
                idList[i] = Long.parseLong(id);
            }
            List<SysAttachment> attachmentList = sysAttachmentDao.findByIdIn(idList);
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

    @Override
    public SysAttachment getByPersistedFileName(String persistedFileName) {
        SysAttachment sysAttachment = sysAttachmentDao.findByPersistedFileName(persistedFileName);
        return sysAttachment;
    }

    @Override
    public void mkDir(Map<String, Object> paraMap) {
        String dirName = (String) paraMap.get("dirName");
        String parentId = (String) paraMap.get("parentId");
        SysAttachment parent = sysAttachmentDao.findById(Long.parseLong(parentId)).get();
        String persistedFileName = parent.getPersistedFileName();
        String realPath = bootdoConfig.getAttachBasePath() + persistedFileName + "/" + dirName;
        //生成数据
        SysAttachment attachment = new SysAttachment();
        attachment.setOriginalFullName(dirName);
        attachment.setOriginalFileName(dirName);
        attachment.setFileExt("");
        attachment.setIsDirectory(1);
        attachment.setPersistedFileName(parent.getPersistedFileName() + "/" + dirName);
        attachment.setParentId(parent == null ? 0l : parent.getId());
        attachment.setFileSize(null);
        sysAttachmentDao.save(attachment);
        attachment.setPath(parent == null ? attachment.getId().toString() : (parent.getPath() + "." + attachment.getId()));
        sysAttachmentDao.save(attachment);

        //创建文件夹
        File file = new File(realPath);
        file.mkdir();
    }

    @Override
    public void downFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> paraMap = RequestUtil.getParameterValueMap(request, false, false);
        String id = (String) paraMap.get("id");
        SysAttachment attachment = sysAttachmentDao.findById(Long.parseLong(id)).get();
        String persistedFileName = attachment.getPersistedFileName();
        String originalFullName = attachment.getOriginalFullName();
        String fullPath = bootdoConfig.getAttachBasePath() + persistedFileName;
        CommonUtils.downLoadFile(request, response, fullPath, originalFullName);
    }

    @Override
    public void del(Map<String, Object> paraMap) throws IOException {
        String ids = (String) paraMap.get("ids");
        if (StringUtils.isNotEmpty(ids)) {
            String[] split = ids.split(",");
            List<Long> idList = new ArrayList<>();
            for (String id : split) {
                idList.add(Long.parseLong(id));
            }
            Iterable<SysAttachment> sysAttachments = this.sysAttachmentDao.findAllById(idList);
            for (SysAttachment sysAttachment : sysAttachments) {
                Integer isDirectory = sysAttachment.getIsDirectory();
                String filePath = bootdoConfig.getAttachBasePath() + sysAttachment.getPersistedFileName();
                if (isDirectory == 1) {
                    //删除数据库(包括该文件夹以及以下的所有文件)
                    String path = sysAttachment.getPath();
                    //删除权限
                    List<SysAttachment> pathReg = sysAttachmentDao.findByPathReg(path);
                    if (pathReg != null) {
                        List<Long> attIdList = new ArrayList<>();
                        for (int i = 0; i < pathReg.size(); i++) {
                            SysAttachment attachment = pathReg.get(i);
                            attIdList.add(attachment.getId());
                            sysAttachmentDao.delete(attachment);
                        }
                        sysAttachmentRoleDao.deleteByAttactmentIdIn(attIdList);
                    }
                    //删除文件
                    FileUtils.deleteDirectory(new File(filePath));
                }else{
                    sysAttachmentRoleDao.deleteByAttactmentId(sysAttachment.getId());
                    //直接删除数据库文件
                    sysAttachmentDao.delete(sysAttachment);
                    //删除文件
                    FileUtil.deleteFile(filePath);
                }
            }
        }
    }
}
