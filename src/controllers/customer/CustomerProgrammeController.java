package controllers.customer;

import application.MainApp;
import helpers.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import models.Screening;
import models.ScreeningDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for CustomerProgramme.fxml
 *
 * Provides the business logic for displaying a customized ListView of upcoming screenings,
 * to filter the displayed list of movies through text-search,
 * to filter the displayed list of movies through a date-picker,
 * and to give the view's navigational button their required functionality.
 */
public class CustomerProgrammeController {

    @FXML
    public ListView<Screening> lvScreenings;
    @FXML
    TextField tfSearch;
    @FXML
    DatePicker dpDate;
    @FXML
    ChoiceBox<String> choiceSort;
    @FXML
    Button btnBookSelected;

    private List<Screening> screeningList = new ArrayList<>();
    private ObservableList<Screening> observableList = FXCollections.observableArrayList();
    //Wrap the observableList in a filteredList to allow for the application of search filters
    private FilteredList<Screening> filteredList = new FilteredList<>(observableList, predicate -> true);

	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the ListView lvScreenings and the ChoiceBox choiceSort.
	 */
    @FXML
    private void initialize() {
        setListView();
	    // Adds the options "Date" and "Name" to the ChoiceBox choiceSort
	    choiceSort.getItems().addAll("Date", "Name");
	    choiceSort.getSelectionModel().selectFirst();
	    // Sets the format of the DatePicker to DD/MM/YYYY
	    dpDate.setConverter(new DateStringConverter());
    }

	/**
	 * This method filters the contents of lvScreenings by the contents of tfSearch
	 */
    @FXML
    private void search() {
	    // Disables the book selected button after every search
	    btnBookSelected.setDisable(true);
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
	 * This method filters the contents of lvScreenings by the date selected in the DatePicker dpDate
	 */
    @FXML
    private void filterByDatePicker() {
        filterByDate(dpDate.getValue());
    }

    /**
     * Removes Screenings from screeningList whose date is before the specified date
     *
     * @param filterDate the date that acts as a cutoff value for the filter
     */
    private void filterByDate(LocalDate filterDate) {
	    // Disables the book selected button after every filtering
	    btnBookSelected.setDisable(true);
    	// Repopulates the observableList with all screenings
        observableList.setAll(screeningList);
        // Only proceeds if a value has been chosen in the DatePicker
        if (!(dpDate.getValue() == null)) {
        	// Iterates through every element in the observableList
            for (int i = observableList.size() - 1; i >= 0; i--) {
                Screening screening = observableList.get(i);
                // Parses the screening's date String into a LocalDate
                LocalDate date = LocalDate.parse(screening.getDate());
                // Filters out all screenings whose date does not equal the filterDate
                if (!date.equals(filterDate)) {
                    observableList.remove(i);
                }
            }
        }
    }

    /**
     * Sets up the central ListView which displays all upcoming screenings with custom ListCells
     *
     * Based on: https://stackoverflow.com/questions/19588029/customize-listview-in-javafx-with-fxml
     */
    void setListView(){
	    // Only allows one item to be selected at any time
        lvScreenings.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Pulls all screenings from the database
        try {
            screeningList = ScreeningDAO.searchScreenings();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Removes Screenings from screeningList whose date is before today
        // This way, only upcoming screenings are displayed in the list
        for (int i = screeningList.size() - 1; i >= 0; i--) {
            Screening screening = screeningList.get(i);
            LocalDate date = LocalDate.parse(screening.getDate());
            int time = screening.getTime();
            if (date.isBefore(LocalDate.now()) ||
	            (date.isEqual(LocalDate.now()) && LocalTime.now().getHour() >= time)) {
                screeningList.remove(i);
            }
        }
        // Fills the observableList with the items just pulled from the database
        observableList.setAll(screeningList);
        // Wrap the filtered list in a sorted List that sorts by date and time from oldest to newest
        SortedList<Screening> sortedList = new SortedList<>(filteredList, new ScreeningDateComparator());
        // Fills the ListView with new content
        lvScreenings.setItems(sortedList);
        // Sets the CellFactory for this ListView to allow for a custom display of upcoming screenings
        lvScreenings.setCellFactory(listView -> new ListViewCell());
        // Adds an event handler that checks whether a list item is currently selected on mouse click
        // and key press and enables or disables interface elements accordingly
        lvScreenings.setOnMouseClicked(new SelectionListener(lvScreenings, btnBookSelected));
        lvScreenings.setOnKeyReleased(new SelectionListener(lvScreenings, btnBookSelected));
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

	/**
	 * Displays the CustomerProgrammeMovie view from CustomerProgrammeMovie.fxml in the center of customerRoot
	 */
	@FXML
    private void showProgrammeMovie() {
        try {
            FXMLLoader loader = new FXMLLoader();
            // Points the FXMLLoader to the view's filepath
            loader.setLocation(MainApp.class.getResource("/views/customer/CustomerProgrammeMovie.fxml"));
            // Loads the view
            AnchorPane customerProgrammeMovie = loader.load();
            // Retrieves the view's controller
            CustomerProgrammeMovieController controller = loader.getController();
            // Sets up the controller with the Screening currently selected in lvScreenings
            controller.setupView(lvScreenings.getSelectionModel().getSelectedItem());
            // Shows the customerProgrammeMovie view in the center of customerRoot
            MainApp.customerRoot.setCenter(customerProgrammeMovie);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
	private void clearDate() {
		dpDate.setValue(null);
		filterByDate(null);
    }

    @FXML
	private void clearSearch() {
		tfSearch.setText("");
		search();
    }
}
