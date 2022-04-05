package com.liyajie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Liyajie
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStruct {
    private String manufacturer;
    private String oui;
    private String productClass;
    private String serialNumber;
}
