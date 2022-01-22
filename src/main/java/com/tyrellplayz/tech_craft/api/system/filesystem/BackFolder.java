package com.tyrellplayz.tech_craft.api.system.filesystem;

import com.tyrellplayz.tech_craft.api.icon.Icon;
import com.tyrellplayz.tech_craft.api.icon.Icons;

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
