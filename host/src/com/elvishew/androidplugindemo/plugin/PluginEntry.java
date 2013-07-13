
package com.elvishew.androidplugindemo.plugin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;


import dalvik.system.DexClassLoader;

public class PluginEntry {

    private static final String TAG = PluginEntry.class.getSimpleName();

    public String packageName = "";

    public String pluginClass = "";

    public String dexPath, dexOutputDir, libPath;

    public String lable;

    public Drawable icon;

    public PluginEntry(String packageName, String pluginClass) {
        this.packageName = packageName;
        this.pluginClass = pluginClass;
    }

    Plugin createPlugin(Context context) {
        try {
            Class<?> c = getClassByName(context, packageName, pluginClass);
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

    private Class<?> getClassByName(Context context, String packageName, final String pluginClass)
            throws ClassNotFoundException {
        Class<?> clazz = null;
        /*Context packageContext;
        try {
            packageContext = context.createPackageContext(packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            ClassLoader loader = packageContext.getClassLoader();
            clazz = loader.loadClass(pluginClass);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }*/
        DexClassLoader cl = new DexClassLoader(dexPath, dexOutputDir, null, context
                .getClassLoader());
        try {
            clazz = cl.loadClass(pluginClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    private boolean isPlugin(Class<?> c) {
        if (c != null) {
            return Plugin.class.isAssignableFrom(c);
        }
        return false;
    }

}
