package com.example.sendmessage.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2018/8/21 15:11
 * description: 短信配置
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "custom.phone")
public class PhoneProperties {

    private String host;

    private String username;

    private String password;

    private String state;

    private String extend;
}
