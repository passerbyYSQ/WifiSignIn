package com.ysq.wifisignin.data;

import java.text.SimpleDateFormat;

/**
 * 常量
 * @author passerbyYSQ
 * @create 2020-05-07 15:59
 */
public interface Constant {
    String REGEX_PHONE = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";

    String API_URL = "http://114.55.219.55:8690/api/";

    String PRIVACY_URL = "http://www.mob.com/about/policy";

    String ALIYUN_ACCESS_KEY_ID = "LTAI4FnM6LpGsjE2qJ8sEqkA";
    String ALIYUN_ACCESS_KEY_SECRET = "LjVhzlltTdYXbeOfrFUtapmhzWhYut";

    String DEFAULT_PORTRAIT_URL = "https://italker-ysq.oss-cn-shenzhen.aliyuncs.com/default/bg_src_morning.jpg";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
}
