package org.caching.server.rcs.services;

import org.caching.server.rcs.dto.CacheManagerDto;

public interface ICachingService {
    public void validateAndPopulate(CacheManagerDto cacheManagerDto);
    public void put(String key, byte[] value);
}
