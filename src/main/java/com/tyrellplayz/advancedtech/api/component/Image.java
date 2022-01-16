package com.tyrellplayz.advancedtech.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.resources.ResourceLocation;

public class Image extends Component {

    private ResourceLocation imageLocation;

    public Image(int left, int top, int width, int height, ResourceLocation imageLocation) {
        super(left, top, width, height);
        this.imageLocation = imageLocation;
    }

    public Image(int left, int top, int width, float aspectRatio, ResourceLocation imageLocation) {
        super(left, top, width, (int)((float)width / aspectRatio));
        this.imageLocation = imageLocation;
    }

    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isVisible()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F,1.0F);
            RenderSystem.setShaderTexture(0,this.imageLocation);
            RenderUtil.drawRectWithFullTexture(stack,this.getXPos(), this.getYPos(), this.getWidth(), this.getHeight());
            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F,0.0F);
        }

    }

    public ResourceLocation getImageLocation() {
        return this.imageLocation;
    }

}
