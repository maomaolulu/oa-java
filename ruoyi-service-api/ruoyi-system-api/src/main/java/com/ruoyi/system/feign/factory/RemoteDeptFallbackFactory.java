package com.ruoyi.system.feign.factory;

import com.ruoyi.common.core.domain.R;
import org.springframework.stereotype.Component;

import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.feign.RemoteDeptService;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RemoteDeptFallbackFactory implements FallbackFactory<RemoteDeptService>
{/* (non-Javadoc)
  * @see feign.hystrix.FallbackFactory#create(java.lang.Throwable)
  */
    @Override
    public RemoteDeptService create(Throwable throwable)
    {
        log.error(throwable.getMessage());
        return new RemoteDeptService()
        {

            @Override
            public SysDept selectSysDeptByDeptId(long deptId)
            {
                return new SysDept();
            }

            @Override
            public List<SysDept> listAudit(List<Long> roleIds) {
                return null;
            }

            /**
             * 查询部门所在公司
             *
             * @param deptId
             */
            @Override
            public Map<String, Object> getBelongCompany2(long deptId) {
                return new HashMap<>(2);
            }

            /**
             * 查询部门所属一级部门
             *
             * @param deptId
             */
            @Override
            public Map<String, Object> getFirstDept(long deptId) {
                return new HashMap<>(2);
            }

            /**
             * 查询部门所在公司
             *
             * @param deptId
             */
            @Override
            public String getBelongCompany3(long deptId) {
                return "";
            }

            /**
             * 无权限过滤查询所有可用部门
             *
             * @return
             */
            @Override
            public Map<Long,String> listEnable2Map() {
                return new HashMap<>(0);
            }
        };
    }
}
