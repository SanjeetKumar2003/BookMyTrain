public class Train {
    private int trainId;
    private String trainName;
    private String source;
    private String destination;
    private double fare;
    private int seats;

    public Train() {}

    public Train(int trainId, String trainName, String source, String destination, double fare, int seats) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.fare = fare;
        this.seats = seats;
    }

    public int getTrainId() { return trainId; }
    public String getTrainName() { return trainName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public double getFare() { return fare; }
    public int getSeats() { return seats; }

    public void setTrainId(int trainId) { this.trainId = trainId; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public void setSource(String source) { this.source = source; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setFare(double fare) { this.fare = fare; }
    public void setSeats(int seats) { this.seats = seats; }

    @Override
    public String toString() {
        return "Train No: " + trainId + " | " + trainName + " | " + source + " -> " + destination
                + " | Fare: " + fare + " | Seats: " + seats;
    }
}
