package com.example.sendmessage.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2018/12/21 13:55
 * description: 状态码
 */
@AllArgsConstructor
@Getter
public enum SystemStatusCode {

    SUCCESS(0, "success", "成功"),
    FAILED(1, "failed", "失败"),

    SC_INTERNAL_SERVER_ERROR(500, "internalServerError", "系统错误");

    private Integer code;
    private String name;
    private String msg;
}
