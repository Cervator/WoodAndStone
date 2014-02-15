package org.terasology.was.generator.plains.crop;

import org.terasology.anotherWorld.coreBiome.PlainsBiome;
import org.terasology.anotherWorld.decorator.BlockCollectionPredicate;
import org.terasology.gf.PlantType;
import org.terasology.gf.generator.BlockFloraSpawnDefinition;
import org.terasology.plantPack.crop.RiceGrowthDefinition;
import org.terasology.was.generator.Blocks;
import org.terasology.world.generator.plugin.RegisterPlugin;

import java.util.Arrays;

/**
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@RegisterPlugin
public class RicePlainsSpawnDefinition extends BlockFloraSpawnDefinition {
    public RicePlainsSpawnDefinition() {
        super(PlantType.GRASS, RiceGrowthDefinition.ID, PlainsBiome.ID, 0.8f, 0.3f,
                new BlockCollectionPredicate(Arrays.asList(Blocks.getBlock("Core:Grass"))));
    }
}
