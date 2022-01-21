package com.tyrellplayz.advancedtech.application.system;

import com.tyrellplayz.advancedtech.api.component.Button;
import com.tyrellplayz.advancedtech.api.component.ButtonList;
import com.tyrellplayz.advancedtech.api.content.Layer;
import com.tyrellplayz.advancedtech.api.content.application.Application;
import com.tyrellplayz.advancedtech.api.icon.Icons;
import com.tyrellplayz.advancedtech.api.system.SystemSettings;
import com.tyrellplayz.advancedtech.api.system.Tooltip;

public class SettingsApplication extends Application {

    private Layer mainLayer;

    private Button systemBtn;
    private Button personalisationBtn;
    private Button applicationsBtn;
    private Button usersBtn;

    private Layer systemLayer;
    private Layer personalisationLayer;
    private ButtonList personalisationOptionsList;

    private Layer applicationsLayer;
    private Layer usersLayer;

    public SettingsApplication() {
        setShowIcon(false);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        mainLayer = new Layer(this,103,100);
        systemBtn = new Button(2,2, Icons.COMPUTER,"System");
        systemBtn.setCustomRender(new Button.FlatStyle());
        systemBtn.setTooltip(new Tooltip("Coming Soon"));

        personalisationBtn = new Button(2,22, Icons.BRUSH,"Personalisation");
        personalisationBtn.setCustomRender(new Button.FlatStyle());
        personalisationBtn.setTooltip(new Tooltip("Coming Soon"));
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
        personalisationLayer = new Layer(this,100,100);
        personalisationOptionsList = new ButtonList(2,2,98,98);

        // Applications

        // Users

    }

    public SystemSettings getSystemSettings() {
        return getWindow().getComputer().getSystemSettings();
    }

}
