package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.redis.util.RedisUtils;
import com.ruoyi.system.domain.Districts;
import com.ruoyi.system.mapper.DistrictsMapper;
import com.ruoyi.system.service.IDistrictsService;
import com.ruoyi.system.util.SystemUtil;
import com.ruoyi.system.vo.AreaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 地区 服务层实现
 *
 * @author ruoyi
 * @date 2018-12-19
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
public class DistrictsServiceImpl implements IDistrictsService {
    @Autowired
    private DistrictsMapper districtsMapper;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询地区信息
     *
     * @param id 地区ID
     * @return 地区信息
     */
    @Override
    public Districts selectDistrictsById(Integer id) {
        return districtsMapper.selectDistrictsById(id);
    }

    /**
     * 查询地区列表
     *
     * @param districts 地区信息
     * @return 地区集合
     */
    @Override
    public List<Districts> selectDistrictsList(Districts districts) {
        // 查询未删除的
        districts.setDelFlag(0);
        return districtsMapper.selectDistrictsList(districts);
    }

    /**
     * 新增地区
     *
     * @param districts 地区信息
     * @return 结果
     */
    @Override
    public int insertDistricts(Districts districts) {
        return districtsMapper.insertDistricts(districts);
    }

    /**
     * 修改地区
     *
     * @param districts 地区信息
     * @return 结果
     */
    @Override
    public int updateDistricts(Districts districts) {
        return districtsMapper.updateDistricts(districts);
    }

    /**
     * 删除地区对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteDistrictsByIds(String ids) {
        return districtsMapper.deleteDistrictsByIds(Convert.toStrArray(ids));
    }

    /**
     * 查询地区树
     *
     * @return
     */
    @Override
    public Map<String, Object> getTree() {
        Map<String, Object> map = new HashMap<>(2);
        String redisStr = redisUtils.get("areaInfo");
        if (StrUtil.isBlank(redisStr)) {
            Districts district = new Districts();
            district.setDelFlag(0);
            List<Districts> districts = districtsMapper.selectDistrictsList(district);
            List<AreaVo> areaVoList = new ArrayList<>();
            List<AreaVo> trees = new ArrayList<>();
            districts.stream().forEach(d -> {
                areaVoList.add(new AreaVo(d.getId(), d.getPid(), d.getExtName()));
                trees.add(new AreaVo(d.getId(), d.getPid(), d.getExtName()));
            });
            map.put("list", trees);
            map.put("tree", buildAreaTree(areaVoList));
            String str = JSONObject.toJSONString(map);
            redisUtils.set("areaInfo", str);
        } else {
            JSONObject json = JSONObject.parseObject(redisStr);
            map = JSONObject.parseObject(json.toJSONString(), new TypeReference<Map<String, Object>>() {
            });
        }
        return map;
    }

    /**
     * 添加地区
     *
     * @param districts 地区名称 父类id
     * @return result
     */
    @Override
    public boolean add(Districts districts) {
        String name = districts.getName();
        // 设置拼音
        districts.setPinyin(PinyinUtil.getPinyin(name));
        // 设置拼音简称
        districts.setPinyinShor(PinyinUtil.getFirstLetter(name, ""));
        districts.setCreateTime(new Date());
        districts.setOperator(SystemUtil.getUserNameCn());
        if (districtsMapper.insertDistricts(districts) != 0) {
            redisUtils.delete("areaInfo");
            return true;
        }
        return false;
    }

    /**
     * 编辑地区
     *
     * @param districts 地区id
     * @return result
     */
    @Override
    public boolean edit(Districts districts) {
        //校验数据库中是否已存在
        Districts district = new Districts();
        district.setId(districts.getId());
        district.setExtName(districts.getExtName());
        if (districtsMapper.checkUnique(district) != 0) {
            throw new RuntimeException("该地区已存在");
        }
        String name = districts.getName();
        // 设置拼音
        districts.setPinyin(PinyinUtil.getPinyin(name));
        // 设置拼音简称
        districts.setPinyinShor(PinyinUtil.getFirstLetter(name, ""));
        districts.setUpdateTime(new Date());
        districts.setOperator(SystemUtil.getUserNameCn());
        if (districtsMapper.updateDistricts(districts) != 0) {
            redisUtils.delete("areaInfo");
            return true;
        }
        return false;
    }

    /**
     * 删除地区
     *
     * @param ids 地区id集合
     * @return result
     */
    @Override
    public boolean delete(Integer[] ids) {
        for (Integer id : ids) {
            Districts district = new Districts();
            district.setId(id);
            district.setDelFlag(1);
            if (districtsMapper.updateDistricts(district) == 0) {
                throw new RuntimeException("删除失败，id=" + id);
            }
        }
        return true;
    }

    private List<AreaVo> buildAreaTree(List<AreaVo> regions) {
        List<AreaVo> tree = new ArrayList<>();
        for (Iterator<AreaVo> it = regions.iterator(); it.hasNext(); ) {
            AreaVo region = (AreaVo) it.next();
            if (region.getPid() == 0) {
                recursionFn(regions, region);
                tree.add(region);
            }
        }
        return tree;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param region
     */
    private void recursionFn(List<AreaVo> list, AreaVo region) {
        // 得到子节点列表
        List<AreaVo> childList = getChildList(list, region);
        region.setChildren(childList);
        for (AreaVo child : childList) {
            if (hasChild(list, child)) {
                //判断是否有子节点
                Iterator<AreaVo> it = childList.iterator();
                while (it.hasNext()) {
                    AreaVo n = (AreaVo) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     *
     * @param list
     * @param region
     * @return
     */
    private List<AreaVo> getChildList(List<AreaVo> list, AreaVo region) {
        List<AreaVo> tlist = new ArrayList<AreaVo>();
        Iterator<AreaVo> it = list.iterator();
        while (it.hasNext()) {
            AreaVo region1 = (AreaVo) it.next();
            if (region1.getPid().longValue() == region.getId().longValue()) {
                tlist.add(region1);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     *
     * @param list
     * @param region
     * @return
     */
    private boolean hasChild(List<AreaVo> list, AreaVo region) {
        return getChildList(list, region).size() > 0 ? true : false;
    }


}
