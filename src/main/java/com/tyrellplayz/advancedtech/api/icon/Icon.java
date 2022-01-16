package com.tyrellplayz.advancedtech.api.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public interface Icon {

    ResourceLocation getId();

    int getU();

    int getV();

    int getWidth();

    int getHeight();

    int getImageWidth();

    int getImageHeight();

    ResourceLocation getImageLocation();

    Color getColour();

    Icon setColour(Color colour);

    default void render(PoseStack stack, double x, double y) {
        this.render(stack, x, y, this.getWidth(), this.getHeight());
    }

    default void render(PoseStack stack, double x, double y, int width, int height) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(this.getColour().getRed() / 255.0F, this.getColour().getGreen() / 255.0F,this.getColour().getBlue() / 255.0F,this.getColour().getAlpha() / 255.0F);
        RenderSystem.setShaderTexture(0,getImageLocation());
        RenderUtil.drawRectWithTexture(stack,x,y,getU(),getV(),width,height,getWidth(),getHeight(),getImageWidth(),getImageHeight());
        RenderSystem.setShaderColor(1.0F, 1.0F,1.0F,1.0F);
    }

}
