package org.caching.server.rcs.services;

public interface ICachingService<T,T1,T2> {
    public T put(T1 key,T2 value);
}
