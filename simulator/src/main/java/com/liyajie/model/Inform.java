package com.liyajie.model;

import lombok.*;

import java.util.List;

/**
 * @author Liyajie
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inform {
    private DeviceStruct deviceStruct;
    private List<EventStruct> eventStructs;
    private int maxEnvelopes;
    private String currentTime;
    private int retryCount;
    private List<ParameterValueStruct> parameterValueStructList;
}
