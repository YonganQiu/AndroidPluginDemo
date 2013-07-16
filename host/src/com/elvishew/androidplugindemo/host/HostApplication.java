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

package com.elvishew.androidplugindemo.host;

import com.elvishew.androidplugindemo.host.plugin.PluginManager;

import android.app.Application;

/**
 * Plugin host application.
 */
public class HostApplication extends Application {

    /**
     * Should be the only instance of {@link PluginManager}.
     */
    private PluginManager mPluginManager;

    @Override
    public Object getSystemService(String name) {
        if (PluginManager.PLUGIN.equals(name)) {
            if (mPluginManager == null) {
                mPluginManager = new PluginManager(this);
            }
            return mPluginManager;
        }
        return super.getSystemService(name);
    }
}
