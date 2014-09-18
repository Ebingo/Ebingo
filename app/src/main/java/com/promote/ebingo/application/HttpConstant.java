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

    public static String getRootUrl() {

        if (LOCALHOST) {
            return TEST_HOST + LOCAL_URL;
        } else {
            return TEST_RHOST + ROOT_URL;
        }
    }

    public static final String KEY = "hdy782634j23487sdfkjw3486";

    public static final String FAIL = "fail";//访问网络返回失败
    public static final String CODE_OK = "100";//成功返回码
    public static final String CODE_FAIL = "101";//失败返回码

    /**
     * 首页显示接口.*
     */
    public static final String getIndex = getRootUrl() + "getIndex";
    /**
     * 获取分类列表 *
     */
    public static final String getCategories = getRootUrl() + "getCategoryList";
    /**
     * 登录 *
     */
    public static final String login = getRootUrl() + "login";
    /**
     * 注册 *
     */
    public static final String register = getRootUrl() + "register";
    /**
     * 获取验证码 *
     */
    public static final String getYzm = getRootUrl() + "getYzm";
    /**
     * 发布信息 *
     */
    public static final String saveInfo = getRootUrl() + "saveInfo";
    /**
     * 获取热门标签 *
     */
    public static final String getHotTags = getRootUrl() + "getHotTags";
    /**
     * 获取企业列表 *
     */
    public static final String getCompanyList = getRootUrl() + "getCompanyList";
    /**
     * 获取供应信息列表 *
     */
    public static final String getSupplyInfoList = getRootUrl() + "getSupplyInfoList";
    /**
     * 获取求购信息列表 *
     */
    public static final String getDemandInfoList = getRootUrl() + "getDemandInfoList";
    /**
     * 获取信息详情接口. *
     */
    public static final String getInfoDetail = getRootUrl() + "getInfoDetail";
    /** 上传图片接口. **/
    public static final String uploadImage = getRootUrl() + "uploadImage";
    /** 更新企业信息. **/
    public static final String updateCompanyInfo = getRootUrl() + "updateCompanyInfo";
    /** 获取当前登录公司的基本参数. **/
    public static final String getCurrentCompanyBaseNum = getRootUrl() + "getCurrentCompanyBaseNum";
    /**
     * 删除供求信息. *
     */
    public static final String deleteInfo = getRootUrl() + "deleteInfo";
    /**
     * 获取收藏列表(getWishlist)
     */
    public static final String getWishlist = getRootUrl() + "getWishlist";
    /**
     * 取消收藏接口
     */
    public static final String cancleWishlist = getRootUrl() + "cancleWishlist";
    /**
     * 申请vip
     */
    public static final String applyVip=getRootUrl()+"applyVip";
    /**
     * 获取拨号纪录接口
     */
    public static final String getCallRecord=getRootUrl()+"getCallRecord";
    /**
     * 意见反馈
     */
    public static final String addAdvice=getRootUrl()+"addAdvice";

    /**
     * 添加收藏
     */
    public static final String addToWishlist=getRootUrl()+"addToWishlist";

    /**
     * 添加通话记录
     */
    public static final String addCallRecord = getRootUrl() + "addCallRecord";
    /**
     * 获取用户的订阅标签
     */
    public static final String getMyTagList = getRootUrl() + "getMyTagList";
    /**
     * 保存订阅标签
     */
    public static final String saveTagList= getRootUrl() + "saveTagList";
    /**
     *获取标签对应列表信息
     */
    public static final String getTagInfoList= getRootUrl() + "getTagInfoList";

    public static final String addCallRecord = getRootUrl() + "addCallRecord";


    /**
     * 获取公司详细信息
     */
    public static final String getCompanyDetail = getRootUrl() + "getCompanyDetail";
    /**
     * 获取公司新闻列表*
     */
    public static final String getCompanyNewsList = getRootUrl() + "getCompanyNewsList";
    /**
     * 获取新闻详情 *
     */
    public static final String getNewsWapUrl = getRootUrl() + "getNewsWapUrl";


}
