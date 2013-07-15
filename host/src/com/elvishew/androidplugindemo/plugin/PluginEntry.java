/*
 * Copyright 2013 Elvis Hew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elvishew.androidplugindemo.plugin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Representing an plugin, contains infomations about plugin like class name,
 * label and icon.
 */
public abstract class PluginEntry {

    private static final String TAG = PluginEntry.class.getSimpleName();

    /**
     * The plugin class that you should used to create an instance.
     * formated as [packageName.className].
     */
    public String pluginClass;

    /**
     * Typical representing the name of this plugin.
     */
    public String label;

    /**
     * The icon of this plugin, you can used it in plugins chooser activity.
     */
    public Drawable icon;

    public PluginEntry(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    /**
     * Create an instance of this plugin.
     */
    Plugin createPlugin(Context context) {
        try {
            Class<?> c = loadClass(context, pluginClass);
            if (isPlugin(c)) {
                Object o = c.newInstance();
                return (Plugin) o;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error adding plugin " + pluginClass + ".");
        }
        return null;
    }

    /**
     * Load class by name.
     */
    private Class<?> loadClass(Context context, final String pluginClass) {
        Class<?> clazz = null;
        ClassLoader cl = getTargetClassLoader(context);
        try {
            clazz = cl.loadClass(pluginClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * Get ClassLoader to load PluginImpl class.
     * 
     * @param context context from plugin host
     * @return the target class loader
     */
    abstract ClassLoader getTargetClassLoader(Context context);

    /**
     * Check if the class if a plugin class.
     * 
     * @param c the class to check
     * @return true if the class is a plugin class, false if not
     */
    private boolean isPlugin(Class<?> c) {
        if (c != null) {
            return Plugin.class.isAssignableFrom(c);
        }
        return false;
    }

    /**
     * Copy a new entry from current one.
     * 
     * @return the copied new entry
     */
    abstract PluginEntry copy();
}
