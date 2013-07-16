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

package com.elvishew.androidplugindemo.host.plugin;

import android.content.Context;
import dalvik.system.DexClassLoader;

/**
 * Representing third-party plugin, from third-party package.
 */
public class ThirdPartyPluginEntry extends PluginEntry {

    /**
     * Dex path: the plugin class from where to find.
     */
    private String dexPath;

    /**
     * The host application data directory.
     */
    private String optimizedDirectory;

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
        PluginEntry entry = new ThirdPartyPluginEntry(pluginClass, dexPath,
                optimizedDirectory);
        entry.label = this.label;
        entry.icon = this.icon;
        return entry;
    }
}
