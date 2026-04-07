package com.hospital.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer patientId;

    private String fullName;
    private String emailId;
    private Long phoneNumber;
    private String gender;
    private Integer age;
    private String password;
}
