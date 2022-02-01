package com.tyrellplayz.tech_craft.core.computer;

import java.awt.*;

public class MainTheme {

    public static final Color WINDOW_BORDER_COLOUR = new Color(64, 64, 64);

    public static final Color TASK_BAR_COLOUR = new Color(64, 64, 64);

    public static final Color TEXT_COLOUR = Color.WHITE;

    public static Color setAlpha(Color colour, int alpha) {
        return new Color(colour.getRed(),colour.getGreen(),colour.getBlue(),alpha);
    }

}
