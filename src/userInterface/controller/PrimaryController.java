package userInterface.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.AnchorPane;
import queryAgent.dataAnalysis.Feature;
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
		toBeSaved.loadConfig(DataController.getInstance().queryManager, "test", "this is a test", isAdvanced);
		StageMaster.getQueryController().addFeature(toBeSaved);
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
