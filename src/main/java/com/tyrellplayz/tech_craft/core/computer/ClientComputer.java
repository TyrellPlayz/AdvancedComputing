package com.tyrellplayz.tech_craft.core.computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tyrellplayz.tech_craft.TechCraft;
import com.tyrellplayz.tech_craft.api.content.Content;
import com.tyrellplayz.tech_craft.api.content.LayeredContent;
import com.tyrellplayz.tech_craft.api.content.application.Application;
import com.tyrellplayz.tech_craft.api.content.application.ApplicationManifest;
import com.tyrellplayz.tech_craft.api.content.dialog.Dialog;
import com.tyrellplayz.tech_craft.api.system.ApplicationSystem;
import com.tyrellplayz.tech_craft.api.system.IFileSystem;
import com.tyrellplayz.tech_craft.api.system.IWindow;
import com.tyrellplayz.tech_craft.api.system.SystemSettings;
import com.tyrellplayz.tech_craft.api.system.filesystem.FileSystem;
import com.tyrellplayz.tech_craft.blockentity.ComputerBlockEntity;
import com.tyrellplayz.tech_craft.manager.OldApplicationManager;
import com.tyrellplayz.tech_craft.manager.TaskManager;
import com.tyrellplayz.zlib.client.gui.screen.GuiScreen;
import com.tyrellplayz.zlib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ClientComputer extends GuiScreen implements ApplicationSystem, IFileSystem {

    private final ResourceLocation GUI_SCREEN = new ResourceLocation(TechCraft.MOD_ID, "textures/gui/screen.png");

    public static final int BORDER = 10;
    public final int SCREEN_WIDTH;
    public final int SCREEN_HEIGHT;
    private final ComputerBlockEntity tile;
    private final TaskBar taskBar;
    private final SystemSettings systemSettings;
    private final FileSystem fileSystem;
    private final CompoundTag systemData;
    private final Font systemFontRenderer;
    private final List<IWindow<? extends Content>> windows;
    private final Map<ResourceLocation, IWindow<? extends Content>> idWindowMap;
    private IWindow<? extends Content> focusedWindow;

    public ClientComputer(ComputerBlockEntity tile) {
        super(new TextComponent("text_screen"), 384, 216);
        this.SCREEN_WIDTH = this.xSize - 10;
        this.SCREEN_HEIGHT = this.ySize - 10;
        this.tile = tile;
        this.taskBar = new TaskBar(this);
        this.windows = new ArrayList<>();
        this.idWindowMap = new HashMap<>();
        this.systemFontRenderer = Minecraft.getInstance().font;
        this.systemData = tile.getSystemData();
        this.systemSettings = new SystemSettings(this);
        this.fileSystem = new FileSystem(this, this.systemData.getCompound("FileSystem"));
        if (tile.getSystemData().contains("Settings")) {
            this.systemSettings.read(tile.getSystemData().getCompound("Settings"));
        }
    }

    protected void init() {
        super.init();
        this.taskBar.init();

        for (IWindow<? extends Content> window : this.windows) {
            if (window != null) {
                window.onPositionChanged(this.left + 5, this.top + 5);
            }
        }
    }

    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderScreen(stack);
        RenderSystem.setShaderTexture(0,this.systemSettings.getBackgroundLocation());
        RenderUtil.drawRectWithFullTexture(stack,(double)this.left + 5.0D, (double)this.top + 5.0D, this.SCREEN_WIDTH, this.SCREEN_HEIGHT);

        for (IWindow<? extends Content> window : this.windows) {
            if (window != null && !window.equals(this.focusedWindow)) {
                window.render(stack, mouseX, mouseY, partialTicks);
            }
        }

        if (this.focusedWindow != null) {
            this.focusedWindow.render(stack, mouseX, mouseY, partialTicks);
        }

        this.taskBar.render(stack, mouseX, mouseY, partialTicks);
        this.getWindowFromPos(mouseX, mouseY);
    }

    private void renderScreen(PoseStack stack) {
        this.renderBackground(stack, 0);
        RenderSystem.setShaderTexture(0,GUI_SCREEN);
        RenderUtil.drawScreenWithTexture(stack, this.left, this.top, 10, this.xSize, this.ySize);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int code) {
        super.mouseClicked(mouseX, mouseY, code);
        double taskX = (double)this.left + 5.0D;
        double taskY = (double)(this.top + this.SCREEN_HEIGHT) + 5.0D - 16.0D;
        if (!RenderUtil.isMouseInside((int)mouseX, (int)mouseY, (int)taskX, (int)taskY, (int)taskX + this.SCREEN_WIDTH, (int)taskY + 16)) {
            IWindow<? extends Content> window = this.getWindowFromPos(mouseX, mouseY);
            if (window != null) {
                if (window.equals(this.focusedWindow)) {
                    this.focusedWindow.onMouseClicked(mouseX, mouseY, code);
                } else {
                    this.focusWindow(window);
                }

                return true;
            }

            this.focusWindow(null);
        } else {
            this.focusWindow(null);
            this.taskBar.mouseClicked(mouseX, mouseY, code);
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int code) {
        if (this.focusedWindow != null) {
            this.focusedWindow.onMouseReleased(mouseX, mouseY, code);
        }

        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int code, double distanceX, double distanceY) {
        if (this.focusedWindow == null) {
            IWindow<? extends Content> window = this.getWindowFromPos(mouseX, mouseY);
            if (window != null) {
                this.focusedWindow = window;
            }
        }

        if (this.focusedWindow != null) {
            this.focusedWindow.onMouseDragged(mouseX, mouseY, code, distanceX, distanceY);
        }

        return true;
    }

    public void mouseMoved(double mouseX, double mouseY) {
        if (this.focusedWindow != null) {
            this.focusedWindow.onMouseMoved(mouseX, mouseY);
        }

    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.focusedWindow != null) {
            this.focusedWindow.onMouseScrolled(mouseX, mouseY, delta);
        }

        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focusedWindow != null) {
            this.focusedWindow.onKeyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (this.focusedWindow != null) {
            this.focusedWindow.onKeyReleased(keyCode, scanCode, modifiers);
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char character, int modifiers) {
        if (this.focusedWindow != null) {
            this.focusedWindow.onCharTyped(character, modifiers);
        }

        return super.charTyped(character, modifiers);
    }

    @Nullable
    public Application getApplication(ResourceLocation id) {
        if (!TechCraft.getApplicationManager().isApplicationLoaded(id)) {
            return null;
        } else {
            ApplicationManifest applicationManifest = TechCraft.getApplicationManager().getApplicationManifestFor(id);
            return !this.isApplicationOpen(id) ? null : (Application)((IWindow)this.idWindowMap.get(applicationManifest.getId())).getContent();
        }
    }

    public Collection<ApplicationManifest> getInstalledApplications() {
        return TechCraft.getApplicationManager().getApplicationManifests().stream().sorted().collect(Collectors.toList());
    }

    public Application[] getRunningApplications() {
        List<Application> applications = new ArrayList<>();

        for (IWindow<? extends Content> window : this.windows) {
            if (window.getContent() instanceof Application) {
                applications.add((Application) window.getContent());
            }
        }

        return applications.toArray(new Application[0]);
    }

    public Application openApplication(ResourceLocation id) {
        if (this.isApplicationOpen(id)) {
            this.focusWindow(this.idWindowMap.get(id));
            return this.focusedWindow.getContent() instanceof Dialog ? (Application)((LayeredContent)this.focusedWindow.getContent()).getParentContent() : (Application)this.focusedWindow.getContent();
        } else {
            Application application = TechCraft.getApplicationManager().createApplication(id);
            if (application == null) {
                return null;
            } else {
                IWindow<Application> newWindow = new Window<>(application);
                this.openWindow(newWindow);
                return application;
            }
        }
    }

    public boolean isApplicationOpen(ResourceLocation id) {
        if (!TechCraft.getApplicationManager().isApplicationLoaded(id)) {
            return false;
        } else {
            ApplicationManifest applicationManifest = TechCraft.getApplicationManager().getApplicationManifestFor(id);
            return applicationManifest != null && this.idWindowMap.containsKey(applicationManifest.getId());
        }
    }

    public boolean isApplicationFocused(ResourceLocation id) {
        return this.focusedWindow != null && this.focusedWindow.getContent() instanceof Application && ((Application) this.focusedWindow.getContent()).getApplicationManifest().getId().equals(id);
    }

    public boolean isApplicationInstalled(ResourceLocation id) {
        return false;
    }

    public void closeApplication(ResourceLocation id) {
        if (TechCraft.getApplicationManager().isApplicationLoaded(id)) {
            ApplicationManifest applicationManifest = TechCraft.getApplicationManager().getApplicationManifestFor(id);
            if (this.isApplicationOpen(id)) {
                IWindow<? extends Content> window = this.idWindowMap.remove(applicationManifest.getId());
                this.closeWindow(window);
            }
        }
    }

    public void openDialog(Dialog dialog) {
        IWindow<Dialog> window = new Window(dialog);
        this.openWindow(window);
    }

    public void closeDialog(Dialog dialog) {
        this.closeWindow(dialog.getWindow());
    }

    public void openStartMenu() {
        if (!this.isStartMenuOpen()) {
            StartMenu startMenu = new StartMenu(this);
            IWindow<? extends StartMenu> startMenuWindow = new Window(startMenu);
            this.openWindow(startMenuWindow);
            startMenuWindow.setPosition(0.0D, this.SCREEN_HEIGHT - 16 - 150);
        }
    }

    public void closeStartMenu() {
        if (this.isStartMenuOpen()) {
            this.closeWindow(this.focusedWindow);
        }
    }

    public StartMenu getStartMenu() {
        return this.isStartMenuOpen() ? (StartMenu)this.focusedWindow.getContent() : null;
    }

    public boolean isStartMenuOpen() {
        return this.focusedWindow != null && this.focusedWindow.getContent() instanceof StartMenu;
    }

    public void focusWindow(IWindow<? extends Content> window) {
        if (window != null) {
            if (this.isWindowOpen(window)) {
                if (this.focusedWindow != null) {
                    this.windows.remove(this.focusedWindow);
                    this.windows.add(this.focusedWindow);
                    this.focusedWindow.setFocus(false);
                }

                if (window.getContent() instanceof LayeredContent && ((LayeredContent)window.getContent()).getActiveDialog() != null) {
                    IWindow<? extends Content> dialogWindow = ((LayeredContent)window.getContent()).getActiveDialog().getWindow();
                    if (!this.isWindowOpen(dialogWindow)) {
                        LogManager.getLogger().error("Trying to open a dialog window when the window is not open. ");
                    } else {
                        this.windows.remove(window);
                        this.windows.add(window);
                        this.focusedWindow = dialogWindow;
                        this.focusedWindow.setFocus(true);
                    }
                } else {
                    this.focusedWindow = window;
                    this.focusedWindow.setFocus(true);
                }
            }
        } else {
            if (this.focusedWindow != null) {
                this.windows.remove(this.focusedWindow);
                this.windows.add(this.focusedWindow);
                this.focusedWindow.setFocus(false);
            }

            this.focusedWindow = null;
        }
    }

    public void openWindow(IWindow<? extends Content> window) {
        if (window != null) {
            if (!this.isWindowOpen(window)) {
                this.windows.add(window);
                if (window.getContent() instanceof Application) {
                    this.idWindowMap.put(((Application)window.getContent()).getApplicationManifest().getId(), window);
                }

                ((Window)window).load(this, this.SCREEN_WIDTH, this.SCREEN_HEIGHT, this.left + 5, this.top + 5);
                this.focusWindow(window);
            }
        }
    }

    public void closeWindow(IWindow<? extends Content> window) {
        if (window != null) {
            if (this.isWindowOpen(window)) {
                window.getContent().onWindowClosed();
                this.windows.remove(window);
                if (window.getContent() instanceof Application) {
                    ResourceLocation id = ((Application)window.getContent()).getApplicationManifest().getId();
                    this.idWindowMap.remove(id);
                }

                if (this.focusedWindow.equals(window)) {
                    this.focusedWindow.setFocus(false);
                    this.focusedWindow = null;
                }

            }
        }
    }

    public boolean isWindowOpen(IWindow<? extends Content> window) {
        return window != null && this.windows.contains(window);
    }

    public IWindow<? extends Content> getWindowFromPos(double x, double y) {
        if (this.focusedWindow != null) {
            double topX = (double)this.left + 5.0D + this.focusedWindow.getFromLeft();
            double topY = (double)this.top + 5.0D + this.focusedWindow.getFromTop();
            double bottomX = topX + (double)this.focusedWindow.getWidth() - 1.0D;
            double bottomY = topY + (double)this.focusedWindow.getHeight() - 1.0D;
            if (RenderUtil.isMouseInside((int)x, (int)y, (int)topX, (int)topY, (int)bottomX, (int)bottomY)) {
                return this.focusedWindow;
            }
        }

        List<IWindow<? extends Content>> windowList = new ArrayList<>(this.windows);
        Collections.reverse(windowList);

        for (IWindow<? extends Content> window : windowList) {
            if (window != null) {
                double topX = (double) this.left + 5.0D + window.getFromLeft();
                double topY = (double) this.top + 5.0D + window.getFromTop();
                double bottomX = topX + (double) window.getWidth() - 1.0D;
                double bottomY = topY + (double) window.getHeight() - 1.0D;
                if (RenderUtil.isMouseInside((int) x, (int) y, (int) topX, (int) topY, (int) bottomX, (int) bottomY)) {
                    return window;
                }
            }
        }

        return null;
    }

    public void onStartup() {
    }

    public void onShutdown() {
        this.updateData();
    }

    public void tick() {
        this.windows.forEach(IWindow::tick);
    }

    public void updateData() {
        if(fileSystem != null) this.systemData.put("FileSystem", this.fileSystem.read());
        this.systemData.put("Settings",this.systemSettings.write());
        this.tile.setSystemData(this.systemData);
        UpdateSystemDataTask updateSystemDataTask = new UpdateSystemDataTask(this.systemData, this.tile.getBlockPos());
        TaskManager.get().sendTask(updateSystemDataTask);
    }

    public SystemSettings getSystemSettings() {
        return this.systemSettings;
    }

    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    public Font getSystemFontRenderer() {
        return this.systemFontRenderer;
    }

}
