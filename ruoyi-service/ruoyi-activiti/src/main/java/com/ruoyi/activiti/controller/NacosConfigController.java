package com.ruoyi.activiti.controller;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.properties.CcProperties;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.feign.RemoteDeptService;
import com.ruoyi.system.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * nacos动态配置信息
 *
 * @menu nacos动态配置信息
 */
@RestController
@RequestMapping("nacos")
public class NacosConfigController extends BaseController {
    private final CcProperties ccProperties;
    private final RemoteUserService remoteUserService;
    private final RemoteDeptService remoteDeptService;

    @Autowired
    public NacosConfigController(CcProperties ccProperties, RemoteUserService remoteUserService, RemoteDeptService remoteDeptService) {
        this.ccProperties = ccProperties;
        this.remoteUserService = remoteUserService;
        this.remoteDeptService = remoteDeptService;
    }

    /**
     * 默认抄送人
     *
     * @param type 流程定义key 如：采购 purchase
     * @return 用户id数组
     */
    @GetMapping("cc")
    public R test(String type) {
        try {
            Long userId = SystemUtil.getUserId();
            SysUser sysUserNow = remoteUserService.selectSysUserByUserId(userId);
            String deptIdNow = String.valueOf(sysUserNow.getDeptId());
            Map<String, Object> belongCompany2 = remoteDeptService.getBelongCompany2(sysUserNow.getDeptId());
            String companyId = belongCompany2.get("companyId").toString();
            List<Map<String, Object>> sysUsers = new ArrayList<>();
//            if("168".equals(companyId)){
//                sysUsers.add(new HashMap<String,Object>(2){{
//                    put("userName", "袁伟明");
//                    put("userId", 586);
//                }});
//            }
            String roleIds = "";
            Set<Long> longs = new HashSet<>();

            // 财务
            if ("businessMoney".equals(type) || "payment".equals(type) || "reimburse".equals(type) || "review".equals(type)) {
//                roleIds = ccProperties.get("idValue");
                roleIds = "348,342";
                if (StrUtil.isNotBlank(roleIds)) {
                    String[] split = roleIds.split(",");
                    for (String s : split) {
                        longs.add(Long.valueOf(s));
                    }
                }
                for (Long aLong : longs) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(aLong);
                    // 过滤离职人员
                    if ("1".equals(sysUser.getStatus())) {
                        continue;
                    }
                    sysUsers.add(new HashMap<String, Object>(2) {{
                        put("userName", sysUser.getUserName());
                        put("userId", sysUser.getUserId());
                    }});
                }
                // 需求反馈
            } else if ("feedback".equals(type)) {
//                roleIds = ccProperties.get("idValue");
                roleIds = "348,26";
                if ("118".equals(companyId) || "119".equals(companyId)) {
                    roleIds += ",5";
                }
                if (StrUtil.isNotBlank(roleIds)) {
                    String[] split = roleIds.split(",");
                    for (String s : split) {
                        longs.add(Long.valueOf(s));
                    }
                }
                for (Long aLong : longs) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(aLong);
                    // 过滤离职人员
                    if ("1".equals(sysUser.getStatus())) {
                        continue;
                    }
                    sysUsers.add(new HashMap<String, Object>(2) {{
                        put("userName", sysUser.getUserName());
                        put("userId", sysUser.getUserId());
                    }});
                }
            } else if ("pr-amount".equals(type) || "pr_amount".equals(type)) {
                // 5-王勇 348-刘大海 37-胡登科
                roleIds = "5,348";
                if (StrUtil.isNotBlank(roleIds)) {
                    String[] split = roleIds.split(",");
                    for (String s : split) {
                        longs.add(Long.valueOf(s));
                    }
                }
                for (Long aLong : longs) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(aLong);
                    // 过滤离职人员
                    if ("1".equals(sysUser.getStatus())) {
                        continue;
                    }
                    sysUsers.add(new HashMap<String, Object>(2) {{
                        put("userName", sysUser.getUserName());
                        put("userId", sysUser.getUserId());
                    }});
                }
            } else if ("contract-project".equals(type)) {
                roleIds = "28";

                if (StrUtil.isNotBlank(roleIds)) {
                    String[] split = roleIds.split(",");
                    for (String s : split) {
                        longs.add(Long.valueOf(s));
                    }
                }
                for (Long aLong : longs) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(aLong);
                    // 过滤离职人员
                    if ("1".equals(sysUser.getStatus())) {
                        continue;
                    }
                    sysUsers.add(new HashMap<String, Object>(2) {{
                        put("userName", sysUser.getUserName());
                        put("userId", sysUser.getUserId());
                    }});
                }
            } else {
                // 其他
                roleIds = ccProperties.get(type);
//                switch (type){
//                    case "purchase":
//                        roleIds = "82,72,14";
//                        break;
//                    case "seal":
//                        roleIds = "82";
//                        break;
//                    default:
//                        break;
//                }
                if (StrUtil.isNotBlank(roleIds)) {
                    longs = remoteUserService.selectUserIdsHasRoles(roleIds);
                }
                for (Long aLong : longs) {
                    SysUser sysUser = remoteUserService.selectSysUserByUserId(aLong);
                    // 过滤离职人员
                    if ("1".equals(sysUser.getStatus())) {
                        continue;
                    }
                    String otherDeptId = sysUser.getOtherDeptId();
                    if (otherDeptId != null && otherDeptId.indexOf(deptIdNow) >= 0) {
                        sysUsers.add(new HashMap<String, Object>(2) {{
                            put("userName", sysUser.getUserName());
                            put("userId", sysUser.getUserId());
                        }});
                    }
                }
            }
            if (longs.isEmpty()) {
                return R.data(sysUsers);
            }
            // 用印 杭州安联默认抄送王勇
            if ("seal".equals(type) && companyId.equals("115")) {
                sysUsers.add(new HashMap<String, Object>(2) {{
                    put("userName", "王勇");
                    put("userId", 5);
                }});
            }
            // 用车 杭州安联默认抄送孙春花
            if ("carApply".equals(type) && companyId.equals("115")) {
                sysUsers.clear();
                sysUsers.add(new HashMap<String, Object>(1) {{
                    put("userName", "孙春花");
                    put("userId", 6);
                }});
            }
            return R.data(sysUsers);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return R.error("查询失败");
        }

    }
}
