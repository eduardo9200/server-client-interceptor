package com.example.iot.server;

import java.time.LocalDate;

public class PatientData {
	private String patientId;
	private String doctorId;
	private Integer pulse;
	private LocalDate dateOfBirth;

	public String getPatientId() {
		return patientId;
	}
	
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getPulse() {
		return pulse;
	}
	
	public void setPulse(Integer pulse) {
		this.pulse = pulse;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
}
