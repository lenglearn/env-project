package com.briup.smart.env.util;

/**
 * Log接口是物联网数据中心项目日志模块的规范
 * 日志模块将日志信息划分为五种级别,不同情况可以使用不同级别的日志进行记录
 */
public interface Log {
    /**
     * 记录debug级别的日志(调试)
     *
     * @param message 日志信息
     */
    void debug(String message);

    /**
     * 记录info级别的日志(正常)
     *
     * @param message 日志信息
     */
    void info(String message);

    /**
     * 记录warn级别的日志(警告)
     *
     * @param message 日志信息
     */
    void warn(String message);

    /**
     * 记录error级别的日志（错误）
     *
     * @param message 日志信息
     */
    void error(String message);

    /**
     * 记录fatal级别的日志(严重)
     *
     * @param message 日志信息
     */
    void fatal(String message);
}
