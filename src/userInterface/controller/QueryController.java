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
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import userInterface.ConnectionManager;
import userInterface.StageMaster;
import userInterface.controller.visualizer.IDataVisualizer;
import userInterface.controller.visualizer.LabelVisualizer;
import userInterface.controller.visualizer.LineChartVisualizer;
import userInterface.controller.visualizer.TableVisualizer;
import utilities.StringUtility;
import utilities.functional.Mapper;
import databaseAgent.QueryManager;
import databaseAgent.dataAnalysis.Feature;
import databaseAgent.dataAnalysis.FeatureManager;
import databaseAgent.dataAnalysis.ServerFeatureManager;
import databaseAgent.queryBuilder.TranslatorFactory;
import databaseAgent.queryComponents.TableFragment;

public class QueryController implements Initializable {

	private static final int MAX_QUERY_HISTORY = 10;
	private QueryManager dataQuery;

	@FXML protected ComboBox<String> cbbFeature;
	@FXML protected Button bQuery;
	@FXML protected Button bAddField;
	@FXML protected Button bAddConstraint;
	@FXML protected Button bSelectAll;

	@FXML protected Button bPrevious;
	@FXML protected Button bNext;

	@FXML public ListView<String> lvFields;
	@FXML protected ListView<String> lvConstraints;
	
	@FXML protected TextField tfMaxResult;
	@FXML protected TextField tfFrom;
	@FXML protected Label lStatus;
	
	@FXML protected CheckBox cbAddComposite;
	
	@FXML protected StackPane visualizerPane;
	private LabelVisualizer labelVisualizer;
	private TableVisualizer tableVisualizer;
	private LineChartVisualizer lineChartVisualizer;
	
	private int cursor;
	private List<QueryResult> results;
	protected List<Integer> constraintID;
	protected FeatureManager featureManager;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		dataQuery = DataController.getInstance().queryManager;
		
		labelVisualizer = new LabelVisualizer();
		lineChartVisualizer = new LineChartVisualizer();
		tableVisualizer = new TableVisualizer();
		constraintID = new ArrayList<Integer>();
		
		cbbFeature.getItems().add("");
		featureManager = new ServerFeatureManager();
		featureManager.add(null);
		
		if (Feature.getDefaultFeatures() != null) {
			for (Feature f : Feature.getDefaultFeatures()) {
				cbbFeature.getItems().add(f.getName());
				featureManager.add(f);
			}
		}
		
