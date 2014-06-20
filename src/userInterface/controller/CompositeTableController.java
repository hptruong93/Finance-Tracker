package userInterface.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import queryAgent.queryBuilder.QueryBuilder;
import queryAgent.queryComponents.TableFragment;
import userInterface.StageMaster;

public class CompositeTableController implements Initializable {

	@FXML protected Button bOK;
	@FXML protected Button bCancel;

	@FXML protected TextField tfCondition;
	@FXML protected TextField tfAlias;
	@FXML protected Button bJoin;
	@FXML protected ComboBox<String> cbbJoinType;
	@FXML protected ListView<String> lvTables;
	
	protected List<TableFragment> tables;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbbJoinType.getItems().addAll(QueryBuilder.JOIN_TYPE);
		cbbJoinType.getSelectionModel().select(0);
		
		lvTables.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tables = new ArrayList<TableFragment>();
	}

	@FXML
	private void join(ActionEvent e) {
		ObservableList<Integer> selected = lvTables.getSelectionModel().getSelectedIndices();
		if (selected.size() < 2) {
			Dialogs.showErrorDialog(null, "Select two or more tables to join!");
			return;
		} else {
			TableFragment current = null;
			for (Integer i : selected) {
				if (current == null) {
					current = tables.get(i);
				} else {
					current.join(tables.get(i), cbbJoinType.getValue(), tfCondition.getText(), tfAlias.getText());
				}
			}
			tables.add(current);
			lvTables.getItems().add(current.toString());
		}
	}
	
	@FXML
	private void bOKPressed(ActionEvent e) {
		try {
			StageMaster.getQueryController().tfFrom.setText(lvTables.getSelectionModel().getSelectedItem());
			bCancelPressed(null);
		} catch (Exception ex) {
			Dialogs.showErrorDialog(null, "Please select a valid table!");
		}
	}
	
	@FXML
	private void bCancelPressed(ActionEvent e) {
		clear();
		StageMaster.compositeTable().hide();
	}
	
	@FXML
	private void lvTableMouseReleased(MouseEvent e) {
		if (e.getClickCount() >= 2) {
			if (e.getButton() == MouseButton.PRIMARY) {
				int index = lvTables.getSelectionModel().getSelectedIndex();
				if (index != -1) {
					StageMaster.getTableBuilderController().tfTable1.setText(lvTables.getItems().get(index));
				}
				
				StageMaster.tableBuilder().show();
				StageMaster.tableBuilder().toFront();
			} else if (e.getButton() == MouseButton.SECONDARY) {
				deleteCurrent();
			}
		}
	}
	
	@FXML
	private void lvTableKeyReleased(KeyEvent e) {
		if (e.getCode() == KeyCode.DELETE) {
			deleteCurrent();
		}
	}
	
	private void deleteCurrent() {
		int index = lvTables.getSelectionModel().getSelectedIndex();
		if (index != -1) {
			tables.remove(index);
			lvTables.getItems().remove(index);
		}
	}
	
	private void clear() {
		lvTables.getItems().clear();
		tables.clear();
		tfCondition.clear();
		tfAlias.clear();
	}
}
