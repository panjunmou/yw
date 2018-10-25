package com.bootdo.common.controller;

import com.bootdo.common.domain.SysAttachmentRole;
import com.bootdo.common.service.SysAttachmentRoleService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.RequestUtil;
import com.bootdo.common.vo.ResultMessage;
import com.bootdo.common.vo.SysAttachmentRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2018-09-12 14:52:29
 */

@Controller
@RequestMapping("/common/attachmentRole")
public class SysAttachmentRoleController {

    @Autowired
    private SysAttachmentRoleService attachmentRoleService;

    @RequestMapping("")
    String AttachmentRole(HttpServletRequest request, Model model) {
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        String id = (String) queryParamMap.get("id");
        model.addAttribute("id", id);
        return "common/attachmentRole/attachmentRole";
    }

    @ResponseBody
    @RequestMapping("/list")
    public PageUtils list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<SysAttachmentRoleVO> attachmentRoleList = attachmentRoleService.list(query);
        int total = attachmentRoleService.count(query);
        PageUtils pageUtils = new PageUtils(attachmentRoleList, total);
        return pageUtils;
    }

    @RequestMapping("/add")
    String add(HttpServletRequest request, Model model) {
        Map<String, Object> queryParamMap = RequestUtil.getParameterValueMap(request, false, false);
        String id = (String) queryParamMap.get("id");
        model.addAttribute("id", id);
        return "common/attachmentRole/add";
    }

    @RequestMapping("/edit/{id}")
    String edit(@PathVariable("id") Long id, Model model) {
        SysAttachmentRole attachmentRole = attachmentRoleService.get(id);
        model.addAttribute("attachmentRole", attachmentRole);
        return "common/attachmentRole/edit";
    }

    /**
     * 保存
     */
    @ResponseBody
    @RequestMapping("/save")
    public ResultMessage save(HttpServletRequest request) {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> paraMap = RequestUtil.getParameterValueMap(request, false, false);
        attachmentRoleService.save(paraMap);
        return resultMessage;
    }

    /**
     * 修改
     */
    @ResponseBody
    @RequestMapping("/update")
    public ResultMessage update(HttpServletRequest request) {
        ResultMessage resultMessage = new ResultMessage();
        Map<String, Object> paraMap = RequestUtil.getParameterValueMap(request, false, false);
        attachmentRoleService.update(paraMap);
        return resultMessage;
    }

    /**
     * 删除
     */
    @PostMapping("/remove")
    @ResponseBody
    public R remove(Long id) {
        if (attachmentRoleService.remove(id) > 0) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 删除
     */
    @PostMapping("/batchRemove")
    @ResponseBody
    public R remove(@RequestParam("ids[]") Long[] ids) {
        attachmentRoleService.batchRemove(ids);
        return R.ok();
    }

    @PostMapping("/delByIds")
    @ResponseBody
    public ResultMessage delByIds(@RequestParam("ids[]") Long[] ids) {
        this.attachmentRoleService.delById(ids);
        return new ResultMessage(ResultMessage.Success, "删除成功");
    }

}
