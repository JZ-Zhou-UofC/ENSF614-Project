package flightapp.business.domain;

import java.time.LocalDateTime;

public class Payment {

    private int id;
    private Reservation reservation;
    private double amount;
    private LocalDateTime paymentDate;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Reservation getReservation() { return reservation; }

    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public double getAmount() { return amount; }

    public void setAmount(double amount) { this.amount = amount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }

    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}
