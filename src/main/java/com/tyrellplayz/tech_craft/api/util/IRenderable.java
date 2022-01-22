package com.tyrellplayz.tech_craft.api.util;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IRenderable {

    void render(PoseStack stack, double mouseX, double mouseY, float partialTicks);

}
