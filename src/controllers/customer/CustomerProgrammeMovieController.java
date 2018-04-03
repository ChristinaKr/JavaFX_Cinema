package controllers.customer;

import application.MainApp;
import controllers.shared.partials.CinemaRoomController;
import helpers.Helpers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Controller class for CustomerProgrammeMovie.fxml
 *
 * Provides the methods for setting up the cinema room with information from a screening object,
 * and for triggering the creation of a new booking entry in the database based on customer selections.
 *
 */
public class CustomerProgrammeMovieController {

	@FXML
	Label lblName, lblDate, lblTime, lblSeats, lblPrice, lblPriceCalc;
	@FXML
	Pane paneCinemaRoom;
	@FXML
	Button btnConfirm;

	private CinemaRoomController cinemaRoomController;
	private Screening screening;


	/**
	 * Sets up the view with information from a provided Screening object
	 *
	 * @param screening the screening which is displayed in the controller's view
	 */
    void setupView(Screening screening) {
    	this.screening = screening;
    	// Retrieves the movie object from the screening
        Movie movie = screening.getMovie();
        // Fills in labels in the view
        lblName.setText(movie.getName());
		lblDate.setText(Helpers.formatDateString(screening.getDate()));
		lblTime.setText(screening.getTime() + ":00");
		// Shows a panel which represents the cinema room and the current status of its seats
		setupCinemaRoom();
    }

	/**
	 * Shows a panel which represents the cinema room and the current status of its seats
	 */
	private void setupCinemaRoom() {
    	try {
		    FXMLLoader loader = new FXMLLoader();
		    // Points the FXMLLoader to the view's filepath
		    loader.setLocation(MainApp.class.getResource("/views/shared/partials/CinemaRoom.fxml"));
		    // Loads the view
		    BorderPane cinemaRoom = loader.load();
		    // Retrieves the view's controller
		    cinemaRoomController = loader.getController();
		    // Sets the screening and parent controller of the view's controller
		    cinemaRoomController.setScreening(screening);
		    cinemaRoomController.setCustomerProgrammeMovieController(this);
		    // Triggers the visualization of seat availability in the new view
		    cinemaRoomController.populateSeats();
		    // Displays the new view in the Panel paneCinemaRoom
		    paneCinemaRoom.getChildren().add(cinemaRoom);
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
    }

	/**
	 * Updates two Labels in the view with the amount of currently selected seats and the resulting price
	 *
	 * @param selectedSeats an ArrayList of currently selected seats
	 */
	public void updateLabels(ArrayList<Seat> selectedSeats) {
    	lblSeats.setText(Helpers.formatSeatList(selectedSeats));
    	lblPriceCalc.setText(selectedSeats.size() + " x £ 8.00");
    	lblPrice.setText("£ " + selectedSeats.size() * 8 + ".00");
    }

	/**
	 * Checks whether at least one seat has been chosen and then creates a new booking which is added to the database
	 */
	@FXML
    private void confirmBooking() {
		// Retrieves the selected seats
	    ArrayList<Seat> selectedSeatList = cinemaRoomController.getSelectedSeats();
	    // If no seats have been selected displays a warning and stops the method
	    if (selectedSeatList.size() == 0) {
	    	new Alert(Alert.AlertType.INFORMATION, "Please select at least one seat " +
			    "before continuing!").showAndWait();
	    	return;
	    }
	    // Creates a new booking with information about the current screening and logged-in user
	    Booking booking = new Booking();
	    booking.setUsername(MainApp.getUsername());
	    booking.setScreenID(screening.getScreenID());
	    booking.setScreening(screening);
		// Writes the seats that the user selected into the booking so that they can be saved for future reference
	    booking.setSeats(selectedSeatList);
	    // Mark the selected seats as booked in the screening
	    for (Seat seat : selectedSeatList) {
	    	int seatIndex = screening.getSeatList().indexOf(seat);
	    	screening.getSeatList().get(seatIndex).setBooked(true);
	    }
	    try {
		    // Updates the Screening entry in the database with the new seat situation
		    ScreeningDAO.updateScreening(screening);
		    // Hands the newly created Booking to the Database
		    BookingDAO.addBooking(booking);
	    } catch (SQLException e) {
		    e.printStackTrace();
	    }
		// Updates the ListView in the CustomerProgramme view
	    MainApp.customerProgrammeController.setListView();
	    // Updates the user's booking history table
	    MainApp.customerProfileController.refreshBookingHistory();
	    // Shows a summary of the booking in a new scene
	    try {
		    FXMLLoader loader = new FXMLLoader();
		    // Points the FXMLLoader to the view's filepath
		    loader.setLocation(MainApp.class.getResource("/views/customer/CustomerBookingSummary.fxml"));
		    // Loads the view
		    AnchorPane customerBookingSummary = loader.load();
		    // Retrieves the view's controller
		    CustomerBookingSummaryController controller = loader.getController();
		    // Sets up the controller with the new booking
			controller.setup(booking);
		    // Shows the customerBookingSummary view in the center of customerRoot
		    MainApp.customerRoot.setCenter(customerBookingSummary);
	    } catch (IOException e) {
		    e.printStackTrace();
	    }
    }

	/**
	 * Shows the CustomerProgramme view in the center of customerRoot
	 */
	@FXML
	private void cancel() {
    	MainApp.customerRoot.setCenter(MainApp.customerProgramme);
    }
}
