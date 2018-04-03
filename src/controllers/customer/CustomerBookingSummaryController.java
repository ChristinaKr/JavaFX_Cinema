package controllers.customer;

import application.MainApp;
import com.google.zxing.WriterException;
import helpers.Helpers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import models.Booking;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static helpers.QRCodeGenerator.createQRDetails;

/**
 * Controller class for CustomerBookingSummary.fxml
 *
 * Provides the business logic for the CustomerBookingSummary view via a
 * setup method that fills the view's controls with content from
 * the Booking object the method is provided with.
 */
public class CustomerBookingSummaryController {

	@FXML
	Label lblMovie, lblDate, lblTime, lblSeats, lblPrice;

	private Booking booking;

	/**
	 * Fills the view's controls with content from the Booking object
	 *
	 * @param booking the booking object whose information is to be displayed
	 */
    public void setup(Booking booking) {
    	this.booking = booking;
		lblMovie.setText(booking.getScreening().getMovie().getName());
		// Displays the day of the week and formatted date for the booked screening
		lblDate.setText(LocalDate.parse(booking.getScreening().getDate()).getDayOfWeek()
			.getDisplayName(TextStyle.SHORT, Locale.UK)
			+ " " + Helpers.formatDateString(booking.getScreening().getDate()));
		lblTime.setText(booking.getScreening().getTime() + ":00");
		lblSeats.setText(booking.getFormattedSeatList());
		lblPrice.setText("£ " + booking.getSeatList().size() * 8);
    }

	/**
	 * Changes the center of CustomerRoot to the  CustomerProgramme view
	 */
	@FXML
    public void closeBookingSummary() {
        MainApp.customerRoot.setCenter(MainApp.customerProgramme);
    }

    @FXML
	private void printTicket() {
	    try {
		    // Creates the QR code with information about the username, bookingID and movie name
		    createQRDetails(booking.getUsername() + " " + booking.getBookingID() + " "
			    + booking.getScreening().getMovie().getName());
	    } catch (IOException | WriterException e) {
		    e.printStackTrace();
	    }
	    // Displays the ticket
	    AnchorPane ticket = Helpers.openTicketView(booking);
	    // Attempts to print
	    Helpers.printTicket(ticket);
    }
}
