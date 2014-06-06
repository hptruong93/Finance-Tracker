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

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;

import userInterface.StageMaster;
import utilities.Mapper;

public class CompositeConstraintController implements Initializable {

	@FXML protected ListView<String> lvConstraints;
	@FXML protected Button bAdd;
	@FXML protected Button bCancel;
    @FXML protected Button bAddNewConstraint;
	@FXML protected ComboBox<String> cbbJoiner;
	@FXML protected Label lStatus;
	@FXML protected CheckBox cbNot;
	private List<Criterion> criteria;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbbJoiner.getItems().addAll("AND", "OR");
		criteria = new ArrayList<Criterion>();
	}

	@FXML
	private void bAddPressed(ActionEvent e) {
		lStatus.setText("");
		Junction junction = null;
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
			
			criteria = (new Mapper<Criterion, Criterion>() {
				@Override
				public Criterion map(Criterion input) {
					return Restrictions.not(input);
				}
			}).map(criteria);
		}
		
		if (joining.equals("AND")) {
			junction = Restrictions.conjunction();
		} else if (joining.equals("OR")) {
			junction = Restrictions.disjunction();
		} else {
			lStatus.setText("Invalid joiner...");
			return;
		}
		
		for (Criterion cr : criteria) {
			junction.add(cr);
		}
		
		MainController.getInstance().dataQuery.addJunction(junction);
		StageMaster.getQueryController().lvConstraints.getItems().add(junction.toString());
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
	
	protected void addConstraint(Criterion c) {
		criteria.add(c);
		lvConstraints.getItems().add(c.toString());
	}
	
	private void clearData() {
		lStatus.setText("");
		lvConstraints.getItems().clear();
		criteria.clear();
		cbNot.setSelected(false);
	}
}
