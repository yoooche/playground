package com.eight.demo.module.utils;


import com.eight.demo.module.common.error.BaseException;
import com.eight.demo.module.constant.StatusCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SuppressWarnings("deprecation")
public class JsonUtils {

    private static final ObjectMapper OM;


    static {
        OM = new ObjectMapper();
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OM.registerModule(new JavaTimeModule());
    }

    public static String toJson(Object object) {
        try {
            return OM.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new BaseException(StatusCode.UNKNOW_ERR, "Json parse failed: " + e.getMessage());
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OM.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new BaseException(StatusCode.UNKNOW_ERR, "Json parse failed " + e.getMessage());
        }
    }

}
