package com.example.sendmessage.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2018/8/16 10:11
 * description:
 */
@Slf4j
public class HttpClientUtils {

    private HttpClientUtils() {

    }


    private static final HttpClientUtils INSTANCE = new HttpClientUtils();
    /**
     * 最大尝试次数
     */
    private static final int MAX_RETRY = 3;

    public static HttpClientUtils getInstance() {
        return INSTANCE;
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }

    public String httpGet(String url, Map<String, Object> paramMap) {
        return _httpRequest(url, null, paramMap, StandardCharsets.UTF_8, 0, HttpMethod.GET, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    public String httpPostFrom(String url, Map<String, Object> paramMap) {
        return _httpRequest(url, null, paramMap, StandardCharsets.UTF_8, 0, HttpMethod.POST, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }

    public String httpPostJson(String url, Map<String, Object> paramMap) {
        return _httpRequest(url, null, paramMap, StandardCharsets.UTF_8, 0, HttpMethod.POST, MediaType.APPLICATION_JSON_VALUE);
    }

    public String httpPostJson(String url, Map<String, String> headerMap, Map<String, Object> paramMap) {
        return _httpRequest(url, headerMap, paramMap, StandardCharsets.UTF_8, 0, HttpMethod.POST, MediaType.APPLICATION_JSON_VALUE);
    }


    /**
     * HttpPost请求，控制尝试次数
     */
    private String _httpRequest(String url, Map<String, String> headerMap, Map<String, Object> paramMap, Charset charset, int reTry, HttpMethod httpMethod, String mediaType) {
        long startTime = System.currentTimeMillis();
        String paramJson = JSONObject.parseObject(JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect)).toJSONString();
        log.info("请求URl：{}", url);
        log.info("请求头：{}", headerMap);
        log.info("请求内容：{}", paramJson);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = null;
        try {
            httpClient = getHttpClient();
            if (httpMethod == HttpMethod.GET) {   //get 请求
                List<NameValuePair> parameters = createNameValuePair(paramMap);
                URIBuilder builder = new URIBuilder(url);
                builder.setParameters(parameters);
                HttpGet httpGet = new HttpGet(builder.build());
                httpGet.setConfig(requestConfig);
                httpGet.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE));
                // 执行请求访问
                response = httpClient.execute(httpGet);
            }
            if (httpMethod == HttpMethod.POST) {  //post 请求
                HttpPost httpPost = new HttpPost(url);
                httpPost.setConfig(requestConfig);
                if (headerMap != null) {
                    headerMap.forEach(httpPost::setHeader);
                }
                if (Objects.equal(mediaType, MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {    //表单提交
                    List<NameValuePair> parameters = createNameValuePair(paramMap);
                    httpPost.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE));
                    HttpEntity paramEntity = new UrlEncodedFormEntity(parameters, charset);
                    httpPost.setEntity(paramEntity);

                }
                if (Objects.equal(mediaType, MediaType.APPLICATION_JSON_VALUE)) { //json提交
                    StringEntity stringEntity = new StringEntity(paramJson, charset);
                    stringEntity.setContentEncoding(charset.toString());
                    stringEntity.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    httpPost.setHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE));
                    httpPost.setEntity(stringEntity);
                }

                response = httpClient.execute(httpPost);
                // 执行请求访问
            }
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        result = EntityUtils.toString(entity, charset);
                    }
//                    logger.info("返回结果为：{}", result);
                } else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    // 302
                    log.error("访问地址已经改变请更新访问地址");
                } else {
                    log.error("操作失败，Request URL：{}, params：{}, httpStatus: {}", url, paramJson, statusCode);
                }
            } else {
                log.error("操作失败，Request URL：{}, params：{}", url, paramJson);
            }

        } catch (Exception e) {
            if (reTry < MAX_RETRY) {
                reTry++;
                log.error("请求失败，尝试再次请求：{},Request URL：{}, params：{}", reTry, url, paramJson);
                return _httpRequest(url, headerMap, paramMap, charset, reTry, httpMethod, mediaType);
            } else {
                log.error("请求异常，已超出最大尝试次数：{}，Request URL：{}, params：{}, Exception:{}", MAX_RETRY, url, paramJson, e);
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("关闭httpclient连接出错，异常信息：{}", e);
            }
            log.info("响应时间：{}", (System.currentTimeMillis() - startTime));
        }
        return result;
    }

    private List<NameValuePair> createNameValuePair(Map<String, Object> paramMap) {
        List<NameValuePair> parameters = new ArrayList<>();
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue() + ""));
            }
        }
        parameters.add(new BasicNameValuePair("livedCode", String.valueOf(System.currentTimeMillis())));
        return parameters;
    }

    private static RequestConfig requestConfig = RequestConfig
            .custom()
            .setCookieSpec(CookieSpecs.STANDARD)
            .setConnectTimeout(15000) // 设置连接超时时间
            .setConnectionRequestTimeout(30000) // 设置请求超时时间
            .setSocketTimeout(60000)// 设置读数据超时时间(单位毫秒)
            .setRedirectsEnabled(true)// 默认允许自动重定向
            .build();

    public HttpPost getGzipHttpPost(String url) {
        HttpPost postMethod = new HttpPost(url);
        postMethod.addHeader(new BasicHeader("Content-Type", "text/html;charset=UTF-8"));
        postMethod.addHeader(new BasicHeader("accept-encoding", "gzip"));
        postMethod.addHeader(new BasicHeader("content-encoding", "gzip"));
        return postMethod;
    }
}
