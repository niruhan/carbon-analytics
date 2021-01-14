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

import java.io.IOException;


public class HeartBeat {

    public void sendPostRequest() throws IOException {
        HttpPost post = new HttpPost("https://webhook.site/e5c57bf4-bc46-4272-b415-315d36374200");
        JSONObject json = new JSONObject();
        json.put("someKey", "someValue2");

        StringEntity params = new StringEntity(json.toString());
        post.addHeader("content-type", "application/json");
        post.setEntity(params);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(post);

        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}
