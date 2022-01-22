package com.tyrellplayz.tech_craft.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class Image extends Component {

    private ResourceLocation imageLocation;
    private Color borderColour;
    private int borderSize = 0;

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
            if(this.borderSize > 0) {
                RenderUtil.drawRectWithColour(stack,getXPos(), getYPos(), width, 1, borderColour);
                RenderUtil.drawRectWithColour(stack,getXPos(), getYPos() + (double)height, width, 1, borderColour);
                RenderUtil.drawRectWithColour(stack,getXPos(), getYPos(), 1, height, borderColour);
                RenderUtil.drawRectWithColour(stack,getXPos() + (double)width - 1.0D, getYPos(), 1, height + 1, borderColour);

                drawImage(stack,getXPos()+borderSize, getYPos()+borderSize,getWidth()-(borderSize*2),getHeight()-(borderSize*2));
            }else {
                drawImage(stack,getXPos(),getYPos(),getWidth(),getHeight());
            }

        }

    }

    private void drawImage(PoseStack stack, double x, double y, int width, int height) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F,1.0F);
        RenderSystem.setShaderTexture(0,this.imageLocation);
        RenderUtil.drawRectWithFullTexture(stack,x, y, width, height);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F,1.0F);
    }

    public ResourceLocation getImageLocation() {
        return this.imageLocation;
    }

    public void setBorder(int size, Color colour) {
        this.borderSize = size;
        this.borderColour = colour;
    }

}
