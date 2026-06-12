package com.example.iot.server;

import java.time.LocalDate;
import java.time.Period;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServerService {
	private static final Logger logger = LoggerFactory.getLogger(ServerService.class);
	
	private boolean pulsoNormal = true;
	
	public String processaBatimentosCardiacos(Integer pulse, LocalDate dateOfBirth) {
		LocalDate today = LocalDate.now();
		Period age = Period.between(dateOfBirth, today);
		
		PulseRateByAgeGroup faixa = PulseRateByAgeGroup.calculaPulsoPorFaixaEtaria(age);
		int minPulse = faixa.getPulsoMinimo();
		int maxPulse = faixa.getPulsoMaximo();
		String faixaIdade = faixa.getDescricao();
		
		if (pulse < minPulse) {
			this.pulsoNormal = false;
			String message = "ALERTA: Bradicardia detectada em " + faixaIdade + "!";
            logger.warn(message);
            return message;
		}
		
		if (pulse > maxPulse) {
			this.pulsoNormal = false;
			String message = "ALERTA: Taquicardia detectada em " + faixaIdade + "!";
            logger.warn(message);
            return message;
		}
		
		return "OK: Pulso normal em " + faixaIdade + ".";
	}
	
	public boolean isPulsoNormal() {
		return this.pulsoNormal;
	}
}
