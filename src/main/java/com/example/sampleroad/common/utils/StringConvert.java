package com.example.sampleroad.common.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StringConvert {
    public static String[] StringToStringArray(String s){
        if(s==""){
            return new String[]{""};
        }
        return s.split(",");
    }

    public static String StringArrayToString(String[] sArray){
        if (sArray == null || sArray.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(sArray[0]);
        for (int i = 1; i < sArray.length; i++) {
            sb.append(",");
            sb.append(sArray[i]);
        }
        return sb.toString();
    }

    public static JSONObject StringToJson(String s) throws ParseException {
        JSONParser jsonParser = new JSONParser();

        //3. To Object
        Object obj = jsonParser.parse(s);

        //4. To JsonObject
        JSONObject jsonObj = (JSONObject) obj;
        return jsonObj;
    }
}
