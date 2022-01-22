package com.tyrellplayz.tech_craft.api.content.application;

import com.tyrellplayz.tech_craft.api.system.filesystem.File;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApplicationPreferences {

    public static final String FILE_NAME = "preferences.config";
    private final Application application;
    private final Map<String, String> preferenceMap = new HashMap();

    public ApplicationPreferences(Application application) {
        this.application = application;
    }

    public String getOrDefault(String key, String def) {
        return this.preferenceMap.getOrDefault(key, def);
    }

    public int getOrDefault(String key, int def) {
        return this.preferenceMap.containsKey(key) && NumberUtils.isParsable(this.preferenceMap.get(key)) ? Integer.parseInt(this.preferenceMap.get(key)) : def;
    }

    public boolean getOrDefault(String key, boolean def) {
        return Boolean.getBoolean(this.preferenceMap.getOrDefault(key, Boolean.toString(def)));
    }

    public void set(String key, Object value) {
        this.preferenceMap.put(key, value.toString());
        this.save();
    }

    public void remove(String key) {
        this.preferenceMap.remove(key);
        this.save();
    }

    public void clear() {
        this.preferenceMap.clear();
        this.save();
    }

    public void load() {
        File preferenceFile = this.application.getApplicationFolder().getFile("preferences.config");
        if (preferenceFile != null) {
            CompoundTag data = preferenceFile.getData();
            for (String key : data.getAllKeys()) {
                this.preferenceMap.put(key, data.getString(key));
            }
        }
    }

    public void save() {
        if (!this.preferenceMap.isEmpty() || this.application.getApplicationFolder().containsFile("preferences.config")) {
            if (this.preferenceMap.isEmpty() && this.application.getApplicationFolder().containsFile("preferences.config")) {
                this.application.getApplicationFolder().deleteFile("preferences.config");
            } else {
                CompoundTag data = new CompoundTag();
                this.preferenceMap.forEach(data::putString);

                try {
                    this.application.getApplicationFolder().createFile("preferences.config", true, data);
                } catch (IOException var3) {
                    var3.printStackTrace();
                }

            }
        }
    }

}
