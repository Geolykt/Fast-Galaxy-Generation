package de.geolykt.fastgalgen.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import de.geolykt.fastgalgen.LandmarkPopulator.Landmark;
import de.geolykt.starloader.api.empire.Star;

import snoddasmannen.galimulator.guides.ppclass_0;

@Mixin(value = ppclass_0.class)
public class LandmarkMixins implements Landmark {
    @Shadow(aliases = "a")
    private snoddasmannen.galimulator.Star center;

    @Shadow(aliases = "b")
    private Map<Star, Float> distances;

    @Override
    @Unique(silent = true) // Behaves like @Intrinsic here
    public Star getCenter() {
        return (Star) this.center;
    }

    @Override
    @Unique(silent = true) // Behaves like @Intrinsic here
    public Map<Star, Float> getDistances() {
        return this.distances;
    }
}
