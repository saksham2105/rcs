package org.caching.server.rcs.services;

import org.caching.server.rcs.dto.CacheManagerDto;
import org.caching.server.rcs.exceptions.CacheException;

public interface ICachingService {
    public void validateAndHandleCaching(CacheManagerDto cacheManagerDto) throws CacheException;
    public void put(String key, byte[] value,CacheManagerDto cacheManagerDto) throws CacheException;
}
