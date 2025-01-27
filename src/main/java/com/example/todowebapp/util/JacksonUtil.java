package com.example.todowebapp.util;

import com.example.todowebapp.exceptions.ApiException;
import com.example.todowebapp.exceptions.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonUtil {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    private JacksonUtil() {
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        } else {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (IOException e) {
                throw new ApiException(ErrorCode.CANNOT_DESERIALIZE_JSON);
            }
        }
    }

    public static String serialize(Object object) {
        if (object == null) {
            return null;
        } else {
            try {
                return objectMapper.writeValueAsString(object);
            } catch (IOException e) {
                throw new ApiException(ErrorCode.CANNOT_SERIALIZE_JSON);
            }
        }
    }
}
