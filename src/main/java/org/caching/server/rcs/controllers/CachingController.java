package org.caching.server.rcs.controllers;

import org.caching.server.rcs.dto.CacheManagerDto;
import org.caching.server.rcs.services.ICachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "${apiPrefix}")
public class CachingController extends CachingControllerBase {

    @Autowired
    private ICachingService cachingService;

    @PostMapping(path = "${cacheApi}")
    public void cache(@RequestBody CacheManagerDto cacheManagerDto) {
       this.cachingService.validateAndPopulate(cacheManagerDto);
    }
}
