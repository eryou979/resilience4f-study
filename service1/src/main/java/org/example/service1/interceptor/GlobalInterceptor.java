package org.example.service1.interceptor;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service1.util.TracerUtil;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author dlz
 * @since 2024/03/06
 */
public class GlobalInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TracerUtil.setTraceId(DigestUtil.md5Hex16(RandomUtil.randomString(64)));
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
