import java.sql.Timestamp;

public class Ticket {
    private int ticketId;
    private String username;
    private int trainId;
    private int seatsBooked;
    private Timestamp bookedOn;

    public Ticket() {}

    public Ticket(int ticketId, String username, int trainId, int seatsBooked, Timestamp bookedOn) {
        this.ticketId = ticketId;
        this.username = username;
        this.trainId = trainId;
        this.seatsBooked = seatsBooked;
        this.bookedOn = bookedOn;
    }

    public int getTicketId() { return ticketId; }
    public String getUsername() { return username; }
    public int getTrainId() { return trainId; }
    public int getSeatsBooked() { return seatsBooked; }
    public Timestamp getBookedOn() { return bookedOn; }

    @Override
    public String toString() {
        return "Ticket #" + ticketId + " | User: " + username + " | Train: " + trainId + " | Seats: " + seatsBooked + " | Booked: " + bookedOn;
    }
}
