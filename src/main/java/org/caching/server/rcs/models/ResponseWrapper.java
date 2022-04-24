package org.caching.server.rcs.models;

import lombok.Data;

@Data
public class ResponseWrapper {
    private boolean success;
    private MessageDetails message;
    private Object data;
}
