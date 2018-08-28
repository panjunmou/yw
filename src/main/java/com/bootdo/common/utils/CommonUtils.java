package com.bootdo.common.utils;

import com.bootdo.common.service.AttachmentService;
import com.bootdo.common.vo.AttachmentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Calendar;

/**
 * Created by wangfei on 2016/5/11.
 */
public class CommonUtils {

    /**
     * 日志
     */
    private static Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * 创建临时文件夹路径
     */
    public static String createTempFilePath(String tempPath, String fileName) {
        File file = new File(tempPath + File.separator);
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = file.getPath() + File.separator + fileName;
        return path;
    }

    /**
     * 创建文件路径
     */
    public static String createFilePath(String tempPath, String fileName) {
        Calendar cal = Calendar.getInstance();
        Integer year = Integer.valueOf(cal.get(1));
        Integer month = Integer.valueOf(cal.get(2) + 1);
        File file = new File(tempPath + File.separator + year + File.separator + month);

        if (!file.exists()) {
            file.mkdirs();
        }
        String path = file.getPath() + File.separator + fileName;
        return path;
    }

    /**
     * 下载附件
     */
    public static void downLoadFile(HttpServletRequest request, HttpServletResponse response, String fullPath,
                                    String fileName) throws IOException {
        OutputStream outp = response.getOutputStream();
        File file = new File(fullPath);
        if (file.exists()) {
            response.setContentType("APPLICATION/OCTET-STREAM");
            String filedisplay = fileName;
            String agent = request.getHeader("USER-AGENT");
            //IE11已经没有MSIE,用Gecko来判断不是IE
            if ((agent != null) && (agent.indexOf("MSIE") == -1) && agent.indexOf("Gecko") == -1) {
                String enableFileName = "=?UTF-8?B?" + Base64.getBase64(filedisplay) + "?=";
                response.setHeader("Content-Disposition", "attachment; filename=" + enableFileName);
            } else {
                filedisplay = URLEncoder.encode(filedisplay, "utf-8");
                response.addHeader("Content-Disposition", "attachment;filename=" + filedisplay);
            }
            FileInputStream in = null;
            try {
                outp = response.getOutputStream();
                in = new FileInputStream(fullPath);
                response.setContentLength(in.available());
                byte[] b = new byte[1024];
                int i = 0;
                while ((i = in.read(b)) > 0) {
                    outp.write(b, 0, i);
                }
                outp.flush();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (outp != null) {
                    outp.close();
                    outp = null;
                    response.flushBuffer();
                }
            }
        } else {
            response.setContentType("text/html; charset=UTF-8");
            outp.write((fileName + ":文件不存在! ").getBytes("utf-8"));
        }
    }

    /**
     * 下载附件逻辑处理
     */
    public static void doDownloadFileDeal(String fileStorePath, AttachmentService attachmentService,
                                          HttpServletRequest request, HttpServletResponse response, Long id) throws IOException {
        long fileId = id;
        if (fileId != 0L) {
            AttachmentVO attachVO = attachmentService.getById(fileId);
            if (null != attachVO) {
                String filePath = attachVO.getPersistedFileName();
                if (StringUtil.isEmpty(filePath)) {
                    return;
                }
                String fullPath = StringUtil.trimSufffix(fileStorePath, File.separator) + File.separator
                        + filePath.replace("/", File.separator);
                String fileName = attachVO.getOriginalFileName();

                CommonUtils.downLoadFile(request, response, fullPath, fileName);
            }
        }
    }

    /**
     * 文件MD5加密
     */
    public static String getMd5ByFile(MultipartFile file) throws Exception {
        return MD5Util.getMD5String(file.getBytes());
    }


    /**
     * 文件MD5加密
     */
    public static String getMd5ByFile(File file) throws Exception {
        return MD5Util.getMD5String(file);
    }

    /**
     * 获取当前网络ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            StringBuilder ip = new StringBuilder("");
            ip = ip.append("127").append(".").append("0").append(".").append("0").append(".").append("1");
            if (ip.toString().equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                if (inet != null) {
                    ipAddress = inet.getHostAddress();
                }
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            String strr = ",";
            if (ipAddress.indexOf(strr) > -1) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(strr));
            }
        }
        return ipAddress;
    }
}
