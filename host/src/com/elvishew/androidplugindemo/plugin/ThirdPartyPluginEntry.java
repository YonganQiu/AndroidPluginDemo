
package com.elvishew.androidplugindemo.plugin;

import android.content.Context;
import dalvik.system.DexClassLoader;

public class ThirdPartyPluginEntry extends PluginEntry {

    private String dexPath, optimizedDirectory;

    public ThirdPartyPluginEntry(String pluginClass, String dexPath, String optimizedDirectory) {
        super(pluginClass);
        this.dexPath = dexPath;
        this.optimizedDirectory = optimizedDirectory;
    }

    @Override
    protected ClassLoader getTargetClassLoader(Context context) {
        return new DexClassLoader(dexPath, optimizedDirectory, null, context.getClassLoader());
    }

    @Override
    public PluginEntry copy() {
        PluginEntry entry = new ThirdPartyPluginEntry(optimizedDirectory, dexPath,
                optimizedDirectory);
        entry.label = this.label;
        entry.icon = this.icon;
        return entry;
    }
}
