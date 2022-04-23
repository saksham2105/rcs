package org.caching.server.rcs.services.impl;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.caching.server.rcs.enums.ServletContainerEnum;
import org.caching.server.rcs.factory.ServletContainerFactory;
import org.caching.server.rcs.services.ICachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.ServletContext;

@Repository("cachingService")
public class CachingService implements ICachingService<Object,String,Object> {

    private static Logger logger = LogManager.getLogger(CachingService.class);

    @Autowired
    private ServletContainerFactory servletContainerFactory;

    /**
     * A Service to Cache Data in Embedded Container
     * @return
     */
    @Override
    public Object put(String key, Object value) {
        //TODO : Compress HashMap and DoublyLinkedList and Store in ServletContext
        logger.info("Inside put for cache key "+key);
        ServletContext servletContext = (ServletContext) this.servletContainerFactory.getServletContainer(ServletContainerEnum.SERVLETCONTEXT);
        servletContext.setAttribute(key,value);
        return null;
    }
}
