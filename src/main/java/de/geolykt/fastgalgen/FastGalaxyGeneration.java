package de.geolykt.fastgalgen;

import de.geolykt.starloader.mod.Extension;

public class FastGalaxyGeneration extends Extension {
    @Override
    public void initialize() {
        this.getLogger().info("Making use of {} for asynchronous task scheduling as appropriate with the currently running version of Java.", JavaInterop.getExecutorType());
    }
}
