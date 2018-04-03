package controllers.employee;

import application.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import models.Movie;
import models.MovieDAO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Controller class for EmployeeAddMovie.fxml
 *
 * This controller handles the business logic of adding a movie
 * to the database. All required input is gathered from the input fields
 * of the associated EmployeeAddMovie view. The specified movie poster
 * is copied to the /img/ path in the working directory and the final movie
 * object is stored in the database.
 */
public class EmployeeAddMovieController {

	@FXML
	TextField tfTitle, tfGenre, tfDirector, tfActor1, tfActor2, tfActor3, tfTrailerURL;
	@FXML
	TextArea taDescription;
	@FXML
	ComboBox<Integer> cbYear;
	@FXML
	ImageView ivImage;
	@FXML
	Label lblCharacters;

	private String imgPath = "";
	private File imageFile;

	private String trailerURLRegEx = "^((https?|ftp|smtp):\\/\\/)?(www.)?[a-z0-9]+\\.[a-z]+(\\/[a-zA-Z0-9#]+\\/?)*$";

	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and sets up the year selection in the combobox cbYear.
	 */
	@FXML
	private void initialize() {
		setupCBYear();
	}

	/**
	 * This method handles clicking on the cancel button in the associated view by displaying
	 * the employeeDashboard in the center of the employeeRoot view.
	 */
	@FXML
	private void cancel() {
		MainApp.employeeRoot.setCenter(MainApp.employeeMovieList);
	}

	/**
	 * This method performs input/existence verification on the information provided by the user and saves the movie.
	 *
	 * The provided image is copied into the /img/ folder in the working directory and the newly created movie is
	 * added to the database for storage and future use.
	 */
	@FXML
	private void done() {
		//Ensures that all fields are filled in
		if (tfTitle.getText().isEmpty() ||
			taDescription.getText().isEmpty() ||
			tfGenre.getText().isEmpty() ||
			tfDirector.getText().isEmpty() ||
			tfActor1.getText().isEmpty() ||
			cbYear.getSelectionModel().getSelectedItem() == null ||
			imgPath.isEmpty() ||
			tfTrailerURL.getText().isEmpty()) {
			//Displays a warning popup message if not all information has been supplied
			Alert alert = new Alert(Alert.AlertType.WARNING,
				"Please fill in all required fields and select an image.");
			alert.setTitle("Warning");
			alert.setHeaderText("Incomplete Information");
			alert.showAndWait();
		} else if (!tfTrailerURL.getText().matches(trailerURLRegEx)) {
			//Displays a warning popup message if the provided URL is not valid
			Alert alert = new Alert(Alert.AlertType.WARNING,
				"Please enter a valid URL.");
			alert.setTitle("Warning");
			alert.setHeaderText("Invalid URL");
			alert.showAndWait();
		} else if (taDescription.getText().length() > 300) {
			//Displays a warning popup message if the entered description is too long
			Alert alert = new Alert(Alert.AlertType.WARNING,
				"Please enter a description of maximum 300 characters.");
			alert.setTitle("Warning");
			alert.setHeaderText("Description too long");
			alert.showAndWait();
		} else {
			//Creates a new movie and fills it with the information from the input fields
			Movie movie = new Movie();
			movie.setName(tfTitle.getText());
			movie.setDescription(taDescription.getText());
			movie.setGenre(tfGenre.getText());
			movie.setDirector(tfDirector.getText());
			//Concatenates the three actor Strings into one newline-separated string
			String actorsString = tfActor1.getText() + '\n'
				+ tfActor2.getText() + '\n'
				+ tfActor3.getText();
			movie.setActors(actorsString);
			movie.setYear(cbYear.getValue());
			movie.setTrailerURL(tfTrailerURL.getText());

			//Copies the image to the /img/ Folder in the working directory
			//Creates a destination file object as a destination for the upcoming copy action
			File destinationFile = new File(System.getProperty("user.dir") + "/img/" + imageFile.getName());
			try {
				//Copies the image file from the specified source to the destination
				Files.copy(imageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Sets the imgPath property to the filename of the new image
			imgPath = destinationFile.getName();
			movie.setImgPath(imgPath);

			try {
				//Adds the movie to the database
				MovieDAO.addMovie(movie);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//Displays the employeeDashboard in the center of EmployeeRoot
			MainApp.employeeRoot.setCenter(MainApp.employeeDashboard);
		}
	}

	/**
	 * Displays a FileChooser to specify the source of the new image file
	 */
	@FXML
	private void addImage() {
		//Creates a new FileChooser
		FileChooser fileChooser = new FileChooser();
		//Sets the ExtensionFilter so that only certain image file formats can be chosen
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
			"Image Files", "*.jpg", "*.jpeg", "*.png", "*.bmp"));
		//Opens the FileChooser and saves the selected file into a variable
		imageFile = fileChooser.showOpenDialog(null);
		//Only proceeds if a file has in fact been chosen
		if (imageFile != null) {
			//Displays the newly chosen file in the current view
			ivImage.setImage(new Image("file:" + imageFile.getPath()));
			//Makes the imgPath non-empty to fulfill completeness assertions when attempting to save with done()
			imgPath = "set";
		}
	}

	/**
	 * Prevents the TextArea taDescription from containing more than 300 characters and updates an informational label
	 */
	@FXML
	private void countCharacters() {
		// Test if taDescription contains more than 300 characters
		if (taDescription.getText().length() >= 300) {
			// Sets the content of taDescription to the first 300 characters
			taDescription.setText(taDescription.getText(0, 300));
			// Positions the caret to the end of the textArea
			taDescription.positionCaret(300);
		}
		// Updates a label
		lblCharacters.setText(300 - taDescription.getText().length() + "/300 characters left");
	}

	/**
	 * Populates the ComboBox cbYear with date values from the current year until 1970
	 */
	private void setupCBYear() {
		int currentYear = LocalDate.now().getYear();
		for (int i = currentYear; i >= 1970; i --) {
			cbYear.getItems().add(i);
		}
	}
}
