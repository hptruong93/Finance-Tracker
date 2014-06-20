package userInterface.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialogs;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.AnchorPane;
import queryAgent.dataAnalysis.Feature;
import queryAgent.queryBuilder.PlainBuilder;
import queryAgent.queryBuilder.QueryBuilder;
import userInterface.StageMaster;

public class PrimaryController implements Initializable {

	@FXML private AnchorPane query;
	@FXML private QueryController queryController;
	@FXML private AnchorPane importPage;
	@FXML private ImportController importPageController;
	
	/************************File menu************************************************/
	
	/************************Tool menu************************************************/
	
	/************************Option menu**********************************************/
	@FXML protected RadioMenuItem rmiLabelVisualizer;
	@FXML protected RadioMenuItem rmiTableVisualizer;
	@FXML protected RadioMenuItem rmiLineChartVisualizer;
	@FXML protected CheckMenuItem cmiAdvancedQuery;
	/************************Help menu************************************************/
	/*********************************************************************************/
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

	@FXML
	private void query(ActionEvent e) {
		
	}
	
	@FXML
	private void delete(ActionEvent e) {
		
	}
	
	@FXML
	private void addField(ActionEvent e) {
		
	}
	
	@FXML
	private void addConstraint(ActionEvent e) {
		
	}
	
	@FXML
	private void modifyFrom(ActionEvent e) {
		
	}
	
	/*********************************************************************************/
	@FXML
	private void saveFeature(ActionEvent e) {
		boolean isAdvanced = cmiAdvancedQuery.isSelected();
		Feature toBeSaved = new Feature();
		
		String name = Dialogs.showInputDialog(null, "Name of the new feature");
		String description = Dialogs.showInputDialog(null, "Description of the feature");
		
		toBeSaved.loadConfig(DataController.getInstance().queryManager, name, description, isAdvanced);
		StageMaster.getQueryController().addFeature(toBeSaved);
	}
	
	@FXML
	private void deleteCurrentFeature(ActionEvent e) {
		int selected = StageMaster.getQueryController().cbbFeature.getSelectionModel().getSelectedIndex();
		if (selected > 0) {
			StageMaster.getQueryController().features.remove(selected);
			StageMaster.getQueryController().cbbFeature.getItems().remove(selected);
		}
	}
	/*********************************************************************************/
	@FXML
	private void queryModeChanged(ActionEvent e) {
		if (cmiAdvancedQuery.isSelected()) {
			DataController.getInstance().queryBuilder = new PlainBuilder();
		} else {
			DataController.getInstance().queryBuilder = new QueryBuilder();
		}
	}
	/*********************************************************************************/
	@FXML
	private void exit(ActionEvent e) {
		Platform.exit();
	}
	/*********************************************************************************/
	public QueryController getQueryController() {
		return queryController;
	}
	
	public ImportController getImportController() {
		return importPageController;
	}
}