		results = new ArrayList<QueryResult>();
		cursor = -1;
	}

	@FXML
	private void query(ActionEvent e) {
		if (!DataController.getInstance().connectionManager.isConnected(ConnectionManager.DATABASE)) {
			Dialogs.showErrorDialog(StageMaster.primaryStage(), "Database connection has not been established.");
		}
		
		int maxResult = -1;
		try {
			maxResult = Integer.parseInt(tfMaxResult.getText());
			if (maxResult < 1) {
				lStatus.setText("Invalid number of max result. Must be greater than 0...");
				Dialogs.showErrorDialog(null, "Invalid number of max result. Must be greater than 0...");
				tfMaxResult.requestFocus();	
			}
		} catch (NumberFormatException ex) {
			lStatus.setText("Invalid number of max result... " + maxResult + " is not a number.");
			Dialogs.showErrorDialog(null, "Invalid number of max result... " + maxResult + " is not a number.");
			tfMaxResult.requestFocus();
		}
		
		dataQuery.setMaxResult(maxResult);
		dataQuery.clearFields();
		if (!lvFields.getItems().isEmpty()) {
			List<String> toAdd;
			if (StageMaster.getPrimaryController().cmiAdvancedQuery.isSelected()) {
				toAdd = lvFields.getItems();
			} else {
				toAdd = new Mapper<String, String>() {
					@Override
					public String map(String input) {
						return TranslatorFactory.getTranslator(TranslatorFactory.STANDARD_TRANSLATOR)
								.fieldTranslate(input.split(" (?i)as ")[0]);
					}
				}.map(lvFields.getItems());
			}
			
			for (String queryField : toAdd) {
				dataQuery.addField(queryField);
			}
		} else {
			dataQuery.setDefaultField();
		}
		
		dataQuery.setFrom(new TableFragment(tfFrom.getText(), null));
		
		Object result = dataQuery.query();
		if (result == null) {
			Dialogs.showErrorDialog(null, "Please check query information.\nCheck log for exception encountered");
			return;
		} else {
			List<String> currentFields = new ArrayList<String>();
			currentFields.addAll(new Mapper<String, String>() {
				@Override
				public String map(String input) {
					return StringUtility.getComponent(input, " (?i)as ", -1);
				}
			}.map(lvFields.getItems()));
			results.add(new QueryResult(currentFields, result));
		}
		
		visualize(results.get(results.size() - 1));
		if (results.size() > MAX_QUERY_HISTORY) {
			results.remove(0);
		}
		
		cursor = results.size() - 1;
		if (results.size() > 1) {
			bPrevious.setDisable(false);
		}
		bNext.setDisable(true);
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
			StageMaster.getConstraintAddController().cbbOption.setDisable(true);
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
	private void bPreviousPressed(ActionEvent e) {
		bNext.setDisable(false);
		cursor--;
		visualize(results.get(cursor));
		
		if (cursor <= 1) {
			bPrevious.setDisable(true);
		}
	}
	
	@FXML
	private void bNextPressed(ActionEvent e) {
		bPrevious.setDisable(false);
		cursor++;
		visualize(results.get(cursor));
		if (cursor >= results.size() - 1) {
			bNext.setDisable(true);
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
			if (mouseEvent.getClickCount() >= 2) {
				deleteConstraint();
			}
		}
	}
	/***********************************************************************************/
	@FXML
	private void changeFeature(ActionEvent e) {
		lvFields.getItems().clear();
		lvConstraints.getItems().clear();
		tfFrom.clear();
		
		int selected = cbbFeature.getSelectionModel().getSelectedIndex();
		if (selected > 0) {
			Feature selectedFeature = featureManager.get(selected);
			StageMaster.getPrimaryController().cmiAdvancedQuery.setSelected(selectedFeature.isAdvanced());
			
			constraintID = selectedFeature.apply(DataController.getInstance().queryManager);
			
			lvFields.getItems().addAll(DataController.getInstance().queryManager.getFields());
			lvConstraints.getItems().addAll(DataController.getInstance().queryManager.getConstraintStrings());
			tfFrom.setText(DataController.getInstance().queryManager.getFromString());
		}
	}
	
	/***********************************************************************************/
	@FXML
	private void tfFromClicked(MouseEvent e) {
		if (StageMaster.getPrimaryController().cmiAdvancedQuery.isSelected()) {
			if (e.getClickCount() >= 2) {
				StageMaster.compositeTable().show();
				StageMaster.compositeTable().toFront();
			}
		} else {
			Dialogs.showErrorDialog(StageMaster.primaryStage(),"Has to be in advanced query mode to change table...");
		}
	}
	
	/***********************************************************************************/
	protected void addFeature(Feature newComer) {
		featureManager.add(newComer);
		cbbFeature.getItems().add(newComer.getName());
	}
	
	private void deleteField() {
		int selectedItem = lvFields.getSelectionModel().getSelectedIndex();
		if (selectedItem != -1 && selectedItem < lvFields.getItems().size()) {
			lvFields.getItems().remove(selectedItem);
		}
	}
	
	private void deleteConstraint() {
		int selectedItem = lvConstraints.getSelectionModel().getSelectedIndex();
		if (selectedItem != -1) {
			lvConstraints.getItems().remove(selectedItem);
			dataQuery.removeConstraint(constraintID.remove(selectedItem));
		}
	}
	
	/***********************************************************************************/
	private IDataVisualizer getDataVisualizer() {
		if (StageMaster.getPrimaryController().rmiLabelVisualizer.isSelected()) {
			return labelVisualizer;
		} else if (StageMaster.getPrimaryController().rmiTableVisualizer.isSelected()) {
			return tableVisualizer;
		} else if (StageMaster.getPrimaryController().rmiLineChartVisualizer.isSelected()) {
			return lineChartVisualizer;
		} else {
			return null;
		}
	}
	
	private void visualize(QueryResult result) {
		IDataVisualizer visualizer = getDataVisualizer();
		visualizer.visualize(result);
		visualizerPane.getChildren().clear();
		visualizerPane.getChildren().add(visualizer.getTool());
	}
	
	/***********************************************************************************/
	public static class QueryResult {
		private List<String> fields;
		private Object result;
		
		private QueryResult(List<String> fields, Object result) {
			this.fields = fields;
			this.result = result;
		}
		
		public List<String> getFields() {
			return fields;
		}
		
		public Object getResult() {
			return result;
		}
	}
}
