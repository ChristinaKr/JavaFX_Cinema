package controllers.customer;

import application.MainApp;
import com.google.zxing.WriterException;
import helpers.DateStringConverter;
import helpers.Helpers;
import helpers.SelectionListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static helpers.QRCodeGenerator.createQRDetails;
import static models.CustomerDAO.searchCustomer;
import static models.CustomerDAO.updateCustomer;

/**
 * Controller class for CustomerProfile.fxml
 *
 * Contains methods for editing and saving customer details, for showing and filtering a list
 * of Bookings and for deleting Bookings from the database.
 */
public class CustomerProfileController {

	@FXML
	TextField tfFamilyName, tfFirstName, tfEmail, tfAddress, tfSearch;
	@FXML
	DatePicker dpBirthdate;
	@FXML
	CheckBox checkNewsletter;
	@FXML
	TableView<Booking> tvBookingHistory;
	@FXML
	TableColumn<Booking, String> tcDate, tcMovie, tcSeat;
	@FXML
	TableColumn<Booking, Integer> tcTime;
	@FXML
	Button btnEdit, btnCancel, btnDelete, btnPrint;

	private Customer customer;

	private ObservableList<Booking> bookingList = FXCollections.observableArrayList();
	private FilteredList<Booking> filteredList = new FilteredList<>(bookingList, predicate -> true);


