package com.ruoyi.training.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.training.entity.TraPostCertificate;
import com.ruoyi.training.entity.vo.SysUserVo;
import com.ruoyi.training.service.TraPostCertificateService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 证书岗位表
 *
 * @author hjy
 * @date 2022/6/21 13:45
 */
@RestController
@RequestMapping("/training/post")
public class TraPostCertificateController extends BaseController {

    private final TraPostCertificateService postCertificateService;

    public TraPostCertificateController(TraPostCertificateService postCertificateService) {
        this.postCertificateService = postCertificateService;
    }


    /**
     * 查询证书岗位列表
     */
    @GetMapping("/list")
    public R list(TraPostCertificate postCertificate) {
        try {
            pageUtil();
            List<TraPostCertificate> list = postCertificateService.selectTraPostCertificateUserList(postCertificate);
            return resultData(list);
        } catch (Exception e) {
            logger.error("查询岗位列表失败，异常信息：" + e);
            return R.error("查询岗位列表失败");
        }
    }

    /**
     * 获取证书岗位详细信息
     */
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        try {
            if (null == id) {
                return R.error("岗位id不能为空！");
            }
            return R.data(postCertificateService.selectTraPostCertificateById(id));
        } catch (Exception e) {
            logger.error("查询岗位失败，异常信息：" + e);
            return R.error("查询岗位失败");
        }

    }


    /**
     * 新增证书岗位
     */
    @PostMapping
    public R add(@RequestBody TraPostCertificate postCertificate) {
        try {
            if (postCertificateService.insertTraPostCertificate(postCertificate) > 0) {
                return R.ok();
            }
            return R.error("岗位新增失败！");
        } catch (Exception e) {
            logger.error("新增岗位失败，异常信息：" + e);
            return R.error("新增岗位失败");
        }
    }

    /**
     * 修改证书岗位
     */
    @OperLog(title = "证书岗位", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody TraPostCertificate postCertificate) {
        try {
            if (null == postCertificate) {
                return R.error("岗位信息不能为空！");
            }
            if (postCertificateService.updateTraPostCertificate(postCertificate) > 0) {
                return R.ok("岗位更新成功！");
            }
            return R.error("岗位更新失败！");
        } catch (Exception e) {
            logger.error("修改岗位失败，异常信息：" + e);
            return R.error("修改岗位失败");
        }
    }

    /**
     * 删除证书岗位
     */
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {
        try {
            if (null == ids || ids.length < 1) {
                return R.error("要删除的证书岗位id不能为空！");
            }
            if (postCertificateService.deleteTraPostCertificateByIds(ids) > 0) {
                return R.ok("岗位删除成功！");
            }
            return R.error("岗位删除失败！");
        } catch (Exception e) {
            logger.error("岗位删除失败，异常信息：" + e);
            return R.error("岗位删除失败");
        }
    }


    /**
     * 岗位批量绑定用户
     *
     * @param postId  岗位id
     * @param userIds 用户id集合
     * @return 状态
     */
    @PostMapping("/bindPost")
    public R bindPost(Long postId, String userIds) {
        try {
            if (null == postId || userIds == null || "".equals(userIds)) {
                return R.error("岗位id或者用户id不能为空！");
            }
            if (postCertificateService.insertPostUsers(postId, userIds) > 0) {
                return R.ok("岗位绑定成功");
            }
            return R.error("岗位绑定失败！");
        } catch (Exception e) {
            logger.error("岗位绑定失败，异常信息：" + e);
            return R.error("岗位绑定失败");
        }
    }

    /**
     * 岗位批量解除绑定用户
     *
     * @param userIds 用户id集合
     * @return 状态
     */
    @PostMapping("/unbindPost")
    public R unbindPost(String userIds) {
        try {
            if (userIds == null) {
                return R.error("用户id不能为空！");
            }
            if (postCertificateService.unbindPost(userIds) > 0) {
                return R.ok("岗位解除绑定成功");
            }
            return R.error("岗位解除绑定失败！");
        } catch (Exception e) {
            logger.error("岗位解除绑定失败，异常信息：" + e);
            return R.error("岗位解除绑定失败");
        }
    }

    /**
     * 获取公司人员信息
     */
    @GetMapping("/getUser")
    public R getUserList(Long companyId) {
        try {
            List<SysUserVo> userList = postCertificateService.getUserList(companyId);
            return R.data(userList);
        } catch (Exception e) {
            return R.error("人员查询失败");
        }
    }
    /**
     * 获取多公司人员信息
     */
    @GetMapping("/getUsers/{ids}")
    public R getUserList(@PathVariable Long[] ids) {
        if (null == ids || ids.length < 1) {
            return R.error("公司ids不能为空");
        }

        final Map<Long, List<SysUserVo>> map = postCertificateService.getUserLists(Arrays.asList(ids));
        return R.data(map);

    }


}
