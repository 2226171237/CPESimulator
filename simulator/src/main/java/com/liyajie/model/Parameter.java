package com.liyajie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Liyajie
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
    private String name;
    private boolean isObject;
    private boolean writeable;
    private String value;
    private String valueType;
}
