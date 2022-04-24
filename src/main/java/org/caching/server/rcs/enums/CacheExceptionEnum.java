package org.caching.server.rcs.enums;

public enum CacheExceptionEnum {
    GENERIC("500","Something went wrong"),
    CACHE("RCS500","Exception occured from while caching data");

    @Override
    public String toString() {
        return super.toString();
    }
    final String code;
    final String text;
    CacheExceptionEnum(String code,String text) {
        this.text = text;
        this.code = code;
    }
    public String getCode() {return this.code;}
    public String getText() {return this.text;}
}
