package org.example.service1.result;

import lombok.Getter;
import org.example.service1.exception.BaseException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dlz
 * @since 2023/09/11
 */
@Getter
public enum ResultCode {
    /**
     * 统一返回.
     */
    SUCCESS("success", 200, "请求成功"),

    ERROR("error", 500, "请求失败"),
    SYSTEM_BUSY("system_busy", 500, "服务器繁忙, 请稍后再试"),

    BAD_REQUEST("bad_request", 400, "错误请求"),
    HEADER_NOT_FOUND("header_not_found", 400, "请求头缺失"),
    TOKEN_INVALID("token_invalid", 400, "Token失效"),
    NO_AUTH("no_auth", 400, "没有权限"),
    ;


    private final String code;
    private final int status;
    private final String message;

    ResultCode(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public static void verifyDuplicate() {
        final Set<Object> set = new HashSet<>();
        for (ResultCode value : values()) {
            if (set.contains(value.getCode())) {
                throw new BaseException("返回码重复");
            }
            set.add(value.getCode());
        }
    }
}
