package com.color.recognition.api.color_recognition_api.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class LogUtil {

    public static void printDebugLog() {
        Path logPath = Paths.get("debug.log");

        if (Files.exists(logPath)) {
            try (Stream<String> lines = Files.lines(logPath)) {
                System.out.println("==== Conteúdo do arquivo debug.log ====");
                lines.forEach(System.out::println);
                System.out.println("==== Fim do arquivo debug.log ====");
            } catch (Exception e) {
                System.err.println("Erro ao ler o arquivo debug.log: " + e.getMessage());
            }
        } else {
            System.err.println("O arquivo debug.log não foi encontrado.");
        }
    }
}
