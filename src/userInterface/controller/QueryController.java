package userInterface.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import userInterface.StageMaster;
import userInterface.controller.visualizer.LabelVisualizer;
import userInterface.controller.visualizer.LineChartVisualizer;
import userInterface.controller.visualizer.TableVisualizer;
import dataAnalysis.DataQuery;

public class QueryController implements Initializable {

	private DataQuery dataQuery;
	@FXML protected Button bQuery;
	@FXML protected Button bAddField;
	@FXML protected Button bAddConstraint;
	@FXML protected Button bSelectAll;
	
	@FXML public ListView<String> lvFields;
	@FXML protected ListView<String> lvConstraints;
	
	@FXML protected CheckBox cbAddComposite;
	@FXML protected TableView tbResult;
	@FXML protected Label lResult;
	@FXML protected LineChart lcResult;
	
	private LabelVisualizer labelVisualizer;
	private TableVisualizer tableVisualizer;
	private LineChartVisualizer lineChartVisualizer;
	
	protected ArrayList<Integer> constraintID; 
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dataQuery = MainController.getInstance().dataQuery;
		labelVisualizer = new LabelVisualizer(lResult);
		lineChartVisualizer = new LineChartVisualizer(lcResult);
		tableVisualizer = new TableVisualizer(tbResult);
		constraintID = new ArrayList<Integer>();
	}

	@FXML
	private void query(ActionEvent e) {
		dataQuery.clearFields();
		if (!lvFields.getItems().isEmpty()) {
			for (String queryField : lvFields.getItems()) {
				if (!dataQuery.setFunction(queryField)) {
					dataQuery.addField(queryField);
				}
			}
		} else {
			dataQuery.setDefaultField();
		}
		
		
		lcResult.setVisible(false);
		tbResult.setVisible(true);
		lResult.setVisible(false);
		tbResult.toFront();
		tableVisualizer.visualize(dataQuery.query());
//		tableVisualizer.visualize(new ArrayList<String>(Arrays.asList("ASDASDASD")));
	}
	
	/***********************************************************************************/
	@FXML
	private void addField(ActionEvent e) {
		StageMaster.getAddFieldController().tfField.clear();
		StageMaster.addField().show();
		StageMaster.addField().toFront();
	}
	
	@FXML
	private void selectAllPressed(ActionEvent e) {
		dataQuery.clearFields();
		lvFields.getItems().clear();
		lvFields.getItems().addAll(StageMaster.getAddFieldController().cbbField.getItems());
		lvFields.getItems().remove(0);
	}
	
	/***********************************************************************************/
	@FXML
	private void addConstraint(ActionEvent e) {
		cbAddComposite.setDisable(true);
		
		if (cbAddComposite.isSelected()) {
			StageMaster.compositeConstraint().show();
			StageMaster.compositeConstraint().toFront();
		} else {
			StageMaster.getConstraintAddController().tfValue.clear();
			StageMaster.addConstraint().show();
			StageMaster.addConstraint().toFront();
		}
	}
	
	/***********************************************************************************/
	@FXML
	private void keyReleasedListViewField(KeyEvent keyEvent) {
		if (keyEvent.getCode() == KeyCode.DELETE) {
			deleteField();
		}
	}
	
	@FXML
	private void mouseReleasedListViewField(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			deleteField();
		}
	}
	
	@FXML
	private void keyReleasedListViewConstraint(KeyEvent keyEvent) {
		if (keyEvent.getCode() == KeyCode.DELETE) {
			deleteConstraint();
		}
	}
	
	@FXML
	private void mouseReleasedListViewConstraint(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			deleteConstraint();
		}
	}
	/***********************************************************************************/
	
	private void deleteField() {
		int selectedItem = lvFields.getSelectionModel().getSelectedIndex();
		if (selectedItem != -1) {
			lvFields.getItems().remove(selectedItem);
		}
	}
	
	private void deleteConstraint() {
		int selectedItem = lvConstraints.getSelectionModel().getSelectedIndex();
		lvConstraints.getItems().remove(selectedItem);
		dataQuery.removeConstraint(constraintID.remove(selectedItem));
	}
}
