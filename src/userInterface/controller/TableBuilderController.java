package userInterface.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import databaseAgent.queryBuilder.QueryBuilder;
import databaseAgent.queryComponents.TableFragment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import userInterface.StageMaster;

public class TableBuilderController implements Initializable {

	@FXML protected TextField tfTable1;
	@FXML protected TextField tfSelect1;
	@FXML protected TextField tfFrom1;
	@FXML protected TextField tfAlias1;
	
	@FXML protected TextField tfResult1;
	
	@FXML protected Button bOK;
	@FXML protected Button bCancel;
	
	private TableFragment table1;
	private QueryBuilder queryBuilder;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		queryBuilder = DataController.getInstance().queryBuilder;
	}

	@FXML
	private void table1Changed(KeyEvent e) {
		table1 = queryBuilder.buildTable(Arrays.asList(tfSelect1.getText().split(", ")), tfTable1.getText(), tfAlias1.getText());
		tfResult1.setText(table1.toString());
	}
	
	@FXML
	private void bOKPressed(ActionEvent e) {
		StageMaster.getCompositeTableController().lvTables.getItems().add(tfResult1.getText());
		StageMaster.getCompositeTableController().tables.add(table1.clone());
	}
	
	@FXML
	private void bClearPressed(ActionEvent e) {
		clear();
	}
	
	@FXML
	private void bCancelPressed(ActionEvent e) {
		clear();
		StageMaster.tableBuilder().hide();
	}
	
	@FXML
	private void windowKeyReleased(KeyEvent e) {
		if (e.getCode() == KeyCode.ESCAPE) {
			bCancelPressed(null);
		}
	}
	
	private void clear() {
		tfSelect1.clear();
		tfTable1.clear();
		tfAlias1.clear();
		
		tfResult1.clear();
	}
}
