package com.color.recognition.api.color_recognition_api.controller;

import com.color.recognition.api.color_recognition_api.service.ColorClassificationService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/color-classification")
public class ColorClassificationController {

    private final ColorClassificationService colorClassificationService;

    public ColorClassificationController(ColorClassificationService colorClassificationService) {
        this.colorClassificationService = colorClassificationService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/predict")
    public ResponseEntity<?> classifyColor(@RequestParam("image") MultipartFile image) {
        try {
            JSONObject jsonResponse = colorClassificationService.classifyColor(image);
            return ResponseEntity.ok(jsonResponse.toString());

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erro ao processar a imagem\", \"details\": \"" + errorMessage.replace("\"", "'") + "\"}");
        }
    }
}
