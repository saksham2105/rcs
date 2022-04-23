package org.caching.server.rcs.enums;

public enum ServletContainerEnum {
    SERVLETCONTEXT("SERVLETCONTEXT");

    @Override
    public String toString() {
        return super.toString();
    }
    final String name;
    ServletContainerEnum(String name) {
        this.name = name;
    }
    public String getName() {return this.name;}
    public static ServletContainerEnum findByName(String name) {
        for(ServletContainerEnum servletContainerEnum : ServletContainerEnum.values()) {
            if (servletContainerEnum.getName().equalsIgnoreCase(name)) {
                return servletContainerEnum;
            }
        }
        return null;
    }

}
