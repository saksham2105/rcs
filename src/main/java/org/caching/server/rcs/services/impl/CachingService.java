package org.caching.server.rcs.services.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.caching.server.rcs.dto.CacheManagerDto;
import org.caching.server.rcs.enums.EvictionPolicyEnum;
import org.caching.server.rcs.enums.ServletContainerEnum;
import org.caching.server.rcs.factory.ServletContainerFactory;
import org.caching.server.rcs.services.ICachingService;
import org.caching.server.rcs.utils.CacheUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.ServletContext;
import java.util.HashMap;

@Repository("cachingService")
public class CachingService implements ICachingService {

    private static Logger logger = LogManager.getLogger(CachingService.class);

    @Autowired
    private ServletContainerFactory servletContainerFactory;

    @Autowired
    private CacheUtility cacheUtility;

    /**
     * A Service to Cache Data in Embedded Container
     *
     * @return
     */

    //TODO : Adding Doubly Linked List for Maximum Cache Elements
    @Override
    public void validateAndPopulate(CacheManagerDto cacheManagerDto) {
        ServletContext servletContext = (ServletContext) this.servletContainerFactory.getServletContainer(ServletContainerEnum.SERVLETCONTEXT);
        if (EvictionPolicyEnum.findByName(cacheManagerDto.getEvictionPolicy()).
                equals(EvictionPolicyEnum.LRU)) {
          logger.info("Inside validate and populate method for evictionPolicy : {}","LRU");
          byte[] cacheBytes = cacheUtility.getCompressedCacheBytesByKey(cacheManagerDto.getKey(),servletContext);
          /* Case of New Cache */
          if (cacheBytes == null) {
              logger.info("Cache Bytes are null for cache name : {}",cacheManagerDto.getCacheName());
              HashMap<String,Object> cacheData = cacheUtility.buildNewCache(cacheManagerDto.getKey(),cacheManagerDto.getValue());
              byte[] compressedBytes = this.cacheUtility.compressCacheData(cacheData);
              this.put(cacheManagerDto.getCacheName(),compressedBytes);
          } else {
              logger.info("Cache Bytes are not null for cache name : {}",cacheManagerDto.getCacheName());
              HashMap<String,Object> cacheData = cacheUtility.decompressCacheData(cacheManagerDto.getCacheName(),servletContext);
              cacheData.put(cacheManagerDto.getKey(),cacheManagerDto.getValue());
              byte[] compressedBytes = this.cacheUtility.compressCacheData(cacheData);
              this.put(cacheManagerDto.getCacheName(),compressedBytes);
          }
        }
        //TODO : Implement more statements in future
        return;
    }

    @Override
    public void put(String key, byte[] value) {
        ServletContext servletContext = (ServletContext) this.servletContainerFactory.getServletContainer(ServletContainerEnum.SERVLETCONTEXT);
        servletContext.setAttribute(key,value);
    }
}
