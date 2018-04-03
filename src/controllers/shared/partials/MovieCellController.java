package controllers.shared.partials;

import application.MainApp;
import controllers.employee.EmployeeScreeningDetailsController;
import helpers.Helpers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Movie;
import models.Screening;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Controller class for MovieCell.fxml
 *
 * Handles the business logic for this view which represents a listview cell for screenings in the cinema.
 *
 * Based on: https://stackoverflow.com/questions/19588029/customize-listview-in-javafx-with-fxml
 */
public class MovieCellController {
	@FXML
	AnchorPane container;
	@FXML
	ImageView imgPoster;
	@FXML
	Label lblTitle, lblDate, lblSeats;
	@FXML
	Button btnDetails;

	private Screening screening;

	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the actions performed by this cell's button
	 * based on what type of user is currently logged in.
	 */
	@FXML
	private void initialize() {
		if (MainApp.isEmployee) {
			//If the logged in user is an employee, clicking the button shows the ScreeningDetails view
			btnDetails.setOnAction(event -> showScreeningDetails());
		} else {
			//If the logged in user is a customer, clicking the button shows the MovieDetails view in a new window
			btnDetails.setOnAction(event -> showMovieDetails());
		}
	}

	/**
	 * Setter method for this controller's screening object
	 * @param screening this controller's screening object
	 */
	public void setScreening(Screening screening) {
		this.screening = screening;
	}

	/**
	 * Fills in the cell's fields with information from the provided screening object
	 *
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void populateCell() throws SQLException, ClassNotFoundException {
		//Sets the movie associated with the provided screening object
		Movie movie = screening.getMovie();
		//Fills the labels with corresponding information
		lblTitle.setText(movie.getName());
		// Displays the time, day of the week and formatted date for the booked screening
		lblDate.setText(screening.getTime() + ":00 on " +
			LocalDate.parse(screening.getDate()).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK) + ", "+
			Helpers.formatDateString(screening.getDate()));
		lblSeats.setText(screening.getBookedSeats() + "/" + screening.getTotalSeats() + " seats booked");

		//Loads the local image into the image view imgMovieImage
		File file = new File(System.getProperty("user.dir") + "/img/" + movie.getImgPath());
		Image img = new Image(file.toURI().toString());
		imgPoster.setImage(img);
	}

	/**
	 * Displays the ScreeningDetails view in the center of employeeRoot
	 */
	@FXML
	private void showScreeningDetails() {
		try {
			//Loads the view and connected controller
			EmployeeScreeningDetailsController controller;
			FXMLLoader loader = new FXMLLoader();
			// Points the FXMLLoader to the view's filepath
			loader.setLocation(MainApp.class.getResource("/views/employee/EmployeeScreeningDetails.fxml"));
			// Loads the view
			AnchorPane employeeScreeningDetails = loader.load();
			// Retrieves the view's controller
			controller = loader.getController();
			//Sets the controller's screening object and calls the setup method which fills in the
			//view's fields with information from the screening object
			controller.setScreening(screening);
			controller.setup();
			//Displays the view in the center of employeeRoot
			MainApp.employeeRoot.setCenter(employeeScreeningDetails);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the MovieDetails view in a new stage that pops up
	 */
	@FXML
	private void showMovieDetails() {
		try {
			// Loads the view and connected controller and creates a new stage for it
			Stage stage = new Stage();
			stage.initOwner(container.getScene().getWindow());
			// Sets the window title to the name of the screening's movie
			stage.setTitle(screening.getMovie().getName());
			FXMLLoader loader = new FXMLLoader();
			// Points the FXMLLoader to the view's filepath
			loader.setLocation(MainApp.class.getResource("/views/shared/partials/MovieDetail.fxml"));
			// Loads the view
			AnchorPane movieDetail = loader.load();
			// Retrieves the view's controller
			MovieDetailController controller = loader.getController();
			// Calls the controller's setup method with the screening's movie object
			// This fills in the view's fields with information from the screening object
			controller.setup(screening.getMovie());
			// Creates a new scene, places it in the newly created stage and displays the stage
			// on top of the current one
			Scene scene = new Scene(movieDetail);
			// Links the stylesheet to the scene
			scene.getStylesheets().add(MainApp.class.getResource("/application/stylesheet.css").toExternalForm());
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Getter method for this controller's container
	 *
	 * @return returns this controller's container
	 */
	public AnchorPane getContainer() {
		return container;
	}
}
