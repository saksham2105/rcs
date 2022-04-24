package org.caching.server.rcs.services.impl;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.caching.server.rcs.dto.CacheManagerDto;
import org.caching.server.rcs.enums.EvictionPolicyEnum;
import org.caching.server.rcs.enums.ServletContainerEnum;
import org.caching.server.rcs.exceptions.CacheException;
import org.caching.server.rcs.factory.ServletContainerFactory;
import org.caching.server.rcs.services.ICachingService;
import org.caching.server.rcs.utils.CacheUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Repository("cachingService")
public class CachingService implements ICachingService {

    private static Logger logger = LogManager.getLogger(CachingService.class);

    @Autowired
    private ServletContainerFactory servletContainerFactory;

    @Autowired
    private CacheUtility cacheUtility;

    @Value("${map.key.suffix:MAP}")
    private String mapKeySuffix;

    @Value("${dll.key.suffix:DLL}")
    private String dllKeySuffix;

    @Value("${key.delimeter:_}")
    private String keyDelimeter;

    /**
     * A Service to Cache Data in Embedded Container
     *
     * @return
     */
    @Override
    public void validateAndHandleCaching(CacheManagerDto cacheManagerDto) throws CacheException {
        ServletContext servletContext = (ServletContext) this.servletContainerFactory.getServletContainer(ServletContainerEnum.SERVLETCONTEXT);
        if (EvictionPolicyEnum.findByName(cacheManagerDto.getEvictionPolicy()).
                equals(EvictionPolicyEnum.LRU)) {
          logger.info("Inside validate and populate method for evictionPolicy : {}","LRU");
          byte[] cacheBytes = cacheUtility.getCompressedCacheBytesByKey(cacheManagerDto.getCacheName().concat(this.keyDelimeter).concat(this.mapKeySuffix),servletContext);
          byte[] dllBytes = cacheUtility.getCompressedLRUDLLBytesByKey(cacheManagerDto.getCacheName().concat(this.keyDelimeter).concat(this.dllKeySuffix),servletContext);
          /* Case of New Cache */
          if (cacheBytes == null || dllBytes == null) {
              logger.info("Cache Bytes or Dll bytes are null for cache name : {}",cacheManagerDto.getCacheName());
              Map<String,Object> cacheData = cacheUtility.buildNewCache(cacheManagerDto.getKey(),cacheManagerDto.getValue(),cacheManagerDto.getMaxElementsInMemory());
              ArrayList<Pair<String ,Object>> doublyLinkedList = cacheUtility.buildNewLRUDLL(cacheManagerDto.getKey(),cacheManagerDto.getValue(),cacheManagerDto.getMaxElementsInMemory());
              byte[] compressedBytes = this.cacheUtility.compressCacheData(cacheData);
              byte[] compressedDllBytes = this.cacheUtility.compressLRUDLL(doublyLinkedList);
              this.put(cacheManagerDto.getCacheName().concat(this.keyDelimeter).concat(this.mapKeySuffix),compressedBytes);
              this.put(cacheManagerDto.getCacheName().concat(this.keyDelimeter).concat(this.dllKeySuffix),compressedDllBytes);
          } else {
              logger.info("Cache Bytes are not null for cache name : {}",cacheManagerDto.getCacheName());
              byte[] compressedBytes = this.cacheUtility.invokeLRUAndGetCache(cacheManagerDto,servletContext);
              byte[] compressedDllBytes = this.cacheUtility.invokeLRUAndGetDLL(cacheManagerDto,servletContext);
              this.put(cacheManagerDto.getCacheName().concat(this.keyDelimeter).concat(this.mapKeySuffix),compressedBytes);
              this.put(cacheManagerDto.getCacheName().concat(this.keyDelimeter).concat(this.dllKeySuffix),compressedDllBytes);
          }
        }
        //TODO : Implement more statements in future
        return;
    }

    /**
     * A function to Put Compressed Data in Servlet Container
     * @param key
     * @param value
     * @throws CacheException
     */
    @Override
    public void put(String key, byte[] value) throws CacheException {
        ServletContext servletContext = (ServletContext) this.servletContainerFactory.getServletContainer(ServletContainerEnum.SERVLETCONTEXT);
        servletContext.setAttribute(key,value);
    }
}
