package com.taosdata.example.mybatisplusdemo.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Author:  Heartsuit
 * Date:  2019/3/14 11:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    /** 结果状态 ,正常响应200，其他状态码都为失败*/
    private int code;
    private String msg;
    private T data;

    // Static methods
    /**
     * 成功时候的调用
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data, CodeMsg.SUCCESS);
    }
    public static <T> Result<T> success() {
        return new Result<T>(CodeMsg.SUCCESS);
    }

    /**
     * 失败时候的调用
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<T>(code, msg);
    }
    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }
    public static <T> Result<T> error(String msg) {
        CodeMsg codeMsg = new CodeMsg(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
        return new Result<T>(codeMsg);
    }

    // Constructor
    private Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Result(T data, CodeMsg codeMsg) {
        this.data = data;
        if (codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }
    private Result(CodeMsg codeMsg) {
        if (codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }

}
