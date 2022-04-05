package com.liyajie.model;

import lombok.*;

import java.util.List;

/**
 * @author Liyajie
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetParameterValuesResponse {
    private List<ParameterValueStruct> parameterValueStructList;
}
