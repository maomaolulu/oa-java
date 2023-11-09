package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.domain.purchase.BizAnticipateGoods;
import com.ruoyi.activiti.service.BizAnticipateGoodsService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采购预提交
 * @author zh
 * @date 2021-02-10
 * @menu 采购预提交
 */
@RestController
@RequestMapping("anticipate_goods")
public class BizAnticipateGoodsController extends BaseController
{

	private final BizAnticipateGoodsService bizAnticipateGoodsService;


	@Autowired
	public BizAnticipateGoodsController( BizAnticipateGoodsService bizAnticipateGoodsService) {
		this.bizAnticipateGoodsService = bizAnticipateGoodsService;
	}


	/**
	 * 查询本人列表
	 *
	 * @return
	 */
	@GetMapping
	@ApiOperation(value = "查询本人列表")
	public R listAll(BizAnticipateGoods bizAnticipateGoods) {
		startPage();
		List<BizAnticipateGoods> bizAnticipateGoods1 = bizAnticipateGoodsService.listAll(bizAnticipateGoods);
		return result(bizAnticipateGoods1);
	}
	/**
	 * 修改
	 *
	 * @return
	 */
	@PostMapping(value = "update")
	@ApiOperation(value = "修改预提交")
	public R update(@RequestBody BizAnticipateGoods bizAnticipateGoods) {

		return R.data(bizAnticipateGoodsService.updateById(bizAnticipateGoods));

	}
	/**
	 * 新增
	 *
	 * @return
	 */
	@PostMapping("save")
	@ApiOperation(value = "新增预提交")
	public R save(@RequestBody List<BizAnticipateGoods> bizAnticipateGoodslist) {

		return R.data(bizAnticipateGoodsService.saveBizAnticipateGoods(bizAnticipateGoodslist));

	}

	/**
	 * 删除
	 */
	@PostMapping("delete")
	@ApiOperation(value = "删除预提交")
	public R remove(@RequestBody Long[] ids)
	{
		try {
			bizAnticipateGoodsService.deleteBizAnticipateGoods(ids);
			return R.ok();
		}catch (Exception e){
			System.err.println(e);
			return R.error();
		}

	}

}