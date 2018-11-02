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
import org.dozer.util.IteratorUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<BootStrapTreeViewVo> listTree(Map<String, Object> queryParamMap) throws Exception {
        String parentId = queryParamMap.get("parentId") == null ? "0" : (String) queryParamMap.get("parentId");
        List<SysAttachmentVO> sysAttachmentList = this.listFlie(queryParamMap);
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
                    bootStrapTreeViewVo.setLazyLoad(true);
                } else {
                    //是文件
                    //不要包含文件,跳过
                    continue;
                }
                BootStrapTreeViewVoState state = new BootStrapTreeViewVoState();
                bootStrapTreeViewVo.setParentId(sysAttachment.getParentId().toString());
                state.setExpanded(false);
                bootStrapTreeViewVo.setState(state);
                list.add(bootStrapTreeViewVo);
            }
        }
        if (parentId.equals("0")) {
            BootStrapTreeViewVo bootStrapTreeViewVo = new BootStrapTreeViewVo();
            bootStrapTreeViewVo.setId("0");
            bootStrapTreeViewVo.setText("上海尧伟建设");
            BootStrapTreeViewVoState state = new BootStrapTreeViewVoState();
            bootStrapTreeViewVo.setParentId(null);
            state.setExpanded(true);
            bootStrapTreeViewVo.setState(state);
            list.add(bootStrapTreeViewVo);
            List<BootStrapTreeViewVo> bootStrapTreeViewVos = BootStrapTreeViewVo.listToTree(list);
            return bootStrapTreeViewVos;
        }
        return list;
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
    @Transactional
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
        attachment.setParentId((parentAtt == null || parentAtt.getId() == null) ? 0l : parentAtt.getId());
        attachment = sysAttachmentDao.save(attachment);

        SysAttachmentVO rvo = new SysAttachmentVO();
        BeanUtils.copyProperties(attachment, rvo);
        return rvo;
    }

    /**
     * 添加附件
     */
    @Override
    @Transactional
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
    @Transactional
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
        SysAttachmentVO vo = null;
        Optional<SysAttachment> attachmentOptional = sysAttachmentDao.findById(id);
        if (attachmentOptional.isPresent()) {
            vo = new SysAttachmentVO();
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
    @Transactional
    public void initFile(Map<String, Object> queryParamMap) throws Exception {
        Iterator<SysAttachment> attachmentIterator = sysAttachmentDao.findAll().iterator();
        List<SysAttachment> attachmentList = IteratorUtils.toList(attachmentIterator);
        Map<String, SysAttachment> sysAttachmentMap = new HashMap<>();
        if (attachmentList != null) {
            for (int i = 0; i < attachmentList.size(); i++) {
                SysAttachment attachment = attachmentList.get(i);
                String persistedFileName = attachment.getPersistedFileName();
                sysAttachmentMap.put(persistedFileName, attachment);
            }
        }
        String attachBasePath = bootdoConfig.getAttachBasePath();
        File fileDir = new File(attachBasePath);
        regFile(fileDir, null, sysAttachmentMap);
    }

    /**
     * 递归生成数据
     *
     * @param fileDir
     * @param parent
     * @param sysAttachmentMap
     * @throws Exception
     */
    @Transactional
    void regFile(File fileDir, SysAttachment parent, Map<String, SysAttachment> sysAttachmentMap) throws Exception {
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
                    String persistedFileName = canonicalPath.replace(bootdoConfig.getAttachBasePath(), "");
                    SysAttachment attachment = sysAttachmentMap.get(persistedFileName);
                    if (attachment != null) {
                        continue;
                    }
                    sysAttachment.setPersistedFileName(persistedFileName);
                    sysAttachment.setParentId(parent == null ? 0l : parent.getId());
                    sysAttachment.setFileSize(directory ? null : file.length());
                    sysAttachmentDao.save(sysAttachment);
                    sysAttachment.setPath(parent == null ? sysAttachment.getId().toString() : (parent.getPath() + "." + sysAttachment.getId()));
                    sysAttachmentDao.save(sysAttachment);
                    if (directory) {
                        regFile(new File(canonicalPath), sysAttachment, sysAttachmentMap);
                    }
                }
            }
        }
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
    @Transactional
    public void mkDir(Map<String, Object> paraMap) {
        String dirName = (String) paraMap.get("dirName");
        String parentId = (String) paraMap.get("parentId");
        Optional<SysAttachment> optional = sysAttachmentDao.findById(Long.parseLong(parentId));
        String realPath;
        if (optional.isPresent()) {
            SysAttachment parent = optional.get();
            String persistedFileName = parent.getPersistedFileName();
            realPath = bootdoConfig.getAttachBasePath() + persistedFileName + "/" + dirName;
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
        } else {
            realPath = bootdoConfig.getAttachBasePath() + "/" + dirName;
            SysAttachment attachment = new SysAttachment();
            attachment.setOriginalFullName(dirName);
            attachment.setOriginalFileName(dirName);
            attachment.setFileExt("");
            attachment.setIsDirectory(1);
            attachment.setPersistedFileName(dirName);
            attachment.setParentId(0l);
            attachment.setFileSize(null);
            sysAttachmentDao.save(attachment);
            attachment.setPath(attachment.getId().toString());
            sysAttachmentDao.save(attachment);
        }
        //创建文件夹
        File file = new File(realPath);
        file.mkdir();
    }

    @Override
    @Transactional
    public void downFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> paraMap = RequestUtil.getParameterValueMap(request, false, false);
        String id = (String) paraMap.get("id");
        if (StringUtils.isNotEmpty(id)) {
            SysAttachment attachment = sysAttachmentDao.findById(Long.parseLong(id)).get();
            Integer isDirectory = attachment.getIsDirectory();
            String persistedFileName = attachment.getPersistedFileName();
            String originalFullName = attachment.getOriginalFullName();
            String fullPath = bootdoConfig.getAttachBasePath() + persistedFileName;
            if (isDirectory == 1) {
                //文件夹
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH) + 1;
                int day = now.get(Calendar.DAY_OF_MONTH);
                String zipTempPath = bootdoConfig.getAttachTempPath() + "/" + year + "/" + month + "/" + day;
                File zipFile = new File(zipTempPath);
                zipFile.mkdirs();
                String zipRealPath = zipTempPath + "/" + UUID.randomUUID().toString() + ".zip";
                ZipUtils.zip(zipRealPath, fullPath);
                CommonUtils.downLoadFile(request, response, zipRealPath, originalFullName + ".zip");
            } else {
                //单个文件
                CommonUtils.downLoadFile(request, response, fullPath, originalFullName);
            }
        } else {
            //多个文件
            String ids = (String) paraMap.get("ids");
            if (StringUtils.isNotEmpty(ids)) {
                String[] split = ids.split(",");
                Long[] idArr = new Long[split.length];
                for (int i = 0; i < split.length; i++) {
                    idArr[i] = Long.parseLong(split[i]);
                }
                List<SysAttachment> sysAttachmentList = sysAttachmentDao.findByIdIn(idArr);
                if (sysAttachmentList != null && !sysAttachmentList.isEmpty()) {
                    List<String> fileList = new ArrayList<>();
                    for (int i = 0; i < sysAttachmentList.size(); i++) {
                        SysAttachment attachment = sysAttachmentList.get(i);
                        String persistedFileName = attachment.getPersistedFileName();
                        String fullPath = bootdoConfig.getAttachBasePath() + persistedFileName;
                        fileList.add(fullPath);
                    }
                    Calendar now = Calendar.getInstance();
                    int year = now.get(Calendar.YEAR);
                    int month = now.get(Calendar.MONTH) + 1;
                    int day = now.get(Calendar.DAY_OF_MONTH);
                    String zipTempPath = bootdoConfig.getAttachTempPath() + "/" + year + "/" + month + "/" + day;
                    File zipFile = new File(zipTempPath);
                    zipFile.mkdirs();
                    String zipRealPath = zipTempPath + "/" + UUID.randomUUID().toString() + ".zip";
                    ZipUtils.zip(zipRealPath, fileList);
                    Long parentId = sysAttachmentList.get(0).getParentId();
                    SysAttachment attachment = sysAttachmentDao.findById(parentId).get();
                    CommonUtils.downLoadFile(request, response, zipRealPath, attachment.getOriginalFileName() + ".zip");
                }
            }
        }
    }

    @Override
    @Transactional
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
                } else {
                    sysAttachmentRoleDao.deleteByAttactmentId(sysAttachment.getId());
                    //直接删除数据库文件
                    sysAttachmentDao.delete(sysAttachment);
                    //删除文件
                    FileUtil.deleteFile(filePath);
                }
            }
        }
    }

    @Override
    public String convertFile(Map<String, Object> queryParamMap) throws IOException {
        String id = (String) queryParamMap.get("id");
        if (StringUtils.isNotEmpty(id)) {
            SysAttachment sysAttachment = sysAttachmentDao.findById(Long.parseLong(id)).get();
            String persistedFileName = sysAttachment.getPersistedFileName();
            String inputFilePath = bootdoConfig.getAttachBasePath() + persistedFileName;
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH) + 1;
            int day = now.get(Calendar.DAY_OF_MONTH);
            String outTempPath = bootdoConfig.getAttachTempPath() + "/" + year + "/" + month + "/" + day;
            File zipFile = new File(outTempPath);
            zipFile.mkdirs();
            String outRealPath = outTempPath + "/" + System.currentTimeMillis() + ".swf";
            outRealPath = Office2Swf.office2Swf(inputFilePath, outRealPath);
            return outRealPath;
        }
        return "";
    }

    @Override
    public void move(Map<String, Object> paraMap) throws Exception {
        String moveAttIds = (String) paraMap.get("moveAttIds");
        String targetId = (String) paraMap.get("targetId");
        String parentId = (String) paraMap.get("parentId");
        if (StringUtil.isEmpty(targetId)) {
            throw new Exception("没有找到目标文件夹!请刷新重试!");
        }
        if (StringUtil.isEmpty(moveAttIds)) {
            throw new Exception("没有找到需要移动的文件,请刷新重试!");
        }
        if (parentId.equals(targetId)) {
            throw new Exception("同一级目录,不需要移动");
        }
        SysAttachment tarSysAtt = null;
        if (!targetId.equals("0")) {
            Optional<SysAttachment> attachmentOptional = sysAttachmentDao.findById(Long.parseLong(targetId));
            if (!attachmentOptional.isPresent()) {
                throw new Exception("没有找到目标文件夹!请刷新重试!");
            }
            tarSysAtt = attachmentOptional.get();
        }
        SysAttachment oldParent = null;
        if (!parentId.equals("0")) {
            Optional<SysAttachment> parentOptional = sysAttachmentDao.findById(Long.parseLong(parentId));
            if (!parentOptional.isPresent()) {
                throw new Exception("没有找到父文件夹!请刷新重试!");
            }
            oldParent = parentOptional.get();
        }
        String[] moveAttIdArr = moveAttIds.split(",");
        Long[] longs = new Long[moveAttIdArr.length];
        for (int i = 0; i < moveAttIdArr.length; i++) {
            String id = moveAttIdArr[i];
            longs[i] = Long.parseLong(id);
        }

        List<SysAttachment> attachmentList = sysAttachmentDao.findByIdIn(longs);
        if (attachmentList != null) {
            for (int i = 0; i < attachmentList.size(); i++) {
                SysAttachment sysAttachment = attachmentList.get(i);
                Integer isDirectory = sysAttachment.getIsDirectory();
                if (isDirectory == 1) {
                    //是文件夹
                    String path = sysAttachment.getPath();
                    List<SysAttachment> sysAttachmentList = sysAttachmentDao.findByPathReg(path);
                    String targetPath = "";
                    String oldPath = "";
                    if (tarSysAtt != null) {
                        targetPath = tarSysAtt.getPath();
                    }
                    if (oldParent != null) {
                        oldPath = oldParent.getPath();
                    }
                    String targetPersistedFileName = "";
                    String oldPersistedFileName = "";
                    if (tarSysAtt != null) {
                        targetPersistedFileName = tarSysAtt.getPersistedFileName();
                    }
                    if (oldParent != null) {
                        oldPersistedFileName = oldParent.getPersistedFileName();
                    }
                    if (sysAttachmentList != null) {
                        for (int j = 0; j < sysAttachmentList.size(); j++) {
                            SysAttachment attachment = sysAttachmentList.get(j);
                            attachment.setPath(attachment.getPath().replace(oldPath, targetPath));
                            attachment.setPersistedFileName(attachment.getPersistedFileName().replace(oldPersistedFileName, targetPersistedFileName));
                        }
                        sysAttachmentDao.saveAll(sysAttachmentList);
                    }
                }
                sysAttachment.setParentId(tarSysAtt == null ? 0l : tarSysAtt.getId());
                sysAttachment.setPath(tarSysAtt == null ? sysAttachment.getId().toString() : tarSysAtt.getPath() + "." + sysAttachment.getId());
                sysAttachment.setPersistedFileName(tarSysAtt == null ? sysAttachment.getOriginalFullName() : tarSysAtt.getPersistedFileName() + "/" + sysAttachment.getOriginalFullName());
            }
            sysAttachmentDao.saveAll(attachmentList);

            for (int i = 0; i < attachmentList.size(); i++) {
                SysAttachment sysAttachment = attachmentList.get(i);
                Integer isDirectory = sysAttachment.getIsDirectory();
                String oldPersist = oldParent == null ? "" : oldParent.getPersistedFileName();
                File sourceFile = new File(bootdoConfig.getAttachBasePath() + oldPersist + "/" + sysAttachment.getOriginalFullName());
                File targetFile = new File(bootdoConfig.getAttachBasePath() + "/" + sysAttachment.getPersistedFileName());
                if (isDirectory == 0) {
                    FileUtils.moveFile(sourceFile, targetFile);
                } else {
                    FileUtils.moveDirectory(sourceFile, targetFile);
                }
            }
        }
    }
}
