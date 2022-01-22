package com.tyrellplayz.tech_craft.api.content.dialog;

import com.tyrellplayz.tech_craft.api.component.Button;
import com.tyrellplayz.tech_craft.api.component.Label;
import com.tyrellplayz.tech_craft.api.component.TextField;
import com.tyrellplayz.tech_craft.api.content.Layer;
import com.tyrellplayz.tech_craft.api.icon.Icon;

import java.util.function.Consumer;
import java.util.function.Function;

public class InputDialog extends Dialog {

    private Consumer<String> inputListener;
    private Function<String, Boolean> validate;
    private final String placeholder;
    private TextField textField;
    private Button okButton;

    public InputDialog(Icon icon, String title, String message, String placeholder) {
        super(icon, title, message);
        this.placeholder = placeholder;
    }

    public void setInputListener(Consumer<String> inputListener) {
        this.inputListener = inputListener;
    }

    public void setValidation(Function<String, Boolean> validate) {
        this.validate = validate;
    }

    public void onLoad() {
        Layer mainLayer = new Layer(this, 150, 54);
        mainLayer.addComponent(new Label(2, 3, this.getMessage()));
        this.textField = new TextField(2, 15, mainLayer.getWidth() - 4);
        this.okButton = new Button(3, 34, "Ok");
        this.textField.setPlaceholder(this.placeholder);
        this.textField.setFocused(true);
        this.textField.setKeyListener((c, code) -> {
            if (this.validate != null) {
                if (this.validate.apply(this.textField.getText())) {
                    this.textField.clearError();
                    this.okButton.setEnabled(true);
                } else {
                    this.textField.setError("Input not valid");
                    this.okButton.setEnabled(false);
                }
            }

            return true;
        });
        mainLayer.addComponent(this.textField);
        this.okButton.setEnabled(false);
        this.okButton.setClickListener((mouseButton) -> {
            String input = this.textField.getText();
            if (this.validate != null) {
                if (this.validate.apply(input)) {
                    if (this.inputListener != null) {
                        this.inputListener.accept(input);
                        this.getWindow().close();
                    }
                } else {
                    this.textField.setError("Input not valid");
                }
            }

        });
        mainLayer.addComponent(this.okButton);
        Button cancelButton = new Button(26, 34, "Cancel");
        cancelButton.setClickListener((mouseButton) -> {
            this.getWindow().close();
        });
        mainLayer.addComponent(cancelButton);
        this.setActiveLayer(mainLayer);
    }

}
