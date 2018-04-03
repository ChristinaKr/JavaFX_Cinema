package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents the movies in the application
 * and is instantiated with rows from the Movies table in the database.
 *
 * Each instance of this class represents a movie of the application.
 * Each movie has a unique movieID, a name, description, genre, year, director, a
 * newline-separated list of actors, an imgPath that holds the relative filepath of
 * its movie poster and a trailer URL
 *
 * This application uses the Data Access Object (DAO) model for structuring its model.
 * This improves the readability and maintainability of the code by providing uniform
 * ways to interact with all models, as well as with the database and by clearly separating
 * the business logic of the application from its data objects.
 *
 * Source: http://www.swtestacademy.com/database-operations-javafx/
 */
public class Movie {

	//Movie properties
	private IntegerProperty movieID;
	private StringProperty name;
	private StringProperty description;
	private StringProperty genre;
	private IntegerProperty year;
	private StringProperty director;
	private StringProperty actors;
	private StringProperty imgPath;
	private StringProperty trailerURL;

	/**
	 * Constructor that initializes all fields
	 */
	public Movie() {
		this.movieID = new SimpleIntegerProperty();
		this.name = new SimpleStringProperty();
		this.description = new SimpleStringProperty();
		this.genre = new SimpleStringProperty();
		this.year = new SimpleIntegerProperty();
		this.director = new SimpleStringProperty();
		this.actors = new SimpleStringProperty();
		this.imgPath = new SimpleStringProperty();
		this.trailerURL = new SimpleStringProperty();
	}

	/**
	 * A custom implementation of the toString() method that returns the movie's name
	 * @return the movie name
	 */
	@Override
	public String toString() {
		return name.getValue();
	}

	//----------------------------------------//
	// Setters and getters for all properties //
	//----------------------------------------//

	//movieID

	/**
	 *
	 * @return this movie's unique movieID
	 */
	public int getMovieID() {
		return movieID.get();
	}

	/**
	 *
	 * @return this movie's movieID property
	 */
	public IntegerProperty movieIDProperty() {
		return movieID;
	}

	/**
	 *
	 * @param movieID this movie's unique movieID
	 */
	void setMovieID(int movieID) {
		this.movieID.set(movieID);
	}

	//name

	/**
	 *
	 * @return this movie's name
	 */
	public String getName() {
		return name.get();
	}

	/**
	 *
	 * @return this movie's name property
	 */
	public StringProperty nameProperty() {
		return name;
	}

	/**
	 *
	 * @param name this movie's name
	 */
	public void setName(String name) {
		this.name.set(name);
	}

	//description

	/**
	 *
	 * @return this movie's description
	 */
	public String getDescription() {
		return description.get();
	}

	/**
	 *
	 * @return this movie's description property
	 */
	public StringProperty descriptionProperty() {
		return description;
	}

	/**
	 *
	 * @param description this movie's description
	 */
	public void setDescription(String description) {
		this.description.set(description);
	}

	//genre

	/**
	 *
	 * @return this movie's genre
	 */
	public String getGenre() {
		return genre.get();
	}

	/**
	 *
	 * @return this movie's genre property
	 */
	public StringProperty genreProperty() {
		return genre;
	}

	/**
	 *
	 * @param genre this movie's genre
	 */
	public void setGenre(String genre) {
		this.genre.set(genre);
	}

	//year

	/**
	 *
	 * @return this movie's release year
	 */
	public int getYear() {
		return year.get();
	}

	/**
	 *
	 * @return this movie's year property
	 */
	public IntegerProperty yearProperty() {
		return year;
	}

	/**
	 *
	 * @param year this movie's release year
	 */
	public void setYear(int year) {
		this.year.set(year);
	}

	//director

	/**
	 *
	 * @return this movie's director
	 */
	public String getDirector() {
		return director.get();
	}

	/**
	 *
	 * @return this movie's director property
	 */
	public StringProperty directorProperty() {
		return director;
	}

	/**
	 *
	 * @param director this movie's director
	 */
	public void setDirector(String director) {
		this.director.set(director);
	}

	//actors

	/**
	 *
	 * @return a newline-separated string of actor names
	 */
	public String getActors() {
		return actors.get();
	}

	/**
	 *
	 * @return this movie's actors property
	 */
	public StringProperty actorsProperty() {
		return actors;
	}

	/**
	 *
	 * @param actors a newline-separated string of actor names
	 */
	public void setActors(String actors) {
		this.actors.set(actors);
	}

	//imgPath

	/**
	 *
	 * @return the relative filepath of this movie's poster
	 */
	public String getImgPath() {
		return imgPath.get();
	}

	/**
	 *
	 * @return this movie's imgPath property
	 */
	public StringProperty imgPathProperty() {
		return imgPath;
	}

	/**
	 *
	 * @param imgPath the relative filepath of this movie's poster
	 */
	public void setImgPath(String imgPath) {
		this.imgPath.set(imgPath);
	}

	/**
	 *
	 * @return the URL of this movie's trailer
	 */
	public String getTrailerURL() {
		return trailerURL.get();
	}

	/**
	 *
	 * @return this movie's trailerURL property
	 */
	public StringProperty trailerURLProperty() {
		return trailerURL;
	}

	/**
	 *
	 * @param trailerURL the URL of this movie's trailer
	 */
	public void setTrailerURL(String trailerURL) {
		this.trailerURL.set(trailerURL);
	}


}
