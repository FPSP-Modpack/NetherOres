package powercrystals.netherores.ores;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockNetherOre extends ItemBlock
{
	protected BlockNetherOres _block;

	public ItemBlockNetherOre(Block block)
	{
		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
		_block = (BlockNetherOres)block;
	}

	@Override
	public int getMetadata(int i)
	{
		return i;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		int index = _block.getBlockIndex();
		Ores[] ores = Ores.values();
		int md = Math.min(index * 16 + stack.getItemDamage(), ores.length - 1);
		return "tile.netherores.ore." + ores[md].name().toLowerCase(Locale.US);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List subTypes)
	{
		int index = _block.getBlockIndex();
		Ores[] ores = Ores.values();
		for (int i = 0, e = Math.min(index * 16 + 15, ores.length - 1) % 16; i <= e; ++i)
		{
			subTypes.add(new ItemStack(_block, 1, i));
		}
	}
}
