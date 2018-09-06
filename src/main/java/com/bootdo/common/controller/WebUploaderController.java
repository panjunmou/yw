package com.bootdo.common.controller;

import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.service.AttachmentService;
import com.bootdo.common.utils.*;
import com.bootdo.common.vo.AttachmentVO;
import com.bootdo.common.vo.ResultMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2016/6/8.
 * 文件上传控制类
 * 支持分片上传，断点续传
 */
@Controller
@RequestMapping({"/common/webupload"})
public class WebUploaderController extends BaseController {
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(WebUploaderController.class);

    @Resource
    private AttachmentService attachmentService;
    @Resource
    private BootdoConfig bootdoConfig;

    /**
     * 获取文件存储路径
     */
    private String getFileStorePath() {
        return bootdoConfig.getAttachPath() == null ? "../att_path" : bootdoConfig.getAttachPath();
    }

    /**
     * 主页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("")
    public String page(Model model, HttpServletRequest request) throws Exception {
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        String parentId = (String) queryParamMap.get("parentId");
        model.addAttribute("parentId", parentId);
        return "common/webupload/initPage";
    }

    /**
     * 文件上传页面
     *
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadPage")
    public String uploadPage(Model model, HttpServletRequest request) throws Exception {
        return "common/webupload/uploadPage";
    }

    /**
     * 查询数据
     *
     * @param request
     * @return
     */
    @RequestMapping("/listFile")
    @ResponseBody
    public ResultMessage listFile(HttpServletRequest request) {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        try {
            List<SysAttachment> sysAttachmentList = attachmentService.listFlie(queryParamMap);
            resultMessage.setData(sysAttachmentList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            resultMessage.setResult(ResultMessage.Error);
            resultMessage.setMessage("listFile Error");
        }
        return resultMessage;
    }

    /**
     * 初始化数据
     *
     * @param request
     * @return
     */
    @RequestMapping("/initFile")
    @ResponseBody
    public ResultMessage initFile(HttpServletRequest request) {
        System.out.println("WebUploaderController.initFile");
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        try {
            attachmentService.initFile(queryParamMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
            resultMessage.setResult(ResultMessage.Error);
            resultMessage.setMessage("initFile Error");
        }
        return resultMessage;
    }

    /**
     * 根据wholeMd5验证系统中是否存在改文件
     * 如果存在，则在后台复制，以实现秒传
     *
     * @param wholeMd5 文件的MD5值
     * @param fileName 文件名称
     * @param ext      文件扩展
     * @return
     */
    @RequestMapping("/isExistWholeFile")
    @ResponseBody
    public ResultMessage isExistWholeFile(String wholeMd5, String fileName, String ext) {
        System.out.println("WebUploaderController.isExistWholeFile");
        logger.info(String.format("%20s md5:%s  fileName:%s", "isExistWholeFile", wholeMd5, fileName));
        ResultMessage mes = new ResultMessage();
        List<SysAttachment> poList = attachmentService.findByFileMd5(wholeMd5);
        //数据库copy一份数据。
        if (CollectionUtils.isNotEmpty(poList)) {
            SysAttachment po = poList.get(0);
            File f = new File(getFileStorePath(), po.getPersistedFileName());
            if (f.exists()) {
                AttachmentVO newVO = new AttachmentVO();
                BeanMapper.copy(po, newVO);
                newVO.setId(null);
                newVO.setCreateDate(new Date());
                newVO.setFileExt(ext);
                newVO.setOriginalFileName(fileName);
                Long newId = attachmentService.add(newVO);
                newVO.setId(newId);
                mes.setData(newVO);
                mes.setResult(1);
                return mes;
            }
        }
        mes.setData(null);
        mes.setResult(0);
        return mes;

    }

    /**
     * 分片验证
     * 根据分片文件内容的MD5
     */
    @RequestMapping("/checkUpload")
    @ResponseBody
    public ResultMessage checkUpload(HttpServletRequest request) throws Exception {
        System.out.println("WebUploaderController.checkUpload");
        ResultMessage mes = new ResultMessage();
        String md5 = request.getParameter("md5");
        Integer chunksNum = Integer.parseInt(request.getParameter("chunks"));
        Integer chunk = Integer.parseInt(request.getParameter("chunk")); //当前分片
        String wholeMd5 = request.getParameter("wholeMd5");//整个文件的md5值
        logger.info(String.format("%20s md5:%s  %s/%s", "checkSliceUploaded", wholeMd5, chunk + 1, chunksNum));
        String chunkFilePath = getChunkFilePath(wholeMd5);
        String chunkFileName = getChunkFileName(chunksNum, chunk, md5);
        File chunkFile = new File(chunkFilePath, chunkFileName);
        if (chunkFile.exists()) {
            String serverMd5 = CommonUtils.getMd5ByFile(chunkFile);
            if (serverMd5 != null && serverMd5.equalsIgnoreCase(md5)) {
                mes.setData(serverMd5);
            } else {
                mes.setData("");
            }
        } else {
            mes.setData("");
            //TODO
        }
        return mes;
    }

    /**
     * 获取分片路径
     */
    private String getChunkFilePath(String wholeMd5) {
        System.out.println("WebUploaderController.getChunkFilePath");
        return getFileStorePath() + File.separator + "upload_files" + File.separator + "temp" + File.separator
                + ShiroUtils.getUserId() + File.separator + wholeMd5;

    }

    /**
     * 获取分片名称
     */
    private String getChunkFileName(Integer chunksNum, Integer chunk, String md5) {
        System.out.println("WebUploaderController.getChunkFileName");
        return chunksNum + "." + chunk + "." + md5;
    }

    /**
     * 文件分片上传请求
     */
    @RequestMapping("/upload")
    @ResponseBody
    public ResultMessage upload(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("WebUploaderController.upload");
        ResultMessage mes = new ResultMessage();
        MultipartFile file = request.getFile("file");
        String md5 = CommonUtils.getMd5ByFile(file);
        //断点续传检查
        //分片总数
        Integer chunksNum = 1;
        if (null != request.getParameter("chunks")) {
            chunksNum = Integer.parseInt(request.getParameter("chunks"));
        }
        //当前分片
        Integer chunk = 0;
        if (null != request.getParameter("chunk")) {
            chunk = Integer.parseInt(request.getParameter("chunk"));
        }
        //整个文件的md5值
        String wholeMd5 = request.getParameter("wholeMd5");

        String chunkFileName = getChunkFileName(chunksNum, chunk, md5);
        File f = new File(getChunkFilePath(wholeMd5), chunkFileName);
        logger.info(String.format("%20s md5:%s  %s/%s to %s", "upload", wholeMd5, chunk + 1, chunksNum, f.getAbsolutePath()));

        FileUtils.writeByteArrayToFile(f, file.getBytes());
        return mes;
    }

    /**
     * 文件分片上传合并
     */
    @RequestMapping("/fileMerge")
    @ResponseBody
    public ResultMessage fileMerge(HttpServletRequest request) throws Exception {
        System.out.println("WebUploaderController.fileMerge");
        ResultMessage mes = new ResultMessage();
        final Integer chunks = Integer.parseInt(request.getParameter("chunks"));//分片总数
        String wholeMd5 = request.getParameter("wholeMd5");//整个文件的md5值
        String extName = request.getParameter("ext");//文件扩展名
        String originalFileName = request.getParameter("originalFileName");
        String newPath = CommonUtils.createFilePath(
                getFileStorePath() + File.separator + "upload_files" + File.separator + ShiroUtils.getUserId(),
                wholeMd5 + "_" + DateUtils.format(new Date(), "HHmmss") + "." + extName); //合并完成后移动到新文件夹，并删除临时文件夹
        String md5FilePath = getChunkFilePath(wholeMd5);
        logger.info(String.format("%20s md5:%s  fileName:%s from %s to %s", "fileMerge", wholeMd5, originalFileName, md5FilePath, newPath));

        File[] fileList = new File(md5FilePath).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("" + chunks);
            }
        });
        Map<String, Object> map = new HashMap<String, Object>();
        if (fileList.length == chunks) {
            for (File object : fileList) {
                String[] names = object.getName().split("\\.");
                map.put(names[1], names[2]);
            }
            //所有文件合并到第一个分片
            File tempFile = new File(CommonUtils.createTempFilePath(getChunkFilePath(wholeMd5), "temp"));
            FileChannel mFileChannel = null;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tempFile);
                mFileChannel = fos.getChannel();
                for (int i = 0; i < fileList.length; i++) {
                    FileChannel inFileChannel = null;
                    FileInputStream fileInputStream = null;
                    try {
                        File curtedFile = new File(CommonUtils.createTempFilePath(getChunkFilePath(wholeMd5),
                                getChunkFileName(chunks, i, map.get(Integer.toString(i)).toString())));
                        fileInputStream = new FileInputStream(curtedFile);
                        inFileChannel = fileInputStream.getChannel();
                        inFileChannel.transferTo(0, inFileChannel.size(), mFileChannel);
                    } finally {
                        IOUtils.closeQuietly(inFileChannel);
                        IOUtils.closeQuietly(fileInputStream);
                    }
                }
            } finally {
                IOUtils.closeQuietly(mFileChannel);
                IOUtils.closeQuietly(fos);
            }
            String tempMD5 = CommonUtils.getMd5ByFile(tempFile);
            if (wholeMd5.equals(tempMD5)) {
                File newFile = new File(newPath);
                FileUtils.copyFile(tempFile, newFile);
                AttachmentVO avo = attachmentService.addAttachment(newPath, getFileStorePath(), originalFileName, extName,
                        newFile.length(), wholeMd5); //更新附件存储路径
                FileUtils.deleteDirectory(new File(getChunkFilePath(wholeMd5))); // 删除临时目录中的分片文件
                mes.setData(avo);
            } else {
                throw new RuntimeException("合并前验证文件出错");
            }
            mes.setResult(1);
        } else {
            mes.setResult(0);
            mes.setMessage("文件分片丢失，请点击重新上传按钮。");
        }
        return mes;
    }

    /**
     * 取消上传操作请求，删除上传的临时文件夹
     */
    @RequestMapping("/deleteTemp")
    @ResponseBody
    public ResultMessage deleteTemp(HttpServletRequest request) throws Exception {
        System.out.println("WebUploaderController.deleteTemp");
        ResultMessage mes = new ResultMessage();
        String wholeMd5 = request.getParameter("wholeMd5");//整个文件的md5值
        String fileName = request.getParameter("fileName");
        if (StringUtil.isNotEmpty(wholeMd5)) {
            FileUtils.deleteDirectory(new File(getChunkFilePath(wholeMd5)));// 删除临时目录中的分片文件
        } else {
            logger.error("删除临时文件夹:" + fileName + "失败");
        }
        return mes;
    }

    /**
     * 更新附件存储路径
     * */
    /**
     * 下载附件
     */
    @RequestMapping("/download")
    @ResponseBody
    public void doDownloadFile(HttpServletRequest request, HttpServletResponse response, Long id) throws IOException {
        System.out.println("WebUploaderController.doDownloadFile");
        CommonUtils.doDownloadFileDeal(getFileStorePath(), attachmentService, request, response, id);
    }
}
