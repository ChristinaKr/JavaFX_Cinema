package application;

import controllers.customer.CustomerProfileController;
import controllers.customer.CustomerProgrammeController;
import controllers.employee.EmployeeDashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This application is a cinema booking system, fully created in Java and implemented as a JavaFX GUI application.
 * It covers two different perspectives: the customer's and the cinema employee's. Both profiles require different
 * applications and functionalities, are however interlinked with each other.
 *
 * After logging in, the application shows either the customer's or the employee's view by detecting the role of the user
 * based on the provided username and password. The cinema employee has the possibility to add new movies to the cinema
 * database, add new screenings of these movies as well as delete any movies or respective screenings. The employee can
 * furthermore see an up-to-date graphical representation of the cinema room for each screening and its respective share
 * of booked and available seats. Additionally, he is able to export a list of all, all upcoming or a certain selection
 * of screenings including information about date, time and amount of booked and available seats.
 *
 * After logging into the application or signing up as a new customer, the customer profile consists of two main views:
 * the customer profile and the current cinema programme. In the first view the customer is able to edit his personal
 * information such as name and email address. Before updating them these text fields have default values transferred
 * from the sign up process. On this view, the customer is also able to see his booking history that is updated automatically
 * after a booking has been made or deleted. The cinema programme view displays a list of upcoming screenings. These can
 * be viewed in more detail by the customer, displaying movie information as well as the current trailer. When making a
 * booking, the customer sees an up-to-date graphical representation of the cinema room and can choose any available seats.
 * Finally, the customer can print his ticket for the booking with an individually generated QR code, either from the
 * booking summary view or his booking history.
 *
 * Both users can log out of the application at the end.
 */

public class MainApp extends Application {

	//Views
	public static Stage loginStage, primaryStage;
	public static BorderPane employeeDashboard, employeeRoot, customerRoot, employeeMovieList;
	public static AnchorPane customerProfile, customerProgramme, customerTicket;
	public static EmployeeDashboardController employeeDashboardController;
	public static CustomerProgrammeController customerProgrammeController;
	public static CustomerProfileController customerProfileController;

	//Logging
	public static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

	//Username
	public static String username;

	//Type of user
	public static boolean isEmployee;


	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Standard JavaFX function that is used to start the application
	 *
	 * @param primaryStage the stage handed to the MainApp by JavaFX
	 */
	@Override
	public void start(Stage primaryStage) {
		// Sets up the logger
		setupLogger();
		MainApp.primaryStage = primaryStage;
		// Prevents resizing of the main window
		primaryStage.setResizable(false);
		// Shows the login screen
		showLogin();
	}

	/**
	 * Starts the application as an employee
	 */
	public static void startAsEmployee() {
		// Sets the window title
		primaryStage.setTitle("JavaCinema Admin");
		// Shows the employee starting views
		showEmployeeRoot();
		showEmployeeDashboard();
		isEmployee = true;
		LOGGER.info("Starting the App as Employee");
	}

	/**
	 * Starts the application as a customer
	 */
	public static void startAsCustomer() {
		// Sets the window title
		primaryStage.setTitle("JavaCinema");
		// Shows the customer starting views
		showCustomerRoot();
		showCustomerProgramme();
		// Loads the customer profile in the background for faster access
		loadCustomerProfile();
		isEmployee = false;
		LOGGER.info("Starting the App as Customer");
	}

	/**
	 * @return the username of the currently logged in user
	 */
	public static String getUsername() {
		return username;
	}

	/**
	 * @param newUsername the username of the currently logged in user
	 */
	public static void setUsername(String newUsername) {
		username = newUsername;
	}

