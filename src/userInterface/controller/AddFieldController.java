package userInterface.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import userInterface.StageMaster;
import dataAnalysis.DataQuery;

public class AddFieldController implements Initializable {
	@FXML protected Button bAdd;
	@FXML protected Button bCancel;
	
	@FXML protected TextField tfField;
	@FXML protected Label lStatus;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void addPressed(ActionEvent e) {
		String toAdd = StageMaster.getAddFieldController().tfField.getText();
		if (DataQuery.validQueryField(toAdd)) {
			StageMaster.getQueryController().lvFields.getItems().add(toAdd);
			lStatus.setText("");
			StageMaster.addField().hide();
		} else {
			lStatus.setText("Invalid field to be added!");
			tfField.requestFocus();
		}
	}
	
	public void cancelPressed(ActionEvent e) {
		lStatus.setText("");
		StageMaster.addField().hide();
	}
}
