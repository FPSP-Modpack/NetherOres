package powercrystals.netherores.render;

import net.minecraft.client.renderer.entity.RenderSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import powercrystals.netherores.NetherOresCore;

public class RenderHellfish extends RenderSilverfish
{
    private static final ResourceLocation hellfishTextures = new ResourceLocation(NetherOresCore.mobTextureFolder + "hellfish.png");

    @Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return hellfishTextures;
    }
}
