package com.bootdo.common.controller;

import com.bootdo.common.domain.SysAttachment;
import com.bootdo.common.service.SysAttachmentService;
import com.bootdo.common.utils.RequestUtil;
import com.bootdo.common.vo.BootStrapTreeViewVo;
import com.bootdo.common.vo.ResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfei on 2016/6/8.
 * 文件上传控制类
 * 支持分片上传，断点续传
 */
@Controller
@RequestMapping({"/common/attactment"})
public class SysAttactmentController extends BaseController {
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(SysAttactmentController.class);

    @Resource
    private SysAttachmentService attachmentService;

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
        return "common/attactment/initPage";
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
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        String parentId = (String) queryParamMap.get("parentId");
        model.addAttribute("parentId", parentId);
        return "common/attactment/uploadPage";
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
     * 获取文件树
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/getAttTree")
    @ResponseBody
    public ResultMessage getAttTree(HttpServletRequest request) throws Exception {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        List<BootStrapTreeViewVo> list = attachmentService.getAttachmentTree(queryParamMap);
        resultMessage.setData(list);
        return resultMessage;
    }

    /**
     * 获取文件树
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/getPersonTree")
    @ResponseBody
    public ResultMessage getPersonTree(HttpServletRequest request) throws Exception {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        List<BootStrapTreeViewVo> list = attachmentService.getPersonTree(queryParamMap);
        resultMessage.setData(list);
        return resultMessage;
    }

    /**
     * 获取文件树
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/getAttByParentId")
    @ResponseBody
    public ResultMessage getAttByParentId(HttpServletRequest request) throws Exception {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        List<BootStrapTreeViewVo> list = attachmentService.getByParentId(queryParamMap);
        resultMessage.setData(list);
        return resultMessage;
    }

    /**
     * 获取文件树
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/getNavList")
    @ResponseBody
    public ResultMessage getNavList(HttpServletRequest request) throws Exception {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        List<SysAttachment> list = attachmentService.getNavList(queryParamMap);
        resultMessage.setData(list);
        return resultMessage;
    }
}
