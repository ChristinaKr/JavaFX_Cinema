package controllers.employee;

import application.MainApp;
import helpers.ListViewCell;
import helpers.ScreeningDateComparator;
import helpers.ScreeningNameComparator;
import helpers.SelectionListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static application.MainApp.employeeMovieList;

/**
 * Controller class for EmployeeDashboard.fxml
 *
 * Provides the business logic for displaying a customized ListView of upcoming screenings,
 * for removing a screening from the database and the list,
 * to export a list of either all or just the selected screenings to a .CSV file,
 * to filter the displayed list of movies through text-search
 * and to give the view's navigational button their required functionality.
 *
 * Sources: https://stackoverflow.com/questions/19588029/customize-listview-in-javafx-with-fxml
 */
public class EmployeeDashboardController {

	@FXML
	ListView<Screening> lvScreenings;
	@FXML
	Button btnAddScreening, btnDeleteScreenings, btnExportSelected, btnExportAll;
	@FXML
	TextField tfSearch;
	@FXML
	ChoiceBox<String> choiceSort;

	private List<Screening> screeningList = new ArrayList<>();
	private ObservableList<Screening> observableList = FXCollections.observableArrayList();
	//Wraps the observableList in a filteredList to allow for the application of search filters
	private FilteredList<Screening> filteredList = new FilteredList<>(observableList, predicate -> true);

