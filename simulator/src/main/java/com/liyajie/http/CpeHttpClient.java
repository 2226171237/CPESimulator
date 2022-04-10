package com.liyajie.http;

import com.liyajie.config.CpeConfig;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Liyajie
 */
public class CpeHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CpeHttpClient.class);

    private static final CpeHttpClient INSTANCE = new CpeHttpClient();

    private final CloseableHttpClient httpClient;

    private final CpeConfig cpeConfig;

    private final String postUrl;

    private CpeHttpClient() {
        cpeConfig = CpeConfig.getInstance();
        postUrl = cpeConfig.getAcsHttpType() + "://" + cpeConfig.getAcsHost() + ":" + cpeConfig.getAcsPort() + cpeConfig.getAcsUrl();
        HttpClientBuilder builder = HttpClients.custom();
        httpClient = builder.build();
    }

    public static CpeHttpClient getInstance() {
        return INSTANCE;
    }

    public String sendRequest(String body) {
        HttpPost httpPost = new HttpPost(postUrl);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "text/xml; charset=\"utf-8\"");
        try {
            httpPost.setEntity(new StringEntity(body));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return getResponseString(response);
        } catch (IOException e) {
            LOGGER.error("sendRequest catch an exception: ", e);
        } finally {
            httpPost.releaseConnection();
        }
        return "";
    }

    private String getResponseString(CloseableHttpResponse response) throws IOException {
        if (response == null) {
            LOGGER.error("sendRequest: response is null.");
            throw new IOException("response is null");
        }
        int status = response.getStatusLine().getStatusCode();
        try (response) {
            HttpEntity entity = response.getEntity();
            String body = entity == null ? "" : EntityUtils.toString(entity, StandardCharsets.UTF_8);
            if (status < HttpStatus.SC_OK || status >= HttpStatus.SC_MULTIPLE_CHOICES) {
                LOGGER.error("sendRequest: response status is {}, reason is {}", status, body);
                throw new IOException("response status is error, status is " + status);
            }
            return body;
        }
    }

    public void close() {
        if (httpClient == null) {
            return;
        }
        try {
            httpClient.close();
            LOGGER.info("HttpClient is closed.");
        } catch (IOException e) {
            LOGGER.error("close httpClient catch an exception: ", e);
        }
    }
}
