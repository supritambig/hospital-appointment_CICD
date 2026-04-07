package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer doctorId;

    private String fullName;
    private String specialization;
    private String qualification;
    private Long phoneNumber;
    private String emailId;
    private String availableDays;   // e.g. "Mon, Wed, Fri"
    private String availableTime;   // e.g. "10:00 AM - 1:00 PM"
}
