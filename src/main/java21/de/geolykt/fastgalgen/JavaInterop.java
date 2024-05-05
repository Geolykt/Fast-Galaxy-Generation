package de.geolykt.fastgalgen;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;

public class JavaInterop {
    public static String getExecutorType() {
        return "VirtualThreadPerTask";
    }

    @NotNull
    public static Executor getExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
