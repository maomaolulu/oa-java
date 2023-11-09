package com.ruoyi.daily.service.impl.statistics;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.daily.domain.asset.AaSku;
import com.ruoyi.daily.domain.asset.Asset;
import com.ruoyi.daily.domain.statistics.*;
import com.ruoyi.daily.mapper.statistics.AssetChangeMapper;
import com.ruoyi.daily.service.statistics.AssetChangeService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
/**
 * @author wuYang
 * @date 2022/8/30 9:21
 */
@Service
public class AssetChangeServiceImpl implements AssetChangeService {

    @Resource
    AssetChangeMapper assetChangeMapper;
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 各分公司固定资产变化趋势
     */
    @Override
    public List<AssetChangeVO> getAssetChange(AssetChangeDTO dto, LocalDate startTime, LocalDate endTime) {
        // 封装where请求
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        // 前端传null就不区分
        queryWrapper.eq(dto.getCompanyId() != null, "company_id", dto.getCompanyId());
        queryWrapper.eq(dto.getDeptId() != null, "dept_id", dto.getDeptId());
        queryWrapper.eq(dto.getState() != null, "state", dto.getState());
        // 移交时间区间
        if (startTime != null && endTime != null) {
            queryWrapper.between("purchase_time", startTime, endTime);
        }
        // 按照采购价计算
        if (dto.getDimension() == null || dto.getDimension() == 0) {
            return assetChangeMapper.getAssetChangeList(queryWrapper);
        } else {
            return assetChangeMapper.getAssetChangeListByValue(queryWrapper);
        }

    }

    /**
     * 各分公司固定资产占比
     */
    @Override
    public List<AssetProportionVO> getAssetProportion(AssetProportionDTO dto, LocalDate startTime, LocalDate endTime) {
        // 封装where请求
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        // 前端传null就不区分
        queryWrapper.eq(dto.getCompanyId() != null, "a.company_id", dto.getCompanyId());
        queryWrapper.eq(dto.getState() != null, "a.state", dto.getState());
        // 如果为空 就展示全部公司排名
        if (dto.getCompanyId() == null) {

            queryWrapper.groupBy("company_id");
        } else {
            // 展示所有的部门 用部门来分组
            queryWrapper.groupBy("a.dept_id");
        }
        // 移交时间区间
        if (startTime != null && endTime != null) {
            queryWrapper.between("a.purchase_time", startTime, endTime);
        }
        // 按照采购价计算
        if (dto.getDimension() == null || dto.getDimension() == 0) {
            // 应业务查询需求分开 展示部门
            if (dto.getCompanyId() != null) {
                return assetChangeMapper.getProportion1(queryWrapper);
            } else {
                return assetChangeMapper.getProportion(queryWrapper);
            }


        } else {
            if (dto.getCompanyId() != null) {
                return assetChangeMapper.getProportionByValue1(queryWrapper);
            } else {
                return assetChangeMapper.getProportionByValue(queryWrapper);
            }
        }
    }

    /**
     * 流动资产和固定资产对比
     */
    @Override
    public AssetContrastVO getContrast(AssetContrastDTO dto, LocalDate startTime, LocalDate endTime) {
        AssetContrastVO result = new AssetContrastVO();
        // 封装where请求
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        // 前端传null就不区分
        queryWrapper.eq(dto.getCompanyId() != null, "p.company_id", dto.getCompanyId());
        // 不比部门了
//        queryWrapper.eq(dto.getDeptId() != null, "p.dept_id", dto.getDeptId());
        queryWrapper.eq(dto.getState() != null, "p.state", dto.getState());
        // 移交时间区间
        if (startTime != null && endTime != null) {
            queryWrapper.between("p.create_time", startTime, endTime);
        }
        // 封装where请求 sku
        QueryWrapper<AaSku> skuQueryWrapper = new QueryWrapper<>();
        // 前端传null就不区分
        skuQueryWrapper.eq(dto.getCompanyId() != null, "p.company_id", dto.getCompanyId());
//        skuQueryWrapper.eq(dto.getDeptId() != null, "k.dept_id", dto.getDeptId());
        skuQueryWrapper.eq(dto.getState() != null, "k.state", dto.getState());
        // 移交时间区间
        if (startTime != null && endTime != null) {
            skuQueryWrapper.between("k.create_time", startTime, endTime);
        }
        // 按照采购价计算
        if (dto.getDimension() == null || dto.getDimension() == 0) {
            Long totalAssetPrice = assetChangeMapper.getTotalAssetPrice(queryWrapper);
            result.setAsset(totalAssetPrice == null ? 0 : totalAssetPrice);
            Long flowTotal = assetChangeMapper.getFlowTotal(skuQueryWrapper);
            result.setFlowAsset(flowTotal == null ? 0 : flowTotal);
            return result;

        } else {
            Long totalAssetPrice = assetChangeMapper.getTotalAssetPriceByValue(queryWrapper);
            result.setAsset(totalAssetPrice == null ? 0 : totalAssetPrice);
            Long flowTotal = assetChangeMapper.getFlowTotalByValue(skuQueryWrapper);
            result.setFlowAsset(flowTotal == null ? 0 : flowTotal);
            return result;

        }
    }

