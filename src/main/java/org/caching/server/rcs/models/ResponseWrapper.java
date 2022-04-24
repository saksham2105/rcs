package org.caching.server.rcs.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseWrapper {
    private boolean success;
    private MessageDetails message;
    private Object data;
}
