package models;

import application.MainApp;
import helpers.DBHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a data access object (DAO) for Movies.
 *
 * Following the DAO design principle, this class acts as a layer between
 * the Model (the Movie) and the business logic of this application.
 * It provides functions to search and return one or multiple Movies from the database.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class MovieDAO {

	/**
	 * This method searches for a Movie in the database based on the provided movieID
	 *
	 * @param movieID the movie's unique identifier
	 * @return the Movie object that was retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static Movie searchMovie(int movieID) throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM movies WHERE movieID=" + movieID;

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsMovie = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getMovieFromResultSet method and
			//get Movie Object
			Movie movie = getMovieFromResultSet(rsMovie);
			//Return Movie object
			return movie;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("While searching a movie with " + movieID
				+ " id, an error occurred: " + e);
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates a new Movie object from the provided result set
	 *
	 * @param rs the resultset containing information for the new Movie
	 * @return a Movie object
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static Movie getMovieFromResultSet(ResultSet rs) throws SQLException {
		//Declare variable movie
		Movie movie = null;
		if (rs.next()) {
			movie = new Movie();
			movie.setMovieID(rs.getInt("movieID"));
			movie.setName(rs.getString("name"));
			movie.setDescription(rs.getString("description"));
			movie.setGenre(rs.getString("genre"));
			movie.setYear(rs.getInt("year"));
			movie.setDirector(rs.getString("director"));
			movie.setActors(rs.getString("actors"));
			movie.setImgPath(rs.getString("imgPath"));
			movie.setTrailerURL(rs.getString("trailerURL"));
		}
		return movie;
	}

	/**
	 * This method searches for a list of Movies in the database
	 *
	 * @return a list of all matching Movie objects that were retrieved from the database
	 * @throws SQLException thrown if an error occurs during information retrieval
	 * @throws ClassNotFoundException thrown if the JSBC driver couldn't be loaded
	 */
	public static ObservableList<Movie> searchMovies() throws SQLException, ClassNotFoundException {
		//Declare a SELECT statement
		String selectStatement = "SELECT * FROM movies";

		//Execute SELECT statement
		try {
			//Get ResultSet from dbExecuteQuery method
			ResultSet rsMovies = DBHelper.dbExecuteQuery(selectStatement);
			//Send ResultSet to the getEmployeeList method and get movie object
			ObservableList<Movie> movieList = getMovieList(rsMovies);
			//Return movie object
			return movieList;
		} catch (SQLException e) {
			MainApp.LOGGER.warning("SQL select operation has failed: " + e);
			//Rethrow exception
			throw e;
		}
	}

	/**
	 * Internal helper method that creates an observable list of new Movie objects from the provided result set
	 *
	 * @param rs the resultset containing information for the new Movies
	 * @return an ObservableList of Movie objects
	 * @throws SQLException thrown if an error occurs during information retrieval
	 */
	private static ObservableList<Movie> getMovieList(ResultSet rs) throws SQLException {
		//Declare an observable List comprising of Movie objects
		ObservableList<Movie> movieList = FXCollections.observableArrayList();

		while (rs.next()) {
			//Create new Movie object and fill it with information from the database
			Movie movie = new Movie();
			movie.setMovieID(rs.getInt("movieID"));
			movie.setName(rs.getString("name"));
			movie.setDescription(rs.getString("description"));
			movie.setGenre(rs.getString("genre"));
			movie.setYear(rs.getInt("year"));
			movie.setDirector(rs.getString("director"));
			movie.setActors(rs.getString("actors"));
			movie.setImgPath(rs.getString("imgPath"));
			movie.setTrailerURL(rs.getString("trailerURL"));
			//Add movie to the ObservableList
			movieList.add(movie);
		}
		return movieList;
	}

	/**
	 * Adds a new row to the Movies table in the database based on a provided Movie Object
	 *
	 * @param movie the Movie object to be saved in the database
	 * @throws SQLException thrown if an error occurs during the database operation
	 */
	public static void addMovie(Movie movie) throws SQLException {
		// Creates a new SQL statement with information from the provided movie object
		String addStatement =
			"INSERT INTO movies "
				+ "(name, description, genre, year, director, actors, imgPath, trailerURL) "
				+ "VALUES ("
				+ "'" + movie.getName() + "'"
				+ ", '" + movie.getDescription() + "'"
				+ ", '" + movie.getGenre() + "'"
				+ ", " + movie.getYear() + ""
				+ ", '" + movie.getDirector() + "'"
				+ ", '" + movie.getActors() + "'"
				+ ", '" + movie.getImgPath() + "'"
				+ ", '" + movie.getTrailerURL() + "'"
				+ ");";
		// Executes the statement
		DBHelper.dbExecuteUpdate(addStatement);
	}

	/**
	 * Deletes the provided Movie from the database
	 * @param movie the movie to be deleted from the database
	 * @throws SQLException thrown if an error occurs during the database operation
	 */
	public static void deleteMovie(Movie movie) throws SQLException {
		// Creates a new SQL statement with information from the provided movie object
		String deleteStatement =
			"DELETE FROM movies WHERE "
				+ "movieID = " + movie.getMovieID() + ";";
		// Executes the statement
		DBHelper.dbExecuteUpdate(deleteStatement);
	}
}
