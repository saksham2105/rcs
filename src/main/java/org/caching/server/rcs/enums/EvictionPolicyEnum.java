package org.caching.server.rcs.enums;

public enum EvictionPolicyEnum {

    LRU("LRU");

    @Override
    public String toString() {
        return super.toString();
    }
    final String name;
    EvictionPolicyEnum(String name) {
        this.name = name;
    }
    public String getName() {return this.name;}
    public static EvictionPolicyEnum findByName(String name) {
        for(EvictionPolicyEnum evictionPolicyEnum : EvictionPolicyEnum.values()) {
            if (evictionPolicyEnum.getName().equalsIgnoreCase(name)) {
                return evictionPolicyEnum;
            }
        }
        return null;
    }

}
