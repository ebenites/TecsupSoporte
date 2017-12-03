package pe.edu.tecsup.appsoporte.models;

import java.util.List;

public class SupportPortal {

    private PhoneNumber phoneNumber;

    private List<Seat> seats;

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "SupportPortal{" +
                "phoneNumber=" + phoneNumber +
                ", seats=" + seats +
                '}';
    }
}
