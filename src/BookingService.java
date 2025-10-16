import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    // Add sample trains if they don't already exist
    public void addSampleTrains() {
        String sql = "INSERT IGNORE INTO trains (train_id, train_name, source, destination, fare, seats) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // train 101
            ps.setInt(1, 101);
            ps.setString(2, "Patna Express");
            ps.setString(3, "patna");
            ps.setString(4, "delhi");
            ps.setDouble(5, 1200);
            ps.setInt(6, 100);
            ps.executeUpdate();

            // train 102
            ps.setInt(1, 102);
            ps.setString(2, "Delhi Rajdhani");
            ps.setString(3, "delhi");
            ps.setString(4, "mumbai");
            ps.setDouble(5, 2200);
            ps.setInt(6, 80);
            ps.executeUpdate();

            // train 103
            ps.setInt(1, 103);
            ps.setString(2, "Howrah Mail");
            ps.setString(3, "howrah");
            ps.setString(4, "patna");
            ps.setDouble(5, 900);
            ps.setInt(6, 150);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding sample trains: " + e.getMessage());
        }
    }

    // Search trains by source & destination
    public List<Train> searchTrains(String source, String destination) {
        List<Train> list = new ArrayList<>();
        String sql = "SELECT * FROM trains WHERE LOWER(source)=? AND LOWER(destination)=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, source.toLowerCase());
            ps.setString(2, destination.toLowerCase());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Train t = new Train(
                        rs.getInt("train_id"),
                        rs.getString("train_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDouble("fare"),
                        rs.getInt("seats")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Error searching trains: " + e.getMessage());
        }
        return list;
    }

    // Book ticket (transactional)
    // returns ticket id (>=1) on success, -1 on failure
    public int bookTicket(String username, int trainId, int seatsToBook) {
        String selectSql = "SELECT seats FROM trains WHERE train_id = ? FOR UPDATE";
        String updateSql = "UPDATE trains SET seats = seats - ? WHERE train_id = ?";
        String insertTicket = "INSERT INTO tickets (username, train_id, seats_booked) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, trainId);
                ResultSet rs = psSelect.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    System.out.println("Train not found.");
                    return -1;
                }
                int available = rs.getInt("seats");
                if (available < seatsToBook) {
                    conn.rollback();
                    System.out.println("Not enough seats. Available: " + available);
                    return -1;
                }
                try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                    psUpdate.setInt(1, seatsToBook);
                    psUpdate.setInt(2, trainId);
                    psUpdate.executeUpdate();
                }
                int generatedId = -1;
                try (PreparedStatement psInsert = conn.prepareStatement(insertTicket, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, username);
                    psInsert.setInt(2, trainId);
                    psInsert.setInt(3, seatsToBook);
                    psInsert.executeUpdate();
                    ResultSet keys = psInsert.getGeneratedKeys();
                    if (keys.next()) {
                        generatedId = keys.getInt(1);
                    }
                }
                conn.commit();
                System.out.println("Booking successful. Ticket ID: " + generatedId);
                return generatedId;
            } catch (SQLException ex) {
                conn.rollback();
                System.out.println("Booking failed: " + ex.getMessage());
                return -1;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Booking error: " + e.getMessage());
            return -1;
        }
    }

    // View tickets for a user
    public List<String> viewTickets(String username) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT t.ticket_id, t.train_id, t.seats_booked, t.booked_on, tr.train_name, tr.source, tr.destination " +
                     "FROM tickets t JOIN trains tr ON t.train_id = tr.train_id " +
                     "WHERE t.username = ? ORDER BY t.booked_on DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String row = "Ticket #" + rs.getInt("ticket_id") +
                        " | Train: " + rs.getInt("train_id") + " (" + rs.getString("train_name") + ")" +
                        " | " + rs.getString("source") + "->" + rs.getString("destination") +
                        " | Seats: " + rs.getInt("seats_booked") +
                        " | Booked On: " + rs.getTimestamp("booked_on");
                results.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching tickets: " + e.getMessage());
        }
        return results;
    }

    // Cancel a ticket (ensure the user owns it), return true if cancelled
    public boolean cancelTicket(String username, int ticketId) {
        String selectSql = "SELECT train_id, seats_booked, username FROM tickets WHERE ticket_id = ?";
        String deleteSql = "DELETE FROM tickets WHERE ticket_id = ?";
        String updateTrainSql = "UPDATE trains SET seats = seats + ? WHERE train_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, ticketId);
                ResultSet rs = psSelect.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    System.out.println("Ticket not found.");
                    return false;
                }
                String owner = rs.getString("username");
                if (!owner.equals(username)) {
                    conn.rollback();
                    System.out.println("You can only cancel your own tickets.");
                    return false;
                }
                int trainId = rs.getInt("train_id");
                int seats = rs.getInt("seats_booked");

                try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
                    psDelete.setInt(1, ticketId);
                    psDelete.executeUpdate();
                }
                try (PreparedStatement psUpdate = conn.prepareStatement(updateTrainSql)) {
                    psUpdate.setInt(1, seats);
                    psUpdate.setInt(2, trainId);
                    psUpdate.executeUpdate();
                }
                conn.commit();
                System.out.println("Ticket cancelled and seats released.");
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.out.println("Cancellation failed: " + ex.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Cancellation error: " + e.getMessage());
            return false;
        }
    }

    // View all trains
    public List<Train> viewAllTrains() {
        List<Train> list = new ArrayList<>();
        String sql = "SELECT * FROM trains";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Train t = new Train(
                        rs.getInt("train_id"),
                        rs.getString("train_name"),
                        rs.getString("source"),
                        rs.getString("destination"),
                        rs.getDouble("fare"),
                        rs.getInt("seats")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching trains: " + e.getMessage());
        }
        return list;
    }
}
