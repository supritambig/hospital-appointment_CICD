package com.hospital.service.implementation;

import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import com.hospital.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public ModelAndView registerPatient(Patient patient) {
        patientRepository.save(patient);
        return new ModelAndView("redirect:/login").addObject("success", "Registration successful! Please login.");
    }

    @Override
    public Patient authenticate(String emailId, String password) {
        Optional<Patient> patient = patientRepository.findByEmailIdAndPassword(emailId, password);
        return patient.orElse(null);
    }
}
