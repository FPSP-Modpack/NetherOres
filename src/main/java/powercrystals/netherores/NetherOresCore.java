package powercrystals.netherores;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;
import static powercrystals.netherores.NetherOresCore.dependencies;
import static powercrystals.netherores.NetherOresCore.modId;
import static powercrystals.netherores.NetherOresCore.modName;
import static powercrystals.netherores.NetherOresCore.version;

import java.io.File;
import java.util.List;
import java.util.Map;

import cofh.CoFHCore;
import cofh.core.CoFHProps;
import cofh.core.world.WorldHandler;
import cofh.lib.util.RegistryUtils;
import cofh.mod.BaseMod;
import cofh.mod.updater.UpdateManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.CustomProperty;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import powercrystals.netherores.entity.EntityArmedOre;
import powercrystals.netherores.entity.EntityHellfish;
import powercrystals.netherores.net.ServerProxy;
import powercrystals.netherores.ores.BlockNetherOres;
import powercrystals.netherores.ores.BlockNetherOverrideOre;
import powercrystals.netherores.ores.ItemBlockNetherOre;
import powercrystals.netherores.ores.Ores;
import powercrystals.netherores.world.BlockHellfish;
import powercrystals.netherores.world.NetherOresWorldGenHandler;
//this import brought to you by the department of redundancies department, the department that brought you this import

@Mod(modid = modId, name = modName, version = version, dependencies = dependencies,
customProperties = @CustomProperty(k = "cofhversion", v = "true"))
public class NetherOresCore extends BaseMod
{
	public static final String modId = "NetherOres";
	public static final String modName = "Nether Ores";
	public static final String version = "@VERSION@";
	public static final String dependencies = CoFHCore.version_group;

	public static final String mobTextureFolder = "netherores:textures/mob/";

	public static Block[] blockNetherOres = new Block[(Ores.values().length+15) / 16];
	public static Block blockHellfish;

	public static Property enableWorldGen;
	public static Property enableExplosions;
	public static Property explosionPower;
	public static Property explosionProbability;
	public static Property enableExplosionChainReactions;
	public static Property enableFortuneExplosions;
	public static Property enableAngryPigmen;
	public static Property pigmenAggroRange;
	public static Property silkyStopsPigmen;
	public static Property enableMobsAngerPigmen;
	public static Property enableHellfish;

	public static Property enableSmeltToOres;
	public static Property enableStandardFurnaceRecipes;
	public static Property enableMaceratorRecipes;
	public static Property enablePulverizerRecipes;
	public static Property enableInductionSmelterRecipes;
	public static Property enableGrinderRecipes;
	public static Property requireSilkTouch;

	public static Property forceOreSpawn;
	public static Property worldGenAllDimensions;
	public static Property enableHellQuartz;

	public static Property hellFishFromOre;
	public static Property hellFishFromOreChance;
	public static Property hellFishPerChunk;
	public static Property hellFishPerGroup;
	public static Property hellFishMinY;
	public static Property hellFishMaxY;
	public static Property hellFishRetrogen;
	public static Property hellFishMaxHealth;

	public static ConfigCategory overrideOres;
	private static Configuration config;

