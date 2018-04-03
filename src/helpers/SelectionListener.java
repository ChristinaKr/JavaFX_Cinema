package helpers;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This EventHandler disables JavaFX Nodes based on the selection model of the provided ListView or TableView
 *
 * Depending on whether a ListView or TableView was handed to the handler, the handler iterates
 * through all other provided Nodes and enables each one of them if there is at least one selected item
 * in the SelectionModel of the ListView/TableView. Otherwise, if no item is currently selected,
 * all Nodes are disabled.
 *
 */
public class SelectionListener implements EventHandler {

	private ArrayList<Node> nodes;
	private ListView listView;
	private TableView tableView;

	/**
	 * Constructor that sets the ListView and Nodes to be operated on
	 *
	 * @param listView The ListView whose selection model will be queried
	 * @param nodes An array of Nodes which will be enabled/disabled
	 */
	public SelectionListener(ListView listView, Node... nodes) {
		this.listView = listView;
		this.nodes = new ArrayList<>(Arrays.asList(nodes));
	}

/**
 * Constructor that sets the TableView and Nodes to be operated on
 *
 * @param tableView The TableView whose selection model will be queried
 * @param nodes An array of Nodes which will be enabled/disabled
 */
	public SelectionListener(TableView tableView, Node... nodes) {
		this.tableView = tableView;
		this.nodes = new ArrayList<>(Arrays.asList(nodes));
	}

	/**
	 * Checks whether a tableView or ListView was provided and enables/disables all provided Nodes accordingly.
	 *
	 * Depending on whether a ListView or TableView was handed to the handler, this method iterates
	 * through all other provided Nodes and enables each one of them if there is at least one selected item
	 * in the SelectionModel of the ListView/TableView. Otherwise, if no item is currently selected,
	 * all Nodes are disabled.
	 *
	 * @param event The event passed to this EventHandler
	 */
	@Override
	public void handle(Event event) {
		if (tableView == null){
			// Checks if at least one item is currently selected in the listView
			if (listView.getSelectionModel().getSelectedItem() == null) {
				// If nothing is selected, disables the provided Nodes
				for (Node node : nodes) {
					node.setDisable(true);
				}
			} else {
				// If one or more items are selected, enables the provided Nodes
				for (Node node : nodes) {
					node.setDisable(false);
				}
			}
		} else {
			// Checks if at least one item is currently selected in the tableView
			if (tableView.getSelectionModel().getSelectedItem() == null) {
				// If nothing is selected, disables the provided Nodes
				for (Node node : nodes) {
					node.setDisable(true);
				}
			} else {
				// If one or more items are selected, enables the provided Nodes
				for (Node node : nodes) {
					node.setDisable(false);
				}
			}
		}
	}
}
