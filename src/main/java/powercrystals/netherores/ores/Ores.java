package powercrystals.netherores.ores;

import appeng.api.AEApi;
import appeng.api.features.IGrinderEntry;
import appeng.api.features.IGrinderRegistry;

import cofh.api.modhelpers.ThermalExpansionHelper;
import cofh.asm.relauncher.Strippable;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.netherores.NetherOresCore;

public enum Ores
{
	//@formatter:off
	/*Name, Chunk, Group, Smelt, Pulv,    Prefix, Drop, Exp*/
	Coal(       8,    16,     2,    5,      null,    2,   1),
	Diamond(    4,     3,     2,    5,     "gem",    2,   3),
	Gold(       8,     6,     2,    4),
	Iron(       8,     8,     2,    4),
	Lapis(      6,     6,     2,   24,     "gem",   16,   2),
	Redstone(   6,     8,     2,   21,    "dust",    6,   2),
	Copper(     8,     8,     2,    4),
	Tin(        8,     8,     2,    4),
	Emerald(    3,     2,     2,    5,     "gem",    2,   3),
	Silver(     6,     4,     2,    4),
	Lead(       6,     6,     2,    4),
	Uranium(    3,     2,     2,    4, "crushed"),
	Nikolite(   8,     4,     2,   21,    "dust",    7,   2),
	Ruby(       6,     3,     2,    5,     "gem",    2,   3),
	Peridot(    6,     3,     2,    5,     "gem",    2,   3),
	Sapphire(   6,     3,     2,    5,     "gem",    2,   3),

	Platinum(   1,     3,     2,    4),
	Nickel(     4,     6,     2,    4),
	Steel(      3,     4,     2,    4),
	Iridium(    1,     2,     2,    4,    "drop"),
	Osmium(     8,     7,     2,    4),
	Sulfur(    12,    12,     2,   24,    "dust",    8,   1, "crystal"),
	Titanium(   3,     2,     2,    4),
	Mithril(    6,     6,     2,    4),
	Adamantium( 5,     4,     2,    4),
	Rutile(     3,     4,     2,    4),
	Tungsten(   8,     8,     2,    4),
	Amber(      5,     6,     2,    5,     "gem",    2,   2),
	Tennantite( 8,     8,     2,    4),
	Salt(       5,     5,     2,   12,    "food",    4,   1),
	Saltpeter(  6,     4,     2,   10,    "dust",    3,   1, "crystal"),
	Magnesium(  4,     5,     2,    8, "crushed");

	//@formatter:on
	private int _blockIndex;
	private int _metadata;
	private String _primary;
	private String _secondary;

	private boolean _registeredSmelting;
	private boolean _registeredMacerator;

	private boolean _oreGenDisable = false;
	private boolean _oreGenForced = false;

	private boolean _retroGenEnabled = true;

	private boolean _silky = true;

	private int _oreGenMinY = 1;
	private int _oreGenMaxY = 127;

	private int _oreGenGroupsPerChunk = 6;
	private int _oreGenBlocksPerGroup = 14;

	private int _miningLevel;

	private int _smeltCount;
	private int _pulvCount;
	private int _dropCount;
	private int _exp;

	private ItemStack _itemDropped;

	private Ores(int groupsPerChunk, int blocksPerGroup, int smeltCount, int maceCount)
	{
		this(groupsPerChunk, blocksPerGroup, smeltCount, maceCount, null);
		_silky = false;
	}

	private Ores(int groupsPerChunk, int blocksPerGroup, int smeltCount, int maceCount, String secondaryType)
	{
		this(groupsPerChunk, blocksPerGroup, smeltCount, maceCount, null, 1, 0, secondaryType);
	}

	private Ores(int groupsPerChunk, int blocksPerGroup, int smeltCount, int maceCount, String type, int dropCount, int exp)
	{
		this(groupsPerChunk, blocksPerGroup, smeltCount, maceCount, type, dropCount, exp, type);
	}

	private Ores(int groupsPerChunk, int blocksPerGroup, int smeltCount, int maceCount, String primaryType, int dropCount, int exp, String secondaryType)
	{
		int meta = ordinal();
		_blockIndex = meta / 16;
		_metadata = meta % 16;
		_oreGenGroupsPerChunk = groupsPerChunk;
		_oreGenBlocksPerGroup = blocksPerGroup;
		_smeltCount = smeltCount;
		_pulvCount = maceCount;
		_dropCount = dropCount;
		_miningLevel = 2;
		_primary = primaryType != null ? primaryType : "ingot";
		_secondary = secondaryType != null ? secondaryType : "crystalline";
		_exp = exp;
	}

	public int getBlockIndex()
	{
		return _blockIndex;
	}

	public int getMetadata()
	{
		return _metadata;
	}

	public String getOreName()
	{
		return "ore" + name();
	}

	public String getSmeltName()
	{
		return _primary + name();
	}

