package com.example.sendmessage.controller.base;


import com.example.sendmessage.custom.SystemStatusCode;
import com.example.sendmessage.entity.ResultMessage;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang tong
 * date: 2018/8/9 14:52
 * description:
 */
public abstract class AbstractController {

    private SystemStatusCode success = SystemStatusCode.SUCCESS;
    private SystemStatusCode failed = SystemStatusCode.FAILED;

    /**
     * 格式输出结果
     *
     * @return
     */
    protected ResultMessage success() {
        return new ResultMessage().ok(success.getCode(), success.getName());
    }

    /**
     * 格式输出结果
     *
     * @param msg 描述
     * @return
     */
    protected ResultMessage success(String msg) {
        return new ResultMessage().ok(success.getCode(), msg);
    }


    /**
     * 格式输出结果
     *
     * @return
     */
    protected ResultMessage failure() {
        return new ResultMessage().error(failed.getCode(), failed.getName());
    }


}
