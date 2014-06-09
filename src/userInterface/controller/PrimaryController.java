package userInterface.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class PrimaryController implements Initializable {

	@FXML private AnchorPane query;
	@FXML private QueryController queryController;
	@FXML private AnchorPane importPage;
	@FXML private ImportController importPageController;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

	public QueryController getQueryController() {
		return queryController;
	}
	
	public ImportController getImportController() {
		return importPageController;
	}
}
