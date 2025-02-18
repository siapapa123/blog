package com.blog.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
    public static <T> Logger getLogger(Class<T> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    public static void info(Logger logger, String format, Object... arguments) {
        logger.info(format, arguments);
    }
    
    public static void error(Logger logger, String format, Object... arguments) {
        logger.error(format, arguments);
    }
    
    public static void debug(Logger logger, String format, Object... arguments) {
        logger.debug(format, arguments);
    }
    
    public static void warn(Logger logger, String format, Object... arguments) {
        logger.warn(format, arguments);
    }
} 