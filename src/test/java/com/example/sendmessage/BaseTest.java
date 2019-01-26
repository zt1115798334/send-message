package com.example.sendmessage;

import com.alibaba.fastjson.JSONObject;
import com.example.sendmessage.utils.RSAUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2018/8/27 10:22
 * description:
 */
public class BaseTest {

    private WebClient webClient = null;
    private  String token = "";

    private String timestamp = null;
    @Before
    public void before() throws Exception {
        timestamp = String.valueOf(System.currentTimeMillis());
        String prefix = "zhongkedianji";
        String suffix = "zkdj@123";
        String str = prefix + timestamp + suffix;
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAImV91yGYl0EDO1XtdKTUndBhL2IeI6BaBoZeC6+NJh/nc0+QoUfl5qdm/iBGmW+9P8fgpg+xiMHFCqy7pM6W30CAwEAAQ==";
        byte[] code1 = RSAUtils.encryptByPublicKey(str.getBytes(), Base64.decodeBase64(publicKey));
        token = Base64.encodeBase64String(code1);
        webClient = WebClient.builder().baseUrl("http://127.0.0.1:8089").build();
    }

    protected void postJSONObject(String url, Object obj) {
        Mono<JSONObject> params = Mono.just(JSONObject.parseObject(JSONObject.toJSONString(obj)));
        String block = webClient.post().uri(url)
                .header("token", token)
                .header("timestamp", timestamp)
                .contentType(MediaType.APPLICATION_JSON)
                .body(params, JSONObject.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        System.out.println("block = " + block);
    }
}
