package com.tyrellplayz.advancedtech.api.util;

import com.mojang.blaze3d.vertex.PoseStack;

public interface CustomRender<E> {

    void render(E e ,PoseStack stack);

}
