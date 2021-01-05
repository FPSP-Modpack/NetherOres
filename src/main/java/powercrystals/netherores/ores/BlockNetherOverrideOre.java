package powercrystals.netherores.ores;

import static powercrystals.netherores.NetherOresCore.*;
import static powercrystals.netherores.ores.BlockNetherOres.*;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.netherores.NetherOresCore;
import powercrystals.netherores.world.BlockHellfish;

public class BlockNetherOverrideOre extends Block implements INetherOre
{
	protected Block _override;

	public BlockNetherOverrideOre(Block override)
	{
		super(override.getMaterial());
		_override = override;
		setStepSound(override.stepSound);
		ObfuscationReflectionHelper.setPrivateValue(ItemBlock.class,
				(ItemBlock)Item.getItemFromBlock(_override), this, "field_150939_a");
	}

	public void setOverride()
	{
		// override this.
		// Ex. Blocks.quartz_ore = this;
	}
	
	public void resetOverride()
	{
		// override this.
		// Ex. Blocks.quartz_ore = _override;
	}
	
	@Override
	public boolean isAssociatedBlock(Block block)
	{
		return block == this | block == _override || _override.isAssociatedBlock(block);
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj == this | obj == _override;
	}

	@Override
	public int hashCode()
	{
		return _override.hashCode();
	}

	/**
	 * Overrides to proxy to the overridden block
	 */

	@Override
	public CreativeTabs getCreativeTabToDisplayOn()
	{
		return _override.getCreativeTabToDisplayOn();
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta)
	{
		return _override.canHarvestBlock(player, meta);
	}

	@Override
	public Item getItemDropped(int a, Random rand, int b)
	{
		return _override.getItemDropped(a, rand, b);
	}

	@Override
	public int quantityDropped(Random rand)
	{
		return _override.quantityDropped(rand);
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random rand)
	{
		return _override.quantityDroppedWithBonus(fortune, rand);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune)
	{
		_override.dropBlockAsItemWithChance(world, x, y, z, meta, chance, fortune);
	}

	@Override
	public int getExpDrop(IBlockAccess world, int a, int b)
	{
		resetOverride();
		int exp = _override.getExpDrop(world, a, b);
		setOverride();
		return exp;
	}

	@Override
	public void dropXpOnBlockBreak(World p_149657_1_, int p_149657_2_, int p_149657_3_, int p_149657_4_, int p_149657_5_)
	{
		_override.dropXpOnBlockBreak(p_149657_1_, p_149657_2_, p_149657_3_, p_149657_4_, p_149657_5_);
	}

	@Override
	public int damageDropped(int meta)
	{
		return _override.damageDropped(meta);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		return _override.getDrops(world, x, y, z, metadata, fortune);
	}

	@Override
	public String getUnlocalizedName()
	{
		return _override.getUnlocalizedName();
	}

	@Override
	public String getLocalizedName()
	{
		return _override.getLocalizedName();
	}

