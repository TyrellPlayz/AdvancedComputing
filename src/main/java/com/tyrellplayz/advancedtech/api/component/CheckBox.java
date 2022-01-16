package com.tyrellplayz.advancedtech.api.component;

import com.google.common.base.Strings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.function.Consumer;

public class CheckBox extends Component{

    public static final int PADDING = 2;

    private String text;
    private boolean checked;
    private Consumer<Boolean> onChecked;

    public CheckBox(int left, int top) {
        this(left, top, (String)null);
    }

    public CheckBox(int left, int top, String text) {
        super(left, top, 10, 10);
        this.text = text;
    }

    public void setOnChecked(Consumer<Boolean> onChecked) {
        this.onChecked = onChecked;
    }

    public void render(PoseStack stack, double mouseX, double mouseY, float partialTicks) {
        if (this.isVisible()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, COMPONENT_TEXTURES);
            int tex_y = 20 * this.getFlag();
            RenderUtil.drawRectWithDefaultTexture(stack,this.getXPos(), this.getYPos(), 0, tex_y, this.getWidth(), this.getHeight(), 20, 20);
            if (!Strings.isNullOrEmpty(this.text)) {
                double x = this.getXPos() + 2.0D + (double)this.getWidth();
                RenderUtil.drawText(stack, this.text, x, this.getYPos() + 1.0F, Color.WHITE);
            }

            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.0F);
        }

    }

    private int getFlag() {
        if (this.isEnabled() && this.checked) {
            return 1;
        } else {
            return this.checked ? 2 : 0;
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int code) {
        if (this.isMouseInside(mouseX, mouseY) && this.isEnabled()) {
            this.checked = !this.checked;
            if (this.onChecked != null) {
                this.onChecked.accept(this.checked);
            }
        }

    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
