package org.caching.server.rcs.controllers;

import org.caching.server.rcs.dto.CacheManagerDto;
import org.caching.server.rcs.exceptions.CacheException;
import org.caching.server.rcs.models.ResponseWrapper;
import org.caching.server.rcs.services.ICachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "${apiPrefix}")
public class CachingController extends CachingControllerBase {

    @Autowired
    private ICachingService cachingService;

    @PostMapping(path = "${cacheApi}")
    public ResponseWrapper cache(@RequestAttribute(value = "cm",required = true) CacheManagerDto cacheManagerDto) throws CacheException {
       this.cachingService.validateAndHandleCaching(cacheManagerDto);
       return ResponseWrapper.builder().success(true).build();
    }

    @GetMapping(path = "${getCacheApi}")
    public ResponseWrapper getCache(@RequestParam(value = "name",required = true) String name) throws CacheException {
        Map<String,Object> cache = (Map<String,Object>) this.cachingService.getCache(name);
        ResponseWrapper responseWrapper = ResponseWrapper.builder().success(true).data(cache).build();
        return responseWrapper;
    }
}
