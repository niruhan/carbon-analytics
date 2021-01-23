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

import net.minidev.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.config.ConfigurationException;
import org.wso2.carbon.config.provider.ConfigProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class produces periodic heartbeats to inform the dashboard server of the existence of a SI node
 */
public class HeartBeatComponent {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatComponent.class);

    private HeartBeatComponent() {}

    public void sendPostRequest() throws IOException {
        HttpPost post = new HttpPost("http://0.0.0.0:9743/api/rest/heartbeat");
        JSONObject json = new JSONObject();
        json.put("groupId", "si1");
        json.put("nodeId", "si2");
        json.put("interval", "3");
        json.put("mgtApiUrl", "sdfsf");

        StringEntity params = new StringEntity(json.toString());
        post.addHeader("content-type", "application/json");
        post.setEntity(params);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post);

        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    public static void invokeHeartbeatExecutorService(ConfigProvider deploymentConfigs) throws IOException,
            ConfigurationException {
        HashMap dashboardConfigs = (LinkedHashMap) deploymentConfigs.getConfigurationObject("dashboard.config");
        HashMap carbonConfigs = (LinkedHashMap) deploymentConfigs.getConfigurationObject("wso2.carbon");
        String heartbeatApiUrl = (String) dashboardConfigs.get("heartbeatApiUrl");
        String mgtApiUrl = (String) dashboardConfigs.get("mgtApiUrl");
        String groupId = "NA";
        long interval = (Integer) dashboardConfigs.get("heartbeatInterval");
        String nodeId = (String) carbonConfigs.get("id");
        String type = (String) carbonConfigs.get("type");

        final HttpPost httpPost = new HttpPost(heartbeatApiUrl);

        JSONObject json = new JSONObject();
        json.put("groupId", groupId);
        json.put("nodeId", nodeId);
        json.put("interval", interval);
        json.put("product", "si");
        json.put("mgtApiUrl", mgtApiUrl);

        StringEntity params = new StringEntity(json.toString());

//        final String payload = "{\n" +
//                "    \"groupId\":\"" + groupId + "\",\n" +
//                "    \"nodeId\":\"" + nodeId + "\",\n" +
//                "    \"interval\":" + interval + ",\n" +
//                "    \"type\":" + type + ",\n" +
//                "    \"isChanged\":" + ChangeRegistryForHeartBeat.getInstance().getIsChangedSinceLastHeartBeat()
//                    + ",\n" +
//                "    \"mgtApiUrl\":\"" + mgtApiUrl + "\"\n" +
//                "}";

//        final StringEntity entity = new StringEntity(payload);
        httpPost.setEntity(params);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Runnable runnableTask = () -> {
            final CloseableHttpClient client = HttpClients.createDefault();
            try {
                client.execute(httpPost);
            } catch (IOException e) {
                log.error("Error occurred while sending http request.", e);
            } finally {
                try {
                    client.close();
                    ChangeRegistryForHeartBeat.getInstance().resetChangedSinceLastHeartBeat();
                } catch (IOException e) {
                    log.error("Error occurred while closing the connection.", e);
                }
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(runnableTask, 1, interval, TimeUnit.SECONDS);
    }

}
