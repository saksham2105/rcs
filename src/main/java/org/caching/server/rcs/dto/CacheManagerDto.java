package org.caching.server.rcs.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CacheManagerDto {
    private String evictionPolicy;
    private Integer maxElementsInMemory;
    private Integer timeToLive;
    private String cacheName;
    private String key;
    private Object value;
}
