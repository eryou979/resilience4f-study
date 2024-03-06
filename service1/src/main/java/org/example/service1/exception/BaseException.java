package org.example.service1.exception;

import lombok.Getter;
import org.example.service1.result.ResultCode;

/**
 * @author dlz
 * @since 2023/09/13
 */

@Getter
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = -5909652969826257919L;

    private final ResultCode resultCode;

    private final Object msg;

    public BaseException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.msg = resultCode.getMessage();
    }

    public BaseException(ResultCode resultCode, Object msg) {
        super(msg.toString());
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public BaseException(ResultCode resultCode, String msg) {
        super(msg);
        this.resultCode = resultCode;
        this.msg = msg;
    }

    public BaseException(String msg) {
        super(msg);
        this.resultCode = ResultCode.ERROR;
        this.msg = msg;
    }

    public BaseException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.resultCode = resultCode;
        this.msg = resultCode.getMessage();
    }

}
