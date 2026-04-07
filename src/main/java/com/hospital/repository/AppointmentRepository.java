package com.hospital.repository;

import com.hospital.entity.Appointment;
import com.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatient(Patient patient);
    List<Appointment> findByDoctor_DoctorId(Integer doctorId);
}