	public String getDustName()
	{
		return "dust" + name();
	}

	public String getAltName()
	{
		return _secondary + name();
	}

	public boolean isRegisteredSmelting()
	{
		return _registeredSmelting;
	}

	public boolean isRegisteredMacerator()
	{
		return _registeredMacerator;
	}

	public int getMaxY()
	{
		return _oreGenMaxY;
	}

	public int getMinY()
	{
		return _oreGenMinY;
	}

	public boolean getRetroGen()
	{
		return _retroGenEnabled;
	}

	public int getGroupsPerChunk()
	{
		return _oreGenGroupsPerChunk;
	}

	public int getBlocksPerGroup()
	{
		return _oreGenBlocksPerGroup;
	}

	public boolean getDisabled()
	{
		return _oreGenDisable;
	}

	public boolean getForced()
	{
		return _oreGenForced;
	}

	public int getSmeltCount()
	{
		return _smeltCount;
	}

	public int getMaceCount()
	{
		return _pulvCount;
	}

	public int getDropCount()
	{
		return _dropCount;
	}

	public ItemStack getDropItem()
	{
		return _itemDropped;
	}

	public int getExp()
	{
		return _exp;
	}

	public ItemStack getItemStack(int amt)
	{
		return new ItemStack(NetherOresCore.getOreBlock(_blockIndex), amt, _metadata);
	}

	public void load()
	{
		NetherOresCore.getOreBlock(_blockIndex).setHarvestLevel("pickaxe", _miningLevel, _metadata);
		if (_oreGenForced | !_oreGenDisable)
		{
			ItemStack oreStack = getItemStack(1);
			OreDictionary.registerOre("oreNether" + name(), oreStack);
			// @deprecated
			GameRegistry.registerCustomItemStack("netherOresBlock" + name(), oreStack);
			GameRegistry.registerCustomItemStack(name(), oreStack);
		}
	}

	public void setDropItem(ItemStack stack)
	{
		if (_silky && _itemDropped == null && stack != null && NetherOresCore.requireSilkTouch.getBoolean()) {
			_itemDropped = stack.copy();
			_itemDropped.stackSize = 1;
		}
	}

	public void registerSmelting(ItemStack smeltStack)
	{
		if (_registeredSmelting)
			return;
		_registeredSmelting = true;
		if (NetherOresCore.enableStandardFurnaceRecipes.getBoolean(true))
		{
			ItemStack smeltTo = smeltStack.copy();
			smeltTo.stackSize = _smeltCount;
			FurnaceRecipes.smelting().func_151394_a(getItemStack(1), smeltTo, 1F);
		}

		if (NetherOresCore.enableInductionSmelterRecipes.getBoolean(true) &&
				Loader.isModLoaded("ThermalExpansion"))
		{
			ItemStack input = getItemStack(1);
			ItemStack regSec = new ItemStack(Blocks.sand);
			ItemStack slagRich = GameRegistry.findItemStack("ThermalExpansion", "slagRich", 1);
			ItemStack slag = GameRegistry.findItemStack("ThermalExpansion", "slag", 1);
			ItemStack smeltToReg = smeltStack.copy();
			int _smeltCount = this._smeltCount;
			if (!NetherOresCore.enableSmeltToOres.getBoolean(true))
				_smeltCount *= 2;
			smeltToReg.stackSize = _smeltCount;
			ItemStack smeltToRich = smeltStack.copy();
			int richSmeltCt = _smeltCount + (int)Math.ceil(_smeltCount / 3f);
			smeltToRich.stackSize = richSmeltCt;

			//energy, primaryInput, secondaryInput, primaryOutput, secondaryOutput, secondaryChance
			ThermalExpansionHelper.addSmelterRecipe(800 * 2 * _smeltCount, input, regSec, smeltToReg, slagRich, 10);
			ThermalExpansionHelper.addSmelterRecipe(800 * 3 * richSmeltCt, input, slagRich, smeltToRich, slag, 100);
		}
	}

	public void registerPulverizing(ItemStack maceStack)
	{
		if (_registeredMacerator)
			return;
		_registeredMacerator = true;

		if (NetherOresCore.enableMaceratorRecipes.getBoolean(true) &&
				Loader.isModLoaded("IC2"))
		{
			registerMacerator(maceStack);
		}

		if (NetherOresCore.enablePulverizerRecipes.getBoolean(true) &&
				Loader.isModLoaded("ThermalExpansion"))
		{
			ItemStack input = getItemStack(1);
			ItemStack pulvPriTo = maceStack.copy();
			ItemStack pulvSecTo = new ItemStack(Blocks.netherrack);

			pulvPriTo.stackSize = _pulvCount;
			pulvSecTo.stackSize = 1;

			//energy, input, primaryOutput, secondaryOutput, secondaryChance
			ThermalExpansionHelper.addPulverizerRecipe(3200, input, pulvPriTo, pulvSecTo, 15);
		}

		if (NetherOresCore.enableGrinderRecipes.getBoolean(true) &&
				Loader.isModLoaded("appliedenergistics2"))
		{
			registerAEGrinder(maceStack.copy());
		}

	}

