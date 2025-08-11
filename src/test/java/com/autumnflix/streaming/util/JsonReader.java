package com.autumnflix.streaming.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class JsonReader {
    public static String readJsonAsString(String filepath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String readObjectAsJson(T object) {
        ObjectWriter ow = new ObjectMapper().registerModule(new JSR310Module()).writer().withDefaultPrettyPrinter();

        try {
            return ow.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
