package com.hospital.service;

import com.hospital.entity.Patient;
import org.springframework.web.servlet.ModelAndView;

public interface PatientService {
    ModelAndView registerPatient(Patient patient);
    Patient authenticate(String emailId, String password);
}
