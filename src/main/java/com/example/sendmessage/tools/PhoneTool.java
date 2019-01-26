package com.example.sendmessage.tools;

import com.example.sendmessage.entity.PhoneInfo;
import com.example.sendmessage.properties.PhoneProperties;
import com.example.sendmessage.utils.HttpClientUtils;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2018/8/21 15:53
 * description: 短信工具类
 */
@AllArgsConstructor
@Slf4j
@Component
public class PhoneTool {

    private final PhoneProperties phoneProperties;

    public boolean sendShortMessage(PhoneInfo phoneInfo) {
        String phoneT = phoneInfo.getPhone();
        String content = phoneInfo.getContent();
        String host = phoneProperties.getHost();
        String username = phoneProperties.getUsername();
        String password = phoneProperties.getPassword();
        String state = phoneProperties.getState();
        String extend = phoneProperties.getExtend();
        log.info("发送短信参数：phoneInfo = {}, content = {}", phoneT, content);
        Map<String, Object> params = Maps.newHashMap();
        params.put("un", username);
        params.put("pw", password);
        params.put("phoneInfo", phoneT);
        params.put("rd", state);
        params.put("msg", content);
        params.put("ex", extend);
        String response = HttpClientUtils.getInstance().httpPostFrom(host, params);
        return StringUtils.isNotEmpty(response);
    }
}