	/**
	 * Sets up the central ListView which displays all upcoming screenings with custom ListCells
	 *
	 * Based on: https://stackoverflow.com/questions/19588029/customize-listview-in-javafx-with-fxml
	 */
	private void setListView(){
		//Only allows one item to be selected at any time
		lvScreenings.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		//Pulls all screenings from the database
		try {
			screeningList = ScreeningDAO.searchScreenings();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		//Removes Screenings from screeningList whose screening time is before now
		//This way, only upcoming screenings are displayed in the list
		for (int i = screeningList.size() - 1; i >= 0; i--) {
			Screening screening = screeningList.get(i);
			LocalDate date = LocalDate.parse(screening.getDate());
			int time = screening.getTime();
			if (date.isBefore(LocalDate.now()) ||
				((date.isEqual(LocalDate.now()) && LocalTime.now().getHour() >= time))) {
				screeningList.remove(i);
			}
		}
		//Fills the observableList with the items just pulled from the database
		observableList.setAll(screeningList);
		//Wrap the filtered list in a sorted List that sorts by date and time from oldest to newest
		SortedList<Screening> sortedList = new SortedList<>(filteredList, new ScreeningDateComparator());
		//Fills the ListView with new content
		lvScreenings.setItems(sortedList);
		//Sets the CellFactory for this ListView to allow for a custom display of upcoming screenings
		lvScreenings.setCellFactory(listView -> new ListViewCell());
		//Adds an event handler that checks whether a list item is currently selected on mouse click
		//and key press and enables or disables interface elements accordingly
		lvScreenings.setOnMouseClicked(new SelectionListener(lvScreenings, btnDeleteScreenings, btnExportSelected));
		lvScreenings.setOnKeyReleased(new SelectionListener(lvScreenings, btnDeleteScreenings, btnExportSelected));
		// Assigns a different Comparator to the sortedList that populates lvScreenings based on which
		// item is currently selected in choiceSort
		choiceSort.setOnAction(event -> {
			if (choiceSort.getSelectionModel().isSelected(0)) {
				// If the first item is selected ("Date"), screenings are sorted by their date
				sortedList.setComparator(new ScreeningDateComparator());
			} else {
				// If the second item is selected ("Name"), screenings are sorted by their title and then by their date
				sortedList.setComparator(new ScreeningNameComparator());
			}
		});
	}

	ObservableList<Screening> getObservableList() {
		return observableList;
	}

	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the related view.
	 */
	@FXML
	private void initialize() {
		setListView();
		// Adds the options "Date" and "Name" to the ChoiceBox choiceSort
		choiceSort.getItems().addAll("Date", "Movie");
		choiceSort.getSelectionModel().selectFirst();
	}

	/**
	 * This method filters the contents of lvScreenings by the contents of tfSearch
	 */
	@FXML
	private void search() {
		// Disables the export and delete buttons after every search
		btnDeleteScreenings.setDisable(true);
		btnExportSelected.setDisable(true);
		//Retrieves the content of tfSearch
		String searchString = tfSearch.getText();
		//Sets the predicate of the filteredList which provides the content for lvScreenings
		//This predicate acts as the filter which determines which items are displayed in
		//the ListView and which are not
		filteredList.setPredicate(screening -> {
			//If the search field is empty, all list items are displayed
			if (searchString == null || searchString.isEmpty()) {
				return true;
			}
			//The string is put into all lower case to make it case-insensitive
			String lowerCaseValue = searchString.toLowerCase();
			//Similarly, the movie titles against which it is compared are put into lower case as well
			if (screening.getMovie().getName().toLowerCase().contains(lowerCaseValue)) {
				return true;
			}
			//If the search field is not null/empty and the screening in the list does not match the predicate
			//the screening is not displayed
			return false;
		});
	}

	/**
	 * Shows the EmployeeAddScreening view and sets it as the center element of the root view
	 */
	@FXML
	private void showEmployeeAddScreening() {
		try {
			FXMLLoader loader = new FXMLLoader();
			// Points the FXMLLoader to the view's filepath
			loader.setLocation(MainApp.class.getResource("/views/employee/EmployeeAddScreening.fxml"));
			// Loads the view
			AnchorPane employeeAddScreening = loader.load();
			MainApp.employeeRoot.setCenter(employeeAddScreening);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes the currently selected screening(s) from the database and refreshes the displayed list
	 */
	@FXML
	private void deleteScreenings() {
		//Gets a list of the currently selected Screening objects
		List<Screening> selectedScreenings = lvScreenings.getSelectionModel().getSelectedItems();
		Alert alert;
		// Displays a different message based on how many items are selected
		if (selectedScreenings.size() == 1 ) {
			alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to permanently delete " +
				"this screening? This will also erase all related bookings.",
				ButtonType.YES, ButtonType.NO);
		} else {
			alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to permanently delete " +
				"these " + selectedScreenings.size() + " screenings? This will also erase all related bookings.",
				ButtonType.YES, ButtonType.NO);
		}
		alert.setTitle("Deletion Confirmation");
		alert.setHeaderText("Delete screenings");
		if (alert.showAndWait().get() == ButtonType.YES) {
			try {
				for (Screening screening : selectedScreenings) {
					//Deletes all bookings for the selected screenings from the database
					List<Booking> bookingList = BookingDAO.searchBookings(screening.getScreenID());
					for (Booking booking : bookingList) {
						BookingDAO.deleteBooking(booking);
					}
					//Removes the screenings from the database
					ScreeningDAO.deleteScreening(screening);
				}
				//Update the ListView
				observableList.removeAll(selectedScreenings);

				//Disable the export selected and delete buttons
				btnDeleteScreenings.setDisable(true);
				btnExportSelected.setDisable(true);


			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Displays the EmployeeAddMovie view
	 */
	@FXML
	private void showEmployeeMovieList() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/employee/EmployeeMovieList.fxml"));
			employeeMovieList = loader.load();
			MainApp.employeeRoot.setCenter(employeeMovieList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Exports the selected screenings as a CSV file at a user specified location
	 *
	 * @see #export
	 */
	@FXML
	private void exportSelectedScreenings() {
		//Saves all selected screenings from the ListView into a local variable
		List<Screening> selectedScreenings = lvScreenings.getSelectionModel().getSelectedItems();
		export(selectedScreenings);
	}

	/**
	 * Exports all screenings in the database as a CSV file at a user specified location
	 *
	 * @see #export
	 */
	@FXML
	private void exportAllScreenings() {
		try {
			export(ScreeningDAO.searchScreenings());
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Exports all upcoming screenings in the database as a CSV file at a user specified location
	 *
	 * @see #export
	 */
	@FXML
	private void exportAllUpcomingScreenings() {
		try {
			// Retrieves all screenings from the database
			ObservableList<Screening> screeningList = ScreeningDAO.searchScreenings();
			for (int i = screeningList.size() -1 ; i >= 0; i--) {
				// Gets the current screening's date
				LocalDate screeningDate = LocalDate.parse(screeningList.get(i).getDate());
				// Tests if the screening's date is in the past
				if (screeningDate.isBefore(LocalDate.now()) ||
					(screeningDate.isEqual(LocalDate.now()) &&
						// If the date is in the past, deletes the screening from the list
						screeningList.get(i).getTime() <= LocalTime.now().getHour())) {
					screeningList.remove(i);
				}
			}
			// Exports all remaining screenings
			export(screeningList);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes a List of Screening objects and exports them to a CSV file at a user specified location
	 *
	 * This method is a helper method for {@link #exportAllScreenings} and {@link #exportSelectedScreenings}
	 *
	 * @param screenings A list of Screening objects which will be exported
	 */
	private void export(List<Screening> screenings) {
		// Creates a new FileChooser
		FileChooser fileChooser = new FileChooser();
		// Adds an ExtensionFilter to the FileChooser to restrict output to .csv files
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
		// Sets the title of the FileChooser
		fileChooser.setTitle("Export Screenings to CSV");
		// Sets the initial filename for the FileChooser
		fileChooser.setInitialFileName("Exported_Screenings.csv");
		// Shows a save dialog
		File file = fileChooser.showSaveDialog(null);
		MainApp.LOGGER.info("Open file chooser");
		if (file != null) {
			//Opens a fileWriter
			try (FileWriter fileWriter = new FileWriter(file)) {
				//Creates a new StringBuilder which will hold the data for the CSV file
				StringBuilder tempStr = new StringBuilder();
				//Writes the first line containing column names
				tempStr.append("Movie Title,Date,Time,Total Seats,Booked Seats,Available Seats");
				//For each item in the selection, retrieves the relevant information and writes it into the StringBuilder
				for (Screening screening : screenings) {
					tempStr.append(System.lineSeparator());
					tempStr.append("" + MovieDAO.searchMovie(screening.getMovieID()).getName() + ",");
					tempStr.append("" + screening.getDate() + ",");
					tempStr.append("" + screening.getTime() + ":00,");
					tempStr.append("" + screening.getTotalSeats() + ",");
					tempStr.append("" + screening.getBookedSeats() + ",");
					tempStr.append("" + screening.getAvailableSeats());
				}
				//Writes the contents of the StringBuilder into the file and flushes the FileWriter
				fileWriter.write(tempStr.toString());
				fileWriter.flush();
			} catch (IOException | SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			MainApp.LOGGER.fine("Exported Screenings to CSV at " + file.getAbsolutePath());
		} else {
			MainApp.LOGGER.fine("Cancelled file selection");
		}
	}

}
