package com.elvishew.androidplugindemo.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.elvishew.androidplugindemo.host.DefaultPlugin;

public class PluginManager {

    public static final String PLUGIN = "plugin";

    private static final String TAG = PluginManager.class.getSimpleName();

    private static final String SHARE_USER_ID = "com.elvishew.androidplugindemo";
    private Context mContext;

    private boolean firstRun = true;

    private final List<PluginEntry> entries = new ArrayList<PluginEntry>();

    private String currentPluginClass;

    public PluginManager(Context context) {
        mContext = context;
    }

    public void init() {
        Log.d(TAG, "init()");

        // If first time, then load plugins from plugins.xml file
        if (firstRun) {
            loadInternalPlugins();
            findExternalPlugins();
            firstRun = false;
        }
    }

    /**
     * Load plugins from res/xml/plugins.xml
     */
    public void loadInternalPlugins() {
        String packageName = mContext.getPackageName();
        int id = mContext.getResources().getIdentifier("plugins", "xml", mContext.getPackageName());
        if (id == 0) {
            pluginConfigurationMissing();
            //We have the error, we need to exit without crashing!
            return;
        }
        XmlResourceParser xml = mContext.getResources().getXml(id);
        int eventType = -1;
        String pluginClass = "";
        PluginEntry entry = null;
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
                String strNode = xml.getName();
                if (strNode.equals("plugin")) {
                    pluginClass = xml.getAttributeValue(null, "class");
                    entry = new PluginEntry(packageName, pluginClass);
                    addPlugin(entry);
                }
            }
            try {
                eventType = xml.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void findExternalPlugins() {
        PackageManager pm = mContext.getPackageManager();

        List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        String pkgName;
        String pluginClass = null;
        PluginEntry plugin;
        for (PackageInfo pkg : pkgs) {
            String sharedUserId = pkg.sharedUserId;

            if (!SHARE_USER_ID.equals(sharedUserId))
                continue;
            pkgName = pkg.packageName;
            pluginClass = pkgName + ".PluginImpl";
            plugin = new PluginEntry(pkgName, pluginClass);
            plugin.dexPath = pkg.applicationInfo.sourceDir;
            plugin.dexOutputDir = mContext.getApplicationInfo().dataDir;
            plugin.lable = pkg.applicationInfo.loadLabel(pm).toString();
            plugin.icon = pkg.applicationInfo.loadIcon(pm);
//            plugin.libPath = pkg.applicationInfo.nativeLibraryDir;

            entries.add(plugin);
        }
        if (pluginClass != null && pluginClass.length() > 0) {
            currentPluginClass = pluginClass;
        }
    }

    private void addPlugin(PluginEntry entry) {
        entries.add(entry);
    }

    public List<PluginEntry> getPlugins() {
        return entries;
    }

    public void setCurrentPlugin(String pluginClass) {
        currentPluginClass = pluginClass;
    }

    public String getCurrentPlugin() {
        return currentPluginClass;
    }

    public Plugin createPlugin(Context context) {
        Plugin plugin = null;
        if (currentPluginClass != null && currentPluginClass.length() > 0) {
            for (PluginEntry entry : entries) {
                if (currentPluginClass.equals(entry.pluginClass)) {
                    plugin = entry.createPlugin(context);
                }
            }
            if (plugin != null) {
                return plugin;
            }
        }
        return new DefaultPlugin(mContext);
    }

    private void pluginConfigurationMissing() {
        Log.e(TAG, "=======================================================================");
        Log.e(TAG, "ERROR: plugin.xml is missing.  Add res/xml/plugins.xml to your project.");
        Log.e(TAG, "=======================================================================");
    }
}
