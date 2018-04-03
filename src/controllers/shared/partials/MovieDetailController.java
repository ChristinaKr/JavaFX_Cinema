package controllers.shared.partials;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import models.Movie;

import java.io.File;

/**
 * Controller class fo MovieDetail.fxml
 *
 * Contains the setup method that fills the view's fields with information about a movie
 */
public class MovieDetailController {

	@FXML
	ImageView imgMovieImage;
	@FXML
	Label lblMovieTitle, lblMovieDescription, lblGenre, lblYear, lblDirector, lblStarring;

	Movie movie;

	/**
	 * Sets up the associated view's fields with information about the selected movie
	 *
	 * @param movie the movie used to set up this view
	 */
	public void setup(Movie movie) {
		this.movie = movie;
		lblMovieTitle.setText(movie.getName());
		lblMovieDescription.setText(movie.getDescription());
		lblGenre.setText(movie.getGenre());
		lblYear.setText("" + movie.getYear());
		lblDirector.setText(movie.getDirector());
		lblStarring.setText(movie.getActors());
		//Loads an image form the /img/ folder into the image view imgMovieImage
		File file = new File(System.getProperty("user.dir") + "/img/" + movie.getImgPath());
		Image img = new Image(file.toURI().toString());
		imgMovieImage.setImage(img);
	}

	@FXML
	private void showTrailer() {
		// Creates a new stage
		Stage stage = new Stage();
		stage.setTitle(movie.getName() + " Trailer");
		BorderPane borderPane = new BorderPane();
		// Creates a new WebView that will display the movie trailer
		WebView trailer = new WebView();
		// Places the WebView in the BorderPane
		borderPane.setCenter(trailer);
		// Points the WebView to the Trailer's URL
		trailer.getEngine().load(movie.getTrailerURL());
		// Creates a new scene containing the BorderPane
		Scene scene = new Scene(borderPane);
		// Places the scene in the stage
		stage.setScene(scene);
		// Displays the stage
		stage.showAndWait();
	}

}
