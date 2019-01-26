package com.example.sendmessage.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.sendmessage.custom.SystemStatusCode;
import com.example.sendmessage.entity.ResultMessage;
import com.example.sendmessage.properties.RSAProperties;
import com.example.sendmessage.utils.RSAUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2018/11/14 13:33
 * description: 身份权限认证过滤 -- 过滤 sys下面的接口
 */
@Slf4j
@Component
@Order(1)
public class AuthTokenFilter implements WebFilter {

    @Autowired
    private RSAProperties rsaProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("token验证...");
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String token = request.getHeaders().getFirst("token");
        String timestamp = request.getHeaders().getFirst("timestamp");
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(timestamp)) {
            log.info("字段为空");
            ResultMessage message = new ResultMessage().ok(SystemStatusCode.FAILED.getCode(), "参数为空");
            return response.writeWith(Mono.just(response.bufferFactory().wrap(JSONObject.toJSONBytes(message))));
        } else {
            long requestInterval = System.currentTimeMillis() - Long.valueOf(timestamp);
            if (requestInterval > 5 * 60 * 1000) {
                ResultMessage message = new ResultMessage().ok(SystemStatusCode.FAILED.getCode(), "请求已过时");
                return response.writeWith(Mono.just(response.bufferFactory().wrap(JSONObject.toJSONBytes(message))));
            } else {
                String privateKey = rsaProperties.getPrivateKey();
                String prefix = rsaProperties.getPrefix();
                String suffix = rsaProperties.getSuffix();

                try {
                    byte[] decode1 = RSAUtils.decryptByPrivateKey(Base64.decodeBase64(token), Base64.decodeBase64(privateKey));
                    //解密后的数据
                    String decryptStr = new String(decode1);
                    if (decryptStr.startsWith(prefix) || decryptStr.endsWith(suffix)) {
                        String replace = decryptStr.replace(prefix, "").replace(suffix, "");
                        if (Objects.equals(timestamp, replace)) {
                            log.info("验证成功");
                            return chain.filter(exchange);
                        }
                    }
                    ResultMessage message = new ResultMessage().ok(SystemStatusCode.FAILED.getCode(), "验证失败");
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(JSONObject.toJSONBytes(message))));


                } catch (Exception e) {
                    e.printStackTrace();
                }
                ResultMessage message = new ResultMessage().ok(SystemStatusCode.FAILED.getCode(), "系统异常");
                return response.writeWith(Mono.just(response.bufferFactory().wrap(JSONObject.toJSONBytes(message))));
            }

        }
    }
}
