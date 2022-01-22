package com.tyrellplayz.tech_craft.api.system;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.api.content.Content;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.tech_craft.api.util.IScreenEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface IWindow<T extends Content> extends IScreenEventListener {

    int TOP_BAR_HEIGHT = 10;
    int BORDER = 2;

    void tick();

    void render(PoseStack stack, int mouseX, int mouseY, float partialTicks);

    void onPositionChanged(double screenLeft, double screenTop);

    void open(ApplicationSystem computer);

    void close();

    double getX();

    double getContentX();

    double getY();

    double getContentY();

    double getFromLeft();

    double getFromTop();

    void setPosition(double fromLeft, double fromTop);

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    @Nonnull
    ApplicationSystem getComputer();

    @Nonnull
    T getContent();

    boolean isOpen();

    @Nullable
    String getTitle();

    void setTitle(String title);

    default boolean hasTitle() {
        return !Strings.isNullOrEmpty(this.getTitle());
    }

    @Nullable
    Icon getIcon();

    void setIcon(Icon icon);

    default boolean hasIcon() {
        return this.getIcon() != null;
    }

    boolean isFocused();

    void setFocus(boolean focus);

    boolean hasBorder();

    void setBorder(boolean border);

    boolean isMovable();

    void setMovable(boolean movable);

    boolean iconIsShown();

    void setShowIcon(boolean showIcon);

    boolean isDragging();

    UUID getId();

}
