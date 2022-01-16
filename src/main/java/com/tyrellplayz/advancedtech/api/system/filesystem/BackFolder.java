package com.tyrellplayz.advancedtech.api.system.filesystem;

import com.tyrellplayz.advancedtech.api.icon.Icon;
import com.tyrellplayz.advancedtech.api.icon.Icons;

public class BackFolder implements FileSystemItem {

    @Override
    public String getName() {
        return "<-";
    }

    @Override
    public Icon getIcon() {
        return Icons.FOLDER;
    }
}