    /**
     * 各种资产变化图
     */
    @Override
    public TotalListVO getChangeList(AssetContrastDTO dto, LocalDate startTime, LocalDate endTime) {
        TotalListVO totalListVO = new TotalListVO();
        // 封装where请求 asset
        QueryWrapper<Asset> queryWrapper = new QueryWrapper<>();
        // 前端传null就不区分
        queryWrapper.eq(dto.getCompanyId() != null, "p.company_id", dto.getCompanyId());
        queryWrapper.eq(dto.getDeptId() != null, "p.dept_id", dto.getDeptId());
        queryWrapper.eq(dto.getState() != null, "p.state", dto.getState());
        // 移交时间区间
        if (startTime != null && endTime != null) {
            queryWrapper.between("p.create_time", startTime, endTime);
        }
        // 封装where请求 sku
        QueryWrapper<AaSku> skuQueryWrapper = new QueryWrapper<>();
        // 前端传null就不区分
        skuQueryWrapper.eq(dto.getCompanyId() != null, "p.company_id", dto.getCompanyId());
        skuQueryWrapper.eq(dto.getDeptId() != null, "k.dept_id", dto.getDeptId());
        skuQueryWrapper.eq(dto.getState() != null, "k.state", dto.getState());
        // 移交时间区间
        if (startTime != null && endTime != null) {
            skuQueryWrapper.between("k.create_time", startTime, endTime);
        }
        // 按照采购价计算
        if (dto.getDimension() == null || dto.getDimension() == 0) {
            // 计算固定资产
            List<AssetChangeVO> assetListMonth = assetChangeMapper.getAssetListMonth(queryWrapper);
            deleteNull(assetListMonth);
            // 计算流动资产
            Future<List<AssetChangeVO>> submit = threadPoolTaskExecutor.submit(new Callable<List<AssetChangeVO>>() {
                /**
                 * Computes a result, or throws an exception if unable to do so.
                 *
                 * @return computed result
                 * @throws Exception if unable to compute a result
                 */
                @Override
                public List<AssetChangeVO> call() throws Exception {
                    return assetChangeMapper.getFlowListMonth(skuQueryWrapper);
                }
            });
            totalListVO.setAssetList(assetListMonth);
            List<AssetChangeVO> flowListMonth = null;
            try {
                flowListMonth = submit.get();

            }  catch (Exception e) {
                e.printStackTrace();
            }

            deleteNull(flowListMonth );
            totalListVO.setFlowAssetList(flowListMonth);
            List<AssetChangeVO> total;
            if (assetListMonth.size() < flowListMonth.size()) {
                total = getTotal(assetListMonth, flowListMonth);
            } else {
                total = getTotal(flowListMonth, assetListMonth);
            }
            totalListVO.setTotalAssetList(total);

        } else {
            // 固定资产
            List<AssetChangeVO> assetListMonth = assetChangeMapper.getAssetListMonthByValue(queryWrapper);
            deleteNull(assetListMonth);
            // 流动资产
            List<AssetChangeVO> flowListMonth = assetChangeMapper.getFlowListMonthByValue(skuQueryWrapper);
            deleteNull(flowListMonth);
            totalListVO.setAssetList(assetListMonth);
            totalListVO.setFlowAssetList(flowListMonth);
            List<AssetChangeVO> total;
            if (assetListMonth.size() < flowListMonth.size()) {
                total = getTotal(assetListMonth, flowListMonth);
            } else {
                total = getTotal(flowListMonth, assetListMonth);
            }
            totalListVO.setTotalAssetList(total);
        }
        return totalListVO;

    }

    public List<AssetChangeVO> getTotal(List<AssetChangeVO> smallSize, List<AssetChangeVO> bigSize) {
        // 总资产变化
        HashMap<String, BigDecimal> map = new HashMap<>();
        List<AssetChangeVO> total = new ArrayList<>();
        smallSize.forEach(e -> {
            if (e != null) {
                map.put(e.getMonth(), e.getTotal() == null ? BigDecimal.ZERO : e.getTotal());
            }
        });
        bigSize.forEach(e -> {
            if (e != null) {
                AssetChangeVO vo = new AssetChangeVO();
                if (map.containsKey(e.getMonth())) {

                    vo.setMonth(e.getMonth());
                    if (e.getTotal() == null) {
                        vo.setTotal(map.get(e.getMonth()));
                    } else {
                        vo.setTotal(e.getTotal().add(map.get(e.getMonth())));

                    }
                    total.add(vo);
                } else {
                    if (e.getMonth() != null) {
                        vo.setMonth(e.getMonth());
                        vo.setTotal(e.getTotal());
                        total.add(vo);
                    }

                }
            }
        });
        return total;
    }

    public void deleteNull(List<AssetChangeVO> list) {
        list.removeIf(new Predicate<AssetChangeVO>() {
            @Override
            public boolean test(AssetChangeVO assetChangeVO) {
                if (assetChangeVO == null) {
                    return true;
                } else return assetChangeVO.getMonth() == null;
            }
        });
    }
}
