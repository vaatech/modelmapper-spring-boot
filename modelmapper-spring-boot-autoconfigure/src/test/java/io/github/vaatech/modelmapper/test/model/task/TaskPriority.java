package io.github.vaatech.modelmapper.test.model.task;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum TaskPriority {
    CRITICAL(50),
    MAJOR(40),
    MODERATE(30),
    MINOR(20),
    NONE(null);

    private static final Map<Integer, TaskPriority> ALL_VALUES = new HashMap<>();

    static {
        EnumSet.allOf(TaskPriority.class).forEach(s -> ALL_VALUES.put(s.level, s));
    }

    private final Integer level;

    TaskPriority(Integer level) {
        this.level = level;
    }

    public static TaskPriority valueOf(final Integer value) {
        return ALL_VALUES.get(value);
    }

    public Integer level() {
        return level;
    }
}
