package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.SysStatusConfig;
import com.ruoyi.activiti.service.SysStatusConfigService;
import com.ruoyi.activiti.test.BizBusinessTest;
import com.ruoyi.common.auth.annotation.HasPermissions;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 静态文件管理表
 *
 * @author zh
 * @date 2021-12-31
 * @menu 静态文件管理表
 */
@RestController
@RequestMapping("sys_status_config")
public class SysStatusConfigController extends BaseController
{

	private final SysStatusConfigService sysStatusConfigService;

	@Resource
	BizBusinessTest bizBusinessTest;


	@Autowired
	public SysStatusConfigController( SysStatusConfigService sysStatusConfigService) {
		this.sysStatusConfigService = sysStatusConfigService;
	}
	@GetMapping("test")
	public void allIn(){
		bizBusinessTest.test();
	}
	@GetMapping("test1")
	public void allIn1(){
		bizBusinessTest.test1();
	}
	@GetMapping("test2")
	public void allIn2(){
		bizBusinessTest.changeMongodb();
	}
	@GetMapping("test3")
	public void oldToNew(){
		bizBusinessTest.syncOldData();
	}


	/**
	 * 获取静态文件地址
	 *
	 * @return
	 */
	@GetMapping("status")
	public R getFielPathName() {
		 SysStatusConfig sysStatusConfig = sysStatusConfigService.selectFielPathName();
		return R.data(sysStatusConfig);
	}
	/**
	 * 获取静态文件地址
	 *
	 * @return
	 */
	@PostMapping
	@HasPermissions("status:style:update")
	public R update(@RequestBody SysStatusConfig sysStatusConfig) {

		return R.data(sysStatusConfigService.updateById(sysStatusConfig));

	}



}