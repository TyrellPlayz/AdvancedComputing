package com.tyrellplayz.advancedtech.api.util;

import com.mojang.blaze3d.vertex.PoseStack;

public interface CustomRender {

    void render(PoseStack stack, double x, double y, int width, int height, int flag);

}
