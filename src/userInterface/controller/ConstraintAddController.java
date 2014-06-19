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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import queryAgent.QueryBuilder;
import queryAgent.RestrictionFragment;
import userInterface.StageMaster;
import utilities.functional.Mapper;

public class ConstraintAddController implements Initializable {

	@FXML
	protected Button bCancel;
	@FXML
	protected Button bAdd;
	@FXML
	protected ComboBox<String> cbbField;
	@FXML
	protected ComboBox<String> cbbFunction;
	@FXML
	protected ComboBox<String> cbbCondition;
	@FXML
	protected ComboBox<String> cbbOption;
	@FXML
	protected TextField tfValue;
	@FXML
	protected Label lStatus;

	private QueryBuilder queryBuilder;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		queryBuilder = DataController.getInstance().queryBuilder;

		cbbCondition.getItems().addAll(QueryBuilder.SUPPORTED_CONDITION);
		cbbField.getItems().add("");
		cbbField.getItems().addAll((new Mapper<String, String>() {
			@Override
			public String map(String input) {
				String[] split = input.split("\\.");
				return split[split.length - 1];
			}
		}).map(QueryBuilder.FIELD_LIST));

		cbbFunction.getItems().add("");
		cbbFunction.getItems().addAll(QueryBuilder.SUPPORTED_FUNCTIONS);
		cbbFunction.getSelectionModel().select(0);
		
		cbbOption.getItems().addAll("", "GROUP BY", "HAVING", "ORDER BY ASC", "ORDER BY DESC");
		cbbOption.getSelectionModel().select(0);
	}

	@FXML
	public void addPressed(ActionEvent e) {
		String field = cbbField.getValue();
		String condition = cbbCondition.getValue();
		String value = tfValue.getText();
		String function = cbbFunction.getValue();
		if (function != null && function.length() != 0) {
			field = function + "(" + field + ")";
		}

		RestrictionFragment identified;
		try {
			identified = queryBuilder.buildConstraint(field, condition, value);
		} catch (Exception ex) {
			identified = null;
		}

		if (identified != null 
				|| cbbOption.getValue().equals("GROUP BY")
				|| cbbOption.getValue().contains("ORDER BY")) {
			if (StageMaster.getQueryController().cbAddComposite.isSelected()) {
				StageMaster.getCompositeConstraintController().addConstraint(identified);
			} else {
				int id = -1;
				if (cbbOption.getValue().equals("HAVING")) {
					id = DataController.getInstance().queryManager.addHaving(identified);
				} else if (cbbOption.getValue().equals("GROUP BY")) {
					id = DataController.getInstance().queryManager.addGroupBy(queryBuilder.buildGroupBy(value));
				} else if (cbbOption.getValue().contains("ORDER BY")) {
					String option = cbbOption.getValue().substring("ORDER BY ".length());
					id = DataController.getInstance().queryManager.addOrderBy(queryBuilder.buildOrderBy(value, option));
				} else {
					id = DataController.getInstance().queryManager.addConstraint(identified);
				}

				StageMaster.getQueryController().constraintID.add(id);

				if (cbbOption.getValue().equals("GROUP BY")) {
					StageMaster.getQueryController().lvConstraints.getItems().add("GROUP BY " + queryBuilder.simplify(value));
				} else if (cbbOption.getValue().contains("ORDER BY")) { 
					String order = cbbOption.getValue();
					StageMaster.getQueryController().lvConstraints.getItems().add(order + " " + queryBuilder.simplify(value));
				} else if (cbbOption.getValue().equals("HAVING")) {
					StageMaster.getQueryController().lvConstraints.getItems().add("HAVING " + identified);
				} else {
					StageMaster.getQueryController().lvConstraints.getItems().add(identified.toString());
				}

				StageMaster.getQueryController().cbAddComposite.setDisable(false);
			}
		} else {
			lStatus.setText("Invalid condition!");
		}
	}

	@FXML
	public void cancelPressed(ActionEvent e) {
		lStatus.setText("");
		StageMaster.getQueryController().cbAddComposite.setDisable(false);
		StageMaster.addConstraint().hide();
		StageMaster.primaryStage().show();
	}
	
	@FXML
	private void windowKeyReleased(KeyEvent e) {
		if (e.getCode() == KeyCode.ESCAPE) {
			cancelPressed(null);
		}
	}
}
