package com.tyrellplayz.tech_craft.api.content.application;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.zlib.util.JsonSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Locale;
import java.util.Objects;

public class ApplicationManifest implements Comparable<ApplicationManifest> {

    private ResourceLocation id;
    private String name;
    private String version;
    private String author;
    private String website;
    private StartPosition startPosition;

    private ApplicationManifest() {
        this.startPosition = StartPosition.CENTER;
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

    public Serializer getSerializer() {
        return new Serializer();
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

    public static class Serializer implements JsonSerializer<ApplicationManifest> {

        @Override
        public ApplicationManifest fromJson(ResourceLocation id, JsonObject jsonObject) throws JsonSyntaxException {
            ApplicationManifest manifest = new ApplicationManifest();
            manifest.id = id;
            manifest.name = GsonHelper.getAsString(jsonObject,"name");
            manifest.version = GsonHelper.getAsString(jsonObject,"version");
            manifest.author = GsonHelper.getAsString(jsonObject,"author");
            manifest.website = GsonHelper.getAsString(jsonObject,"website","");
            manifest.startPosition = StartPosition.valueOf(GsonHelper.getAsString(jsonObject,"startPosition",StartPosition.CENTER.name().toLowerCase()).toUpperCase());
            return manifest;
        }

        @Nullable
        @Override
        public ApplicationManifest fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ApplicationManifest manifest = new ApplicationManifest();
            manifest.id = id;
            manifest.name = buffer.readUtf();
            manifest.version = buffer.readUtf();
            manifest.author = buffer.readUtf();
            manifest.website = buffer.readUtf();
            manifest.startPosition = buffer.readEnum(StartPosition.class);
            return manifest;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ApplicationManifest applicationManifest) {
            buffer.writeResourceLocation(applicationManifest.getId());
            buffer.writeUtf(applicationManifest.getName());
            buffer.writeUtf(applicationManifest.getVersion());
            buffer.writeUtf(applicationManifest.getAuthor());
            buffer.writeUtf(applicationManifest.getWebsite());
            buffer.writeEnum(applicationManifest.getStartPosition());
        }
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
