package powercrystals.netherores.entity;

import static powercrystals.netherores.NetherOresCore.blockHellfish;
import static powercrystals.netherores.NetherOresCore.hellFishMaxHealth;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import powercrystals.netherores.world.BlockHellfish;

public class EntityHellfish extends EntitySilverfish
{
	
	public EntityHellfish(World world)
	{
		super(world);
		rand.setSeed(world.getSeed() ^ getEntityId());
		isImmuneToFire = true;
		stepHeight = 1.0F;
		hurtTime = 15;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).
		setBaseValue(hellFishMaxHealth.getDouble(12.5D));

		getEntityAttribute(SharedMonsterAttributes.movementSpeed).
		setBaseValue(0.925D + (rand.nextDouble() * .1));

		getEntityAttribute(SharedMonsterAttributes.attackDamage).
		setBaseValue(1.5D + (rand.nextDouble()));

		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).
		setBaseValue(rand.nextDouble());
	}

	@Override
	protected void updateEntityActionState()
	{
		int cooldown = allySummonCooldown;
		super.updateEntityActionState();
		allySummonCooldown = cooldown;

		if (!worldObj.isRemote)
		{
			int X;
			int Y;
			int Z;

			if (allySummonCooldown > 0)
			{
				allySummonCooldown--;

				if (allySummonCooldown == 0)
				{
					X = MathHelper.floor_double(posX);
					Y = MathHelper.floor_double(posY);
					Z = MathHelper.floor_double(posZ);

					l: for (int y = 0; y <= 5 & y >= -5; y = y <= 0 ? 1 - y : 0 - y)
						for (int x = 0; x <= 10 & x >= -10; x = x <= 0 ? 1 - x : 0 - x)
							for (int z = 0; z <= 10 & z >= -10; z = z <= 0 ? 1 - z : 0 - z)
							{
								Block block = worldObj.getBlock(X + x, Y + y, Z + z);

								if (block == blockHellfish)
								{
									if (!worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
										worldObj.setBlock(X + x, Y + y, Z + z, Blocks.netherrack, 0, 3);
									else
										worldObj.func_147480_a(X + x, Y + y, Z + z, false);
									BlockHellfish.spawnHellfish(worldObj, X + x, Y + y, Z + z);

									if (rand.nextBoolean())
										break l;
								}
							}
				}
			}

			if (entityToAttack == null && !hasPath())
			{
				X = MathHelper.floor_double(posX);
				Y = MathHelper.floor_double(posY + 0.5D);
				Z = MathHelper.floor_double(posZ);
				int direction = rand.nextInt(6);
				Block block = worldObj.getBlock(X + Facing.offsetsXForSide[direction],
						Y + Facing.offsetsYForSide[direction], Z + Facing.offsetsZForSide[direction]);

				if (block == Blocks.netherrack && rand.nextInt(3) == 0)
				{
					worldObj.setBlock(X + Facing.offsetsXForSide[direction],
							Y + Facing.offsetsYForSide[direction],
							Z + Facing.offsetsZForSide[direction], blockHellfish, 0, 0);
					spawnExplosionParticle();
					setDead();
				}
				else
					updateWanderPath();
			}
			else if (entityToAttack != null && !hasPath())
			{
				entityToAttack = null;
			}
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity par1Entity)
	{
		par1Entity.setFire(3);
		return super.attackEntityAsMob(par1Entity);
	}

	@Override
	public float getBlockPathWeight(int par1, int par2, int par3)
	{
		float d = 0;
		if (worldObj.getBlock(par1, par2 - 1, par3) == Blocks.netherrack)
			d = 10F;
		/*
		 *  Default is 0.5 - brightness (pathing to darkness).
		 *  The nether is always dark. Light means torches, means players.
		 */
		return d + worldObj.getLightBrightness(par1, par2, par3) - .5F;
	}
}