	@Override
	public boolean getEnableStats()
	{
		return _override.getEnableStats();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon func_149735_b(int a, int b)
	{
		return _override.func_149735_b(a, b);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
	{
		return _override.getIcon(world, x, y, z, side);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int a, int b)
	{
		return _override.getIcon(a, b);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register)
	{
		_override.registerBlockIcons(register);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName()
	{
		return _override.getItemIconName();
	}

	@Override
	public boolean isBurning(IBlockAccess world, int x, int y, int z)
	{
		return _override.isBurning(world, x, y, z);
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	{
		return _override.getFlammability(world, x, y, z, face);
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	{
		return _override.isFlammable(world, x, y, z, face);
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face)
	{
		return _override.getFireSpreadSpeed(world, x, y, z, face);
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		return _override.canSilkHarvest(world, player, x, y, z, metadata);
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z,
			double explosionX, double explosionY, double explosionZ)
	{
		return _override.getExplosionResistance(entity, world, x, y, z, explosionX, explosionY, explosionZ);
	}

	@Override
	public void onBlockClicked(World p_149699_1_, int p_149699_2_, int p_149699_3_, int p_149699_4_, EntityPlayer p_149699_5_)
	{
		_override.onBlockClicked(p_149699_1_, p_149699_2_, p_149699_3_, p_149699_4_, p_149699_5_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
	{
		_override.randomDisplayTick(p_149734_1_, p_149734_2_, p_149734_3_, p_149734_4_, p_149734_5_);
	}

	@Override
	public void onBlockDestroyedByPlayer(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_, int p_149664_5_)
	{
		_override.onBlockDestroyedByPlayer(p_149664_1_, p_149664_2_, p_149664_3_, p_149664_4_, p_149664_5_);
	}

	@Override
    public float getBlockHardness(World p_149712_1_, int p_149712_2_, int p_149712_3_, int p_149712_4_)
    {
        return _override.getBlockHardness(p_149712_1_, p_149712_2_, p_149712_3_, p_149712_4_);
    }

	@Override
	public float getExplosionResistance(Entity p_149638_1_)
	{
		return _override.getExplosionResistance(p_149638_1_);
	}

	@Override
	public int tickRate(World p_149738_1_)
	{
		return _override.tickRate(p_149738_1_);
	}

	@Override
	public boolean getTickRandomly()
	{
		return _override.getTickRandomly();
	}

	@Override
	public void velocityToAddToEntity(World p_149640_1_, int p_149640_2_, int p_149640_3_, int p_149640_4_, Entity p_149640_5_, Vec3 p_149640_6_)
	{
		_override.velocityToAddToEntity(p_149640_1_, p_149640_2_, p_149640_3_, p_149640_4_, p_149640_5_, p_149640_6_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess p_149677_1_, int p_149677_2_, int p_149677_3_, int p_149677_4_)
	{
		return _override.getMixedBrightnessForBlock(p_149677_1_, p_149677_2_, p_149677_3_, p_149677_4_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
		return _override.getRenderBlockPass();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_, int p_149633_4_)
	{
		return _override.getSelectedBoundingBoxFromPool(p_149633_1_, p_149633_2_, p_149633_3_, p_149633_4_);
	}

	@Override
	public boolean isCollidable()
	{
		return _override.isCollidable();
	}

	@Override
	public boolean canCollideCheck(int meta, boolean boat)
	{
		return _override.canCollideCheck(meta, boat);
	}

	@Override
	public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_)
	{
		return _override.canPlaceBlockAt(p_149742_1_, p_149742_2_, p_149742_3_, p_149742_4_);
	}

	@Override
	public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
	{
		onNeighborBlockChange(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_, Blocks.air);
		_override.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
	}

	@Override
	public void onEntityWalking(World p_149724_1_, int p_149724_2_, int p_149724_3_, int p_149724_4_, Entity p_149724_5_)
	{
		_override.onEntityWalking(p_149724_1_, p_149724_2_, p_149724_3_, p_149724_4_, p_149724_5_);
	}

	@Override
	public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
	{
		_override.onEntityCollidedWithBlock(p_149670_1_, p_149670_2_, p_149670_3_, p_149670_4_, p_149670_5_);
	}

	@Override
	public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
	{
		_override.updateTick(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_, p_149674_5_);
	}

	@Override
	public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		return _override.onBlockActivated(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, p_149727_5_, 0, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void onBlockDestroyedByExplosion(World p_149723_1_, int p_149723_2_, int p_149723_3_, int p_149723_4_, Explosion p_149723_5_)
	{
		_override.onBlockDestroyedByExplosion(p_149723_1_, p_149723_2_, p_149723_3_, p_149723_4_, p_149723_5_);
	}

	@Override
	public MapColor getMapColor(int a)
	{
		return _override.getMapColor(a);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess p_149709_1_, int p_149709_2_, int p_149709_3_, int p_149709_4_, int p_149709_5_)
	{
		return _override.isProvidingWeakPower(p_149709_1_, p_149709_2_, p_149709_3_, p_149709_4_, p_149709_5_);
	}

	@Override
	public boolean canProvidePower()
	{
		return _override.canProvidePower();
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer p_149737_1_, World p_149737_2_, int p_149737_3_, int p_149737_4_, int p_149737_5_)
	{
		return _override.getPlayerRelativeBlockHardness(p_149737_1_, p_149737_2_, p_149737_3_, p_149737_4_, p_149737_5_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return _override.getBlockColor();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int p_149741_1_)
	{
		return _override.getRenderColor(p_149741_1_);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess p_149720_1_, int p_149720_2_, int p_149720_3_, int p_149720_4_)
	{
		return _override.colorMultiplier(p_149720_1_, p_149720_2_, p_149720_3_, p_149720_4_);
	}

	/*
	 * These methods can cause loops.
	 */

	private ThreadLocal<Boolean> calling = new ThreadLocal<Boolean>();

	@Override
	public void harvestBlock(World world, EntityPlayer player, int p_149636_3_, int p_149636_4_, int p_149636_5_, int p_149636_6_)
	{
		if (calling.get() == Boolean.TRUE)
			return;
		calling.set(Boolean.TRUE);
		resetOverride();
		_override.harvestBlock(world, player, p_149636_3_, p_149636_4_, p_149636_5_, p_149636_6_);
		setOverride();
		calling.set(null);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		if (calling.get() == Boolean.TRUE)
			return _override.getLightValue();
		calling.set(Boolean.TRUE);
		int r = _override.getLightValue(world, x, y, z);
		calling.set(null);
		return r;
	}

	/**
	 * NetherOre behavior
	 */

	private ThreadLocal<Boolean> explode = new ThreadLocal<Boolean>(),
			willAnger = new ThreadLocal<Boolean>();

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
	{
		boolean silky = player == null || !EnchantmentHelper.getSilkTouchModifier(player);
		explode.set(silky);
		willAnger.set(true);
		boolean r = _override.removedByPlayer(world, player, x, y, z, willHarvest);
		if (silky || NetherOresCore.silkyStopsPigmen.getBoolean(true))
			angerPigmen(player, world, x, y, z);
		willAnger.set(false);
		explode.set(true);
		if (enableFortuneExplosions.getBoolean(true))
		{
			int i = EnchantmentHelper.getFortuneModifier(player);
			i = i > 0 ? world.rand.nextInt(i) : 0;
			while (i --> 0)
				checkExplosionChances(this, world, x, y, z);
		}
		return r;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		if (explode.get() != Boolean.FALSE)
			checkExplosionChances(this, world, x, y, z);
		if (willAnger.get() != Boolean.TRUE)
			angerPigmen(world, x, y, z);
		if (hellFishFromOre.getBoolean(false))
			if (world.rand.nextInt(10000) < hellFishFromOreChance.getInt())
				BlockHellfish.spawnHellfish(world, x, y, z);
		_override.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
	{
		explode.set(false);
		willAnger.set(NetherOresCore.enableMobsAngerPigmen.getBoolean(true) ||
				explosion == null || !(explosion.getExplosivePlacedBy() instanceof EntityLiving));
		resetOverride();
		_override.onBlockExploded(world, x, y, z, explosion);
		setOverride();
		willAnger.set(true);
		explode.set(true);
		if (NetherOresCore.enableExplosionChainReactions.getBoolean(true))
			checkExplosionChances(this, world, x, y, z);
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side)
	{
		return side == ForgeDirection.UP;
	}
}
