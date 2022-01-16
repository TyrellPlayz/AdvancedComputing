package com.tyrellplayz.advancedtech.api.content.dialog;

import com.tyrellplayz.advancedtech.api.component.Button;
import com.tyrellplayz.advancedtech.api.component.Label;
import com.tyrellplayz.advancedtech.api.content.Layer;
import com.tyrellplayz.advancedtech.api.icon.Icons;

public class ErrorDialog extends Dialog {

    public ErrorDialog(String title, String message) {
        super(Icons.ERROR, title, message);
    }

    public void onLoad() {
        Layer layer = new Layer(this, 170, 42);
        Label label = new Label(2, 2, this.getMessage());
        layer.addComponent(label);
        Button buttonOk = new Button(2, 22, "Ok");
        buttonOk.setClickListener((code) -> {
            this.getWindow().close();
        });
        layer.addComponent(buttonOk);
        this.setActiveLayer(layer);
    }

}
