package com.taller.trivia.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class ErrorMessageLoader {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("messages");

    public static String getMessage(String code, Object... args) {
            try {
                String message = resourceBundle.getString(code);

                return MessageFormat.format(message, args);
            } catch (Exception e) {
                return "Mensaje no encontrado";
            }
    }
}
