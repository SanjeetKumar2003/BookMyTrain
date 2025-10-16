import java.util.List;
import java.util.Scanner;

public class IRCTCAPP {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final BookingService bookingService = new BookingService();

    public static void main(String[] args) {
        // Ensure sample trains exist
        bookingService.addSampleTrains();

        User currentUser = null;
        while (true) {
            if (currentUser == null) {
                System.out.println("\n-----Welcome to IRCTC APP-----");
                System.out.println("1. Register:");
                System.out.println("2. Login:");
                System.out.println("3. Exit:");
                System.out.print("Enter Choice:");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        register();
                        break;
                    case "2":
                        currentUser = login();
                        break;
                    case "3":
                        System.out.println("Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } else {
                // user menu
                System.out.println("\n----- User Menu -----");
                System.out.println("1. Search Trains:");
                System.out.println("2. Book Ticket:");
                System.out.println("3. View My Tickets:");
                System.out.println("4. Cancel Tickets:");
                System.out.println("5. View All Trains:");
                System.out.println("6. Logout:");
                System.out.print("Enter Choice:");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        searchTrains();
                        break;
                    case "2":
                        bookTicket(currentUser);
                        break;
                    case "3":
                        viewMyTickets(currentUser);
                        break;
                    case "4":
                        cancelTicket(currentUser);
                        break;
                    case "5":
                        viewAllTrains();
                        break;
                    case "6":
                        currentUser = null;
                        System.out.println("Logged out.");
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        }
    }

    private static void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String fullname = scanner.nextLine().trim();
        System.out.print("Enter contact: ");
        String contact = scanner.nextLine().trim();

        boolean ok = userService.registerUser(username, password, fullname, contact);
        if (ok) {
            System.out.println("Registration Successful!");
        } else {
            System.out.println("Registration failed. Username might already exist.");
        }
    }

    private static User login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        User u = userService.loginUser(username, password);
        if (u == null) {
            System.out.println("No User Found with this username or invalid password");
            return null;
        } else {
            System.out.println("Welcome : " + u.getFullname() + "!");
            return u;
        }
    }

    private static void searchTrains() {
        System.out.print("Enter source station: ");
        String src = scanner.nextLine().trim();
        System.out.print("Enter destination station: ");
        String dest = scanner.nextLine().trim();
        List<Train> trains = bookingService.searchTrains(src, dest);
        if (trains.isEmpty()) {
            System.out.println("No Trains Found between " + src + " and " + dest);
        } else {
            System.out.println("Trains Found:");
            for (Train t : trains) {
                System.out.println(t);
            }
        }
    }

    private static void bookTicket(User user) {
        System.out.print("Enter train id to book: ");
        String tidStr = scanner.nextLine().trim();
        int trainId;
        try {
            trainId = Integer.parseInt(tidStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid train id.");
            return;
        }
        System.out.print("Enter number of seats: ");
        String seatsStr = scanner.nextLine().trim();
        int seats;
        try {
            seats = Integer.parseInt(seatsStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid seats number.");
            return;
        }
        int ticketId = bookingService.bookTicket(user.getUsername(), trainId, seats);
        if (ticketId > 0) {
            System.out.println("Booked successfully. Ticket ID: " + ticketId);
        } else {
            System.out.println("Booking failed.");
        }
    }

    private static void viewMyTickets(User user) {
        List<String> tickets = bookingService.viewTickets(user.getUsername());
        if (tickets.isEmpty()) {
            System.out.println("You have no tickets.");
        } else {
            for (String t : tickets) {
                System.out.println(t);
            }
        }
    }

    private static void cancelTicket(User user) {
        System.out.print("Enter Ticket ID to cancel: ");
        String tStr = scanner.nextLine().trim();
        int ticketId;
        try {
            ticketId = Integer.parseInt(tStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ticket id.");
            return;
        }
        boolean ok = bookingService.cancelTicket(user.getUsername(), ticketId);
        if (ok) {
            System.out.println("Ticket cancelled.");
        } else {
            System.out.println("Could not cancel ticket.");
        }
    }

    private static void viewAllTrains() {
        List<Train> trains = bookingService.viewAllTrains();
        if (trains.isEmpty()) {
            System.out.println("No trains found.");
        } else {
            for (Train t : trains) {
                System.out.println(t);
            }
        }
    }
}
