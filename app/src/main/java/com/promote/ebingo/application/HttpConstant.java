package com.promote.ebingo.application;

/**
 * Created by jch on 2014/8/28.
 */
public class HttpConstant {

    private static final String TEST_RHOST = "http://218.244.149.129"; //remote

    private static final boolean LOCALHOST = false;

    private static final String TEST_HOST = "http://192.168.1.161";

    private static final String HOST = "";

    private static final String PORT = "";

    private static final String ROOT_URL = "/eb/index.php?s=/Home/Api/";

    public static String LOCAL_URL = "/ebingoo/index.php?s=/Home/Api/";

    public static String getHost() {

        if (Constant.isReleaseAble()) {
            return HOST + PORT;
        } else {
            return TEST_HOST + PORT;
        }
    }

    private static String getRootUrl() {

        if (LOCALHOST) {
            return TEST_HOST + LOCAL_URL;
        } else {
            return TEST_RHOST + ROOT_URL;
        }
    }

    public static final String KEY = "hdy782634j23487sdfkjw3486";

    public static final String FAIL="fail";//访问网络返回失败

    /**
     * 首页显示接口.*
     */
    public static final String getIndex = getRootUrl() + "getIndex";

    public static final String getCategories=getRootUrl()+"getCategoryList";

    public static final String login=getRootUrl()+"login";

    public static final String register=getRootUrl()+"register";

    public static final String getYzm=getRootUrl()+"getYzm";

    public static final String saveInfo=getRootUrl()+"saveInfo";
    /** 获取企业列表 **/
    public static final String getCompanyList=getRootUrl() +"getCompanyList";
    /** 获取供应信息列表 **/
    public static final String getSupplyInfoList = getRootUrl() + "getSupplyInfoList";
    /** 获取求购信息列表 **/
    public static final String getDemandInfoList = getRootUrl() + "getDemandInfoList";

}
