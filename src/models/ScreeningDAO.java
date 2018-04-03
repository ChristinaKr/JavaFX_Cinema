package models;

import application.MainApp;
import helpers.DBHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a data access object (DAO) for Screenings.
 *
 * Following the DAO design principle, this class acts as a layer between
 * the Model (the Screening) and the business logic of this application.
 * It provides functions to search and return one or multiple Screenings from the database.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class ScreeningDAO {

	/**
	 * This method searches for a Screening in the database based on the provided screenID
	 *
	 * @param screenID the screening's unique identifier
	 * @return the Screening object that was retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	static Screening searchScreening(String screenID) throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM screenings WHERE screenID=" + screenID;

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsScreening = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getScreeningFromResultSet method and return the Screening object
			return getScreeningFromResultSet(rsScreening);
		} catch (SQLException e) {
			MainApp.LOGGER.warning("While searching a screening with " + screenID
				+ " id, an error occurred: " + e);
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates a new Screening object from the provided result set
	 *
	 * @param rs the resultset containing information for the new Screening
	 * @return a Screening object
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static Screening getScreeningFromResultSet(ResultSet rs) throws SQLException {
		//Declare variable screening
		Screening screening = new Screening();
		if (rs.next()) {
			screening.setScreenID(rs.getInt("screenID"));
			screening.setMovieID(rs.getInt("movieID"));
			screening.setSeats(rs.getString("seats"));
			screening.setDate(rs.getString("date"));
			screening.setTime(rs.getInt("time"));
		}
		return screening;
	}

	/**
	 * This method searches for a list of Screenings in the database
	 *
	 * @return a list of all matching Screening objects that were retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static ObservableList<Screening> searchScreenings() throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM screenings";

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsScreenings = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getEmployeeList method and return the screening object
			return getScreeningList(rsScreenings);
		} catch (SQLException e) {
			MainApp.LOGGER.warning("SQL select operation has failed: " + e);
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates an observable list of new Screening objects from the provided result set
	 *
	 * @param rs the resultset containing information for the new Screening
	 * @return an ObservableList of Screening objects
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static ObservableList<Screening> getScreeningList(ResultSet rs) throws SQLException {
		//Declare an observable List comprising of Screening objects
		ObservableList<Screening> screeningList = FXCollections.observableArrayList();

		while (rs.next()) {
			//Create new Screening object and fill it with information from the database
			Screening screening = new Screening();
			screening.setScreenID(rs.getInt("screenID"));
			screening.setMovieID(rs.getInt("movieID"));
			screening.setSeats(rs.getString("seats"));
			screening.setDate(rs.getString("date"));
			screening.setTime(rs.getInt("time"));

			//Add screening to the ObservableList
			screeningList.add(screening);
		}
		return screeningList;
	}

	/**
	 * Adds a new row to the Screenings table in the database based on a provided Screening Object
	 *
	 * @param screening the Screening object to be saved in the database
	 * @throws SQLException thrown if an error occurs during the database operation
	 */
	public static void addScreening(Screening screening) throws SQLException {
		// Creates a new SQL statement with information from the provided screening object
		String addStatement =
			  "INSERT INTO screenings "
			+ "(movieID, seats, date, time) "
			+ "VALUES ("
			+ "" + screening.getMovieID()
			+ ", '" + screening.getSeats() + "'"
			+ ", '" + screening.getDate() + "'"
			+ ", " + screening.getTime()
			+ ");";
		DBHelper.dbExecuteUpdate(addStatement);
	}

	//Updates the seat availability of a screening in the database
	public static void updateScreening(Screening screening) throws SQLException {
		// Creates a new SQL statement with information from the provided screening object
		String updateStatement =
			"UPDATE screenings "
				+ "SET "
				+ "seats = '" + screening.getSeats() + "' "
				+ "WHERE "
				+ "screenID = " + screening.getScreenID() + ";";
		// Executes the statement
		DBHelper.dbExecuteUpdate(updateStatement);
	}

	/**
	 * Deletes the provided Screening from the database
	 * @param screening the booking to be deleted from the database
	 * @throws SQLException thrown if an error occurs during the database operation
	 */
	public static void deleteScreening(Screening screening) throws SQLException {
		// Creates a new SQL statement with information from the provided screening object
		String deleteStatement =
			"DELETE FROM screenings WHERE "
			+ "screenID = " + screening.getScreenID() + ";";
		// Executes the statement
		DBHelper.dbExecuteUpdate(deleteStatement);
	}

}
