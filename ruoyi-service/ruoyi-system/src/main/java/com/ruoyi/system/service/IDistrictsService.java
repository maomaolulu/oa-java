package com.ruoyi.system.service;

import com.ruoyi.system.domain.Districts;

import java.util.List;
import java.util.Map;

/**
 * 地区 服务层
 * 
 * @author ruoyi
 * @date 2018-12-19
 */
public interface IDistrictsService 
{
	/**
     * 查询地区信息
     * 
     * @param id 地区ID
     * @return 地区信息
     */
	public Districts selectDistrictsById(Integer id);
	
	/**
     * 查询地区列表
     * 
     * @param districts 地区信息
     * @return 地区集合
     */
	public List<Districts> selectDistrictsList(Districts districts);
	
	/**
     * 新增地区
     * 
     * @param districts 地区信息
     * @return 结果
     */
	public int insertDistricts(Districts districts);
	
	/**
     * 修改地区
     * 
     * @param districts 地区信息
     * @return 结果
     */
	public int updateDistricts(Districts districts);
		
	/**
     * 删除地区信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	public int deleteDistrictsByIds(String ids);
	/**
	 * 查询地区树
	 * @return
	 */
	Map<String,Object> getTree();

	/**
	 * 添加地区
	 *
	 * @param districts 地区名称 父类id
	 * @return result
	 */
	boolean add(Districts districts);

	/**
	 * 编辑地区
	 *
	 * @param districts 地区id...
	 * @return result
	 */
	boolean edit(Districts districts);

	/**
	 * 删除地区
	 *
	 * @param ids 地区id集合
	 * @return result
	 */
	boolean delete(Integer[] ids);
}
