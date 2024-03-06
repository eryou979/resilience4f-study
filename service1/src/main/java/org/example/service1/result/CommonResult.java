package org.example.service1.result;

import lombok.Data;
import org.example.service1.util.TracerUtil;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dlz
 * @since 2023/09/11
 */
@Data
public class CommonResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 3214327569943760059L;

    private String traceId;
    private String code;
    private String message;
    private T error;
    private T result;

    public static <T> CommonResult<T> success() {
        final CommonResult<T> result = new CommonResult<>();
        init(result, ResultCode.SUCCESS);
        return result;
    }


    public static <T> CommonResult<T> success(T data) {
        final CommonResult<T> result = new CommonResult<>();
        init(result, ResultCode.SUCCESS);
        result.setResult(data);
        return result;
    }

    public static <T> CommonResult<T> error() {
        final CommonResult<T> result = new CommonResult<>();
        init(result, ResultCode.ERROR);
        return result;
    }

    public static <T> CommonResult<T> error(ResultCode resultCode) {
        final CommonResult<T> result = new CommonResult<>();
        init(result, resultCode);
        return result;
    }

    public static <T> CommonResult<T> error(ResultCode resultCode, T error) {
        final CommonResult<T> result = new CommonResult<>();
        init(result, resultCode);
        result.setError(error);
        return result;
    }

    public static <T> CommonResult<T> error(T error) {
        final CommonResult<T> result = new CommonResult<>();
        init(result, ResultCode.ERROR);
        result.setError(error);
        return result;
    }

    private static <T> void init(CommonResult<T> result, ResultCode resultCode) {
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setTraceId(TracerUtil.traceId());
    }
}

