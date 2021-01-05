package powercrystals.netherores.ores;

import static powercrystals.netherores.NetherOresCore.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.netherores.NetherOresCore;
import powercrystals.netherores.entity.EntityArmedOre;
import powercrystals.netherores.gui.NOCreativeTab;
import powercrystals.netherores.world.BlockHellfish;

public class BlockNetherOres extends Block implements INetherOre {
	private final int _blockIndex;
	private final IIcon[] _netherOresIcons = new IIcon[16];
	private final Ores[] _ores;

	public BlockNetherOres(int blockIndex) {
		super(Blocks.netherrack.getMaterial());
		setHardness(5.0F);
		setResistance(1.0F);
		setBlockName("netherores.ore." + blockIndex);
		setStepSound(soundTypeStone);
		setCreativeTab(NOCreativeTab.tab);
		_blockIndex = blockIndex;
		Ores[] ores = Ores.values();
		_ores = Arrays.copyOfRange(ores, blockIndex * 16, Math.min(blockIndex * 16 + 16, ores.length));
	}

	public int getBlockIndex() {
		return _blockIndex;
	}

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		for (int i = 0, e = _ores.length; i < e; i++) {
			_netherOresIcons[i] = ir.registerIcon("netherores:" + _ores[i].name());
		}
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return _netherOresIcons[meta];
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		int count = quantityDropped(metadata, fortune, world.rand);

		ItemStack stack = new ItemStack(getItemDropped(metadata, world.rand, fortune), 1, damageDropped(metadata));
		if (metadata < _ores.length && _ores[metadata].getDropItem() != null) {
			stack = _ores[metadata].getDropItem();
		} else {
			count = 1;
		}

		for (int i = 0; i < count; i++) {
			ret.add(stack.copy());
		}
		return ret;
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		int i = _ores[meta].getDropCount();
		if (i > 1) {
			int j = fortune > 0 ? random.nextInt(fortune + 1) + 1 : 1;
			return random.nextInt(i * j) + j;
		}
		return 1;
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return true;
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}

	private Random rand = new Random();

	@Override
	public int getExpDrop(IBlockAccess world, int meta, int fortune) {
		int exp = 0;
		if (_ores[meta].getDropCount() > 1 && _ores[meta].getDropItem() != null) {
			exp = _ores[meta].getExp();
			exp = MathHelper.getRandomIntegerInRange(rand, exp, exp * 2 + 1) + (exp > 1 ? rand.nextInt(exp) : 0);
		}
		return exp;
	}

	private ThreadLocal<Boolean> explode = new ThreadLocal<Boolean>(), willAnger = new ThreadLocal<Boolean>();

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		boolean silky = player == null || !EnchantmentHelper.getSilkTouchModifier(player);
		explode.set(silky);
		willAnger.set(true);
		boolean r = super.removedByPlayer(world, player, x, y, z, willHarvest);
		if (silky || silkyStopsPigmen.getBoolean(true))
			angerPigmen(player, world, x, y, z);
		willAnger.set(false);
		explode.set(true);
		if (enableFortuneExplosions.getBoolean(true)) {
			int i = EnchantmentHelper.getFortuneModifier(player);
			i = i > 0 ? world.rand.nextInt(i) : 0;
			while (i-- > 0)
				checkExplosionChances(this, world, x, y, z);
		}
		return r;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (explode.get() != Boolean.FALSE)
			checkExplosionChances(this, world, x, y, z);
		if (willAnger.get() != Boolean.TRUE)
			angerPigmen(world, x, y, z);
		if (hellFishFromOre.getBoolean(false))
			if (world.rand.nextInt(10000) < hellFishFromOreChance.getInt())
				BlockHellfish.spawnHellfish(world, x, y, z);
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
		explode.set(false);
		willAnger.set(enableMobsAngerPigmen.getBoolean(true) || explosion == null
				|| !(explosion.getExplosivePlacedBy() instanceof EntityLiving));
		super.onBlockExploded(world, x, y, z, explosion);
		willAnger.set(true);
		explode.set(true);
		if (enableExplosionChainReactions.getBoolean(true))
			checkExplosionChances(this, world, x, y, z);
	}

	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.UP;
	}

	public static void checkExplosionChances(Block block, World world, int x, int y, int z) {
		if (!world.isRemote && enableExplosions.getBoolean(true)) {
			for (int xOffset = -1; xOffset <= 1; xOffset++) {
				for (int yOffset = -1; yOffset <= 1; yOffset++) {
					for (int zOffset = -1; zOffset <= 1; zOffset++) {
						if ((xOffset | yOffset | zOffset) == 0)
							continue;

						int tx = x + xOffset;
						int ty = y + yOffset;
						int tz = z + zOffset;

						block = world.getBlock(tx, ty, tz);
						if (block instanceof INetherOre && world.rand.nextInt(1000) < explosionProbability.getInt()) {
							EntityArmedOre eao = new EntityArmedOre(world, tx + 0.5, ty + 0.5, tz + 0.5, block);
							world.spawnEntityInWorld(eao);

							world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "game.tnt.primed", 1.0F, 1.0F);
						}
					}
				}
			}
		}
	}

	public static void angerPigmen(EntityPlayer player, World world, int x, int y, int z) {
		final int _aggroRange = NetherOresCore.pigmenAggroRange.getInt();
		if (enableAngryPigmen.getBoolean(true)) {
			List<EntityPigZombie> list = world.getEntitiesWithinAABB(EntityPigZombie.class,
					AxisAlignedBB.getBoundingBox(x - _aggroRange, y - _aggroRange, z - _aggroRange, x + _aggroRange + 1,
							y + _aggroRange + 1, z + _aggroRange + 1));
			for (int j = 0; j < list.size(); j++) {
				Method becomeAngryAt = ReflectionHelper.findMethod(EntityPigZombie.class, null,
						new String[] { "becomeAngryAt", "func_70835_c" }, Entity.class);
				try {
					becomeAngryAt.invoke(list.get(j), player);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void angerPigmen(World world, int x, int y, int z) {
		angerPigmen(null, world, x, y, z);
	}
}
