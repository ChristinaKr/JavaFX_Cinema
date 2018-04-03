package controllers.employee;

import application.MainApp;
import helpers.SelectionListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import models.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller class for EmployeeMovieList.fxml
 *
 * Contains the business logic for navigation, setting up the view, searching the listview
 * displaying the EmployeeAddMovie view and for deleting movies
 * and associated screenings and movies from the database.
 *
 * Source: http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
 *
 */
public class EmployeeMovieListController {

	@FXML
	TableView<Movie> tvMovies;
	@FXML
	TableColumn<Movie, String> tcName, tcYear, tcDirector, tcActors, tcGenre;
	@FXML
	Button btnRemove;
	@FXML
	TextField tfSearch;

	private ObservableList<Movie> movieList = FXCollections.observableArrayList();
	private FilteredList<Movie> filteredList = new FilteredList<>(movieList, predicate -> true);
	private SortedList<Movie> sortedList = new SortedList<>(filteredList);

	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the related view.
	 */
	@FXML
	private void initialize() {
		setupTableView();
	}

	/**
	 * Sets up the TableView with a list of all movies in the database
	 */
	private void setupTableView() {
		try {
			// Retrieves all movies from the database and places them in the tableview
			movieList.addAll( MovieDAO.searchMovies());
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		// Binds the sortedList to tvMovies' comparator Property
		sortedList.comparatorProperty().bind(tvMovies.comparatorProperty());
		// Populates the tableview's columns with information about the movies
		tvMovies.setItems(sortedList);
		tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tcYear.setCellValueFactory(new PropertyValueFactory<>("year"));
		tcDirector.setCellValueFactory(new PropertyValueFactory<>("director"));
		tcGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
		tcActors.setCellValueFactory(new PropertyValueFactory<>("actors"));

		tvMovies.getSortOrder().addAll(tcName);

		// Adds EventHandlers that enable/disable the Remove button when a movie is selected/unselected
		tvMovies.setOnMouseClicked(new SelectionListener(tvMovies, btnRemove));
		tvMovies.setOnKeyPressed(new SelectionListener(tvMovies, btnRemove));
	}

	/**
	 * Displays the EmployeeAddMovie view
	 */
	@FXML
	private void showEmployeeAddMovie() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/employee/EmployeeAddMovie.fxml"));
			AnchorPane employeeAddMovie = loader.load();
			MainApp.employeeRoot.setCenter(employeeAddMovie);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes the currently selected movie from the database
	 *
	 * Displays an alert asking for confirmation and if confirmation is given
	 * removes all associated screenings and bookings from the database
	 */
	@FXML
	private void remove() {
		// Display an information dialogue asking for confirmation
		Alert alert = new Alert(Alert.AlertType.INFORMATION, "Are you sure you want to delete this movie?" +
			" This will also permanently remove all screenings and bookings related to this movie.", ButtonType.YES, ButtonType.NO);
		alert.setTitle("Information");
		alert.setHeaderText("Are you sure?");
		// Only proceeds if "YES" is clicked
		if (alert.showAndWait().get() == ButtonType.YES) {
			// Retrieves the selected movie from the TableView
			Movie selectedMovie = tvMovies.getSelectionModel().getSelectedItem();
			try {
				// Creates Lists for screenings and bookings
				ObservableList<Screening> screeningList = FXCollections.observableArrayList();
				ObservableList<Booking> bookingList = FXCollections.observableArrayList();
				// Retrieves all screenigns from the database
				screeningList.addAll(ScreeningDAO.searchScreenings());
				for (Screening screening : screeningList) {
					// Matches screenings with the movieID of the movie that's about to be deleted
					if (screening.getMovie().getMovieID() == selectedMovie.getMovieID()) {
						// Retrieves all bookings associated with this screening
						bookingList.addAll(BookingDAO.searchBookings(screening.getScreenID()));
						for (Booking booking : bookingList) {
							// Deletes the bookings from the database
							BookingDAO.deleteBooking(booking);
						}
						// Deletes the screenings from the database
						ScreeningDAO.deleteScreening(screening);
					}
				}
				// Deletes the movies from the database
				MovieDAO.deleteMovie(selectedMovie);
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			// Deletes the movies from the tableview
			movieList.remove(selectedMovie);
			ObservableList<Screening> observableList =
				MainApp.employeeDashboardController.getObservableList();
			// Deletes the just removed screenings from the listview on the EmployeeDashboard view
			for (int i = observableList.size() - 1; i >= 0; i--) {
				if (observableList.get(i).getMovieID() == selectedMovie.getMovieID()) {
					observableList.remove(i);
				}
			}
		}
	}

	/**
	 * This method filters the contents of tvMovies by the contents of tfSearch
	 */
	@FXML
	private void search() {
		//Retrieves the content of tfSearch
		String searchString = tfSearch.getText();
		//Sets the predicate of the filteredList which provides the content for tvMovies
		//This predicate acts as the filter which determines which items are displayed in
		//the TableView and which are not
		filteredList.setPredicate(movie -> {
			//If the search field is empty, all list items are displayed
			if (searchString == null || searchString.isEmpty()) {
				return true;
			}
			//The string is put into all lower case to make it case-insensitive
			String lowerCaseValue = searchString.toLowerCase();
			//Similarly, the movie titles against which it is compared are put into lower case as well
			if (movie.getName().toLowerCase().contains(lowerCaseValue)) {
				return true;
			}
			//If the search field is not null/empty and the movie in the table does not match the predicate
			//the movie is not displayed
			return false;
		});
	}

	/**
	 * Displays the EmployeeDashboard view in the center of EmployeeRoot
	 */
	@FXML
	private void back() {
		MainApp.employeeRoot.setCenter(MainApp.employeeDashboard);
	}
}
