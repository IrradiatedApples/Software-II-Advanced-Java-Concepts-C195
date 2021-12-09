/**
 *
 * @author Arthur J Amende
 * */
package Classes;

import Helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Appointment Calendar tracking all Appointments.
 * */
public class AppointmentCalendar {
    private ObservableList<Appointment> allAppointments;

    private static final ZoneId hqZoneID = ZoneId.of("America/New_York");

    /**
     * Constructor retrieving all appointments from the database
     * */
    public AppointmentCalendar(){
        allAppointments = JDBC.getAllAppointments();
    }

    /**
     * @param newAppointment Appointment added to Appointment Calendar
     * */
    public boolean addAppointment(Appointment newAppointment){
        return JDBC.addAppointment(newAppointment);
    }

    /**
     * @param appointmentToUpdate Appointment data to update existing Appointment in the database
     * */
    public boolean updateAppointment(Appointment appointmentToUpdate){
        return JDBC.updateAppointment(appointmentToUpdate);
    }

    /**
     * @param appointmentToDelete Appointment to be deleted from the database
     * @return true if deleted, false if not deleted
     * */
    public boolean deleteAppointment(Appointment appointmentToDelete){
        if (JDBC.deleteAppointment(appointmentToDelete.getId())){
            allAppointments = JDBC.getAllAppointments();
            return true;
        }
        return false;
    }

    /**
     * @return all Appointments in Appointment Calendar
     * */
    public ObservableList<Appointment> getAllAppointments(){
        return FXCollections.observableArrayList(allAppointments);
    }

    /**
     * Determines if the startTime and endTime of an appointment are inside business hours of
     * Monday through Friday, 8:00 a.m. to 10:00 p.m. Eastern Time.
     * @param startTime the Part to be copied
     * @param endTime the Part to be copied
     * @return true if in business hours , false if outside business hours
     * */
    public static boolean inBusinessHours(LocalDateTime startTime, LocalDateTime endTime){
        //ET
        ZonedDateTime[] meetingTimes = {startTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(hqZoneID),
                                        endTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(hqZoneID)};

        for (ZonedDateTime meetingTime : meetingTimes) {
            //Saturday
            if (meetingTime.getDayOfWeek().getValue() == 6) {
                return false;
            }
            //Sunday
            if (meetingTime.getDayOfWeek().getValue() == 7) {
                return false;
            }
            //Before 8 am
            if (meetingTime.getHour() < 8) {
                return false;
            }
            //After 10 pm
            if (meetingTime.getHour() > 22) {
                return false;
            }
            if (meetingTime.getHour() == 22 && meetingTime.getMinute() != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param dateTime the Part to be copied
     * @return a copy of dateTime in Eastern Time zone.
     * */
    public static ZonedDateTime toHQZoneID(LocalDateTime dateTime){
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(hqZoneID);
    }
}