	@Strippable("mod:IC2")
	private void registerMacerator(ItemStack maceStack)
	{
		ItemStack input = getItemStack(1);
		ItemStack maceTo = maceStack.copy();
		maceTo.stackSize = _pulvCount;
		Recipes.macerator.addRecipe(new RecipeInputItemStack(input), null, maceTo.copy());
	}

	@Strippable("mod:appliedenergistics2")
	private void registerAEGrinder(ItemStack maceStack)
	{
		ItemStack maceTo = maceStack.copy();
		maceTo.stackSize = _pulvCount;

		IGrinderRegistry grinder = AEApi.instance().registries().grinder();

		for (ItemStack ore : OreDictionary.getOres(getOreName()))
		{
			IGrinderEntry recipe = grinder.getRecipeForInput(ore);

			if (recipe != null)
			{
				grinder.addRecipe(getItemStack(1), maceTo, recipe.getEnergyCost() * 2);
				return;
			}
		}
		// if there's no overworld recipe to get the energy cost from, default to 16 turns
		grinder.addRecipe(getItemStack(1), maceTo, 16);
	}

	public void loadConfig(Configuration c)
	{
		String cat = "WorldGen.Ores." + name();
		_oreGenMaxY = c.get(cat, "MaxY", _oreGenMaxY).setRequiresMcRestart(true).getInt();
		_oreGenMinY = c.get(cat, "MinY", _oreGenMinY).setRequiresMcRestart(true).getInt();
		if (_oreGenMinY >= _oreGenMaxY)
		{
			_oreGenMinY = _oreGenMaxY - 1;
			c.get(cat, "MinY", _oreGenMinY).set(_oreGenMinY);
		}

		_oreGenGroupsPerChunk = c.get(cat, "GroupsPerChunk", _oreGenGroupsPerChunk).setRequiresMcRestart(true).getInt();
		_oreGenBlocksPerGroup = c.get(cat, "BlocksPerGroup", _oreGenBlocksPerGroup).setRequiresMcRestart(true).getInt();
		_oreGenDisable = c.get(cat, "Disable", false, "Disables generation of " + name() +
				" (overrides global ForceOreSpawn)").setRequiresMcRestart(true).getBoolean(false);
		_oreGenForced = c.get(cat, "Force", false, "Force " + name() +
				" to generate (overrides Disable)").setRequiresMcRestart(true).getBoolean(false);
		_miningLevel = c.get(cat, "MiningLevel", _miningLevel, "The pickaxe level required to mine " +
				name()).setRequiresMcRestart(true).getInt();

		_retroGenEnabled = c.get(cat, "Retrogen", true, "Retroactively generate " + name() + " if enabled in CoFHCore").
				setRequiresMcRestart(true).getBoolean(true);

		cat = "Processing.Ores." + name();
		_smeltCount = c.get(cat, "SmeltedCount", _smeltCount, "Output from smelting " + name()).setRequiresMcRestart(true).getInt();
		_primary = c.get(cat, "PrimaryOrePrefix", _primary, "Output from smelting " +
				name() + " if ore" + name() + " is not found or SmeltToOre is false (i.e., " + _primary + name() +
				")").setRequiresMcRestart(true).getString();
		_pulvCount = c.get(cat, "PulverizedCount", _pulvCount, "Output from grinding " + name()).setRequiresMcRestart(true).getInt();
		_secondary = c.get(cat, "AlternateOrePrefix", _secondary, "Output from grinding " +
				name() + " if dust" + name() + " is not found (i.e., " + _secondary + name() +
				")").setRequiresMcRestart(true).getString();

		cat = "Mining.Ores." + name();
		if (_dropCount > 1) {
			_dropCount = c.get(cat, "DropCount", _dropCount, "Drop count when not harvesting " + name() + " with Silk Touch")
					.setRequiresMcRestart(true).getInt();
			_exp = c.get(cat, "Exp", _exp, "Exp dropped when not harvesting " + name() + " with Silk Touch")
					.setRequiresMcRestart(true).getInt();
		}
		_silky = c.get(cat, "RequireSilkTouch", _silky, "Require Silk Touch to get this ore in ore form.").setRequiresMcRestart(true).getBoolean();
	}

	public void postConfig(Configuration c)
	{
		String cat = "WorldGen.Ores." + name();
		_oreGenDisable |= !(_registeredSmelting | _registeredMacerator);
		if (!c.get(cat, "Disable", _oreGenDisable, "Disables generation of " + name() +
				" (overrides global ForceOreSpawn)").wasRead()) {
			c.get(cat, "Disable", _oreGenDisable, "Disables generation of " + name() +
					" (overrides global ForceOreSpawn)").set(_oreGenDisable);
		}
	}
}