	/**
	 * Standard JavaFX method
	 * <p>
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized. It populates the controls that display information about the
	 * currently logged in customer with information about said customer and adds SelectionListeners
	 * to the view's TableView that disables the print and delete buttons if no item is selected and
	 * enables them otherwise.
	 */
	@FXML
	private void initialize() {
		// Sets the format of the DatePicker to DD/MM/YYYY
		dpBirthdate.setConverter(new DateStringConverter());
		try {
			// Retrieve a customer from the database based on the currently logged in user's username
			customer = searchCustomer(MainApp.username);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		// Populate the controls that display information about the currently logged in customer
		tfFamilyName.setText(customer.getFamilyname());
		tfFirstName.setText(customer.getFirstname());
		tfEmail.setText(customer.getEmail());
		tfAddress.setText(customer.getAddress());
		dpBirthdate.setValue(LocalDate.parse(customer.getBirthdate()));
		checkNewsletter.setSelected(customer.getNewsletter() == 1);
		// Adds SelectionListeners to the view's TableView that disables the print and delete buttons
		// if no item is selected and enables them otherwise
		tvBookingHistory.setOnMouseClicked(new SelectionListener(tvBookingHistory, btnDelete, btnPrint));
		tvBookingHistory.setOnKeyPressed(new SelectionListener(tvBookingHistory, btnDelete, btnPrint));
		// Sets up the TableView that displays the customer's booking history
		setupBookingHistory();
	}

	/**
	 * Performs input verification and updates the currently logged in customer's database entry
	 * <p>
	 * This method retrieves the content of the input fields on the associated view
	 * and performs existence tests and a test for the syntactical correctness of the
	 * provided e-Mail address. If all tests pass, the logged-in Customer's database entry
	 * is updated with the data from the view's controls.
	 */
	@FXML
	private void saveProfileInfo() {
		// Regular expression that matches eMail addresses
		// adapted from http://emailregex.com
		String eMailRegex = "(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." +
			"[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c" +
			"\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b" +
			"\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-zA-Z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)" +
			"+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|" +
			"[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|" +
			"[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a" +
			"\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

		// Verifies that all fields are filled
		if (tfFamilyName.getText().isEmpty() ||
				tfFirstName.getText().isEmpty() ||
				tfEmail.getText().isEmpty() ||
				tfAddress.getText().isEmpty() ||
				// Verifies that a value has been chosen in the date picker
				dpBirthdate.getValue() == null) {
			// Displays a warning popup if any of the input fields were left blank
			Alert alert = new Alert(Alert.AlertType.WARNING,"Please fill " +
				"in all fields.");
			alert.setHeaderText("Incomplete Information");
			alert.setTitle("Warning");
			alert.showAndWait();
			// Verifies that the provided eMail address has a valid format
		} else if (!tfEmail.getText().matches(eMailRegex)) {
			// Displays a warning popup if the email does not match the regular expression
			Alert alert = new Alert(Alert.AlertType.WARNING,"Please provide " +
				"a valid email address.");
			alert.setHeaderText("Incorrect Email");
			alert.setTitle("Warning");
			alert.showAndWait();
		// Verifies that the provided birthdate is not in the future
		} else if (dpBirthdate.getValue().isAfter(LocalDate.now())) {
			// Displays a warning popup
			Alert alert = new Alert(Alert.AlertType.WARNING, "Sorry, we do not serve time travellers!");
			alert.setHeaderText("Greetings from the past");
			alert.setTitle("Warning");
			alert.showAndWait();
		// Verifies that the provided birthdate is at least 16 years in the past
		} else if (dpBirthdate.getValue().isAfter(LocalDate.now().minusYears(16))) {
			// Displays a warning popup
			Alert alert = new Alert(Alert.AlertType.WARNING, "You have to be " +
				"at least 16 years old to make an account.");
			alert.setHeaderText("Invalid age");
			alert.setTitle("Warning");
			alert.showAndWait();
		} else {
			// Replaces the information in the controller's customer object with the new information
			customer.setFamilyname(tfFamilyName.getText());
			customer.setFirstname(tfFirstName.getText());
			customer.setAddress(tfAddress.getText());
			customer.setBirthdate(dpBirthdate.getValue().toString());
			customer.setEmail(tfEmail.getText());
			customer.setNewsletter(checkNewsletter.isSelected() ? 1 : 0);
			try {
				// Updates the customer's database entry
				updateCustomer(customer);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// Prevents the view's input fields from being edited
			tfFamilyName.setEditable(false);
			tfFirstName.setEditable(false);
			tfEmail.setEditable(false);
			tfAddress.setEditable(false);
			dpBirthdate.setDisable(true);
			checkNewsletter.setDisable(true);

			// Sets the edit button's text to "Edit"
			btnEdit.setText("Edit");
			// On action the edit button now performs editProfile()
			btnEdit.setOnAction(event -> editProfile());
			// Hides the cancel button
			btnCancel.setVisible(false);
		}
	}

	/**
	 * Makes the view's input fields editable and changes the edit button to a save button
	 */
	@FXML
	private void editProfile() {
		// Makes the view's input fields editable
		tfFamilyName.setEditable(true);
		tfFirstName.setEditable(true);
		tfEmail.setEditable(true);
		tfAddress.setEditable(true);
		dpBirthdate.setDisable(false);
		checkNewsletter.setDisable(false);
		// Changes the edit button's text to "save"
		btnEdit.setText("Save");
		// On action the edit button now performs saveProfileInfo()
		btnEdit.setOnAction(event -> saveProfileInfo());
		// Makes the cancel button visible
		btnCancel.setVisible(true);
	}

	/**
	 * Reverts changes to the view's input fields to values from the controller's customer object
	 * and makes input fields non-editable.
	 */
	@FXML
	private void cancel() {
		//Restore prior customer values
		tfFamilyName.setText(customer.getFamilyname());
		tfFirstName.setText(customer.getFirstname());
		tfEmail.setText(customer.getEmail());
		tfAddress.setText(customer.getAddress());
		dpBirthdate.setValue(LocalDate.parse(customer.getBirthdate()));
		checkNewsletter.setSelected(customer.getNewsletter() == 1);
		// Disable editing of the view's input fields
		tfFamilyName.setEditable(false);
		tfFirstName.setEditable(false);
		tfEmail.setEditable(false);
		tfAddress.setEditable(false);
		dpBirthdate.setDisable(true);
		checkNewsletter.setDisable(true);
		// Sets the edit button's text to "Edit"
		btnEdit.setText("Edit");
		// On action the edit button now performs editProfile()
		btnEdit.setOnAction(event -> editProfile());
		// Hides the cancel button
		btnCancel.setVisible(false);
	}

	/**
	 * This method filters the contents of tvBookingHistory by the contents of tfSearch via movie names
	 */
	@FXML
	private void search() {
		// Disables the print and delete buttons after every search
		btnPrint.setDisable(true);
		btnDelete.setDisable(true);
		//Retrieves the content of tfSearch
		String searchString = tfSearch.getText();
		//Sets the predicate of the filteredList which provides the content for tvBookingHistory
		//This predicate acts as the filter which determines which items are displayed in
		//the TableView and which are not
		filteredList.setPredicate(booking -> {
			//If the search field is empty, all list items are displayed
			if (searchString == null || searchString.isEmpty()) {
				return true;
			}
			//The string is put into all lower case to make it case-insensitive
			String lowerCaseValue = searchString.toLowerCase();
			//Similarly, the movie titles against which it is compared are put into lower case as well
			if (booking.getScreening().getMovie().getName().toLowerCase().contains(lowerCaseValue)) {
				return true;
			}
			//If the search field is not null/empty and the screening in the list does not match the predicate
			//the screening is not displayed
			return false;
		});
	}

	/**
	 * Deletes the currently selected booking(s) from the database and refreshes the displayed table
	 */
	@FXML
	private void deleteBookings() {
		// Retrieves the selected booking
		Booking selectedBooking = tvBookingHistory.getSelectionModel().getSelectedItem();
		// Parses the booking's date
		LocalDate bookingDate = LocalDate.parse(selectedBooking.getScreening().getDate());
		// Retrieves the booking's time
		int bookingTime = selectedBooking.getScreening().getTime();
		// Displays a warning message if the booking is in the past
		if (bookingDate.isBefore(LocalDate.now()) ||
			(bookingDate.isEqual(LocalDate.now()) && LocalTime.now().getHour() >= bookingTime)) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Your selected " +
				"booking is in the past and can not be deleted.");
			alert.setTitle("Information");
			alert.setHeaderText("Booking is in the past");
			alert.showAndWait();
		} else {
			// Displays a confirmation dialogue to ensure the user wants to delete the selected booking
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to permanently " +
			"delete this booking?",	ButtonType.YES, ButtonType.NO);
			alert.setTitle("Deletion Confirmation");
			alert.setHeaderText("Delete bookings");
			// Only deletes if the confirmation dialogue is confirmed
			if (alert.showAndWait().get() == ButtonType.YES) {
				try {
					// Removes the bookings from the database
					BookingDAO.deleteBooking(selectedBooking);
					// Retrieve the seat list of the screening that the booking was for
					ArrayList<Seat> screeningSeatList = selectedBooking.getScreening().getSeatList();
					// Makes the booking's seats available again in the screening
					for (Seat seat : selectedBooking.getSeatList()) {
						// Searches for the corresponding seat in the screening
						int seatIndex = screeningSeatList.indexOf(seat);
						// Makes the seat available for new bookings
						screeningSeatList.get(seatIndex).setBooked(false);
						// Updates the screening in the database
						ScreeningDAO.updateScreening(selectedBooking.getScreening());
						// Refreshes the programme list
						MainApp.customerProgrammeController.setListView();
					}
				} catch (SQLException e) {
				e.printStackTrace();
				}
				// Removes the selected booking from the table
				bookingList.remove(selectedBooking);

				// Disable the print and delete buttons
				btnPrint.setDisable(true);
				btnDelete.setDisable(true);
			}
		}
	}

	/**
	 * Refreshes contents of the TableView tvBookingHistory
	 */
	void refreshBookingHistory() {
		try {
			// Clears out the list that provides data for the TableView
			bookingList.clear();
			// Refills the list with bookings for the currently logged-in user from the database
			bookingList.addAll(BookingDAO.searchBookings(customer.getUsername()));
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fills the TableView tvBookingHistory with bookings from the database
	 */
	private void setupBookingHistory() {
		// Queries bookings from the database
		refreshBookingHistory();
		// Sets the cell values for each table column to properties of the booking and its related screening and movie
		tcDate.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
		tcTime.setCellValueFactory(new PropertyValueFactory<>("formattedTime"));
		tcMovie.setCellValueFactory(cellData -> cellData.getValue().getScreening().getMovie().nameProperty());
		// Populates the table column tcSeat with the output of the method formattedSeatList() from the Booking class
		tcSeat.setCellValueFactory(new PropertyValueFactory<>("formattedSeatList"));
		// Populates the tableview's columns with information about the bookings
		// Links the TableView tvBookingHistory to filteredList
		tvBookingHistory.setItems(filteredList);
	}


	/**
	 * Displays the ticket for the currently selected booking and opens a print dialogue
	 */
	@FXML
	public void showTicketView() {
		// Retrieves the currently selected booking
		Booking selectedBooking = tvBookingHistory.getSelectionModel().getSelectedItem();
		try {
			// Creates the QR code with information about the username, bookingID and movie name
			createQRDetails(selectedBooking.getUsername() + " " + selectedBooking.getBookingID() + " "
				+ selectedBooking.getScreening().getMovie().getName());
		} catch (IOException | WriterException e) {
			e.printStackTrace();
		}
		// Displays the ticket
		AnchorPane ticket = Helpers.openTicketView(selectedBooking);
		// Attempts to print
		Helpers.printTicket(ticket);
	}
}


