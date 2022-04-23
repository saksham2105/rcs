package org.caching.server.rcs.factory;

import org.caching.server.rcs.enums.ServletContainerEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Component
public class ServletContainerFactory {
    @Autowired
    private ServletContext servletContext;

    public Object getServletContainer(ServletContainerEnum servletContainerEnum) {
        switch (servletContainerEnum) {
            case SERVLETCONTEXT:
                return servletContext;
            default:
                return null;
        }
    }
}
