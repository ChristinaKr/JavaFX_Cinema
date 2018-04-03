package controllers.employee;

import application.MainApp;
import controllers.shared.partials.CinemaRoomController;
import helpers.Helpers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import models.Movie;
import models.MovieDAO;
import models.Screening;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller class for EmployeeScreeningDetails.fxml
 *
 * Contains the business logic for navigation, and the setup of the associated view's fields and containing view
 *
 */
public class EmployeeScreeningDetailsController  {

	@FXML
	Pane paneCinemaRoom;
	@FXML
	Button btnBack;
	@FXML
	Label lblName, lblDate, lblTime, lblSeatTotal, lblSeatBooked, lblSeatAvailable;
	@FXML
	PieChart pieChart;


	private Screening screening;
	private Movie movie;

	/**
	 * Displays the employeeDashboard view in the center of the employeeRoot view
	 */
	@FXML
	private void back() {
		MainApp.employeeRoot.setCenter(MainApp.employeeDashboard);
	}


	/**
	 * Sets up the view by filling in data about the selected movie and calling showCinemaRoom.
	 *
	 */
	public void setup() {
		try {
			//Retrieves the movie associated with the screening from the database
			movie = MovieDAO.searchMovie(screening.getMovieID());
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		//Fills in the labels of the view with information about the movie and screening
		lblName.setText(movie.getName());
		lblDate.setText("" + Helpers.formatDateString(screening.getDate()));
		lblTime.setText("" + screening.getTime() + ":00");
		lblSeatTotal.setText("Total: " + screening.getTotalSeats());
		lblSeatAvailable.setText("Available: " + screening.getAvailableSeats());
		lblSeatBooked.setText("Booked: " + screening.getBookedSeats());

		//Adds data to the piechart in the view with two entries: Available Seats and Booked Seats
		//The information is retrieved from the screening object
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
			new PieChart.Data("Available Seats", screening.getAvailableSeats()),
			new PieChart.Data("Booked Seats", screening.getBookedSeats()));
		//Connects the data to the piechart
		pieChart.setData(pieChartData);
		pieChart.setStartAngle(0);
		//Displays the cinemaRoom view in the center of this controller's view
		showCinemaRoom();
	}

	/**
	 * Set the screening object of this controller
	 *
	 * @param screening The screening displayed in the view
	 */
	public void setScreening(Screening screening) {
		this.screening = screening;
	}

	/**
	 * Loads the CinemaRoom view and handles the setup of its fields
	 */
	private void showCinemaRoom() {
		CinemaRoomController controller;
		try {
			//Loads the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/shared/partials/CinemaRoom.fxml"));
			javafx.scene.Parent cinemaRoom = loader.load();
			controller = loader.getController();
			//Sets the screening object and triggers the correct display of seating information for this screening
			controller.setScreening(screening);
			controller.populateSeats();
			//Displays the view in paneCinemaRoom
			paneCinemaRoom.getChildren().add(cinemaRoom);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
