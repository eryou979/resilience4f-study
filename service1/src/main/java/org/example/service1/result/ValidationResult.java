package org.example.service1.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationResult {
    /**
     * 校验是否成功
     */
    private boolean success;
    /**
     * 错误消息
     */
    private List<ErrorMessage> errorMessages = new ArrayList<>();

    /**
     * 构造
     */
    public ValidationResult(boolean success) {
        this.success = success;
    }

    /**
     * 添加错误
     */
    public void addErrorMessage(ErrorMessage errorMessage) {
        this.errorMessages.add(errorMessage);
    }

}
