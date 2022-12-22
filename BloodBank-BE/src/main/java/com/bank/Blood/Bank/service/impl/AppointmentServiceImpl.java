package com.bank.Blood.Bank.service.impl;

import com.bank.Blood.Bank.dto.RegisteredUserDTO;
import com.bank.Blood.Bank.model.Appointment;
import com.bank.Blood.Bank.model.Center;
import com.bank.Blood.Bank.model.Staff;
import com.bank.Blood.Bank.repository.AppointmentRepository;
import com.bank.Blood.Bank.repository.CenterRepository;
import com.bank.Blood.Bank.service.AppointmentService;
import com.bank.Blood.Bank.service.CenterService;
import com.bank.Blood.Bank.service.StaffService;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private CenterService centerService;
    private StaffService staffService;
    private final CenterRepository centerRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  CenterRepository centerRepository){this.appointmentRepository = appointmentRepository;
        this.centerRepository = centerRepository;
    }
    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment save(Appointment appointment, Integer id) {
        Staff staff = staffService.findOne(id);
        Center center = centerService.findOne(staff.getCenter().getId());
        validateWorkingHours(appointment, center);
        validateCenterAvailability(appointment, center, staff);
        return appointmentRepository.save(appointment);
    }

    public boolean validateWorkingHours(Appointment appointment, Center center) {
        LocalTime startTime = appointment.getTime();
        Duration duration = Duration.ofMinutes(appointment.getDuration());
        LocalTime endTime = startTime.plus(duration);
        LocalTime centerStartTime = center.getStartTime();
        LocalTime centerEndTime = center.getEndTime();

        //checks if appointment starts and ends in center working hours
        if(!(startTime.isAfter(centerStartTime) && endTime.isBefore(centerEndTime))) {
            return false;
        }
        return true;
    }

    public boolean validateCenterAvailability(Appointment appointment, Center center, Staff staff) {
        List<Appointment> existingAppointments = findAllByCenter(staff);
        LocalTime startTime = appointment.getTime();
        Duration appDuration = Duration.ofMinutes(appointment.getDuration());
        LocalTime endTime = startTime.plus(appDuration);

        for(Appointment existingAppointment : existingAppointments) {

            LocalTime existingStartTime = existingAppointment.getTime();
            Duration duration = Duration.ofMinutes(existingAppointment.getDuration());
            LocalTime existingEndTime = existingStartTime.plus(duration);

            //checks if new appointment falls within the time of the existing appointment
            if (startTime.isAfter(existingStartTime) && startTime.isBefore(existingEndTime)) {
                return false;
            }

            //checks if the end time of the new appointment falls within the time of the existing appointment.
            if (endTime.isAfter(existingStartTime) && endTime.isBefore(existingEndTime)) {
                return false;
            }

            //checks if the start time of the existing appointment falls within the time of the new appointment
            if (existingStartTime.isAfter(startTime) && existingStartTime.isBefore(endTime)) {
                return false;
            }

            //checks if the end time of the existing appointment falls within the time of the new appointment
            if (existingEndTime.isAfter(startTime) && existingEndTime.isBefore(endTime)) {
                return false;
            }
        }
        return true;
    }

    public List<Appointment> findAllByCenter(Staff staff) {

        List<Appointment> allAppointments = findAll();
        List<Appointment> centerAppointments = new ArrayList<Appointment>();
        for(Appointment ap : allAppointments) {
            if(ap.getCenter().getId().equals(staff.getCenter().getId())) {
                centerAppointments.add(ap);
            }
        }
        return centerAppointments;
    }

}