	@SidedProxy(clientSide="powercrystals.netherores.net.ClientProxy",serverSide="powercrystals.netherores.net.ServerProxy")
	public static ServerProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt)
	{
		setConfigFolderBase(evt.getModConfigurationDirectory());
		loadConfig(getCommonConfig());
		loadLang();

		for (int i = 0, e = blockNetherOres.length; i < e; ++i)
		{
			Block b = blockNetherOres[i] = new BlockNetherOres(i);
			GameRegistry.registerBlock(b, ItemBlockNetherOre.class, b.getUnlocalizedName());
		}

		blockHellfish = new BlockHellfish();
		GameRegistry.registerBlock(blockHellfish, ItemBlock.class, "netherOresBlockHellfish");
		GameRegistry.registerCustomItemStack("netherOresBlockHellfish", new ItemStack(blockHellfish));

		if (enableHellQuartz.getBoolean(true))
		{
			BlockNetherOverrideOre quartz = new BlockNetherOverrideOre(Blocks.quartz_ore) {
				@Override
				public void setOverride()
				{
					Blocks.quartz_ore = this;
				}

				@Override
				public void resetOverride()
				{
					Blocks.quartz_ore = _override;
				}
			};
			Blocks.quartz_ore = quartz;
			RegistryUtils.overwriteEntry(Block.blockRegistry, "minecraft:quartz_ore", quartz);
		}

		for (Ores o : Ores.values())
		{
			o.load();
		}

		EntityRegistry.registerModEntity(EntityArmedOre.class, "ArmedOre", 0, this, 80, 5, false);
		EntityRegistry.registerModEntity(EntityHellfish.class, "netherOresHellfish", 1, this, 160, 5, true);
	}

	@EventHandler
	public void load(FMLInitializationEvent evt)
	{
		WorldHandler.instance.registerFeature(new NetherOresWorldGenHandler());

		proxy.load();

		//UpdateManager.registerUpdater(new UpdateManager(this, null, CoFHProps.DOWNLOAD_URL));
	}

	@EventHandler
	public void handleIMC(IMCEvent e)
	{
		processIMC(e.getMessages());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		if (!enableSmeltToOres.getBoolean(true))
			Ores.Coal.registerSmelting(new ItemStack(Items.coal));  // If not smelting to ore, smelt coal ore to coal.
		Ores.Coal.setDropItem(new ItemStack(Items.coal));

		for (Ores ore : Ores.values()) {
			String oreName;
			oreName = ore.getOreName(); // Ore
			if (enableSmeltToOres.getBoolean(true) && OreDictionary.getOres(oreName).size() > 0)
				registerOreDictOre(ore, oreName, OreDictionary.getOres(oreName).get(0));

			oreName = ore.getSmeltName(); // Ingot
			if (OreDictionary.getOres(oreName).size() > 0)
				registerOreDictSmelt(ore, oreName, OreDictionary.getOres(oreName).get(0));

			oreName = ore.getDustName(); // Dust
			if (OreDictionary.getOres(oreName).size() > 0)
				registerOreDictDust(ore, oreName, OreDictionary.getOres(oreName).get(0));

			oreName = ore.getAltName(); // Gem
			if (OreDictionary.getOres(oreName).size() > 0)
				registerOreDictGem(ore, oreName, OreDictionary.getOres(oreName).get(0));
		}

		Ores.Coal.registerPulverizing(new ItemStack(Items.coal));

		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent evt)
	{
		processIMC(FMLInterModComms.fetchRuntimeMessages(this));
		for (Map.Entry<String, Property> e : overrideOres.getValues().entrySet())
		{
			String name = e.getKey();
			Block ore = Block.getBlockFromName(name);
			if (isBlockInvalid(ore))
			{
				overrideOres.remove(name);
				continue;
			}
			if (e.getValue().setRequiresMcRestart(true).getBoolean(true))
				RegistryUtils.overwriteEntry(Block.blockRegistry, name, new BlockNetherOverrideOre(ore));
		}

		for (Ores o : Ores.values())
		{
			o.postConfig(config);
		}
		_log.info("Load Complete.");
		config.save();
	}

	private boolean isBlockInvalid(Block block)
	{
		return Block.getIdFromBlock(block) <= 175; // TODO: 175 (in 1.7.2)
	}

	private void processIMC(List<IMCMessage> l)
	{
		for (IMCMessage m : l)
		{
			try
			{
				String k = m.key;
				if ("registerOverrideOre".equals(k))
				{
					String name = m.getStringValue();
					Block ore = Block.getBlockFromName(name);
					if (isBlockInvalid(ore))
						throw new IllegalArgumentException("Cannot override vanilla blocks via IMC.");
					Property a = overrideOres.get(name);
					a.getBoolean(true);
					if (!a.wasRead())
						a.comment = "Override the '" + name + "' block (registered by '" + m.getSender() + "')";
				}
				else
					_log.debug("Unknown IMC message (%s) from %s", k, m.getSender());
			}
			catch (Throwable e)
			{
				_log.error("Bad IMC message (%s) from %s", m.key, m.getSender(), e);
			}
		}
	}

	public static Block getOreBlock(int index)
	{
		if (index >= 0 && index < blockNetherOres.length)
			return blockNetherOres[index];
		return null;
	}

	private void loadConfig(File f)
	{
		Configuration c = new Configuration(f);
		config = c;
		c.load();

		explosionPower = c.get(CATEGORY_GENERAL, "ExplosionPower", 2);
		explosionPower.comment = "How powerful an explosion will be. Creepers are 3, TNT is 4, electrified creepers are 6. This affects both the ability of the explosion to punch through blocks as well as the blast radius.";
		explosionProbability = c.get(CATEGORY_GENERAL, "ExplosionProbability", 75);
		explosionProbability.comment = "The likelyhood an adjacent netherore will turn into an armed ore when one is mined. Percent chance out of 1000 (lower is less likely).";
		enableExplosions = c.get(CATEGORY_GENERAL, "ExplosionEnable", true);
		enableExplosions.comment = "NetherOres have a chance to explode when mined if true.";
		enableExplosionChainReactions = c.get(CATEGORY_GENERAL, "ExplosionChainReactEnable", true);
		enableExplosionChainReactions.comment = "NetherOre explosions can trigger more explosions if true. Does nothing if ExplosionEnable is false.";
		enableFortuneExplosions = c.get(CATEGORY_GENERAL, "FortuneExplosionEnable", true);
		enableFortuneExplosions.comment = "NetherOres have a higher chance to explode when mined with fortune if true.";
		enableAngryPigmen = c.get(CATEGORY_GENERAL, "AngryPigmenEnable", true);
		enableAngryPigmen.comment = "If true, when NetherOres are mined, nearby pigmen become angry to the player.";
		pigmenAggroRange = c.get(CATEGORY_GENERAL, "PigmenAggroRange", 32);
		pigmenAggroRange.comment = "How far away Pigmen gets angry";
		silkyStopsPigmen = c.get(CATEGORY_GENERAL, "SilkyAngryPigmenEnable", false);
		silkyStopsPigmen.comment = "If true, when NetherOres are mined with Silk Touch, nearby pigmen become angry to the player.";
		enableMobsAngerPigmen = c.get(CATEGORY_GENERAL, "MobsAngerPigmen", true);
		enableMobsAngerPigmen.comment = "If true, any entity not a player exploding a NetherOre will anger nearby pigmen. This only accounts for exploding, entities breaking the blocks normally will still anger pigmen.";
		hellFishMaxHealth = c.get(CATEGORY_GENERAL, "HellFish.MaxHealth", 12.5, null, 8.0, Double.MAX_VALUE);
		hellFishMaxHealth.comment = "The maximum health a HellFish will have when spawned.";
		requireSilkTouch = c.get(CATEGORY_GENERAL, "RequireSilkTouch", true);
		requireSilkTouch.comment = "If ores like Coal, Diamond, Emerald requires Silk Touch to drop as ore (If this is false it overrides the per ore setting)";

		enableSmeltToOres = c.get("Processing.Enable", "SmeltToOre", true);
		enableSmeltToOres.comment = "Set this to false to remove smelting NetherOres to ores (i.e., nether iron ore -> 2x normal iron ore).\nInstead, ores will smelt to ingots or some other appropriate item.";
		enableStandardFurnaceRecipes = c.get("Processing.Enable", "StandardFurnaceRecipes", true);
		enableStandardFurnaceRecipes.comment = "Set this to false to remove the standard furnace recipes (i.e., nether iron ore -> normal iron ore).";
		enableMaceratorRecipes = c.get("Processing.Enable", "MaceratorRecipes", true);
		enableMaceratorRecipes.comment = "Set this to false to remove the IC2 Macerator recipes (i.e., nether iron ore -> 4x iron dust).";
		enablePulverizerRecipes = c.get("Processing.Enable", "PulverizerRecipes", true);
		enablePulverizerRecipes.comment = "Set this to false to remove the TE Pulvierzer recipes (i.e., nether iron ore -> 4x iron dust).";
		enableInductionSmelterRecipes = c.get("Processing.Enable", "InductionSmelterRecipes", true);
		enableInductionSmelterRecipes.comment = "Set this to false to remove the TE Induction Smelter recipes (i.e., nether iron ore -> 2x normal iron ore).";
		enableGrinderRecipes = c.get("Processing.Enable", "GrinderRecipes", true);
		enableGrinderRecipes.comment = "Set this to false to remove the AE Grind Stone recipes (i.e., nether iron ore -> 4x iron dust).";

		forceOreSpawn = c.get("WorldGen.Enable", "ForceOreSpawn", false);
		forceOreSpawn.comment = "If true, will spawn nether ores regardless of if a furnace or macerator recipe was found. If false, at least one of those two must be found to spawn the ore.";
		worldGenAllDimensions = c.get("WorldGen.Enable", "AllDimensionWorldGen", false);
		worldGenAllDimensions.comment = "If true, Nether Ores worldgen will run in all dimensions instead of just the Nether. It will still require netherrack to place ores.";
		enableWorldGen = c.get("WorldGen.Enable", "OreGen", true);
		enableWorldGen.comment = "If true, Nether Ores oregen will run and places ores in the world where appropriate. Only disable this if you intend to use the ores with a custom ore generator. (overrides per-ore forcing; hellfish still generate if enabled)";
		enableHellQuartz = c.get("WorldGen.Enable", "OverrideNetherQuartz", true).setRequiresMcRestart(true);
		enableHellQuartz.comment = "If true, Nether Quartz ore will be a NetherOre and will follow the same rules as all other NetherOres.";

		hellFishFromOre = c.get("WorldGen.HellFish", "EnableSpawningFromOre", false);
		hellFishFromOre.comment = "If true, Hellfish will spawn from broken NetherOres.";
		hellFishFromOreChance = c.get("WorldGen.HellFish", "SpawningFromOreChance", 1000);
		hellFishFromOreChance.comment = "The chance out of 10000 that a broken ore will spawn a hellfish.";
		hellFishPerChunk = c.get("WorldGen.HellFish", "GroupsPerChunk", 9);
		hellFishPerChunk.comment = "The maximum number of hellfish veins per chunk.";
		hellFishPerGroup = c.get("WorldGen.HellFish", "BlocksPerGroup", 12);
		hellFishPerGroup.comment = "The maximum number of hellfish blocks per vein.";
		enableHellfish = c.get("WorldGen.HellFish", "Enable", true);
		enableHellfish.comment = "If true, Hellfish will spawn in the Nether. Note that setting this false will not kill active Hellfish mobs.";
		hellFishMinY = c.get("WorldGen.HellFish", "MinY", 1);
		hellFishMaxY = c.get("WorldGen.HellFish", "MaxY", 127);
		if (hellFishMinY.getInt() >= hellFishMaxY.getInt())
			hellFishMinY.set(hellFishMaxY.getInt() - 1);
		hellFishRetrogen = c.get("WorldGen.HellFish", "Retrogen", true, "Retroactively generate HellFish");

		for (Ores o : Ores.values())
		{
			o.loadConfig(c);
		}

		overrideOres = c.getCategory("Overrides");
		overrideOres.setComment("A set of blocks from other mods to override to act like NetherOres.\n" +
				"This does not include controlling oregen, or recipes; only behavior when mined or destroyed.");

		c.save();
	}

	@SubscribeEvent
	public void registerOreEvent(OreRegisterEvent event)
	{
		registerOreDictionaryEntry(event.Name, event.Ore);
	}

	private void registerOreDictionaryEntry(String oreName, ItemStack stack)
	{
		for (Ores ore : Ores.values())
		{
			if (enableSmeltToOres.getBoolean(true)) {
				registerOreDictOre(ore, oreName, stack);
			}
			registerOreDictSmelt(ore, oreName, stack);
			registerOreDictDust(ore, oreName, stack);
			registerOreDictGem(ore, oreName, stack);
		}
	}

	private void registerOreDictOre(Ores ore, String oreName, ItemStack stack)
	{
		if (!ore.isRegisteredSmelting() && ore.getOreName().equals(oreName))
			ore.registerSmelting(stack);
	}

	private void registerOreDictSmelt(Ores ore, String oreName, ItemStack stack)
	{
		if (ore.getSmeltName().equals(oreName)) {
			ore.registerSmelting(stack);
			ore.setDropItem(stack);
		}
	}

	private void registerOreDictDust(Ores ore, String oreName, ItemStack stack)
	{
		if (!ore.isRegisteredMacerator() && ore.getDustName().equals(oreName))
			ore.registerPulverizing(stack);
	}

	private void registerOreDictGem(Ores ore, String oreName, ItemStack stack)
	{
		if (!ore.isRegisteredMacerator() && ore.getAltName().equals(oreName))
			ore.registerPulverizing(stack);
	}

	@Override
	public String getModId()
	{
		return modId;
	}

	@Override
	public String getModName()
	{
		return modName;
	}

	@Override
	public String getModVersion()
	{
		return version;
	}
}
