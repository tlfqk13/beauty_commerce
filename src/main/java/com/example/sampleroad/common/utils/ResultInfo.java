package com.example.sampleroad.common.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ResultInfo {
    private int code;
    private String message;
    private Object result;

    public enum Code {
        SUCCESS(200),
        CREATED(202);

        int value;

        Code(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public ResultInfo(Code code, String message, Object result) {
        this.code = code.getValue();
        this.message = message;
        this.result = result;
    }

    public ResultInfo(Code code, String message) {
        this.code = code.getValue();
        this.message = message;
    }

    public static HashMap<String, Object> makeResultMap(Object object){
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", object);
        return resultMap;
    }
}