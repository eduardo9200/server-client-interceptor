package com.example.iot.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class InterceptorController {
	private static final Logger logger = LoggerFactory.getLogger(InterceptorController.class);
	
	private final String SERVER_URL = "http://127.0.0.1:8080/data";

    @PostMapping("/data")
    public ResponseEntity<?> intercept(@RequestBody Map<String, Object> payload) {

        logger.info("INTERCEPTADOR CHAMADO");
        logger.info("DADO INTERCEPTADO: {}", payload);

        // Simular ataque (alterar payload)
        if (payload.containsKey("data")) {
            String data = payload.get("data").toString();

            if (data.length() > 4) {
                data = data.substring(0, data.length() - 4) + "AAAA";
                payload.put("data", data);
            }
        }

        logger.warn("DADO MODIFICADO: {}", payload);

        try {
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(
                    SERVER_URL,
                    payload,
                    String.class
            );

            logger.info("RESPOSTA DO SERVIDOR: {}", response.getBody());

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Erro ao encaminhar requisição");
        }
    }

    @GetMapping("/data")
    public String test() {
    	return "Interceptador ativo";
    }
}
