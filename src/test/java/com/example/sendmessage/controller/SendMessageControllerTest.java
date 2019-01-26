package com.example.sendmessage.controller;

import com.example.sendmessage.BaseTest;
import com.example.sendmessage.entity.PhoneInfo;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2019/1/26 16:30
 * description:
 */
public class SendMessageControllerTest extends BaseTest {

    @Test
    public void sendMessage() {
        PhoneInfo phoneInfo = new PhoneInfo();
        phoneInfo.setPhone("15600663638");
        phoneInfo.setContent("teste");
        String url = "/sendMessage";
        postJSONObject(url, phoneInfo);
    }
}