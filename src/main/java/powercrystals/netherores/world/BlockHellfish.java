package powercrystals.netherores.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.netherores.NetherOresCore;
import powercrystals.netherores.entity.EntityHellfish;
import powercrystals.netherores.net.ServerProxy;

public class BlockHellfish extends BlockNetherrack
{
	public BlockHellfish()
	{
		setHardness(0.4F);
		setStepSound(soundTypePiston);
		setBlockName("netherores.hellfish");
		setBlockTextureName("netherrack");
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side)
	{
		return side == ForgeDirection.UP;
	}

	@Override
	public boolean canSilkHarvest()
	{
		return false;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune)
	{
		return Item.getItemFromBlock(Blocks.netherrack);
	}

	@Override
	public int quantityDropped(Random rand)
	{
		return NetherOresCore.enableHellfish.getBoolean(true) ? 0 : 1;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		spawnHellfish(world, x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean isReplaceableOreGen(World world, int x, int y, int z, Block target)
	{
		return this == target || Blocks.netherrack.isReplaceableOreGen(world, x, y, z, target);
	}

	public static void spawnHellfish(World world, int x, int y, int z)
	{
		if (!world.isRemote &&
				!ServerProxy.isChunkPopulating(world, x, y, z) &&
				NetherOresCore.enableHellfish.getBoolean(true))
		{
			EntityHellfish hellfish = new EntityHellfish(world);
			hellfish.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
			world.spawnEntityInWorld(hellfish);
			hellfish.spawnExplosionParticle();
		}
	}
}
