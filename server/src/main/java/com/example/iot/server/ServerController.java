package com.example.iot.server;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.security.KeyPair;
import java.time.LocalDate;
import java.util.Base64;

@RestController
public class ServerController {
	private static final Logger logger = LoggerFactory.getLogger(ServerController.class);
	
    private static KeyPair keyPair;
    
    @Autowired
    private ServerService service;

    static {
        try {
            keyPair = CryptoUtil.generateRSAKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/data")
    public String receive(@RequestBody EncryptedRequest req) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(req.data);
            byte[] encryptedKey = Base64.getDecoder().decode(req.key);
            byte[] iv = Base64.getDecoder().decode(req.iv);

            byte[] aesKeyBytes = CryptoUtil.decryptRSA(encryptedKey, keyPair.getPrivate());
            SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

            String calculatedHmac = CryptoUtil.calculateHMAC(encryptedData, aesKey);
            if (!calculatedHmac.equals(req.hmac)) {
            	String msgError = "Atenção! Integridade de dados comprometida!";
            	logger.error(msgError);
                throw new Exception(msgError);
            }

            byte[] decrypted = CryptoUtil.decryptAES(encryptedData, aesKey, iv);
            String json = new String(decrypted);

            logger.info("Dados recebidos: {}", json);
            
            Gson gson = new GsonBuilder()
            	    .registerTypeAdapter(LocalDate.class,
            	        (JsonDeserializer<LocalDate>) (jsonElement, type, context) ->
            	            LocalDate.parse(jsonElement.getAsString()))
            	    .create();
            
            PatientData data = gson.fromJson(json, PatientData.class);
            String message = service
            	.processaBatimentosCardiacos(data.getPulse(), data.getDateOfBirth());
            
            return message + "\nDados processados com sucesso!";

        } catch (Exception e) {
            return "Erro: " + e.getMessage();
        }
    }

    @GetMapping("/public-key")
    public String getPublicKey() {
        return Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }
}