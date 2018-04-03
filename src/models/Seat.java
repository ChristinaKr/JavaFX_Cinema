package models;

/**
 * This class represents the cinema's Seats in the application
 *
 * Each instance of this class represents a seat within the cinema.
 * Each seat has a row, number and a boolean value indicating whether
 * it is still available for booking.
 *
 * Furthermore, seats are cloneable.
 */
public class Seat implements Cloneable {

	private char row;
	private int number;
	private boolean booked;

	/**
	 *
	 * @param row this seat's row
	 * @param number this seat's number within the row
	 * @param booked a boolean flag indicating whether the seat is booked (true) or not (false)
	 */
	public Seat(char row, int number, boolean booked) {
		this.row = row;
		this.number = number;
		this.booked = booked;
	}

	/**
	 * @return a copy of this seat object
	 */
	@Override
	public Seat clone() {
		return new Seat(row, number, booked);
	}

	/**
	 *
	 * @return a String containing the Seat's row identifier and number
	 */
	@Override
	public String toString() {
		return "" + row + number;
	}

	/**
	 * Compares two seat objects based on their row identifier and number
	 * @param object The object to be compared to this seat
	 * @return a boolean value indicating whether the provided object equals this Seat object
	 */
	public boolean equals(Object object) {
		// Return false if null is provided
		if (object == null)
			return false;
		// Return true if the provided class is not a Seat or a subclass of Seat
		if (!Seat.class.isAssignableFrom(object.getClass())) {
			return false;
		}
		// Cast the object to Seat
		final Seat seat = (Seat) object;
		// Compare the provided Seat to this one based on its row and number
		return (seat.getNumber() == this.getNumber() && seat.getRow() == this.getRow());
	}

	//----------------------------------------//
	// Setters and getters for all properties //
	//----------------------------------------//

	// Row

	/**
	 *
	 * @return this seat's row
	 */
	public char getRow() {
		return row;
	}

	/**
	 *
	 * @param row this seat's row
	 */
	public void setRow(char row) {
		this.row = row;
	}

	/**
	 *
	 * @return this seat's number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 *
	 * @param number this seat's number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 *
	 * @return this seat's booking status
	 */
	public boolean isBooked() {
		return booked;
	}

	/**
	 *
	 * @param booked this seat's booking status
	 */
	public void setBooked(boolean booked) {
		this.booked = booked;
	}

}
