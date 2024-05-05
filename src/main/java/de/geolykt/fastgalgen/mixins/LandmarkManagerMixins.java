package de.geolykt.fastgalgen.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import de.geolykt.fastgalgen.FastGalaxyGeneration;
import de.geolykt.fastgalgen.JavaInterop;
import de.geolykt.fastgalgen.LandmarkPopulator;
import de.geolykt.fastgalgen.LandmarkPopulator.Landmark;
import de.geolykt.starloader.api.Galimulator;
import de.geolykt.starloader.api.empire.Star;

import snoddasmannen.galimulator.Space;
import snoddasmannen.galimulator.guides.LandmarkManager;
import snoddasmannen.galimulator.guides.ppclass_0;

@Mixin(LandmarkManager.class)
public class LandmarkManagerMixins {
    /**
     * As landmarks are stored in a O(n) collections [or a List in more plain terms],
     * one should be aware that not too many landmarks can be stored at any point in time.
     */
    @Unique
    private static final int LANDMARK_DENSITY = 5;

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger(FastGalaxyGeneration.class);

    @Shadow(aliases = "a")
    private static ArrayList<ppclass_0> landmarks;

    @Overwrite
    public static void regenerateLandmarks() {
        LandmarkManagerMixins.LOGGER.info("Picking landmarks");
        LandmarkManagerMixins.landmarks = new ArrayList<>();

        final float negativeHalfWidth = -Galimulator.getMap().getWidth() / 2;
        final float negativeHalfHeight = -Galimulator.getMap().getHeight() / 2;
        final float boardFactorX = Galimulator.getMap().getWidth() / LANDMARK_DENSITY;
        final float boardFactorY = Galimulator.getMap().getHeight() / LANDMARK_DENSITY;
        for (int x = 0; x < LandmarkManagerMixins.LANDMARK_DENSITY; x++) {
            gridLoop:
            for (int y = 0; y < LandmarkManagerMixins.LANDMARK_DENSITY; y++) {
                Star nearestStar = Galimulator.getNearestStar(x * boardFactorX + negativeHalfWidth, y * boardFactorY + negativeHalfHeight, Galimulator.getMap().getWidth());
                if (nearestStar == null) {
                    continue;
                }
                for (ppclass_0 landmark : LandmarkManagerMixins.landmarks) {
                    if (landmark.a == nearestStar) {
                        continue gridLoop;
                    }
                }
                LandmarkManagerMixins.landmarks.add(new ppclass_0((snoddasmannen.galimulator.Star) nearestStar));
            }
        }

        if (LandmarkManagerMixins.landmarks.isEmpty()) {
            LandmarkManagerMixins.landmarks = null;
            throw new IllegalStateException("No landmarks could be set. This edge case could occur when no stars exist in the galaxy. This hints towards a mod incompatibility problem");
        }

        List<CompletableFuture<Void>> alltasks = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger();
        Space.h("(0 of " + LandmarkManagerMixins.landmarks.size() + ")");
        for (ppclass_0 landmark : LandmarkManagerMixins.landmarks) {
            alltasks.add(CompletableFuture.runAsync(() -> {
                LandmarkPopulator.populateLandmark((Landmark) landmark);
                Space.h("(" + counter.incrementAndGet() + " of " + LandmarkManagerMixins.landmarks.size() + ")");
            }, JavaInterop.getExecutor()));
        }
        CompletableFuture.allOf(alltasks.toArray(new CompletableFuture[0])).join();
    }
}