package com.liyajie.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author Liyajie
 */
@Data
@Builder
public class Parameter {
    private String name;
    private boolean isObject;
    private boolean writeable;
    private String value;
    private String valueType;
}
