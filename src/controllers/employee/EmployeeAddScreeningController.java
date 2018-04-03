package controllers.employee;

import application.MainApp;
import controllers.shared.partials.MovieDetailController;
import helpers.DateStringConverter;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import models.Movie;
import models.MovieDAO;
import models.Screening;
import models.ScreeningDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controller class for EmployeeAddScreening.fxml
 *
 * Provides the business logic for the addition of screenings to the database given information entered by the user
 * in the EmployeeAddScreening view, for displaying information about the selected movie in a designated Pane and for
 * several helper methods required to give all view control elements their respective functionality.
 *
 */
public class EmployeeAddScreeningController {

	@FXML
	Pane paneMovieDetails;
	@FXML
	ListView<Movie> lvMovieList;
	@FXML
	DatePicker dateDate;
	@FXML
	ChoiceBox<String> choiceTime;
	@FXML
	Button btnAdd;

	private MovieDetailController movieDetailController;

	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the related view.
	 */
	@FXML
	private void initialize() {
		// Displays movieDetail view within paneMovieDetails
		showMovieDetailView();
		// Fills in the available time slots and adds EventHandlers to the date picker and time picker
		setupTimeAndDate();
		// Fills in the list of all movies
		initializeListviewMovieList();
		// Sets the format of the DatePicker to DD/MM/YYYY
		dateDate.setConverter(new DateStringConverter());
	}

	/**
	 * Adds a screening to the database for the currently selected movie, date and time
	 */
	@FXML
	private void add() {
		// Retrieves the date from the DatePicker
		LocalDate selectedDate = dateDate.getValue();
		String date = selectedDate.toString();
		// Reads the time from the choiceBox choiceTime and parse it as a two digit integer number
		int time = Integer.parseInt(choiceTime.getSelectionModel().getSelectedItem().substring(0, 2));
		// Checks if the selected date and time are in the past
		if (selectedDate.isBefore(LocalDate.now()) ||
			(selectedDate.isEqual(LocalDate.now()) && LocalTime.now().getHour() >= time)) {
			// Displays a warning if the date and time are in the past
			Alert alert = new Alert(Alert.AlertType.WARNING, "You cannot schedule a screening in" +
				" the past.");
			alert.setHeaderText("No scheduling in the past");
			alert.setTitle("Scheduling conflict");
			alert.showAndWait();
			return;
		}
		// Checks if a screening at this time already exists in the database
		ObservableList<Screening> allScreenings;
		try {
			// Retrieves all screenings from the database
			allScreenings = ScreeningDAO.searchScreenings();
			for (Screening tempScreen : allScreenings) {
				// Checks if a screening has the same date and time as the screening about to be created
				if (tempScreen.getDate().equals(date)) {
					if (tempScreen.getTime() == time) {
						// Displays a warning if a match is found and returns the function
						Alert alert = new Alert(Alert.AlertType.WARNING, "There already exists a screening for "
							+ tempScreen.getMovie().getName() + " at this selected time. Please choose a" +
							" different time or remove the scheduled screening.");
						alert.setHeaderText("This time slot is already taken");
						alert.setTitle("Scheduling conflict");
						alert.showAndWait();
						return;
					}
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		// Creates a new Screening object and set its relevant values
		Screening newScreening = new Screening();
		// Sets the number of seats in the cinema
		int numberOfSeats = 50;
		StringBuilder seatString = new StringBuilder();
		// Creates a String with a 0 for every seat in the cinema
		for (int i = 0; i < numberOfSeats; i++) {
			seatString.append("0");
		}
		newScreening.setSeats(seatString.toString());
		newScreening.setDate(date);
		newScreening.setTime(time);
		newScreening.setMovieID(lvMovieList.getSelectionModel().getSelectedItem().getMovieID());
		// Adds the newly created screening to the database
		try {
			ScreeningDAO.addScreening(newScreening);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Updates the list of upcoming screenings in the EmployeeDashboard so it displays the new Screening
		MainApp.employeeDashboardController.getObservableList().add(newScreening);
		// Displays the EmployeeDashboard in place of this view
		MainApp.employeeRoot.setCenter(MainApp.employeeDashboard);
	}

	/**
	 * Provides business logic for the cancel button
	 */
	@FXML
	private void cancel() {
		//Displays the EmployeeDashboard in place of this view
		MainApp.employeeRoot.setCenter(MainApp.employeeDashboard);
	}

	/**
	 * Loads the MovieDetail view and places it in the designated Pane paneMovieDetails
	 */
	private void showMovieDetailView() {
		// Creates a new FXMLLoader
		FXMLLoader loader = new FXMLLoader();
		// Sets the location of the .FXML file
		loader.setLocation(MainApp.class.getResource("/views/shared/partials/MovieDetail.fxml"));
		AnchorPane movieDetail = null;
		try {
			// Loads the FXML file
			movieDetail = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Saves the controller in an instance variable
		movieDetailController = loader.getController();
		// Adds the newly loaded view to the designated Pane paneMovieDetails
		paneMovieDetails.getChildren().add(movieDetail);
	}

	/**
	 * Initializes the ListView lvMovieList which displays the available Movies
	 *
	 * Queries all movies from the database, displays them in the ListView,
	 * and adds a ChangeListener which automatically updates the fields of
	 * movieDetailController when the movie selection in lvMovieList changes.
	 */
	private void initializeListviewMovieList() {
		//Only allows one item to be selected at any time
		lvMovieList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		//Pulls all movies from the database and display them in lvMovieList
		try {
			ObservableList<Movie> movies = MovieDAO.searchMovies();
			lvMovieList.setItems(movies);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		//Adds a ChangeListener to selectedItemProperty that populates the movieDetail view
		//with information about the currently selected movie.
		lvMovieList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
			//Only perform these actions if an item is currently selected
			if (lvMovieList.getSelectionModel().getSelectedItem() != null) {
				movieDetailController.setup(newValue);
			}
		});
		//Preselects the first entry in the list for the user
		lvMovieList.getSelectionModel().selectFirst();
		//Adds an event handler that checks whether a list item is currently selected on mouse click
		//and key press and enables or disables the Add button accordingly
		lvMovieList.setOnMouseClicked(new CompletenessTester());
		lvMovieList.setOnKeyReleased(new CompletenessTester());
	}

	/**
	 * Populates values for the ChoiceBox choiceTime
	 */
	private void setupTimeAndDate() {
		for (int i = 12; i <= 24; i++) {
			choiceTime.getItems().add(i + ":00");
		}
		choiceTime.setOnAction(new CompletenessTester());
		dateDate.setOnAction(new CompletenessTester());
	}

	/**
	 * EventHandler that checks whether all required input has been supplied and disables/enables the Add button accordingly
	 *
	 */
	private class CompletenessTester implements EventHandler {
		@Override
		public void handle(Event event) {
			//Checks if at least one item is currently selected in lvScreenings
			if (dateDate.getValue() == null ||
				choiceTime.getValue() == null ||
				lvMovieList.getSelectionModel().getSelectedItem() == null) {
				//If nothing is selected, disable btnExportSelected and btnDeleteScreenings
				btnAdd.setDisable(true);
			} else {
				//If one or more items are selected, enable btnExportSelected and btnDeleteScreenings
				btnAdd.setDisable(false);
			}
		}
	}
}