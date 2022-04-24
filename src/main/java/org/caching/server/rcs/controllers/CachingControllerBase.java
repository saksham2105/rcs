package org.caching.server.rcs.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.caching.server.rcs.enums.CacheExceptionEnum;
import org.caching.server.rcs.exceptions.CacheException;
import org.caching.server.rcs.models.MessageDetails;
import org.caching.server.rcs.models.ResponseWrapper;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class CachingControllerBase {
    private static final Logger logger = LogManager.getLogger(CachingControllerBase.class);

    @ExceptionHandler({Exception.class})
    public ResponseWrapper handleGenericException(Exception exception) {
        logger.error("Generic exception arrived",exception,exception.getMessage());
        logger.error(exception,exception);
        MessageDetails message = MessageDetails.builder().code(CacheExceptionEnum.GENERIC.getCode()).text(CacheExceptionEnum.GENERIC.getText()).build();
        ResponseWrapper responseWrapper = ResponseWrapper.builder().success(false).message(message).build();
        return responseWrapper;
    }

    @ExceptionHandler({CacheException.class})
    public ResponseWrapper handleCacheException(CacheException cacheException) {
        logger.error("Generic exception arrived",cacheException,cacheException.getMessage());
        logger.error(cacheException,cacheException);
        MessageDetails message = MessageDetails.builder().code(CacheExceptionEnum.CACHE.getCode()).text(CacheExceptionEnum.CACHE.getText()).build();
        ResponseWrapper responseWrapper = ResponseWrapper.builder().success(false).message(message).build();
        return responseWrapper;
    }
}
