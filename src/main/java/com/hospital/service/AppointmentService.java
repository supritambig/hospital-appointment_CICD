package com.hospital.service;

import com.hospital.entity.Appointment;
import com.hospital.entity.Patient;
import org.springframework.ui.Model;

import java.util.List;

public interface AppointmentService {
    void bookAppointment(Appointment appointment);
    List<Appointment> getAppointmentsByPatient(Patient patient);
    List<Appointment> getAllAppointments();
    void updateStatus(Long id, String status);
    void cancelAppointment(Long id);
    void loadBookingPage(Model model);
}
