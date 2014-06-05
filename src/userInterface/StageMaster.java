package userInterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import queryAgent.QueryAgent;
import userInterface.controller.AddFieldController;
import userInterface.controller.ConstraintAddController;
import userInterface.controller.QueryController;

public class StageMaster extends Application {

	private static Stage primaryStage;
	private static Stage addField;
	private static Stage addConstraint;
	private static QueryController queryController;
	private static AddFieldController addFieldController;
	private static ConstraintAddController constraintAddController;
	
	public static void main(String[] args) {
		try {
			launch(args);
		} finally {
			QueryAgent.closeFactory();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		StageMaster.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/userInterface/xml/Main.fxml"));
		Parent primaryScene = (Parent)loader.load();
		Scene mainScene = new Scene(primaryScene);
		StageMaster.queryController = loader.getController();
		
		primaryStage.setScene(mainScene);
		primaryStage.setTitle("Finance Tracker");
		primaryStage.show();
		
		/***********************************************************************************/
		loader = new FXMLLoader(getClass().getResource("/userInterface/xml/FieldAdd.fxml"));
		Parent addField = (Parent)loader.load();
		StageMaster.addFieldController = loader.getController();
		
		Scene addFieldScene = new Scene(addField);
		StageMaster.addField = new Stage();
		StageMaster.addField.setTitle("Add Query Field");
		StageMaster.addField.setScene(addFieldScene);
		StageMaster.addField.setResizable(false);
		
		/***********************************************************************************/
		loader = new FXMLLoader(getClass().getResource("/userInterface/xml/ConstraintAdd.fxml"));
		Parent addConstraint = (Parent)loader.load();
		StageMaster.constraintAddController = loader.getController();
		
		Scene addConstraintScene = new Scene(addConstraint);
		StageMaster.addField = new Stage();
		StageMaster.addField.setTitle("Add Constraint");
		StageMaster.addField.setScene(addConstraintScene);
		StageMaster.addField.setResizable(false);
	}
	
	public static Stage primaryStage() {
		return primaryStage;
	}
	
	public static Stage addField() {
		return addField;
	}
	
	public static Stage addConstraint() {
		return addConstraint;
	}
	
	public static QueryController getQueryController() {
		return queryController;
	}
	
	public static AddFieldController getAddFieldController() {
		return addFieldController;
	}
	
	public static ConstraintAddController getConstraintAddController() {
		return constraintAddController;
	}
}