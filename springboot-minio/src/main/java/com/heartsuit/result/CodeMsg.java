package com.heartsuit.result;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Author:  Heartsuit
 * Date:  2019/3/14 11:12
 */
@Getter
public class CodeMsg {
    private int code;
    private String msg;

    // 通用的错误码
    public static final CodeMsg SUCCESS =new CodeMsg(HttpStatus.OK.value(), "success");
    public static final CodeMsg BAD_REQUEST = new CodeMsg(HttpStatus.BAD_REQUEST.value(), "请求无效");
    public static final CodeMsg SERVER_ERROR = new CodeMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务端异常");
    public static final CodeMsg NO_HANDLER_FOUND = new CodeMsg(HttpStatus.NOT_FOUND.value(), "未找到对应资源");
    public static final CodeMsg UNAUTHORIZED = new CodeMsg(HttpStatus.UNAUTHORIZED.value(), "未认证或登录状态过期");
    public static final CodeMsg FORBIDDEN = new CodeMsg(HttpStatus.FORBIDDEN.value(), "未授权");
    // 自定义错误码
    public static final CodeMsg PARAMETER_ERROR = new CodeMsg(4000, "参数不正确！");
    /*用户相关：验证码*/
    public static final CodeMsg CAPTCHA_EXPIRED = new CodeMsg(4001, "验证码不存在或已过期");
    public static final CodeMsg CAPTCHA_INVALID = new CodeMsg(4002, "验证码错误");
    /*用户相关：认证授权*/
    public static final CodeMsg BAD_CREDENTIAL = new CodeMsg(4003, "用户名或密码错误");
    public static final CodeMsg ACCOUNT_NOT_FOUND = new CodeMsg(4004, "账号不存在");
    public static final CodeMsg ACCOUNT_NOT_ACTIVATED = new CodeMsg(4005, "账号未激活");

    /*角色相关*/
    public static final CodeMsg ROLE_ID_CONFLICT = new CodeMsg(4101,"角色ID已存在");

    public static final CodeMsg ROLE_USER_CONNECTED = new CodeMsg(4102,"角色用户存在关联");

    public static final CodeMsg ROLE_PERMISSION_DENIED = new CodeMsg(4103,"角色权限不足");

    public static final CodeMsg JOB_USER_CONNECTED = new CodeMsg(4104,"所选岗位存在用户关联，请取消关联后再试");

    public static final CodeMsg DEPT_JOB_CONNECTED = new CodeMsg(4104,"所选部门中存在岗位关联，请取消关联后再试");

    public static CodeMsg error(String msg){
        return new CodeMsg(HttpStatus.BAD_REQUEST.value(),msg);
    }

    // 限流
    public static final CodeMsg RATE_LIMIT = new CodeMsg(4000,"达到阈值啦!");
    // 熔断
    public static final CodeMsg DEGRADE = new CodeMsg(4000,"熔断啦!");


    /*文件上传相关*/

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
