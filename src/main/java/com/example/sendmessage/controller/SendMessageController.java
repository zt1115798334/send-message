package com.example.sendmessage.controller;

import com.example.sendmessage.controller.base.AbstractController;
import com.example.sendmessage.entity.PhoneInfo;
import com.example.sendmessage.entity.ResultMessage;
import com.example.sendmessage.tools.PhoneTool;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2019/1/26 15:47
 * description:
 */
@RestController
@AllArgsConstructor
public class SendMessageController extends AbstractController {

    private final PhoneTool phoneTool;

    @PostMapping(value = "sendMessage")
    public Mono<ResultMessage> sendMessage(@RequestBody PhoneInfo phoneInfo) {
        phoneTool.sendShortMessage(phoneInfo);
        return Mono.just(success("发送成功"));
    }


}
