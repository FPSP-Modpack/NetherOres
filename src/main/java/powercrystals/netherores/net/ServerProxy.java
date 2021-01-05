package powercrystals.netherores.net;

import cofh.asmhooks.event.ModPopulateChunkEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

public class ServerProxy
{
	private static HashMap<World, HashSet<ChunkCoordIntPair>> chunks =
			new HashMap<World, HashSet<ChunkCoordIntPair>>();
	public static boolean isChunkPopulating(World world, int x, int y, int z)
	{
		return chunks.containsKey(world) && chunks.get(world).contains(new ChunkCoordIntPair(x >> 4, z >> 4));
	}

	public void load()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void evt(PopulateChunkEvent.Pre evt)
	{
		if (!chunks.containsKey(evt.world))
			chunks.put(evt.world, new HashSet<ChunkCoordIntPair>());
		chunks.get(evt.world).add(new ChunkCoordIntPair(evt.chunkX, evt.chunkZ));
	}

	@SubscribeEvent
	public void evt(PopulateChunkEvent.Post evt)
	{
		if (!chunks.containsKey(evt.world))
			return;
		chunks.get(evt.world).remove(new ChunkCoordIntPair(evt.chunkX, evt.chunkZ));
	}

	@SubscribeEvent
	public void evt(ModPopulateChunkEvent.Pre evt)
	{
		if (!chunks.containsKey(evt.world))
			chunks.put(evt.world, new HashSet<ChunkCoordIntPair>());
		chunks.get(evt.world).add(new ChunkCoordIntPair(evt.chunkX, evt.chunkZ));
	}

	@SubscribeEvent
	public void evt(ModPopulateChunkEvent.Post evt)
	{
		if (!chunks.containsKey(evt.world))
			return;
		chunks.get(evt.world).remove(new ChunkCoordIntPair(evt.chunkX, evt.chunkZ));
	}
}
