package com.heartsuit.exception;

import com.heartsuit.result.CodeMsg;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Author:  Heartsuit
 * Date:  2020-02-26 11:19
 * Version: 1.0
 */
@Getter
public class CustomException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer code;

    public CustomException(String msg) {
        super(msg);
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public CustomException(CodeMsg codeMsg) {
        super(codeMsg.getMsg());
        this.code = codeMsg.getCode();
    }

    public CustomException(Integer code, String msg){
        super(msg);
        this.code = code;
    }
}
