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
		
		PulsoPorFaixaEtaria faixa = PulsoPorFaixaEtaria.calculaPulsoPorFaixaEtaria(age);
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
	
	public enum PulsoPorFaixaEtaria {
		RN(0, 0, 90, 180, "Recém-nascido"),
		BEBE(0, 0, 90, 180, "Bebê"),
		CRIANCA_1_3(1, 3, 80, 170, "Criança de 1 a 3 anos"),
		CRIANCA_4_5(4, 5, 70, 160, "Criança de 4 a 5 anos"),
		CRIANCA_6_12(6, 12, 60, 140, "Criança de 6 a 12 anos"),
		ADOLESCENTE(13, 17, 50, 120, "Adolescente"),
		ADULTO(18, 64, 50, 120, "Adulto"),
		IDOSO(65, Integer.MAX_VALUE, 50, 120, "Idoso");
		
		private final int idadeMinima;
		private final int idadeMaxima;
		private final int pulsoMinimo;
		private final int pulsoMaximo;
		private final String descricao;
		
		private PulsoPorFaixaEtaria(
			int idadeMinima,
			int idadeMaxima,
			int pulsoMinimo,
			int pulsoMaximo,
			String descricao
		) {
			this.idadeMinima = idadeMinima;
			this.idadeMaxima = idadeMaxima;
			this.pulsoMinimo = pulsoMinimo;
			this.pulsoMaximo = pulsoMaximo;
			this.descricao = descricao;
		}
		
		public static PulsoPorFaixaEtaria calculaPulsoPorFaixaEtaria(Period age) {
			int anos = age.getYears();
			int meses = age.getMonths();
			int dias = age.getDays();
			
			if (anos == 0 && meses == 0 && dias <= 28) {
				return RN;
			}
			
			if (anos < 1) {
				return BEBE;
			}
			
			if (anos <= 3) {
				return CRIANCA_1_3;
			}
			
			if (anos <= 5) {
				return CRIANCA_4_5;
			}
			
			if (anos <= 12) {
				return CRIANCA_6_12;
			}
			
			if (anos <= 17) {
				return ADOLESCENTE;
			}
			
			if (anos <= 64) {
				return ADULTO;
			}
			
			return IDOSO;
		}
		
		public int getIdadeMinima() {
			return this.idadeMinima;
		}
		
		public int getIdadeMaxima() {
			return this.idadeMaxima;
		}
		
		public int getPulsoMinimo() {
			return this.pulsoMinimo;
		}
		
		public int getPulsoMaximo() {
			return this.pulsoMaximo;
		}
		
		public String getDescricao() {
			return this.descricao;
		}
	}
	
	public boolean isPulsoNormal() {
		return this.pulsoNormal;
	}
}
