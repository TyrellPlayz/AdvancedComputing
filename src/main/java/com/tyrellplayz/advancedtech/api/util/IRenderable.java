package com.tyrellplayz.advancedtech.api.util;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IRenderable {

    void render(PoseStack stack, double mouseX, double mouseY, float partialTicks);

}
