package powercrystals.netherores.world;

import static powercrystals.netherores.NetherOresCore.blockHellfish;
import static powercrystals.netherores.NetherOresCore.enableHellfish;
import static powercrystals.netherores.NetherOresCore.enableWorldGen;
import static powercrystals.netherores.NetherOresCore.forceOreSpawn;
import static powercrystals.netherores.NetherOresCore.getOreBlock;
import static powercrystals.netherores.NetherOresCore.hellFishMaxY;
import static powercrystals.netherores.NetherOresCore.hellFishMinY;
import static powercrystals.netherores.NetherOresCore.hellFishPerChunk;
import static powercrystals.netherores.NetherOresCore.hellFishPerGroup;
import static powercrystals.netherores.NetherOresCore.hellFishRetrogen;
import static powercrystals.netherores.NetherOresCore.worldGenAllDimensions;

import java.util.Random;

import cofh.api.world.IFeatureGenerator;
import net.minecraft.world.World;
import powercrystals.netherores.ores.Ores;

public class NetherOresWorldGenHandler implements IFeatureGenerator
{
	private final String name = "NetherOres:WorldGen";

	@Override
	public String getFeatureName()
	{
		return name;
	}

	@Override
	public boolean generateFeature(Random random, int chunkX, int chunkZ, World world, boolean newGen)
	{
		if (world.provider.dimensionId == -1 || worldGenAllDimensions.getBoolean(false))
		{
			generateNether(world, random, chunkX * 16, chunkZ * 16, newGen);
			return true;
		}
		return false;
	}

	private void generateNether(World world, Random random, int chunkX, int chunkZ, boolean newGen)
	{
		if (enableWorldGen.getBoolean(true))
			for (Ores o : Ores.values()) if (o.getForced() || 
					((o.isRegisteredSmelting() ||
							o.isRegisteredMacerator() ||
							forceOreSpawn.getBoolean(false)) &&
							!o.getDisabled())) if (newGen || o.getRetroGen())
				for (int i = o.getGroupsPerChunk(); i --> 0; )
				{
					int x = chunkX + random.nextInt(16);
					int y = o.getMinY() + random.nextInt(o.getMaxY() - o.getMinY());
					int z = chunkZ + random.nextInt(16);
					new WorldGenNetherOres(getOreBlock(o.getBlockIndex()),
							o.getMetadata(), o.getBlocksPerGroup()).generate(world, random, x, y, z);
				}

		if (enableHellfish.getBoolean(true) && (newGen || (!newGen && hellFishRetrogen.getBoolean())))
		{
			int hellfishVein = hellFishPerGroup.getInt();
			int minY = hellFishMinY.getInt(), maxY = hellFishMaxY.getInt();

			for (int i = hellFishPerChunk.getInt(); i --> 0; )
			{
				int x = chunkX + random.nextInt(16); 
				int y = minY + random.nextInt(maxY - minY);
				int z = chunkZ + random.nextInt(16);
				new WorldGenNetherOres(blockHellfish, 0, hellfishVein).generate(world, random, x, y, z);
			}
		}
	}
}
