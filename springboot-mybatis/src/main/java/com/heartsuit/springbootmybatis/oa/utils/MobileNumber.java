package com.heartsuit.springbootmybatis.oa.utils;

import java.util.Random;

/**
 * @Author Heartsuit
 * @Date 2021-08-10
 */
public class MobileNumber {
    //中国移动
    public static final String[] CHINA_MOBILE = {
            "134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159",
            "182", "183", "184", "187", "188", "178", "147", "172", "198"
    };
    //中国联通
    public static final String[] CHINA_UNICOM = {
            "130", "131", "132", "145", "155", "156", "166", "171", "175", "176", "185", "186", "166"
    };
    //中国电信
    public static final String[] CHINA_TELECOME = {
            "133", "149", "153", "173", "177", "180", "181", "189", "199"
    };

    /**
     * 生成手机号
     *
     * @param operator 0 移动 1 联通 2 电信
     */
    public static String generate(int operator) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        String mobileTag;//手机号前三位
        int temp;
        switch (operator) {
            case 0:
                mobileTag = CHINA_MOBILE[random.nextInt(CHINA_MOBILE.length)];
                break;
            case 1:
                mobileTag = CHINA_UNICOM[random.nextInt(CHINA_UNICOM.length)];
                break;
            case 2:
                mobileTag = CHINA_TELECOME[random.nextInt(CHINA_TELECOME.length)];
                break;
            default:
                mobileTag = "运营商标志位有误！";
                break;
        }
        if (mobileTag.length() > 3) {
            return mobileTag;
        }
        sb.append(mobileTag);
        //生成手机号后8位
        for (int i = 0; i < 8; i++) {
            temp = random.nextInt(10);
            sb.append(temp);
        }
        return sb.toString();
    }
}
