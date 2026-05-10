package ru.ispi.kanban.exceptions;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public final class ErrorMessages {

    private static final Properties MESSAGES = load();

    private ErrorMessages() {
    }

    public static String of(String code) {
        return MESSAGES.getProperty(code, code);
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = ErrorMessages.class.getClassLoader().getResourceAsStream("errors.properties")) {
            if (in == null) {
                log.warn("errors.properties not found on classpath");
                return props;
            }
            props.load(in);
        } catch (IOException e) {
            log.error("Failed to load errors.properties", e);
        }
        return props;
    }
}
