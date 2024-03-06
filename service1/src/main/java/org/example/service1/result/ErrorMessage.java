package org.example.service1.result;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 错误消息，包括字段名（字段路径）、字段值、消息内容
 */
@Data
@Accessors(chain = true)
public class ErrorMessage {
    /**
     * 属性字段名称
     */
    private String property;
    /**
     * 错误值
     */
    private Object value;
    /**
     * 错误信息
     */
    private String message;

    public static String listFormat(String property, int index) {
        return StrUtil.format("{}[{}]", property, index);
    }
}
