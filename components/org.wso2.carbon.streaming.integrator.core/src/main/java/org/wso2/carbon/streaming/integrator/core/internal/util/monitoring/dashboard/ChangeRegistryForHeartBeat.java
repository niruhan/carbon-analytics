/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.streaming.integrator.core.internal.util.monitoring.dashboard;

/**
 * This class keeps track of any changes to siddhi apps between heartbeats
 */
public class ChangeRegistryForHeartBeat {
    private static ChangeRegistryForHeartBeat instance = new ChangeRegistryForHeartBeat();
    private boolean isChangedSinceLastHeartBeat = false;
    private final Object lock = new Object();

    private ChangeRegistryForHeartBeat() {}

    public static ChangeRegistryForHeartBeat getInstance() {
        return instance;
    }

    public synchronized boolean getIsChangedSinceLastHeartBeat() {
        synchronized (lock) {
            return isChangedSinceLastHeartBeat;
        }
    }

    public synchronized void setChangedSinceLastHeartBeat() {
        synchronized (lock) {
            isChangedSinceLastHeartBeat = true;
        }
    }

    public synchronized void resetChangedSinceLastHeartBeat() {
        synchronized (lock) {
            isChangedSinceLastHeartBeat = false;
        }
    }
}
