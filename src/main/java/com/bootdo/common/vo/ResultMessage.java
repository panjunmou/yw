package com.bootdo.common.vo;

import org.activiti.engine.impl.util.json.JSONStringer;

public class ResultMessage {
    public static final int Success = 1;

    public static final int Fail = 0;
    public static final int Error = 2;
    public static final int Warnning = 3;
    private int result = 1;
    private String message = "";
    private Object data;

    public ResultMessage() {
    }

    public ResultMessage(int result, String message) {
        this.result = result;
        this.message = message;
    }

    public ResultMessage(int result, String message, Object data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        JSONStringer stringer = new JSONStringer();
        stringer.object();
        stringer.key("result");
        stringer.value((long) this.result);
        stringer.key("message");
        stringer.value(this.message);
        stringer.endObject();
        return stringer.toString();
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
