package com.twitter.ms.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import static main.java.com.leon.baobui.constants.PathConstants.AUTH_USER_ID_HEADER;

@UtilityClass
public class Utils {
    public static String parseUserId(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader(AUTH_USER_ID_HEADER);
    }
}
