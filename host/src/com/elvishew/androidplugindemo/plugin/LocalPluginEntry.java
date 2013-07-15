
package com.elvishew.androidplugindemo.plugin;

import android.content.Context;

public class LocalPluginEntry extends PluginEntry {

    public LocalPluginEntry(String pluginClass) {
        super(pluginClass);
    }

    @Override
    protected ClassLoader getTargetClassLoader(Context context) {
        return context.getClassLoader();
    }

    @Override
    public PluginEntry copy() {
        PluginEntry entry = new LocalPluginEntry(pluginClass);
        entry.label = this.label;
        entry.icon = this.icon;
        return entry;
    }
}
