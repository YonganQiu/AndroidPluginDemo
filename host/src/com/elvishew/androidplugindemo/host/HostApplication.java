package com.elvishew.androidplugindemo.host;

import com.elvishew.androidplugindemo.plugin.PluginManager;

import android.app.Application;

public class HostApplication extends Application {

    private PluginManager mPluginManager;

    @Override
    public Object getSystemService(String name) {
        if (PluginManager.PLUGIN.equals(name)) {
            if (mPluginManager == null) {
                mPluginManager = new PluginManager(this);
                mPluginManager.init();
            }
            return mPluginManager;
        }
        return super.getSystemService(name);
    }
}
