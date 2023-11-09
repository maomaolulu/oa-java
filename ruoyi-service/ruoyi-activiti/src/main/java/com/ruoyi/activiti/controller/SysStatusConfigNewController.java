package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.SysStatusConfig;
import com.ruoyi.activiti.service.SysStatusConfigService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 静态文件管理表(修改)
 *
 * @author zh
 * @date 2023-02-10
 * @menu 静态文件管理表(修改)
 */
@RestController
@RequestMapping("sys_status_config_new")
public class SysStatusConfigNewController extends BaseController
{

	private final SysStatusConfigService sysStatusConfigService;



	@Autowired
	public SysStatusConfigNewController(SysStatusConfigService sysStatusConfigService) {
		this.sysStatusConfigService = sysStatusConfigService;
	}


//	/**
//	 * 获取静态文件地址
//	 *
//	 * @return
//	 */
//	@GetMapping("status")
//	public R getFielPathName() {
//		 SysStatusConfig sysStatusConfig = sysStatusConfigService.selectFielPathName();
//		return R.data(sysStatusConfig);
//	}
	/**
	 * 修改静态文件类型
	 *
	 * @return
	 */
	@PostMapping( "update")
	public R update(@RequestBody SysStatusConfig sysStatusConfig) {

		return R.data(sysStatusConfigService.updateById(sysStatusConfig));

	}



}