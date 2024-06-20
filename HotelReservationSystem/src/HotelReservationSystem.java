import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;


public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/login_schema";

    private static final String username = "root";

    private static final String password = "jain2001";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println( e.getMessage() );
        }

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/login_schema", "root", "jain2001");
            while (true) {
                System.out.println();
                System.out.println("*+Hotel Management System+*");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an option");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        reserveRoom(con, scanner);
                        break;
                    case 2:
                        viewReservations(con, scanner);
                        break;
                    case 3:
                        getRoomNumber(con, scanner);
                        break;
                    case 4:
                        updateReservation(con, scanner);
                        break;
                    case 5:
                        deleteReservation(con, scanner);
                        break;
                    case 6:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid Choice. Try Again");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

   
    private static void reserveRoom(Connection con, Scanner scanner) {
        try {
            System.out.println("Enter Guest Name:");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.println("Enter Room Number:");
            int roomNumber = scanner.nextInt();
            System.out.println("Enter Contact Number:");
            String contactNumber = scanner.next();

            String sql ="INSERT INTO reservations (guest_name, room_number, contact_number) " +
                    "Values ('" + guestName +"', " + roomNumber +", '" + contactNumber + "')";

            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0 ) {
                    System.out.println("Reservation Succesful!");
                } else {
                    System.out.println("Reservation Failed");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static  void viewReservations(Connection con, Scanner scanner) throws  SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try (Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {

        System.out.println("Current Reservations");
        System.out.println("+----------------+----------------+----------------+----------------+------------------+");
        System.out.println("| Reservation ID | Guest          | Room Number    | Contact Number | Reservation Date|");
        System.out.println("+----------------+----------------+----------------+----------------+------------------+");

        while(resultSet.next()) {
            int reservationId = resultSet.getInt("reservation_id");
            String guestName = resultSet.getString("guest_name");
            int roomNumber = resultSet.getInt("room_number");
            String contactNumber = resultSet.getString("contact_number");
            String reservationDate = resultSet.getTimestamp("reservation_date").toString();


//            Format and Displayy
            System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s  |\n",
                    reservationId, guestName, roomNumber, contactNumber, reservationDate);
        }

        System.out.println("+----------------+----------------+----------------+----------------+----------------");
       }
    }


    private static void getRoomNumber(Connection con, Scanner scanner) {
        try {
            System.out.println("Enter reservation ID:");
            int reservationId = scanner.nextInt();
            System.out.println("Enter Guest name:");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations" +
                    "WHERE reservation_id = " + reservationId + "'" +
                    "AND guest_name = '" + guestName + "'";

            try (Statement statement =  con.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID" + reservationId +
                            " and Guest" + guestName + " is: " + roomNumber);
                }else {
                    System.out.println("Reservation not found for the given ID and Guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void updateReservation(Connection con,Scanner scanner) {
        try {
            System.out.println("Enter reservation ID to update:");
            int reservationID = scanner.nextInt();
            scanner.nextLine();

            if (!reservationExists(con, reservationID)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationID;

            try (Statement statement = con.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void deleteReservation(Connection con,Scanner scanner) {

        try {
            System.out.println("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(con, reservationId)) {
                System.out.println("Reservation not found or the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = con.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static boolean reservationExists(Connection con, int reservationId) {
            try {
                String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

                try (Statement statement = con.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)) {

                   return resultSet.next();
                }
            } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
        }


        public static void exit() throws InterruptedException {
        System.out.println("Existing System");
        int i = 5;
        while (i!=0) {
            System.out.println(".");
            Thread.sleep(450);
            i--;
            }
        System.out.println();
        System.out.println("Thankyou for using Hotel Reservation System!!!");
    }

}





