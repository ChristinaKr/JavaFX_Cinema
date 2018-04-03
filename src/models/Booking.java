package models;

import helpers.Helpers;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * This class represents the movie bookings in the application
 * and is instantiated with rows from the Bookings table in the database.
 *
 * Each instance of this class represents a booking that a user has made.
 * Each booking has a bookingID (unique identifier of the booking), a screenID
 * (unique identifier of its associated screening), the username of the user
 * who made the booking, and a List of seats that belong to the booking.
 *
 * Booking seats are represented as a comma separated String of seat identifiers
 * such as A10 or B2 in the database. They are parsed into an ArrayList to allow for
 * easier handling of Seats throughout the application.
 *
 * This application uses the Data Access Object (DAO) model for structuring its model.
 * This improves the readability and maintainability of the code by providing uniform
 * ways to interact with all models, as well as with the database and by clearly separating
 * the business logic of the application from its data objects.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class Booking {

	// Booking Properties
	private IntegerProperty bookingID, screenID;
	private StringProperty username;
	private ArrayList<Seat> seatList;
	private Screening screening;

	/**
	 * Constructor that initializes all fields
	 */
	public Booking() {
		bookingID = new SimpleIntegerProperty();
		username = new SimpleStringProperty();
		screenID = new SimpleIntegerProperty();
	}

	//----------------------------------------//
	// Setters and getters for all properties //
	//----------------------------------------//

	// BookingID
	/**
	 *
	 * @return  this booking's associated bookingID
	 */
	public int getBookingID() {
		return bookingID.get();
	}

	/**
	 *
	 * @return  this booking's associated bookingID property
	 */
	public IntegerProperty bookingIDProperty() {
		return bookingID;
	}

	/**
	 *
	 * @param bookingID this booking's associated bookingID
	 */
	public void setBookingID(int bookingID) {
		this.bookingID.set(bookingID);
	}

	// Username
	/**
	 *
	 * @return this booking's associated username
	 */
	public String getUsername() {
		return username.get();
	}

	/**
	 *
	 * @return this booking's associated username property
	 */
	public StringProperty usernameProperty() {
		return username;
	}

	/**
	 * @param username this booking's associated username
	 */
	public void setUsername(String username) {
		this.username.set(username);
	}

	// ScreenID

	/**
	 * @return  this booking's screenID
	 */
	public int getScreenID() {
		return screenID.get();
	}

	/**
	 * @return  this booking's screenID property
	 */
	public IntegerProperty screenIDProperty() {
		return screenID;
	}

	/**
	 * @param screenID this booking's screenID
	 */
	public void setScreenID(int screenID) {
		this.screenID.set(screenID);
		try {
			this.screening = ScreeningDAO.searchScreening("" + screenID);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Screening
	/**
	 * @return this booking's screening object
	 */
	public Screening getScreening() {
		return screening;
	}

	/**
	 * @param screening this booking's screening object
	 */
	public void setScreening(Screening screening) {
		this.screening = screening;
	}

	// Seats

	/**
	 * Iterates over the List of seats and places them in a comma-separated String
	 *
	 * @return a comma separated String of seat identifiers (eg. A8,D10)
	 */
	public String getSeats() {
		String seatString = "";
		for (Seat seat : seatList) {
			seatString += "" + seat.getRow() + seat.getNumber() + ",";
		}
		return seatString;
	}

	/**
	 * Takes a comma separates list of seat identifiers and parses them into a list of Seat objects
	 * Instead of creating new instances of Seat, the seat objects are retrieved from the
	 * screening object that belongs to this booking. This way, the booking references the
	 * same instances of the seat objects as the screening does.
	 *
	 * @param seatString a comma separated String of seat identifiers (eg. A8,D10)
	 */
	public void setSeats(String seatString) {
		// Splits the seatString into an array of Strings
		String[] stringArray = seatString.split(",");
		// Creates a new ArrayList
		seatList = new ArrayList<>(stringArray.length);
		for (int i = 0; i < stringArray.length; i++) {
			// Retrieves the row of the seat
			char row = stringArray[i].charAt(0);
			// Retrieves the seat number of the seat
			int number = Integer.parseInt(stringArray[i].substring(1));
			// Creates a temporary booked seat object with the row and number
			Seat tempSeat = new Seat(row, number, true);
			// Searches a seat in the related booking that matches the just created temporary seat
			int seatIndex = screening.getSeatList().indexOf(tempSeat);
			// Adds the screening's seat object to this booking's seatList
			seatList.add(screening.getSeatList().get(seatIndex));
		}
	}

	/**
	 * @param seatList An ArrayList containing Seat elements
	 */
	public void setSeats(ArrayList<Seat> seatList) {
		this.seatList = seatList;
	}

	/**
	 * @return An ArrayList containing Seat Elements
	 */
	public ArrayList<Seat> getSeatList() {
		return seatList;
	}

	/**
	 * Returns a String of all seats, formatted good human readability
	 * Necessary function for the usage of PropertyValueFactory
	 *
	 * @return a well-formatted list of all seats associated with this booking
	 */
	public String getFormattedSeatList() {
		return Helpers.formatSeatList(seatList);
	}

	/**
	 * Returns a this booking's time as a String of format HH:MM
	 * Necessary function for the usage of PropertyValueFactory
	 *
	 * @return a String of this booking's time in format HH:MM
	 */
	public String getFormattedTime() {
		return getScreening().getTime() + ":00";
	}

	public String getFormattedDate() {
		return Helpers.formatDateString(getScreening().getDate());
	}
}
