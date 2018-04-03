package models;

import application.MainApp;
import helpers.DBHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a data access object (DAO) for Bookings.
 *
 * Following the DAO design principle, this class acts as a layer between
 * the Model (the Booking) and the business logic of this application.
 * It provides functions to search and return one or multiple Bookings from the database
 * as well as to add or delete a booking from the database.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class BookingDAO {

	/**
	 * This method searches for an Booking in the database based on the provided bookingID
	 *
	 * @param bookingID the booking's unique ID
	 * @return the Booking object that was retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static Booking searchBooking(int bookingID) throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM bookings WHERE bookingID=" + bookingID;

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsBooking = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getBookingFromResultSet method and
			//get Booking Object
			Booking booking = getBookingFromResultSet(rsBooking);
			//Return Booking object
			return booking;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("While searching a booking with " + bookingID
				+ " id, an error occurred: " + e.getMessage());
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates a new booking object from the provided result set
	 *
	 * @param rs the resultset containing information for the new booking
	 * @return a Booking object
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static Booking getBookingFromResultSet(ResultSet rs) throws SQLException {
		//Declare variable booking
		Booking booking = null;
		if (rs.next()) {
			booking = new Booking();
			booking.setBookingID(rs.getInt("bookingID"));
			booking.setUsername(rs.getString("username"));
			booking.setScreenID(rs.getInt("screenID"));
			try {
				booking.setScreening(ScreeningDAO.searchScreening("" + booking.getScreenID()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			booking.setSeats(rs.getString("seats"));
		}
		return booking;
	}

	/**
	 * This method searches for a list of Bookings in the database
	 *
	 * @return a list of all matching Booking objects that were retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static ObservableList<Booking> searchBookings() throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM bookings";

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsBookings = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getEmployeeList method and get booking object
			ObservableList<Booking> bookingList = getBookingList(rsBookings);
			//Return booking object
			return bookingList;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("SQL select operation has failed: " + e.getMessage());
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates an observable list of new booking objects from the provided result set
	 *
	 * @param rs the resultset containing information for the new bookings
	 * @return an ObservableList of Booking objects
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static ObservableList<Booking> getBookingList(ResultSet rs) throws SQLException {
		//Declare an observable List comprising of Booking objects
		ObservableList<Booking> bookingList = FXCollections.observableArrayList();

		while (rs.next()) {
			//Create new Booking object and fill it with information from the database
			Booking booking = new Booking();
			booking.setBookingID(rs.getInt("bookingID"));
			booking.setUsername(rs.getString("username"));
			booking.setScreenID(rs.getInt("screenID"));
			try {
				booking.setScreening(ScreeningDAO.searchScreening("" + booking.getScreenID()));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			booking.setSeats(rs.getString("seats"));
			//Add booking to the ObservableList
			bookingList.add(booking);
		}
		return bookingList;
	}

	/**
	 * This method searches for a list of Bookings in the database based on the provided username
	 *
	 * @param username the booking's associated username
	 * @return a list of all matching Booking objects that were retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static ObservableList<Booking> searchBookings(String username) throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM bookings WHERE username = '" + username + "'";
		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsBookings = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getEmployeeList method and get booking object
			ObservableList<Booking> bookingList = getBookingList(rsBookings);
			//Return booking object
			return bookingList;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("SQL select by username operation has failed: " + e.getMessage());
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * This method searches for a list of Bookings in the database based on its screenID
	 *
	 * @param screenID the screening's unique ID
	 * @return a list of all matching Booking objects that were retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static ObservableList<Booking> searchBookings(int screenID) throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM bookings WHERE screenID = " + screenID;
		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsBookings = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getEmployeeList method and get booking object
			ObservableList<Booking> bookingList = getBookingList(rsBookings);
			//Return booking object
			return bookingList;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("SQL select by screenID operation has failed: " + e.getMessage());
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Adds a new row to the Bookings table in the database based on a provided booking Object
	 *
	 * @param booking the booking object to be saved in the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	public static void addBooking(Booking booking) throws SQLException {
		// Creates a new SQL statement with information from the provided booking object
		String addStatement =
			"INSERT INTO bookings "
				+ "(username, screenID, seats) "
			+ "VALUES ("
				+ "'" + booking.getUsername() + "'"
				+ ", " + booking.getScreenID()
				+ ", '" + booking.getSeats() + "'"
				+ ");";
		// Executes the statement
		DBHelper.dbExecuteUpdate(addStatement);

	}

	/**
	 * Deletes the provided booking from the database
	 * @param booking the booking to be deleted from the database
	 * @throws SQLException thrown if an error occurs during the database operation
	 */
	public static void deleteBooking(Booking booking) throws SQLException {
		String deleteStatement =
			"DELETE FROM bookings WHERE "
				+ "bookingID = " + booking.getBookingID() + ";";
		// Executes the statement
		DBHelper.dbExecuteUpdate(deleteStatement);

	}
}
