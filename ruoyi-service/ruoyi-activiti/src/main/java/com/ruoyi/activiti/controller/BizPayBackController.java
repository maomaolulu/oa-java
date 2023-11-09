package com.ruoyi.activiti.controller;

import cn.hutool.core.date.DateUtil;
import com.ruoyi.activiti.domain.fiance.BizPayBack;
import com.ruoyi.activiti.service.BizPayBackService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 回款管理
 *
 * @author zh
 * @date 2021-12-26
 * @menu 回款管理
 */
@RestController
@RequestMapping("pay_back")
public class BizPayBackController extends BaseController
{

	private final BizPayBackService bizPayBackService;

	@Autowired
	public BizPayBackController( BizPayBackService bizPayBackService) {
		this.bizPayBackService = bizPayBackService;
	}




	/**
	 * 获取查询回款管理编号
	 *
	 * @return
	 */
	@GetMapping("code")
	public R getCode() {
		Date date = new Date();
		String timestamp = String.valueOf(date.getTime());
		String today = DateUtil.today();
		today = today.replace("-", "");
		return R.data("HT" + today + timestamp);
	}

	/**
	 * 查询回款管理列表
	 */
	@ApiOperation(value = "查询回款管理列表", notes = "查询回款管理列表")
	@GetMapping("list")
	public R list(BizPayBack dto)
	{
		startPage();
        return result(bizPayBackService.selectBizPayBack(dto));
	}


	/**
	 * 新增回款管理
	 */
	@ApiOperation(value = "新增回款管理", notes = "新增回款管理")
	@PostMapping("save")
	public R addSave(@RequestBody BizPayBack bizPayBack)
	{

		return bizPayBackService.insertBizPayBack(bizPayBack);
	}

	/**
	 * 修改收款账号
	 */
	@ApiOperation(value = "修改回款管理", notes = "")
	@PostMapping("update")
	public R update(@RequestBody BizPayBack bizPayBack)
	{
		return bizPayBackService.update(bizPayBack);
	}
	/**
	 * 删除收款账号
	 */
	@OperLog
	@ApiOperation(value = "删除回款管理", notes = "")
	@PostMapping("delete")
	public R remove(@RequestBody Long[] ids)
	{
		return bizPayBackService.delete(ids);
	}

}