package helpers;

import application.MainApp;
import controllers.customer.CustomerTicketController;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Booking;
import models.Seat;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class contains a collection of static helper methods
 * used in various places throughout the application.
 */
public class Helpers {

	/**
	 * This method logs the currently logged in user out of the application and shows the login screen
	 */
	public static void logout() {
		String username = MainApp.username;
		// Sets the username to a blank string
		MainApp.setUsername("");
		// Closes the primary stage displaying the running application
		MainApp.primaryStage.close();
		MainApp.LOGGER.info("Logged out " + username);
		// Shows the login window
		MainApp.showLogin();
	}

	/**
	 * Takes a date String of the format YYYY-MM-DD and outputs it as DD/MM/YYYY
	 *
	 * @param dateString the unformatted date String
	 * @return the date formatted as DD/MM/YYYY
	 */
	public static String formatDateString(String dateString) {
		String[] strArray = dateString.split("-");
		return strArray[2] + "/"
			+ strArray[1] + "/"
			+ strArray[0];
	}

	/**
	 * Opens a new window displaying the currently selected booking's ticket
	 *
	 * The ticket contains details about the booking as well as a QR code
	 */
	public static AnchorPane openTicketView(Booking booking) {
		FXMLLoader loader = new FXMLLoader();
		// Points the FXMLLoader to the view's filepath
		loader.setLocation(MainApp.class.getResource("/views/customer/CustomerTicket.fxml"));
		AnchorPane customerTicket = null;
		try {
			// Loads the view
			customerTicket = loader.load();
			// Retrieves the view's controller
			CustomerTicketController customerTicketController = loader.getController();
			// Sets up the controller with the booking currently selected in tvBookingHistory
			customerTicketController.setup(booking);

			// Displays a new window containing the ticket
			Stage ticketStage = new Stage();
			Scene ticketScene = new Scene(customerTicket);
			ticketScene.getStylesheets().add(MainApp.class.getResource("/application/stylesheet.css").toExternalForm());
			ticketStage.setScene(ticketScene);
			ticketStage.show();
			MainApp.LOGGER.info("Showing the customer's ticket");

		} catch (IOException e) {
			e.printStackTrace();
		}
		return customerTicket;
	}

	/**
	 * Attempts to print the concurrently opened ticket using a PrinterJob.
	 * If no printer can be located, displays a warning message.
	 */
	public static void printTicket(Parent parent) {
		// Creates a new PrinterJob
		PrinterJob job = PrinterJob.createPrinterJob();
		// If a printer could be located...
		if (job != null) {
			// Displays a print dialogue for the customerTicket's window
			MainApp.LOGGER.info("Showing print dialogue");
			job.showPrintDialog(parent.getScene().getWindow());
			// On successful print, ends the PrintJob
			boolean success = job.printPage(parent);
			if (success) {
				job.endJob();
			}
			// If no printer could be located...
		} else {
			// Displays an informational popup
			new Alert(Alert.AlertType.INFORMATION,
				"No printers could be located on your computer.").showAndWait();
			MainApp.LOGGER.warning("Could not find any printers");
		}
	}

	/**
	 * Returns a String of all seats, formatted good human readability
	 *
	 * @return a well-formatted list of all seats associated with this booking
	 */
	public static String formatSeatList(ArrayList<Seat> seatList) {
		String returnString = "";
		for (Seat seat : seatList) {
			returnString += seat + ", ";
		}
		// Return formatted string if its length is at least 2
		if (returnString.length() >= 2) {
			// Two characters are cut off the end to avoid a trailing comma and whitespace
			return returnString.substring(0, returnString.length() - 2);
		}
		// Otherwise return an empty String
		return "";
	}
}
