package helpers;

import application.MainApp;
import controllers.shared.partials.MovieCellController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import models.Screening;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This is a helper class used for the creation of custom ListView cells
 * with the MovieCell view from MovieCell.fxml
 */
public class ListViewCell extends ListCell<Screening>
{
	/**
	 * Customised version of the updateItem method that loads a MovieCell controller as the graphic for a ListView cell
	 *
	 * @param screening The screening object that will be used to fill in the fields in the MovieCell view
	 * @param empty whether or not this cell represents data from the list. If it is empty, then it does not represent any domain data, but is a cell being used to render an "empty" row
	 * @see javafx.scene.control.ListCell#updateItem(Object, boolean)
	 */
	@Override
	public void updateItem(Screening screening, boolean empty)
	{
		// Calls the updateItem method of the superclass
		super.updateItem(screening, empty);
		// Checks whether the empty flag is set or whether the screening is null
		if (screening != null) {
			MovieCellController controller = null;
			// Creates a new FXMLLoader for the MovieCell view at MovieCell.fxml
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/shared/partials/MovieCell.fxml"));
			try	{
				// Loads the view and retrieves its associated controller
				fxmlLoader.load();
				controller = fxmlLoader.getController();
				// Sets the screening value for the controller which is used to populate the view's fields
				controller.setScreening(screening);
				try {
					// Populates the listview cell with information from the screening object
					controller.populateCell();
				} catch (SQLException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				MainApp.LOGGER.warning("Could not load a MovieCell from MovieCell.fxml");
				e.printStackTrace();
			}
			// Sets the MovieCell view as the graphic for this ListView cell
			if (controller != null) {
				setGraphic(controller.getContainer());
			}
		} else {
			// Displays an empty cell
			setGraphic(null);
		}
	}
}