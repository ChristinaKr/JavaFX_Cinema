package controllers.shared.partials;

import application.MainApp;
import controllers.customer.CustomerProgrammeMovieController;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import models.Screening;
import models.Seat;

import java.util.ArrayList;

/**
 * Controller class for CinemaRoom.fxml
 *
 * Contains the business logic for displaying the availability of the cinema's seats
 * and contains an EventHandler which handles customer seat selection through mouse clicks
 */
public class CinemaRoomController {

	@FXML
	Pane paneSeats;

	private Screening screening;

	private CustomerProgrammeMovieController customerProgrammeMovieController;
	private ObservableList<Node> cinemaSeats;
	private ArrayList<Seat> selectedSeats = new ArrayList<>();
	private ArrayList<Seat> seats;

	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the ObservableList cinemaSeats with the children
	 * of paneSeats. This is done because paneSeats contains the ImageView objects which represent
	 * the seats in the cinema.
	 */
	@FXML
	private void initialize() {
		cinemaSeats = paneSeats.getChildren();
	}

	/**
	 * Getter method for the ArrayList selectedSeats
	 * @return an ArrayList of all currently selected seats
	 */
	public ArrayList<Seat> getSelectedSeats() {
		return selectedSeats;
	}

	/**
	 * Setter method for this view's screening object
	 * @param screening The screening object of this view
	 */
	public void setScreening(Screening screening) {
		this.screening = screening;
	}

	/**
	 * Setter method for the instance of the CustomerProgrammeMovie view's controller
	 * @param customerProgrammeMovieController The controller of the CustomerProgrammeMovie view
	 */
	public void setCustomerProgrammeMovieController(CustomerProgrammeMovieController customerProgrammeMovieController) {
		this.customerProgrammeMovieController = customerProgrammeMovieController;
	}

	/**
	 * Handles the display of graphical seat availability information and adds an EventHandler to each seat
	 */
	public void populateSeats() {
		seats = screening.getSeatList();
		//Creates the default image for all seats in the cinema
		Image imgSeat = new Image("file:" + System.getProperty("user.dir") + "/img/seat/seat.png");
		//In the associated CinemaRoom view, the seats are represented by ImageViews with accompanying labels
		//All ImageViews are listed first, in order, followed by all labels
		//Therefore the first half of cinemaSeats contains all ImageViews representing all seats
		for (int i = 0; i < cinemaSeats.size()/2; i++) {
			//Sets the default image for every seat in the view
			((ImageView)cinemaSeats.get(i)).setImage(imgSeat);
			if (!MainApp.isEmployee) {
				//If the currently logged-in user is a customer, a SeatSelectionHandler is added to each seat
				cinemaSeats.get(i).setOnMouseClicked(new SeatSelectionHandler());
			}
			if (seats.get(i).isBooked()) {
				//Lowers the opacity for booked seats
				cinemaSeats.get(i).setOpacity(0.4);
			} else {
				//Sets the opacity of empty seats to 1
				cinemaSeats.get(i).setOpacity(1);
			}
		}
	}

	/**
	 * Private class that handles the selection of seats via mouse clicks
	 *
	 * Displays a different image for the associated seat based on whether it is still available
	 * and whether the user has already added it to its selection or not. If the seat is not yet selected,
	 * it is added to the selection list, otherwise it is removed. The displayed image changes accordingly.
	 *
	 * This EventHandler was placed in its own private class instead of in an anonymous class
	 * for better code readability and clarity.
	 */
	private class SeatSelectionHandler implements EventHandler{
		@Override
		public void handle(Event event) {
			//Gets the index number which the clicked-on ImageView has in paneSeats in the associated view
			//This index number signifies the location of the seat in the cinema
			int seatNum = cinemaSeats.indexOf(event.getTarget());
			//Creates a copy of the correlating seat from the screening object's seats list.
			//A copy is used instead of the screening objet's actual seat object to facilitate the
			//cancellation of the current selection should a user decide not to complete the booking
			Seat selectedSeat = seats.get(seatNum).clone();
			//Sets the image for selected seats
			Image imgSeatSelected = new Image("file:" + System.getProperty("user.dir") + "/img/seat/seat_selected.png");
			//Sets the image for not-selected seats
			Image imgSeat = new Image("file:" + System.getProperty("user.dir") + "/img/seat/seat.png");
			//Only allows actions to be performed on still available seats
			if (!selectedSeat.isBooked()) {
				//Checks whether the clicked-on seat is already in the list of selected seats
				if (selectedSeats.contains(selectedSeat)) {
					//Repaints the seat
					((ImageView)event.getTarget()).setImage(imgSeat);
					//Removes the seat from selection
					selectedSeats.remove(selectedSeat);
					//Makes the seat available again
					selectedSeat.setBooked(false);
					//Updates a label that displays the number of currently selected seats
					customerProgrammeMovieController.updateLabels(selectedSeats);
				} else {
					//Repaints the seat
					((ImageView)event.getTarget()).setImage(imgSeatSelected);
					//Adds a clone of the seat to selection
					selectedSeats.add(selectedSeat);
					//Makes the seat unavailable
					selectedSeat.setBooked(true);
					//Updates a label that displays the number of currently selected seats
					customerProgrammeMovieController.updateLabels(selectedSeats);
				}
			}
		}
	}
}
