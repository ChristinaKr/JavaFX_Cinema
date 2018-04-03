package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class represents the screenings in the application
 * and is instantiated with rows from the Screenings table in the database.
 *
 * Each instance of this class represents a screening of the application.
 * Each screening has a unique screenID, movieID, screening time, screening date,
 * an ArrayList containing its seat elements, Strings for the total, available and booked
 * number of seats and a reference for this screening's associated movie object.
 *
 * This application uses the Data Access Object (DAO) model for structuring its model.
 * This improves the readability and maintainability of the code by providing uniform
 * ways to interact with all models, as well as with the database and by clearly separating
 * the business logic of the application from its data objects.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class Screening {

	//Screening Properties
	private IntegerProperty screenID, movieID, time;
	private StringProperty date;
	private StringProperty totalSeats, availableSeats, bookedSeats;
	private ArrayList<Seat> seats;
	private Movie movie;

	/**
	 * Constructor that initializes all fields
	 */
	public Screening() {
		screenID = new SimpleIntegerProperty();
		movieID = new SimpleIntegerProperty();
		seats = new ArrayList<>();
		date = new SimpleStringProperty();
		time = new SimpleIntegerProperty();
		movie = null;
	}

	//----------------------------------------//
	// Setters and getters for all properties //
	//----------------------------------------//
	//screenID

	/**
	 *
	 * @return this screening's unique identifier
	 */
	public int getScreenID() {
		return screenID.get();
	}

	/**
	 *
	 * @return this screening's screenID property
	 */
	public IntegerProperty screenIDProperty() {
		return screenID;
	}

	/**
	 *
	 * @param screenID this screening's unique identifier
	 */
	public void setScreenID(int screenID) {
		this.screenID.set(screenID);
	}

	// MovieID

	/**
	 *
	 * @return this screening's movie's unique identifier
	 */
	public int getMovieID() {
		return movieID.get();
	}

	/**
	 *
	 * @return this screening's movieID property
	 */
	public IntegerProperty movieIDProperty() {
		return movieID;
	}

	/**
	 * When a new movieID is set, the corresponding Movie is loaded from the database
	 *
	 * @param movieID this screening's movie's unique identifier
	 */
	public void setMovieID(int movieID) {
		this.movieID.set(movieID);
		try {
			movie = MovieDAO.searchMovie(movieID);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	//Seats
	/**
	 *
	 * @return an ArrayList containing Seat objects representing this screening's list of seats
	 */
	public ArrayList<Seat> getSeatList() {
		return seats;
	}

	/**
	 *
	 * @return a String containing this screening's list of seats made up of 0's and 1's - 0 for an empty seat, 1 for a booked one
	 */
	public String getSeats() {
		return seatArrayListToString(seats);
	}

	/**
	 * Saves the provided String as an Arraylist of Seat elements
	 *
	 * @param seatsStr a String containing this screening's list of seats made up of 0's and 1's - 0 for an empty seat, 1 for a booked one
	 */
	public void setSeats(String seatsStr) {
		seats = seatStringToArrayList(seatsStr);
	}

	/**
	 * Calculates the number of available seats for this screening
	 * @return the number of available seats
	 */
	public int getAvailableSeats() {
		int seatCount = 0;
		for (Seat seat : seats) {
			if (!seat.isBooked()) {
				seatCount++;
			}
		}
		return seatCount;
	}


	/**
	 * Calculates the number of booked seats for this screening
	 * @return the number of booked seats
	 */
	public int getBookedSeats() {
		int seatCount = 0;
		for (Seat seat : seats) {
			if (seat.isBooked()) {
				seatCount++;
			}
		}
		return seatCount;
	}

	/**
	 * Calculates the number of total seats for this screening
	 * @return the number of total seats
	 */
	public int getTotalSeats() {
		return seats.size();
	}

	//Time

	/**
	 *
	 * @return the screening time
	 */
	public int getTime() {
		return time.get();
	}

	/**
	 *
	 * @return this screening's time property
	 */
	public IntegerProperty timeProperty() {
		return time;
	}

	/**
	 *
	 * @param time the screening time
	 */
	public void setTime(int time) {
		this.time.set(time);
	}

	/**
	 *
	 * @return this screening's date
	 */
	public String getDate() {
		return date.get();
	}

	/**
	 *
	 * @return this screening's date property
	 */
	public StringProperty dateProperty() {
		return date;
	}

	/**
	 *
	 * @param date this screening's date
	 */
	public void setDate(String date) {
		this.date.set(date);
	}

	/**
	 *
	 * @return this screening's associated movie object
	 */
	public Movie getMovie() {
		return movie;
	}

	/**
	 *
	 * @param movie this screening's associated movie object
	 */
	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	/**
	 * Takes a String of 0's and 1's and parses them as available/unavailable seats for this screening
	 *
	 * @param seatString a String containing this screening's list of seats made up of 0's and 1's - 0 for an empty seat, 1 for a booked one
	 * @return an ArrayList of Seat objects
	 */
	private ArrayList<Seat> seatStringToArrayList(String seatString) {
		// Creates a new ArrayList
		ArrayList<Seat> seatArrayList = new ArrayList<>(seatString.length());
		for (int i = 0; i < seatString.length(); i++) {
			final int SEATS_PER_ROW = 10;
			// Calculates the row letter
			// 65 is the ASCII code for capital A, adding to this leads to alphabetically increasing row identifiers
			char row = (char)(65 + ((i / SEATS_PER_ROW)));
			// Calculates the seat number within the row
			int number = (i % SEATS_PER_ROW) + 1;
			Seat seat;
			if (seatString.charAt(i) == '0') {
				// If the seatString contains a 0 at this position, creates an available seat
				seat = new Seat(row, number, false);
			} else {
				// If the seatString contains a 1 at this position, creates a booked seat
				seat = new Seat(row, number, true);
			}
			seatArrayList.add(seat);
		}
		return seatArrayList;
	}

	/**
	 * Takes an ArrayList of Seat objects and turns them into a String of 0's and 1's signifying empty/available seats in order
	 *
	 * @param seatArrayList an ArrayList of Seat objects
	 * @return a String containing this screening's list of seats made up of 0's and 1's - 0 for an empty seat, 1 for a booked one
	 */
	private String seatArrayListToString(ArrayList<Seat> seatArrayList) {
		StringBuilder seatString = new StringBuilder();
		for (Seat seat : seatArrayList) {
			if (seat.isBooked()) {
				// If is booked, add a 1 to the String
				seatString.append('1');
			} else {
				// If is available, add a 0 to the String
				seatString.append('0');
			}
		}
		return seatString.toString();
	}
}
