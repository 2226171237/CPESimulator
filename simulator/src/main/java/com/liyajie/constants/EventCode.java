package com.liyajie.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Liyajie
 */
@AllArgsConstructor
public enum EventCode {
    /**
     * 0 BOOT 首次上上电
     */
    BOOT_0("0 BOOT"),

    /**
     * 1 BOOTSTRAP 重启
     */
    BOOTSTRAP_1("1 BOOTSTRAP"),

    // 2 PERIODIC

    /**
     * 4 VALUE CHANGE
     */
    VALUE_CHANGE_4("4 VALUE CHANGE");
    

    @Getter
    private final String name;

    public static String combineEvents(EventCode... events) {
        StringBuilder builder = new StringBuilder();
        if (events.length > 0) {
            builder.append(events[0].getName());
        }
        for (int i = 1; i < events.length; i++) {
            builder.append(";");
            builder.append(events[i].getName());
        }
        return builder.toString();
    }

    public static List<String> splitEvents(String events) {
        return Arrays.asList(events.split(";").clone());
    }
}
