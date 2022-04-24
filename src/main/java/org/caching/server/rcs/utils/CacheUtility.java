package org.caching.server.rcs.utils;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.caching.server.rcs.dto.CacheManagerDto;
import org.caching.server.rcs.exceptions.CacheException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

//TODO : Adding interceptors and impelementing TTL Function
@Service
public class CacheUtility {

    private static Logger logger = LogManager.getLogger(CacheUtility.class);

    @Value("${map.key.suffix:MAP}")
    private String mapKeySuffix;

    @Value("${dll.key.suffix:DLL}")
    private String dllKeySuffix;

    @Value("${key.delimeter:_}")
    private String keyDelimeter;

    /**
     * This Method will return Compressed Cache
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
     * This method will return Compressed DLL
     * @param key
     * @param servletContext
     * @return
     */
    public byte[] getCompressedLRUDLLBytesByKey(String key, ServletContext servletContext) {
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
    public byte[] compressCacheData(Map<String,Object> cacheData) {
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
     * This method will compress LRU DLL
     * @param doublyLinkedList
     * @return
     */
    public byte[] compressLRUDLL(ArrayList<Pair<String,Object>> doublyLinkedList) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzipOutputStream);
            objectOut.writeObject(doublyLinkedList);
            objectOut.close();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error("Exception encountered while compressing LRU DLL : {}",e,e.getMessage());
            return null;
        }
    }

    /**
     * This method will decompress and build cache data for processing
     * @param key
     * @param servletContext
     * @return
     */
    public Map<String,Object> decompressCacheData(String key,ServletContext servletContext) {
        try {
            byte[] compressedBytes = this.getCompressedCacheBytesByKey(key.concat(this.keyDelimeter).concat(this.mapKeySuffix),servletContext);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            ObjectInputStream objectIn = new ObjectInputStream(gzipInputStream);
            Map<String,Object> cacheData = (Map<String,Object>) objectIn.readObject();
            objectIn.close();
            return cacheData;
        } catch (Exception e) {
           logger.error("Exception encountered while decompressing cache data for key : {}",key);
           logger.error(e,e);
           return null;
        }
    }

    /**
     * This method will return decompress DLL
     * @param key
     * @param servletContext
     * @return
     */
    public ArrayList<Pair<String,Object>> decompressLRUDLL(String key,ServletContext servletContext) {
        try {
            byte[] compressedBytes = this.getCompressedLRUDLLBytesByKey(key.concat(this.keyDelimeter).concat(this.dllKeySuffix),servletContext);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            ObjectInputStream objectIn = new ObjectInputStream(gzipInputStream);
            ArrayList<Pair<String,Object>> doublyLinkedList = (ArrayList<Pair<String,Object>>) objectIn.readObject();
            objectIn.close();
            return doublyLinkedList;
        } catch (Exception e) {
            logger.error("Exception encountered while decompressing LRU Dll for key : {}",key);
            logger.error(e,e);
            return null;
        }
    }

    /**
     * Method to Build Fresh New Cache Map
     * @param key
     * @param value
     * @param maxElements
     * @return
     * @throws CacheException
     */
    public Map<String ,Object> buildNewCache(String key,Object value,int maxElements) throws CacheException {
        if (maxElements == 0) {
            throw new CacheException("Max elements can't be zero");
        }
        Map<String,Object> cache = new LinkedHashMap<>(maxElements);
        cache.put(key,value);
        return cache;
    }

    /**
     * Method to populate fresh new Doubly Linked List for LRU
     * @param key
     * @param value
     * @param maxElements
     * @return
     * @throws CacheException
     */
    public ArrayList<Pair<String,Object>> buildNewLRUDLL(String key,Object value,int maxElements) throws CacheException {
        if (maxElements == 0) {
            throw new CacheException("Max elements can't be zero");
        }
        ArrayList<Pair<String,Object>> doublyLinkedList = new ArrayList<>(maxElements);
        doublyLinkedList.add(new Pair<>(key,value));
        return doublyLinkedList;
    }

    /**
     * Helper Method to remove last recently used element from cache
     * @param cacheData
     * @param key
     */
    private void removeLeastRecentlyUsedElement(Map<String,Object> cacheData,String key) {
        cacheData.remove(key);
    }

    /**
     * Helper method to remove last recently used element from doubly linked list
     * @param doublyLinkedList
     */
    private void removeLeastRecentlyUsedElementFromDll(ArrayList<Pair<String,Object>> doublyLinkedList) {
       doublyLinkedList.remove(0);
    }

    /**
     * This method will Check if map size overflows or not and return compressed cache
     * @param cacheManagerDto
     * @param servletContext
     * @return
     */
    public byte[] invokeLRUAndGetCache(CacheManagerDto cacheManagerDto, ServletContext servletContext) {
        Map<String,Object> cacheData = this.decompressCacheData(cacheManagerDto.getCacheName().concat(keyDelimeter).concat(mapKeySuffix),servletContext);
        /*  Necessary check for cache overflow*/
        if (cacheData.size() >= cacheManagerDto.getMaxElementsInMemory()) {
            //Remove Least Recently Used Element and Add New Element
            Pair<String,Object> pair = this.decompressLRUDLL(cacheManagerDto.getCacheName(),servletContext).get(0);
            this.removeLeastRecentlyUsedElement(cacheData,pair.getKey());
            cacheData.put(cacheManagerDto.getKey(),cacheManagerDto.getValue());
        } else {
            cacheData.put(cacheManagerDto.getKey(),cacheManagerDto.getValue());
        }
        byte[] compressedBytes = this.compressCacheData(cacheData);
        return compressedBytes;
    }

    /**
     * This Method will Check if Dll Size overflow and return compressed bytes
     * @param cacheManagerDto
     * @param servletContext
     * @return
     */
    public byte[] invokeLRUAndGetDLL(CacheManagerDto cacheManagerDto,ServletContext servletContext) {
        ArrayList<Pair<String,Object>> doublyLinkedList = this.decompressLRUDLL(cacheManagerDto.getCacheName().concat(keyDelimeter).concat(dllKeySuffix),servletContext);
        if (doublyLinkedList.size() >= cacheManagerDto.getMaxElementsInMemory()) {
            this.removeLeastRecentlyUsedElementFromDll(doublyLinkedList);
            doublyLinkedList.add(doublyLinkedList.size()-1,new Pair<>(cacheManagerDto.getKey(),cacheManagerDto.getValue()));
        } else {
            doublyLinkedList.add(new Pair<>(cacheManagerDto.getKey(),cacheManagerDto.getValue()));
        }
        byte[] compressedBytes = this.compressLRUDLL(doublyLinkedList);
        return compressedBytes;
    }

    public void scheduleTimerTask(Timer timer,CacheManagerDto cacheManagerDto,
                                  ServletContext servletContext,String key,byte[] value) {
        final int[] currentMillisecond = {0};
        ReentrantLock lock = new ReentrantLock();
        int totalMilliSecondsForProcessing = cacheManagerDto.getTimeToLive() * 1000; //In Seconds
        try {
            //Acquiring Lock so that no one can remodify
            lock.lock();
            servletContext.setAttribute(key,value);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (currentMillisecond[0] >= totalMilliSecondsForProcessing) {
                        timer.cancel();
                        servletContext.removeAttribute(cacheManagerDto.getCacheName().concat(keyDelimeter).concat(mapKeySuffix));
                        servletContext.removeAttribute(cacheManagerDto.getCacheName().concat(keyDelimeter).concat(dllKeySuffix));
                    } else {
                        currentMillisecond[0] += 1000;
                    }
                }
            },0,1000);
        } catch (Exception e) {
            logger.error("Error Occured while scheduling timer task shutting down timer");
            logger.error(e,e);
            timer.cancel();
            lock.unlock();
        }
        finally {
            timer.cancel();
            lock.unlock();
        }
    }
}
