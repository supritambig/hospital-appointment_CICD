package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    private String appointmentDate;   // stored as String "YYYY-MM-DD"
    private String appointmentTime;
    private String reason;
    private String status;            // PENDING, CONFIRMED, CANCELLED

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
}
