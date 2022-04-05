package com.liyajie.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Liyajie
 */
@AllArgsConstructor
@Getter
public enum CpeMethod {
    /**
     * Inform
     */
    INFORM("Inform"),

    /**
     * GetParameterNames
     */
    GET_PARAMETER_NAMES("GetParameterNames"),

    /**
     * GetParameterValues
     */
    GET_PARAMETER_VALUES("GetParameterValues"),

    /**
     * SetParameterValues
     */
    SET_PARAMETER_VALUES("SetParameterValues"),

    /**
     * AddObject
     */
    ADD_OBJECT("AddObject"),

    /**
     * DeleteObject
     */
    DELETE_OBJECT("DeleteObject"),

    /**
     * Download
     */
    DOWNLOAD("Download");

    private final String name;
}
