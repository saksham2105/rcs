package org.caching.server.rcs.interceptors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.caching.server.rcs.dto.CacheManagerDto;
import org.caching.server.rcs.enums.CacheExceptionEnum;
import org.caching.server.rcs.models.MessageDetails;
import org.caching.server.rcs.models.ResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CacheInterceptor implements HandlerInterceptor {

    private Gson gson = new Gson();

    private static Logger logger = LogManager.getLogger(CacheInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        logger.info("Inside preHandle for CacheInterceptor");
        byte[] bytes = IOUtils.toByteArray(request.getInputStream());
        if (bytes == null) {
            logger.info("Got null bytes returning failure response");
            return sendFailureResponse(response,CacheExceptionEnum.INVALID_REQUEST.getCode(),CacheExceptionEnum.INVALID_REQUEST.getText());
        }
        String cacheManagerDtoString = new String(bytes);
        CacheManagerDto cacheManagerDto = gson.fromJson(cacheManagerDtoString,CacheManagerDto.class);
        if (cacheManagerDto.getKey() == null
                || cacheManagerDto.getTimeToLive() == null || cacheManagerDto.getCacheName() == null
                || cacheManagerDto.getMaxElementsInMemory() == null || cacheManagerDto.getValue() == null
        || cacheManagerDto.getEvictionPolicy() == null) {
            logger.info("Got null parameters returning failure response");
            return sendFailureResponse(response,CacheExceptionEnum.INVALID_REQUEST.getCode(),CacheExceptionEnum.INVALID_REQUEST.getText());
        }
        logger.info("Returning True");
        request.setAttribute("cm",cacheManagerDto);
        return true;
    }

    private boolean sendFailureResponse(HttpServletResponse response,String code,String text) throws IOException {
        MessageDetails message = MessageDetails.builder().code(code).text(text).build();
        ResponseWrapper responseWrapper = ResponseWrapper.builder().success(false).message(message).build();
        String responseString = gson.toJson(responseWrapper);
        response.getOutputStream().println(responseString);
        return false;
    }
}
