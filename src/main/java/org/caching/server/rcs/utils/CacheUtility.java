package org.caching.server.rcs.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class CacheUtility {

    private static Logger logger = LogManager.getLogger(CacheUtility.class);
    /**
     * This method will return compressed cache in String format
     * @param key
     * @param servletContext
     * @return
     */
    public byte[] getCompressedCacheBytesByKey(String key, ServletContext servletContext) {
        if (servletContext.getAttribute(key) != null) {
            logger.info("Key : {} is found in servlet context",key);
            return (byte[]) servletContext.getAttribute(key);
        }
        return null;
    }

    /**
     * This method will compress cache data
     * @param cacheData
     */
    public byte[] compressCacheData(HashMap<String,Object> cacheData) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzipOutputStream);
            objectOut.writeObject(cacheData);
            objectOut.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
           logger.error("Exception encountered while compressing cache data : {}",e,e.getMessage());
           return null;
        }
    }

    /**
     * This method will decompress and build cache data for processing
     * @param key
     * @param servletContext
     * @return
     */
    public HashMap<String,Object> decompressCacheData(String key,ServletContext servletContext) {
        try {
            byte[] compressedBytes = this.getCompressedCacheBytesByKey(key,servletContext);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            ObjectInputStream objectIn = new ObjectInputStream(gzipInputStream);
            HashMap<String,Object> cacheData = (HashMap<String,Object>) objectIn.readObject();
            objectIn.close();
            return cacheData;
        } catch (Exception e) {
           logger.error("Exception encountered while decompressing cache data for key : {}",key);
           logger.error(e,e);
           return null;
        }
    }

    public HashMap<String ,Object> buildNewCache(String key,Object value) {
        HashMap<String,Object> cache = new HashMap<>();
        cache.put(key,value);
        return cache;
    }
}
