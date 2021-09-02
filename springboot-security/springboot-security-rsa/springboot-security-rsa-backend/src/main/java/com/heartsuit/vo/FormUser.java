package com.heartsuit.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Heartsuit
 * @Date 2020-03-21
 */
@Getter
@Setter
public class FormUser {
    private String username;

    private String password;

    @Override
    public String toString() {
        return "{username=" + username  + ", password= ******}";
    }
}
