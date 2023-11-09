package com.ruoyi.ehs.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.ehs.domain.signin.BizSignIn;
import com.ruoyi.ehs.domain.signin.dto.BizSignInAddDTO;
import com.ruoyi.ehs.domain.signin.dto.BizSignInDTO;
import com.ruoyi.ehs.feign.RemoteFile;
import com.ruoyi.ehs.mapper.BizSignInMapper;
import com.ruoyi.ehs.service.BizSignInService;
import com.ruoyi.system.util.SystemUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * @author wuYang
 * @date 2022/9/19 15:42
 */
@Service
public class BizSignInServiceImpl implements BizSignInService {
    public static final String key = "bc1d930157bc84407375987cc0eb9089";
    public static final String url = "https://restapi.amap.com/v3/geocode/regeo";

    @Resource
    BizSignInMapper bizSignInMapper;

    @Resource
    RemoteFile remoteFile;


    /**
     * 新增
     *
     */
    @Transactional
    @Override
    public void add(BizSignInAddDTO dto) {

        BizSignIn bizSignIn = new BizSignIn();
        BeanUtils.copyProperties(dto,bizSignIn);

        // 调用高德api
        String address = getMap(dto.getLng(), dto.getLat());
        List<String> img = dto.getImg();
        String image = String.join(",", img);
        bizSignIn.setTime(LocalDateTime.now())
                .setCompanyId(SystemUtil.getCompanyId())
                .setDeptId(SystemUtil.getDeptId())
                .setUserId(SystemUtil.getUserId())
                .setUserName(SystemUtil.getUserNameCn())
                .setImg(image)
                .setAddress(address);

        bizSignInMapper.insert(bizSignIn);

        List<String> timestamp = dto.getTimestamp();
        for (String s : timestamp) {
            remoteFile.update(bizSignIn.getId(),s,"sign-in");
        }

    }

    /**
     * 高德经纬度转换
     * @param lng 经度
     * @param lat 维度
     * @return 地址
     */
    public  String getMap(BigDecimal lng, BigDecimal lat) {
        String ans = "";
        String latString = lat.toString();
        String lngString = lng.toString();
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("key",key);
        parameters.put("location",lngString+","+latString);
        String result = HttpUtil.get(url, parameters);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject regeocode = jsonObject.getJSONObject("regeocode");
        String formatted_address = regeocode.get("formatted_address").toString();
        ans+=formatted_address;
        return ans;

    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {

    }

    /**
     * 查询
     */
    @Override
    public List<BizSignIn> selectPage(BizSignInDTO dto, LocalDateTime start,LocalDateTime end) {
        QueryWrapper<BizSignIn> bizSignInQueryWrapper = new QueryWrapper<>();
        bizSignInQueryWrapper.like(StringUtils.isNotBlank(dto.getUserName()),"user_name",dto.getUserName())
                .eq(dto.getCompanyId()!=null,"dept_id", dto.getCompanyId())
                .eq(dto.getDeptId()!=null,"dept_id",dto.getDeptId());

        // 移交时间区间
        if (start!= null && end!= null) {
            bizSignInQueryWrapper.between("tr.create_time",start, end);
        }
       return bizSignInMapper.selectList(bizSignInQueryWrapper);

    }

    /**
     * 更新
     */
    @Override
    public void update() {

    }
}
