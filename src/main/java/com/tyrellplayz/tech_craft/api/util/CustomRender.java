package com.tyrellplayz.tech_craft.api.util;

import com.mojang.blaze3d.vertex.PoseStack;

public interface CustomRender<E> {

    void render(E e ,PoseStack stack);

}
