package org.caching.server.rcs.controllers;

import org.caching.server.rcs.dto.CacheManagerDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "${apiPrefix}")
public class CachingController {

    @PostMapping(value = "cache")
    public void cache(@RequestBody CacheManagerDto cacheManagerDto) {

    }
}
