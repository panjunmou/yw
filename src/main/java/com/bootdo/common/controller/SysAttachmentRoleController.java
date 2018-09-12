package com.bootdo.common.controller;

import com.bootdo.common.domain.SysAttachmentRole;
import com.bootdo.common.service.SysAttachmentRoleService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    String AttachmentRole() {
        return "common/attachmentRole/attachmentRole";
    }

    @ResponseBody
    @RequestMapping("/list")
    public PageUtils list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<SysAttachmentRole> attachmentRoleList = attachmentRoleService.list(query);
        int total = attachmentRoleService.count(query);
        PageUtils pageUtils = new PageUtils(attachmentRoleList, total);
        return pageUtils;
    }

    @RequestMapping("/add")
    String add() {
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
    @PostMapping("/save")
    public R save(SysAttachmentRole attachmentRole) {
        if (attachmentRoleService.save(attachmentRole) > 0) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 修改
     */
    @ResponseBody
    @RequestMapping("/update")
    public R update(SysAttachmentRole attachmentRole) {
        attachmentRoleService.update(attachmentRole);
        return R.ok();
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

}
