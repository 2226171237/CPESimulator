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
@AllArgsConstructor
@NoArgsConstructor
public class ParameterInfoStruct {
    private String name;
    private boolean writeable;
}
