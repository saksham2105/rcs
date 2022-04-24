package org.caching.server.rcs.configurations;

import org.caching.server.rcs.interceptors.CacheInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class CacheInterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private CacheInterceptor cacheInterceptor;

    @Value("${apiPrefix:/rcs/}")
    private String apiPrefix;

    @Value("${cacheApi:cache}")
    private String cacheApi;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cacheInterceptor).addPathPatterns(apiPrefix.concat(cacheApi));
    }
}
