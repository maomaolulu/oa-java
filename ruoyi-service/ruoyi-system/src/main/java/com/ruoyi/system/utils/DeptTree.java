package com.ruoyi.system.utils;


import com.ruoyi.system.vo.DeptTreeVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 树形结构
 * @author: zx
 * @date: 2021/10/25 10:02
 */
public class DeptTree {
    private List<DeptTreeVo> menuList = new ArrayList<DeptTreeVo>();
    public DeptTree(List<DeptTreeVo> menuList) {
        this.menuList=menuList;
    }

    /**
     * 建立树形结构
     *
     * @return
     */
    public List<DeptTreeVo> buildTree(){
        List<DeptTreeVo> treeMenus =new  ArrayList<DeptTreeVo>();
        for(DeptTreeVo menuNode : getRootNode()) {
            menuNode=buildChilTree(menuNode);
            treeMenus.add(menuNode);
        }
        return treeMenus;
    }

    /**
     * 递归，建立子树形结构
     *
     * @param pNode
     * @return
     */
    private DeptTreeVo buildChilTree(DeptTreeVo pNode){
        List<DeptTreeVo> chilMenus =new  ArrayList<DeptTreeVo>();
        for(DeptTreeVo menuNode : menuList) {
            if(menuNode.getParentId().equals(pNode.getDeptId())) {
                chilMenus.add(buildChilTree(menuNode));
            }
        }
        pNode.setChildren(chilMenus);
        return pNode;
    }

    /**
     * 获取根节点
     *
     * @return
     */
    private List<DeptTreeVo> getRootNode() {
        List<DeptTreeVo> rootMenuLists =new  ArrayList<DeptTreeVo>();
        for(DeptTreeVo menuNode : menuList) {
            if(menuNode.getParentId()==0) {
                rootMenuLists.add(menuNode);
            }
        }
        return rootMenuLists;
    }
}