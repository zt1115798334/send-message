package com.example.sendmessage.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 1/16/2019 11:36 AM
 * description:
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "custom.rsa")
public class RSAProperties {
    private String privateKey;
    private String prefix;
    private String suffix;
}
