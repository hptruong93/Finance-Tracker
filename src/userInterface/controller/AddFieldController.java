package userInterface.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import userInterface.StageMaster;
import utilities.functional.Mapper;
import dataAnalysis.DataQuery;

public class AddFieldController implements Initializable {
	@FXML protected Button bAdd;
	@FXML protected Button bCancel;
	
	@FXML protected TextField tfField;
	@FXML protected Label lStatus;

	@FXML protected ComboBox<String> cbbFunction;
	@FXML protected ComboBox<String> cbbField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cbbField.getItems().add("");
		cbbField.getItems().addAll((new Mapper<String, String>() {
			@Override
			public String map(String input) {
				String[] split = input.split("\\.");
				return split[split.length - 1];
			}
		}).map(DataQuery.FIELD_LIST));
		cbbField.getSelectionModel().select(0);
		
		cbbFunction.getItems().add("");
		cbbFunction.getItems().addAll(DataQuery.SUPPORTED_FUNCTIONS);
		cbbFunction.getSelectionModel().select(0);
	}
	
	@FXML
	public void addPressed(ActionEvent e) {
		String toAdd = StageMaster.getAddFieldController().tfField.getText();
		if (DataQuery.validQueryField(toAdd)) {
			StageMaster.getQueryController().lvFields.getItems().add(toAdd);
			lStatus.setText("");
		} else {
			lStatus.setText("Invalid field to be added!");
			tfField.requestFocus();
		}
	}
	
	@FXML
	public void cancelPressed(ActionEvent e) {
		lStatus.setText("");
		StageMaster.addField().hide();
	}
	
	@FXML
	public void cbbFunctionChanged(ActionEvent e) {
		tfField.setText(combine(cbbFunction.getValue(), cbbField.getValue()));
	}
	
	@FXML
	public void cbbFieldChanged(ActionEvent e) {
		tfField.setText(combine(cbbFunction.getValue(), cbbField.getValue()));
	}
	
	private String combine(String function, String field) {
		if (function == null || field == null) {
			return "";
		} else if (function.length() == 0) {
			return field;
		} else if (field.length() == 0) {
			return "";
		} else {
			return function + "(" + field + ")";
		}
	}
}
