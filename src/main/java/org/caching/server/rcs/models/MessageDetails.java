package org.caching.server.rcs.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDetails {
    private String code;
    private String text;
}
