package com.hospital.controller;

import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.AppointmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    // ── Dashboard ─────────────────────────────────────────
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";
        model.addAttribute("totalDoctors", doctorRepository.count());
        model.addAttribute("totalPatients", patientRepository.count());
        model.addAttribute("totalAppointments", appointmentService.getAllAppointments().size());
        return "admin_dashboard";
    }

    // ── All Appointments ──────────────────────────────────
    @GetMapping("/appointments")
    public String allAppointments(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        return "admin_appointments";
    }

    // ── Update Appointment Status ─────────────────────────
    @PostMapping("/appointments/status/{id}")
    public ModelAndView updateStatus(@PathVariable Long id,
                                     @RequestParam String status,
                                     HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return new ModelAndView("redirect:/login");
        appointmentService.updateStatus(id, status);
        return new ModelAndView("redirect:/admin/appointments");
    }

    // ── Manage Doctors ────────────────────────────────────
    @GetMapping("/doctors")
    public String doctors(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("doctor", new Doctor());
        return "admin_doctors";
    }

    @PostMapping("/doctors/add")
    public ModelAndView addDoctor(@ModelAttribute Doctor doctor, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return new ModelAndView("redirect:/login");
        doctorRepository.save(doctor);
        return new ModelAndView("redirect:/admin/doctors");
    }

    @PostMapping("/doctors/delete/{id}")
    public ModelAndView deleteDoctor(@PathVariable Integer id, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return new ModelAndView("redirect:/login");
        doctorRepository.deleteById(id);
        return new ModelAndView("redirect:/admin/doctors");
    }

    // ── Manage Patients ───────────────────────────────────
    @GetMapping("/patients")
    public String patients(HttpSession session, Model model) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/login";
        model.addAttribute("patients", patientRepository.findAll());
        return "admin_patients";
    }

    @PostMapping("/patients/delete/{id}")
    public ModelAndView deletePatient(@PathVariable Integer id, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return new ModelAndView("redirect:/login");
        patientRepository.deleteById(id);
        return new ModelAndView("redirect:/admin/patients");
    }
}
