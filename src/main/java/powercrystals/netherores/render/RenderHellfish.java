package powercrystals.netherores.render;

import powercrystals.netherores.NetherOresCore;

import net.minecraft.client.renderer.entity.RenderSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderHellfish extends RenderSilverfish
{
    private static final ResourceLocation hellfishTextures = new ResourceLocation(NetherOresCore.mobTextureFolder + "hellfish.png");

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return hellfishTextures;
    }
}
