/*
 * Copyright (C) 2015 The Android Open Source Project.
 *
 *        yinglovezhuzhu@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tushuangxi.smart.tv.lding.utils.network;

import android.net.NetworkInfo;

/**
 * 网络状态观察者
 * Created by yinglovezhuzhu@gmail.com on 2015/6/11.
 */
public interface  NetworkObserver {

    /**
     * 网络状态发生改变
     * @param networkConnected 是否没有连接，比如：没有网络
     * @param currentNetwork 当前网络连接信息，没有为null
     * @param lastNetwork 上一个网络连接信息，没有为null
     */
    public abstract void onNetworkStateChanged(boolean networkConnected, NetworkInfo currentNetwork,
                                               NetworkInfo lastNetwork);
}
