package controllers.shared;

import application.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Account;
import models.AccountDAO;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller class for Login.fxml
 *
 * Handles the business logic for the login process and showing the sign up view
 */
public class LoginController {

	@FXML
	TextField tfUsername;
	@FXML
	PasswordField pfPassword;
	@FXML
	Label lblWarning;
	@FXML
	ImageView imageLogo;


	/**
	 * Standard JavaFX method
	 *
	 * This method is called after the constructor has been called and all @FXML fields
	 * have been initialized and places the JavaCinema logo into the Login view.
	 */
	@FXML
	private void initialize() {
		imageLogo.setImage(new Image("file:" + System.getProperty("user.dir") + "/img/Logo.png"));
	}

	/**
	 * Compares the entered username and password with the database and processes login
	 *
	 * The entered username is searched for in the database. If a match is found, the
	 * password of the database match is compared to the entered password.
	 * If the password matches the username, a check is performed whether the user
	 * is an employee or a customer. The respective view is subsequently displayed.
	 */
	@FXML
	private void login() {
		//Resets the warning label
		lblWarning.setText("");
		//Pulls text from the entry fields in the login view
		String username = tfUsername.getText();
		String password = pfPassword.getText();
		Account account = null;

		//Searches for the username in the database
		try {
			account = AccountDAO.searchAccount(username);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		//If no matching account is found, displays a warning message
		if (account == null) {
			lblWarning.setText("Username could not be found");
			MainApp.LOGGER.warning("Unable to find user " + username);
		//If a matching account is found, compares the entered password with that of the matched account
		} else if (account.getPassword().equals(password)) {
			switch (account.getEmployee()) {
				//Displays the correct scene depending on the account type
				case 0:
					MainApp.loginStage.close();
					MainApp.setUsername(username);
					MainApp.startAsCustomer();
					break;
				case 1:
					MainApp.loginStage.close();
					MainApp.setUsername(username);
					MainApp.startAsEmployee();
					break;
			}
		//If the password does not match that of the matched account, displays a warning.
		} else {
			lblWarning.setText("Incorrect Password");
			MainApp.LOGGER.warning("User " + username + " tried to log in with password " + password + "and failed.");
		}
	}

	/**
	 * Loads a new stage containing the sign up view from SignUp.fxml and performs a setup method
	 */
	@FXML
	private void signup() {
		// Creates a new FXMLLoader
		FXMLLoader loader = new FXMLLoader();
		// Sets the location of the .FXML file
		loader.setLocation(MainApp.class.getResource("/views/shared/SignUp.fxml"));
		AnchorPane signup;
		try {
			// Loads the FXML file
			signup = loader.load();
			// Retrieves the view's associated controller
			SignUpController signUpController = loader.getController();
			// Creates a new stage and scene
			Stage signupStage = new Stage();
			signupStage.setTitle("Sign up");
			Scene scene = new Scene(signup);
			// Sets the stylesheet of the scene
			scene.getStylesheets().add(MainApp.class.getResource("/application/stylesheet.css").toExternalForm());
			// Places the scene on the stage
			signupStage.setScene(scene);
			// Passes the stage to the signUpController to allow it to close it again after completion
			signUpController.setStage(signupStage);
			// Displays the stage
			signupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