	/**
	 * Displays the login screen
	 */
	public static void showLogin() {
		// Creates a new stage
		loginStage = new Stage();
		loginStage.setTitle("Log in");
		loginStage.setResizable(false);

		// Creates a new FXMLLoader
		FXMLLoader loader = new FXMLLoader();
		// Sets the location of the .FXML file
		loader.setLocation(MainApp.class.getResource("/views/shared/Login.fxml"));
		AnchorPane login;
		try {
			// Loads the FXML file
			login = loader.load();
			// Creates a new scene
			Scene scene = new Scene(login);
			// Links the stylesheet to the scene
			scene.getStylesheets().add(MainApp.class.getResource("/application/stylesheet.css").toExternalForm());
			// Places the scene in the stage
			loginStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info("Showing Login");
		// Displays the login window
		loginStage.showAndWait();
	}

	/**
	 * Sets up the logger for this application
	 */
	private void setupLogger() {
		//Create new FileHandler
		try {
			FileHandler fileHandler = new FileHandler(System.getProperty("user.dir") + "/logs/log.txt");
			//Set Formatter of fileHandler to SimpleFormatter
			fileHandler.setFormatter(new SimpleFormatter());
			//Add fileHandler to LOGGER
			LOGGER.addHandler(fileHandler);
		} catch (IOException e) {
			LOGGER.warning("Couldn't register fileHandler to Logger. Please ensure that your working directory" +
				" is the directory that contains this programme.");
			e.printStackTrace();
		}
		LOGGER.setLevel(Level.FINE);
	}


	/**
	 * Shows EmployeeRoot and sets it as the scene of primaryStage
	 */
	private static void showEmployeeRoot() {
		// Creates a new FXMLLoader
		FXMLLoader loader = new FXMLLoader();
		// Sets the location of the .FXML file
		loader.setLocation(MainApp.class.getResource("/views/employee/EmployeeRoot.fxml"));
		try {
			// Loads the FXML file
			employeeRoot = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Creates a new scene
		Scene scene = new Scene(employeeRoot);
		// Links the stylesheet to the scene
		scene.getStylesheets().add(MainApp.class.getResource("/application/stylesheet.css").toExternalForm());
		// Places the scene in the new stage
		primaryStage.setScene(scene);
		// Displays the stage
		primaryStage.show();
		LOGGER.info("Showing primaryStage with EmployeeRoot");
	}

	/**
	 * Shows EmployeeDashboard in the center of EmployeeRoot
	 */
	private static void showEmployeeDashboard() {
		// Creates a new FXMLLoader
		FXMLLoader loader = new FXMLLoader();
		// Sets the location of the .FXML file
		loader.setLocation(MainApp.class.getResource("/views/employee/EmployeeDashboard.fxml"));
		try {
			// Loads the FXML file
			employeeDashboard = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Saves the controller in a class variable
		employeeDashboardController = loader.getController();
		// Displays the view in the center of employeeRoot
		employeeRoot.setCenter(employeeDashboard);
		LOGGER.info("Showing Employee Dashboard");
	}

	/**
	 * Shows CustomerRoot and sets it as the scene of primaryStage
	 */
	private static void showCustomerRoot() {
		// Creates a new FXMLLoader
		FXMLLoader loader = new FXMLLoader();
		// Sets the location of the .FXML file
		loader.setLocation(MainApp.class.getResource("/views/customer/CustomerRoot.fxml"));
		try {
			// Loads the FXML file
			customerRoot = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Creates a new scene
		Scene scene = new Scene(customerRoot);
		// Links the stylesheet to the scene
		scene.getStylesheets().add(MainApp.class.getResource("/application/stylesheet.css").toExternalForm());
		// Places the scene in the new stage
		primaryStage.setScene(scene);
		// Displays the new stage
		primaryStage.show();
		LOGGER.info("Showing primaryStage with CustomerRoot");
	}

	/**
	 * Loads the CustomerProfile view to facilitate quicker access at a later time
	 */
	private static void loadCustomerProfile() {
		try {
			// Creates a new FXMLLoader
			FXMLLoader loader = new FXMLLoader();
			// Sets the location of the .FXML file
			loader.setLocation(MainApp.class.getResource("/views/customer/CustomerProfile.fxml"));
			// Loads the FXML file
			customerProfile = loader.load();
			// Saves the controller in a class variable
			customerProfileController = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the CustomerProgramme view
	 */
	private static void showCustomerProgramme() {
		try {
			// Creates a new FXMLLoader
			FXMLLoader loader = new FXMLLoader();
			// Sets the location of the .FXML file
			loader.setLocation(MainApp.class.getResource("/views/customer/CustomerProgramme.fxml"));
			// Loads the FXML file
			customerProgramme = loader.load();
			// Saves the controller in a class variable
			customerProgrammeController = loader.getController();
			// Displays customerProgramme in the center of customerRoot
			customerRoot.setCenter(customerProgramme);
			LOGGER.info("Showing Customer Programme");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
