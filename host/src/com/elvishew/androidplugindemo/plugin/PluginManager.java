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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.elvishew.androidplugindemo.host.plugin.DefaultPlugin;

/**
 * Manager all plugins, both local plugins and third-party plugins.
 * <p>
 * You can fetch it via {@link Application#getSystemService(String)}, with the
 * name of {@link #PLUGIN}.
 */
public class PluginManager {

    /**
     * Used to fetch a {@link PluginManager} via
     * {@link Application#getSystemService(String)}
     */
    public static final String PLUGIN = "plugin";

    private static final String TAG = PluginManager.class.getSimpleName();

    private static final String SHARE_USER_ID = "com.elvishew.androidplugindemo";

    private Context mContext;

    private final List<PluginEntry> mPluginEntries = new ArrayList<PluginEntry>();

    /**
     * When {@link #createPlugin(Context)}, this class will be used.
     */
    private String mCurrentPluginClass;

    private BroadcastReceiver mPackageChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                // TODO
            } else if (Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
                // TODO
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                // TODO
            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                // TODO
            }
            
        }
    };

    public PluginManager(Context context) {
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        context.registerReceiver(mPackageChangedReceiver, filter);
        init();
    }

    private void init() {
        loadConfigsAndLocalPlugins();
        findThirdPartyPlugins();
    }

    /**
     * Load configs and local plugins from res/xml/configs.xml
     */
    public void loadConfigsAndLocalPlugins() {
        int id = mContext.getResources().getIdentifier("configs", "xml", mContext.getPackageName());
        if (id == 0) {
            printConfigsMissingMsg();
            return;
        }
        XmlResourceParser xml = mContext.getResources().getXml(id);
        int eventType = -1;
        String defaultPlugin = null;
        String labelId, iconId;
        String pluginClass;
        LocalPluginEntry entry;
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
                String strNode = xml.getName();
                if (strNode.equals("plugin")) {
                    pluginClass = xml.getAttributeValue(null, "class");
                    labelId = xml.getAttributeValue(null, "label_id");
                    iconId = xml.getAttributeValue(null, "icon_id");
                    entry = new LocalPluginEntry(pluginClass);
                    entry.label = getLabel(labelId);
                    entry.icon = getIcon(iconId);
                    mPluginEntries.add(entry);

                    // Check if this plugin should be the default one.
                    if (xml.getAttributeBooleanValue(null, "default", false)) {
                        defaultPlugin = pluginClass;
                    }
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

        // Setup default plugin.
        if (!TextUtils.isEmpty(defaultPlugin)) {
            setCurrentPlugin(defaultPlugin);
        }
    }

    private String getLabel(String labelId) {
        int id = mContext.getResources()
                .getIdentifier(labelId, "string", mContext.getPackageName());
        String label = null;
        if (id > 0) {
            label =  mContext.getResources().getString(id);
        }
        return label;
    }

    private Drawable getIcon(String iconId) {
        int id = mContext.getResources().getIdentifier(iconId, "drawable",
                mContext.getPackageName());
        Drawable drawable = null;
        if (id > 0) {
            drawable =  mContext.getResources().getDrawable(id);
        }
        return drawable;
    }

    /**
     * Query the system is any third-party plugin exists. <br>
     * Package that contains plugin should has the shareUserId of
     * {@value #SHARE_USER_ID}, and plugin class with the name "PluginImpl".
     */
    private void findThirdPartyPlugins() {
        PackageManager pm = mContext.getPackageManager();

        List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        String pkgName;
        String pluginClass = null;
        ThirdPartyPluginEntry entry;
        for (PackageInfo pkg : pkgs) {
            String sharedUserId = pkg.sharedUserId;

            if (!SHARE_USER_ID.equals(sharedUserId)) {
                continue;
            }
            pkgName = pkg.packageName;
            pluginClass = pkgName + ".PluginImpl";
            entry = new ThirdPartyPluginEntry(pluginClass, pkg.applicationInfo.sourceDir,
                    mContext.getApplicationInfo().dataDir);

            // TODO: Loading label and icon will cost time, maybe we should do
            // it background.
            entry.label = pkg.applicationInfo.loadLabel(pm).toString();
            entry.icon = pkg.applicationInfo.loadIcon(pm);

            mPluginEntries.add(entry);
        }
    }

    /**
     * Get plugin entries currently system has, both local's and third-party's.
     * 
     * @return the plugin entries
     */
    public List<PluginEntry> getPlugins() {
        List<PluginEntry> plugins = new ArrayList<PluginEntry>();
        for (PluginEntry entry : mPluginEntries) {
            plugins.add(entry.copy());
        }
        return mPluginEntries;
    }

    /**
     * Set the plugin to be used when {@link #createPlugin(Context)}.
     * 
     * @param pluginClass the class name of the plugin to be set
     */
    public void setCurrentPlugin(String pluginClass) {
        mCurrentPluginClass = pluginClass;
    }

    /**
     * Get the currently using plugin class name.
     * 
     * @return the currently using plugin class name
     */
    public String getCurrentPlugin() {
        return mCurrentPluginClass;
    }

    /**
     * Create an plugin instance.
     * 
     * @return the newly created plugin instance
     */
    public Plugin createPlugin(Context context) {
        Plugin plugin = null;
        if (mCurrentPluginClass != null && mCurrentPluginClass.length() > 0) {
            for (PluginEntry entry : mPluginEntries) {
                if (mCurrentPluginClass.equals(entry.pluginClass)) {
                    plugin = entry.createPlugin(context);
                    break;
                }
            }
            if (plugin != null) {
                return plugin;
            }
        }
        return new DefaultPlugin(mContext);
    }

    /**
     * Print configurations missing error message.
     */
    private void printConfigsMissingMsg() {
        Log.e(TAG, "=======================================================================");
        Log.e(TAG, "ERROR: configs.xml is missing.  Add res/xml/configs.xml to your project.");
        Log.e(TAG, "=======================================================================");
    }
}
