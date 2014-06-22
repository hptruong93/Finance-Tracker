package userInterface.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Dialogs.DialogResponse;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import userInterface.StageMaster;
import userProfile.UserProfile;
import utilities.FileUtility;
import utilities.JSONUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import databaseAgent.dataAnalysis.Feature;
import databaseAgent.queryBuilder.PlainBuilder;
import databaseAgent.queryBuilder.QueryBuilder;

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
	protected UserProfile userProfile;
	/*********************************************************************************/
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		userProfile = new UserProfile("HP");
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
		if (name != null) {
			String description = Dialogs.showInputDialog(null, "Description of the feature");

			if (description != null) {
				toBeSaved.loadConfig(DataController.getInstance().queryManager, name, description, isAdvanced);
				StageMaster.getQueryController().addFeature(toBeSaved);
			}
		}
	}
	
	@FXML
	private void deleteCurrentFeature(ActionEvent e) {
		int selected = StageMaster.getQueryController().cbbFeature.getSelectionModel().getSelectedIndex();
		if (selected > 0) {
			StageMaster.getQueryController().featureManager.remove(selected);
			StageMaster.getQueryController().cbbFeature.getItems().remove(selected);
		}
	}
	
	@FXML
	private void exportFeatures(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(StageMaster.primaryStage());
		if (file != null) {
			JsonRootNode write = JsonNodeFactories.object(JsonNodeFactories.field("user_info", userProfile.jsonize()),
				JsonNodeFactories.field("features", StageMaster.getQueryController().featureManager.jsonize()));
			JSONUtility.dumpToFile(write, file);
		}
	}
	
	@FXML
	private void importFeatures(ActionEvent e) {
		DialogResponse response = Dialogs.showConfirmDialog(StageMaster.primaryStage(), "All current features will be removed.\nAre you sure?");
		if (response.compareTo(DialogResponse.YES) == 0) {
			File file = new FileChooser().showOpenDialog(StageMaster.primaryStage());
			if (file != null) {
				StageMaster.getQueryController().featureManager.clear();
				StageMaster.getQueryController().featureManager.add(null);
				StageMaster.getQueryController().cbbFeature.getItems().clear();
				StageMaster.getQueryController().cbbFeature.getItems().add("");

				JsonRootNode n = FileUtility.readJSON(file);
				for (JsonNode sub : n.getArrayNode("features")) {
					Feature f = new Feature();
					f.loadConfig(sub);
					StageMaster.getQueryController().addFeature(f);
				}
			}
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
