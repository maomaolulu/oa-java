package com.ruoyi.activiti.consts;

/**
 * url常量类
 *
 * @author: 张豪
 * @create: 2023-04-10
 */
public class UrlConstants {

    /**
     * 运营系统测试环境-前缀
     */
    public static final String YY_CP_TEST_PRE = "http://192.168.0.229:8989/anlian_sys";

    /**
     * 运营系统正式环境-前缀
     */
    public static final String YY_CP_ONLINE_PRE = "http://47.111.249.220:81/proxyAnlianSysJava";

    /**
     * 获取token
     */
    public static final String TOKEN_PATH = "/anlian/interface/sys/out/login";

    /**
     * 查询信息
     */
    public static final String SELECT_PATH = "/anlian/project/getContractProjects";

    /**
     * 修改
     */
    public static final String UPDATE_PATH = "/anlian/project/updateProjectInfo";

    /**
     * 检查项目编号
     */
    public static final String CHECK_PATH = "/anlian/project/checkIdentifier";

    /**
     * 检查项目编号
     */
    public static final String TYPE_PATH = "/anlian/project/getProjectType";

    /**
     * 测试查询
     */
    public static final String JPSELECT_TEST = "http://192.168.0.229:8989/anlian_sys/anlian/project/getInfomation";
    /**
     * 测试修改
     */
    public static final String JPUPDATE_TEST = "http://192.168.0.229:8989/anlian_sys/anlian/project/updateProAmount";
    /**
     * 线上查询
     */
    public static final String JPSELECT_ONLINE = "http://47.111.249.220:81/proxyAnlianSysJava/anlian/project/getInfomation";
    /**
     * 线上修改
     */
    public static final String JPUPDATE_ONLINE = "http://47.111.249.220:81/proxyAnlianSysJava/anlian/project/updateProAmount";

    /**
     * 量远-报价审批-测试环境地址
     */
    public static final String LY_QUOTATION_TEST = "http://192.168.0.155:8080/liangyuan/quotation/updateStatus";

    /**
     * 量远-报价审批-正式环境地址
     */
    public static final String LY_QUOTATION_ONLINE = "http://60.190.21.178:10090/proxyAnlianShlySysJava/liangyuan/quotation/updateStatus";

    /**
     * 量远-合同评审-测试环境地址
     */
    public static final String LY_CONTRACT_TEST = "http://192.168.0.69:8080/liangyuan/contract/updateReviewStatus";

    /**
     * 量远-合同评审-正式环境地址
     */
    public static final String LY_CONTRACT_ONLINE = "http://60.190.21.178:10090/proxyAnlianShlySysJava/liangyuan/contract/updateReviewStatus";

    /**
     * 运营-获取token-测试
     */
    public static final String YY_TOKEN_TEST = YY_CP_TEST_PRE + TOKEN_PATH;

    /**
     * 运营-获取token-正式
     */
    public static final String YY_TOKEN_ONLINE = YY_CP_ONLINE_PRE + TOKEN_PATH;

    /**
     * 运营-合同项目信息修改-查询-测试
     */
    public static final String YY_CP_SELECT_TEST = YY_CP_TEST_PRE + SELECT_PATH;

    /**
     * 运营-合同项目信息修改-查询-正式
     */
    public static final String YY_CP_SELECT_ONLINE = YY_CP_ONLINE_PRE + SELECT_PATH;

    /**
     * 运营-合同项目信息修改-修改-测试
     */
    public static final String YY_CP_UPDATE_TEST = YY_CP_TEST_PRE + UPDATE_PATH;

    /**
     * 运营-合同项目信息修改-修改-正式
     */
    public static final String YY_CP_UPDATE_ONLINE = YY_CP_ONLINE_PRE + UPDATE_PATH;

    /**
     * 运营-合同项目信息修改-项目编号是否占用-测试
     */
    public static final String YY_CP_USING_TEST = YY_CP_TEST_PRE + CHECK_PATH;

    /**
     * 运营-合同项目信息修改-项目编号是否占用-正式
     */
    public static final String YY_CP_USING_ONLINE = YY_CP_ONLINE_PRE + CHECK_PATH;

    /**
     * 运营-合同项目信息修改-合同项目类型-测试
     */
    public static final String YY_CP_TYPE_TEST = YY_CP_TEST_PRE + TYPE_PATH;

    /**
     * 运营-合同项目信息修改-合同项目类型-正式
     */
    public static final String YY_CP_TYPE_ONLINE = YY_CP_ONLINE_PRE + TYPE_PATH;

    /**
     * 运营2.0采购信息入库-测试环境
     */
    public static final String YY2_PURCHASE_TEST="http://127.0.0.1:8080/data/equip/save/purchase";

    /**
     * 运营2.0采购信息入库-正式环境
     */
    public static final String YY2_PURCHASE_ONLINE="XXXXXXX";
}
