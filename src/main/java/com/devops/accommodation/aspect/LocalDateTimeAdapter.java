package com.devops.accommodation.aspect;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ftn.devops.utils.DateUtils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {
    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(DateUtils.TimestampToString(localDateTime));
    }
}
