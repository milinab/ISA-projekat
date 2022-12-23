package com.bank.Blood.Bank.repository;

import com.bank.Blood.Bank.model.Appointment;
import com.bank.Blood.Bank.service.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    public List<Appointment> findAll();

}

