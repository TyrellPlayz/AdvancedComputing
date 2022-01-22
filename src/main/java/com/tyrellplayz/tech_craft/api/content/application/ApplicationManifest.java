package com.tyrellplayz.tech_craft.api.content.application;

import com.google.gson.JsonObject;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.tech_craft.util.validator.Ignored;
import com.tyrellplayz.tech_craft.util.validator.Optional;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.Objects;

public class ApplicationManifest implements Comparable<ApplicationManifest> {

    @Ignored
    private JsonObject jsonObject;
    private ResourceLocation id;
    private String name;
    private String version;
    private String author;
    @Optional
    private String website;
    @Optional
    private StartPosition startPosition;

    private ApplicationManifest() {
        this.startPosition = StartPosition.CENTER;
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getAuthor() {
        return author;
    }

    public Icon getIcon() {
        return new AppIcon(getId());
    }

    public String getWebsite() {
        return website;
    }

    public StartPosition getStartPosition() {
        return startPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationManifest that = (ApplicationManifest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(ApplicationManifest applicationManifest) {
        return this.getName().compareTo(applicationManifest.getName());
    }

    public record AppIcon(ResourceLocation id) implements Icon {

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public int getU() {
            return 0;
        }

        @Override
        public int getV() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 14;
        }

        @Override
        public int getHeight() {
            return 14;
        }

        @Override
        public int getImageWidth() {
            return 14;
        }

        @Override
        public int getImageHeight() {
            return 14;
        }

        @Override
        public Color getColour() {
            return Color.WHITE;
        }

        @Override
        public Icon setColour(Color colour) {
            return this;
        }

        @Override
        public ResourceLocation getImageLocation() {
            return new ResourceLocation(id.getNamespace(), "textures/app/" + id.getPath() + ".png");
        }
    }

}
