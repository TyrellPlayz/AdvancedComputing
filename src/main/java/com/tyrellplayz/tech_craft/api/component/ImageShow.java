package com.tyrellplayz.tech_craft.api.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImageShow extends Component {

    private List<ResourceLocation> images = new ArrayList<>();
    private int imageIndex = 0;

    private Color borderColour;
    private int borderSize = 0;

    public ImageShow(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    public ImageShow(int left, int top, int width, float aspectRatio) {
        super(left, top, width, (int)((float)width / aspectRatio));
    }

    public List<ResourceLocation> getImages() {
        return images;
    }

    public void setImages(List<ResourceLocation> images) {
        this.images = images;
    }

    public void nextImage() {
        if((imageIndex+1) >= images.size()) {
            this.imageIndex = 0;
        }else {
            this.imageIndex++;
        }
    }

    public void previousImage() {
        if((imageIndex-1) < 0) {
            this.imageIndex = images.size() - 1;
        }else {
            this.imageIndex--;
        }
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public int findImageIndex(ResourceLocation location) {
        for (int i = 0; i < images.size(); i++) {
            ResourceLocation image = images.get(i);
            if(image.equals(location)) return i;
        }
        return 0;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isVisible()) {
            if(this.borderSize > 0) {
                RenderUtil.drawRectWithColour(stack,getXPos(),getYPos(),width,height,borderColour);

                drawImage(stack,getXPos()+borderSize, getYPos()+borderSize,getWidth()-(borderSize*2),getHeight()-(borderSize*2));
            }else {
                drawImage(stack,getXPos(),getYPos(),getWidth(),getHeight());
            }
        }
    }

    private void drawImage(PoseStack stack, double x, double y, int width, int height) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F,1.0F);
        RenderSystem.setShaderTexture(0,images.get(imageIndex));
        RenderUtil.drawRectWithFullTexture(stack,x, y, width, height);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F,1.0F);
    }

    public void setBorder(int size, Color colour) {
        this.borderSize = size;
        this.borderColour = colour;
    }

}
