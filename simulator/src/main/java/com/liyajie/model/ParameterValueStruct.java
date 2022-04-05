package com.liyajie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Liyajie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParameterValueStruct {
    private String name;
    private String value;
    private String type;
}
