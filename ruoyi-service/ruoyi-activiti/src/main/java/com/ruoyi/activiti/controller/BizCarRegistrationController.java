package com.ruoyi.activiti.controller;

import cn.hutool.core.exceptions.StatefulException;
import com.ruoyi.activiti.domain.car.BizCarRegistration;
import com.ruoyi.activiti.domain.car.BizReserveDetail;
import com.ruoyi.activiti.service.BizCarRegistrationService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.OperLog;
import com.ruoyi.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车辆查询
 *
 * @author zh
 * @date 2022-02-21
 * @menu 车辆查询
 */
@Api(tags = {"汽车管理"})
@RestController
@RequestMapping("biz_car_registration")
public class BizCarRegistrationController extends BaseController
{


	private final BizCarRegistrationService bizCarRegistrationService;


	@Autowired
	public BizCarRegistrationController( BizCarRegistrationService bizCarRegistrationService) {
		this.bizCarRegistrationService = bizCarRegistrationService;
	}


	/**
	 * 查询车辆数据分页
	 *
	 * @return
	 */
	@GetMapping("listAllPage")
	@ApiOperation(value = "查询车辆数据分页")
	public R listAll(BizCarRegistration bizCarRegistration) {
		startPage();
		List<BizCarRegistration> bizCarRegistrations = bizCarRegistrationService. listAllPage(bizCarRegistration);
		return result(bizCarRegistrations);
	}

	/**
	 * 查询可预约车辆
	 *
	 * @return
	 */
	@GetMapping("list_idle")
	@ApiOperation(value = "查询可预约车辆")
	public R listIdle(String goToDate,String goBackDate) {
		try {

			List<BizCarRegistration> bizCarRegistrations = bizCarRegistrationService. listIdle(goToDate,goBackDate);
			return result(bizCarRegistrations);
		}catch (Exception e){
			logger.error("查询可预约车辆",e);
			return R.error("查询可预约车辆失败");
		}
	}

	/**
	 * 查询车辆使用详情
	 *
	 * @return
	 */
	@GetMapping("list_detail")
	@ApiOperation(value = "查询车辆使用详情")
	public R listInfo(String plateNumber) {
		try {
			List<BizReserveDetail> reserveDetails = bizCarRegistrationService.getReserveDetail(plateNumber,null);
			return result(reserveDetails);
		}catch (Exception e){
			logger.error("查询车辆使用详情",e);
			return R.error("查询车辆使用详情失败");
		}
	}

	/**
	 * 获取手动还车列表
	 *
	 * @return
	 */
	@GetMapping("list_return_car")
	@ApiOperation(value = "获取手动还车列表")
	public R listReturnCar(String plateNumber) {
		try {
			List<BizReserveDetail> reserveDetails = bizCarRegistrationService.getReserveDetail(plateNumber,"1");
			return result(reserveDetails);
		}catch (Exception e){
			logger.error("获取手动还车列表",e);
			return R.error("获取手动还车列表失败");
		}
	}



	/**
	 * 查询车辆最新里程数
	 *
	 * @return
	 */
	@GetMapping("last_mileage")
	@ApiOperation(value = "查询车辆最新里程数")
	public R getLatestMileage(String plateNumber) {
		try {
			return R.data(bizCarRegistrationService.getLatestMileage(plateNumber));
		}catch (Exception e){
			logger.error("获取最新里程数失败",e);
			return R.error("获取最新里程数失败");
		}
	}

	/**
	 * 手动还车
	 *
	 * @return
	 */
	@GetMapping("return_car")
	@ApiOperation(value = "手动还车")
	public R returnCar(Long id,String remark) {
		try {
			bizCarRegistrationService.returnCar(id,remark);
			return R.ok("还车成功");
		}catch (Exception e){
			logger.error("手动还车失败",e);
			return R.error("手动还车失败");
		}
	}

	/**
	 * 新增车辆
	 * @param bizCarRegistration
	 * @return
	 */
	@PostMapping("save")
	@ApiOperation(value = "车辆管理新增")
	@OperLog(title = "车辆管理新增", businessType = BusinessType.INSERT)
	public R save(@RequestBody BizCarRegistration bizCarRegistration){
		try {
			return  R.ok("新增成功",bizCarRegistrationService.save(bizCarRegistration));
		}catch (StatefulException e){
			return R.error(e.getMessage());
		}catch (Exception e){
			return R.error("新增失败");
		}

	}

	/**
	 * 车辆修改
	 * @param bizCarRegistration
	 * @return
	 */
	@PostMapping("update")
	@ApiOperation(value = "车辆管理修改")
	@OperLog(title = "车辆管理修改", businessType = BusinessType.UPDATE)
	public R update(@RequestBody BizCarRegistration bizCarRegistration){
		try {
			return  R.ok("修改成功",bizCarRegistrationService.update(bizCarRegistration));
		}catch (Exception e){
			return R.error("修改失败");
		}

	}

	/**
	 * 删除车辆
	 * @param ids
	 * @return
	 */
	@PostMapping("delete")
	@ApiOperation(value = "删除车辆管理")
	@OperLog(title = "删除车辆管理", businessType = BusinessType.DELETE)
	public R delete( @RequestBody Long[] ids ){
		try {
			bizCarRegistrationService.delete(ids);
			return R.ok("删除成功");
		}catch (Exception e){
			return R.error("删除失败");
		}
	}



}