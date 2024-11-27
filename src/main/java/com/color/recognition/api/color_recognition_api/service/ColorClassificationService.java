package com.color.recognition.api.color_recognition_api.service;

import com.color.recognition.api.color_recognition_api.util.LogUtil;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.nio.file.*;

@Service
public class ColorClassificationService {

    public JSONObject classifyColor(MultipartFile image) throws Exception {
        File imageFile = null;
        Path tempScript = null;

        try {
            imageFile = File.createTempFile("uploaded_image", ".jpg");
            image.transferTo(imageFile);
            imageFile.deleteOnExit();

            tempScript = Files.createTempFile("color_classifier", ".py");
            tempScript.toFile().deleteOnExit();

            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("color_classifier.py")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("Script Python não encontrado no classpath");
                }
                Files.copy(inputStream, tempScript, StandardCopyOption.REPLACE_EXISTING);
            }

            Path modelPath = getModelPath();

            ProcessBuilder processBuilder = new ProcessBuilder("python", tempScript.toString(), imageFile.getAbsolutePath(), modelPath.toString());
            processBuilder.environment().put("PYTHONIOENCODING", "utf-8");

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            String output = new String(process.getInputStream().readAllBytes());
            output = output.replaceAll(".*tensorflow.*\\n", "").trim();

            String[] lines = output.split("\n");
            String jsonOutput = lines[lines.length - 1].trim();

            if (!jsonOutput.startsWith("{") || !jsonOutput.endsWith("}")) {
                throw new Exception("Resposta do script não é um JSON válido: " + jsonOutput);
            }
            return new JSONObject(jsonOutput);

        } catch (Exception e) {
            LogUtil.printDebugLog();
            throw e;
            
        } finally {
            if (imageFile != null && imageFile.exists()) {
                imageFile.delete();
            }
            if (tempScript != null && Files.exists(tempScript)) {
                try {
                    Files.delete(tempScript);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }

    private Path getModelPath() throws Exception {
        URL resource = getClass().getClassLoader().getResource("color_classifier_model_keras.keras");
        if (resource != null) {
            return Paths.get(resource.toURI());
        }

        String userDir = System.getProperty("user.dir");
        Path modelPath = Paths.get(userDir, "color_classifier_model_keras.keras");
        if (Files.exists(modelPath)) {
            return modelPath;
        } else {
            throw new FileNotFoundException("Modelo não encontrado no caminho especificado.");
        }
    }
}