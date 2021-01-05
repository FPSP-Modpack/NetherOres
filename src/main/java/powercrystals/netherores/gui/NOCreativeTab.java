package powercrystals.netherores.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import powercrystals.netherores.NetherOresCore;

public class NOCreativeTab extends CreativeTabs
{
	public static final NOCreativeTab tab = new NOCreativeTab("Nether Ores");

	public NOCreativeTab(String label)
	{
		super(label);
	}

	@Override
	public String getTranslatedTabLabel()
	{
		return this.getTabLabel();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return Item.getItemFromBlock(NetherOresCore.blockNetherOres[0]);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int func_151243_f()
	{
		return 1;
	}
}
