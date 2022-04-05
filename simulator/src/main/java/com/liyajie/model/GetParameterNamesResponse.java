package com.liyajie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Liyajie
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetParameterNamesResponse {
    private List<ParameterInfoStruct> parameterInfoStructList;
}
