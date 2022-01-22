package com.tyrellplayz.tech_craft.api.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.util.Mth;

public abstract class Animation {

    public Animation() { }

    public abstract void runTick(PoseStack stack, double mouseX, double mouseY, float partialTicks);

    public abstract void reset();

    public static class Wait extends Animation {
        private int ticks = 0;
        private final int showTicks;
        private IRenderable render;

        public Wait(int showTicks, IRenderable render) {
            this.showTicks = showTicks;
            this.render = render;
        }

        @Override
        public void runTick(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
            if (this.render != null && this.ticks >= this.showTicks) {
                this.render.render(stack, mouseX, mouseY, partialTicks);
            }

            ++this.ticks;
            this.ticks = Mth.clamp(this.ticks, 0, this.showTicks);
        }

        @Override
        public void reset() {
            this.ticks = 0;
        }
    }

}
