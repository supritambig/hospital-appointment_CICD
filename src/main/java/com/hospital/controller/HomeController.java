package com.hospital.controller;

import com.hospital.entity.Appointment;
import com.hospital.entity.Patient;
import com.hospital.service.AppointmentService;
import com.hospital.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    // ── Home ──────────────────────────────────────────────
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // ── Register ──────────────────────────────────────────
    @GetMapping("/register")
    public ModelAndView showRegister(Model model) {
        model.addAttribute("patient", new Patient());
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView register(@ModelAttribute Patient patient) {
        return patientService.registerPatient(patient);
    }

    // ── Login ─────────────────────────────────────────────
    @GetMapping("/login")
    public ModelAndView showLogin() {
        return new ModelAndView("login");
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String emailId,
                              @RequestParam String password,
                              HttpSession session) {
        // Admin shortcut
        if (emailId.equals("admin") && password.equals("admin")) {
            session.setAttribute("role", "ADMIN");
            return new ModelAndView("redirect:/admin/dashboard");
        }
        Patient patient = patientService.authenticate(emailId, password);
        if (patient != null) {
            session.setAttribute("patient", patient);
            session.setAttribute("role", "PATIENT");
            return new ModelAndView("redirect:/patient/dashboard");
        }
        return new ModelAndView("login").addObject("error", "Invalid credentials. Please try again.");
    }

    // ── Logout ────────────────────────────────────────────
    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.invalidate();
        return new ModelAndView("redirect:/login");
    }

    // ── Patient Dashboard ─────────────────────────────────
    @GetMapping("/patient/dashboard")
    public String patientDashboard(HttpSession session, Model model) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) return "redirect:/login";
        model.addAttribute("patient", patient);
        model.addAttribute("appointments", appointmentService.getAppointmentsByPatient(patient));
        return "patient_dashboard";
    }

    // ── Book Appointment ──────────────────────────────────
    @GetMapping("/patient/book")
    public String showBooking(HttpSession session, Model model) {
        if (session.getAttribute("patient") == null) return "redirect:/login";
        appointmentService.loadBookingPage(model);
        return "book_appointment";
    }

    @PostMapping("/patient/book")
    public ModelAndView bookAppointment(@ModelAttribute Appointment appointment,
                                        @RequestParam Integer doctorId,
                                        HttpSession session) {
        Patient patient = (Patient) session.getAttribute("patient");
        if (patient == null) return new ModelAndView("redirect:/login");

        appointment.setPatient(patient);
        // Doctor is set via hidden field binding; ensure it's resolved
        appointmentService.bookAppointment(appointment);
        return new ModelAndView("redirect:/patient/dashboard").addObject("success", "Appointment booked!");
    }

    // ── Cancel Appointment (patient) ──────────────────────
    @PostMapping("/patient/cancel/{id}")
    public ModelAndView cancelAppointment(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("patient") == null) return new ModelAndView("redirect:/login");
        appointmentService.cancelAppointment(id);
        return new ModelAndView("redirect:/patient/dashboard");
    }
}
