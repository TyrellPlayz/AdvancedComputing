package com.tyrellplayz.advancedtech.application.system;

import com.tyrellplayz.advancedtech.api.component.*;
import com.tyrellplayz.advancedtech.api.content.Layer;
import com.tyrellplayz.advancedtech.api.content.application.Application;
import com.tyrellplayz.advancedtech.api.icon.Icons;
import com.tyrellplayz.advancedtech.api.system.SystemSettings;
import com.tyrellplayz.advancedtech.api.system.Tooltip;
import com.tyrellplayz.advancedtech.core.computer.MainTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

public class SettingsApplication extends Application {

    private Layer mainLayer;

    private Button systemBtn;
    private Button personalisationBtn;
    private Button applicationsBtn;
    private Button usersBtn;

    private Layer systemLayer;

    private Layer personalisationLayer;
    private Tab personalisationTab;
    private Layer backgroundLayer;

    private Button personalisationOptionWallpaper;

    private Layer applicationsLayer;

    private Layer usersLayer;

    public SettingsApplication() {
        setShowIcon(false);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        mainLayer = new Layer(this,213,100);

        systemBtn = new Button(2,2, Icons.COMPUTER,"System");
        systemBtn.setCustomRender(new Button.FlatStyle());
        systemBtn.setClickListener(i -> {
            setActiveLayer(personalisationLayer);
        });

        personalisationBtn = new Button(2,22, Icons.BRUSH,"Personalisation");
        personalisationBtn.setCustomRender(new Button.FlatStyle());
        personalisationBtn.setClickListener(i -> {
            setActiveLayer(personalisationLayer);
        });

        applicationsBtn = new Button(2,42, Icons.WINDOW,"Apps");
        applicationsBtn.setCustomRender(new Button.FlatStyle());
        applicationsBtn.setTooltip(new Tooltip("Coming Soon"));

        usersBtn = new Button(2,62, Icons.USER,"Users");
        usersBtn.setCustomRender(new Button.FlatStyle());
        usersBtn.setTooltip(new Tooltip("Coming Soon"));

        mainLayer.addComponent(systemBtn);
        mainLayer.addComponent(personalisationBtn);
        mainLayer.addComponent(applicationsBtn);
        mainLayer.addComponent(usersBtn);

        setActiveLayer(mainLayer);

        // System

        // Personalisation
        personalisationLayerSetup();


        // Applications

        // Users

    }

    private void systemLayerSetup() {

    }

    private void personalisationLayerSetup() {
        personalisationLayer = new Layer(this,213,100);

        personalisationTab = new Tab(66,2,148,100);
        personalisationLayer.addComponent(new Rectangle(67,0,2,personalisationLayer.getHeight(), MainTheme.WINDOW_BORDER_COLOUR));

        ItemList<String> personalisationOptionList = new ItemList<>(1,1,66,98);
        personalisationOptionList.getItems().add("Home");
        personalisationOptionList.getItems().add("Background");
        personalisationOptionList.setGetIcon(text -> {
            if(text.equals("Home")) return Icons.LEFT_ARROW;
            return null;
        });
        personalisationOptionList.setOnItemSelected((text, index) -> {
            if(index >= 1) {
                personalisationTab.setActiveTab(index-1);
            }else if(index == 0) {
                setActiveLayer(mainLayer);
            }
            return false;
        });
        personalisationLayer.addComponent(personalisationOptionList);

        // Background
        backgroundLayer = new Layer(this,personalisationTab.getWidth(),personalisationTab.getHeight());
        backgroundLayer.addComponent(new Label(5,2,"Background"));

        ImageShow backgroundImgShow = new ImageShow(25,15,100,1.78F);
        backgroundImgShow.setImages(getBackgrounds());
        backgroundImgShow.setImageIndex(backgroundImgShow.findImageIndex(getSystemSettings().getBackgroundLocation()));
        backgroundImgShow.setBorder(1,MainTheme.WINDOW_BORDER_COLOUR);
        backgroundLayer.addComponent(backgroundImgShow);

        Button backgroundLeftBtn = new Button(5,34,Icons.LEFT_ARROW);
        backgroundLeftBtn.setCustomRender(new Button.FlatStyle());
        backgroundLeftBtn.setClickListener(code -> {
            if(code == 0) {
                backgroundImgShow.previousImage();
            }
        });
        backgroundLayer.addComponent(backgroundLeftBtn);

        Button wallpaperRightBtn = new Button(127,34,Icons.RIGHT_ARROW);
        wallpaperRightBtn.setCustomRender(new Button.FlatStyle());
        wallpaperRightBtn.setClickListener(code -> {
            if (code == 0) {
                backgroundImgShow.nextImage();
            }
        });
        backgroundLayer.addComponent(wallpaperRightBtn);

        Button setBackgroundBtn = new Button(31,73,"Set Background");
        setBackgroundBtn.setClickListener(code -> {
            ResourceLocation imageLocation = backgroundImgShow.getImages().get(backgroundImgShow.getImageIndex());
            getSystemSettings().setBackgroundLocation(imageLocation);
        });
        backgroundLayer.addComponent(setBackgroundBtn);

        personalisationTab.addTab(backgroundLayer);
        personalisationTab.setActiveTab(0);

        personalisationLayer.addComponent(personalisationOptionWallpaper);
        personalisationLayer.addComponent(personalisationTab);
    }

    public List<ResourceLocation> getBackgrounds() {
        return Minecraft.getInstance().getResourceManager().listResources("textures/gui/background",name -> name.endsWith(".png")).stream()
                .sorted(ResourceLocation::compareTo)
                .collect(Collectors.toList());
    }

    public SystemSettings getSystemSettings() {
        return getWindow().getComputer().getSystemSettings();
    }

}
