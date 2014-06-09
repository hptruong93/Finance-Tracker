package userInterface.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import queryAgent.RestrictionFragment;
import userInterface.StageMaster;

public class CompositeConstraintController implements Initializable {

	@FXML protected ListView<String> lvConstraints;
	@FXML protected Button bAdd;
	@FXML protected Button bCancel;
    @FXML protected Button bAddNewConstraint;
	@FXML protected ComboBox<String> cbbJoiner;
	@FXML protected Label lStatus;
	@FXML protected CheckBox cbNot;
	private List<RestrictionFragment> criteria;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbbJoiner.getItems().addAll("AND", "OR");
		criteria = new ArrayList<RestrictionFragment>();
	}

	@FXML
	private void bAddPressed(ActionEvent e) {
		lStatus.setText("");
		String joining = cbbJoiner.getValue();
		if (cbNot.isSelected()) {
			if (joining.equals("AND")) {
				joining = "OR";
			} else if (joining .equals("OR")){
				joining = "AND";
			} else {
				lStatus.setText("Invalid joiner...");
				return;
			}
			
			for (RestrictionFragment rf : criteria) {
				rf.not();
			}
		}
		
		String joiner;
		if (joining.equals("AND")) {
			joiner = "AND";
		} else if (joining.equals("OR")) {
			joiner = "OR";
		} else {
			lStatus.setText("Invalid joiner...");
			return;
		}
		
		RestrictionFragment resultConstraint;
		if (!criteria.isEmpty()) {
			resultConstraint = criteria.get(0);
			for (int i = 0; i < criteria.size(); i++) {
				resultConstraint.join(criteria.get(i), joiner);
			}
			DataController.getInstance().queryManager.addConstraint(resultConstraint);
			StageMaster.getQueryController().lvConstraints.getItems().add(resultConstraint.toString());
		}
		
		clearData();
		StageMaster.getQueryController().cbAddComposite.setDisable(false);
		StageMaster.compositeConstraint().hide();
		StageMaster.primaryStage().show();
	}
	
	@FXML
	private void bCancelPressed(ActionEvent e) {
		clearData();
		StageMaster.getQueryController().cbAddComposite.setDisable(false);
		StageMaster.compositeConstraint().hide();
		StageMaster.primaryStage().show();
	}

	@FXML
	private void bAddConstraintPressed(ActionEvent e) {
		StageMaster.primaryStage().hide();
		StageMaster.addConstraint().show();
		StageMaster.addConstraint().toFront();
	}
	 
	protected void addConstraint(RestrictionFragment rf) {
		this.criteria.add(rf);
		lvConstraints.getItems().add(rf.toString());
	}
	
	private void clearData() {
		StageMaster.getConstraintAddController().cbbOption.setDisable(false);
		lStatus.setText("");
		lvConstraints.getItems().clear();
		criteria.clear();
		cbNot.setSelected(false);
	}
}
