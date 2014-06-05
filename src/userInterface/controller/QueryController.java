package userInterface.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import org.apache.commons.lang3.StringUtils;

import userInterface.StageMaster;
import dataAnalysis.DataQuery;

public class QueryController implements Initializable {

	private DataQuery dataQuery;
	@FXML protected Button bQuery;
	@FXML protected Button bAddField;
	@FXML protected Button bAddConstraint;
	
	@FXML protected ListView<String> lvFields;
	@FXML protected ListView<String> lvConstraints;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dataQuery = MainController.getInstance().dataQuery;
	}

	public void query(ActionEvent e) {
		dataQuery.clearFields();
		if (!lvFields.getItems().isEmpty()) {
			for (String field : lvFields.getItems()) {
				if (StringUtils.countMatches(field, "(") == 1) {//Must be a function then
					String[] splitted = field.replace(")", "").split("(");
					dataQuery.setFunction(splitted[0], splitted[1]);
					break;
				} else {//Not a function
					dataQuery.addField(field);
				}
			}
		} else {
			dataQuery.setDefaultField();
		}
		
		dataQuery.setDefaultField();		
		System.out.println(dataQuery.query());
	}
	
	/***********************************************************************************/
	public void addField(ActionEvent e) {
		StageMaster.getAddFieldController().tfField.clear();
		StageMaster.addField().show();
		StageMaster.addField().toFront();
	}
	
	/***********************************************************************************/
	public void addConstraint(ActionEvent e) {
	}
}
