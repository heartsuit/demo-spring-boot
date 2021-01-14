package com.heartsuit.utils;

/**
 * @Author Heartsuit
 * @Date 2021-01-12
 */
public final class JwtConstant {
    /**
     * 对称加密密钥
     * 仅服务端存储，生产中建议使用复杂度高的密钥或采用非对称加密eg:RSA
     */
    public static final String SECRET = "heartsuit";

    /**
     * Token有效期
     */
    public static final long VALIDITY_SECONDS = 60 * 60 * 12; // default 12 hours

    /**
     * 权限
     */
    public static final String AUTH_KEY = "auth";

    /**
     * 头信息中Token的Key
     */
    public static final String HEADER = "authorization";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    private JwtConstant() {
    }
}
