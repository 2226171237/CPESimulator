package com.liyajie.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Liyajie
 */
@NoArgsConstructor
@Builder
@Data
public class Inform {
    private DeviceStruct deviceStruct;
    private List<EventStruct> eventStructs;
    private int maxEnvelopes;
    private String currentTime;
    private int retryCount;
    private List<ParameterValueStruct> parameterValueStructList;
}
