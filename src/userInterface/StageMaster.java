package userInterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import userInterface.controller.AddFieldController;
import userInterface.controller.CompositeConstraintController;
import userInterface.controller.CompositeTableController;
import userInterface.controller.ConstraintAddController;
import userInterface.controller.DataController;
import userInterface.controller.ImportController;
import userInterface.controller.PrimaryController;
import userInterface.controller.QueryController;
import userInterface.controller.TableBuilderController;

public class StageMaster extends Application {

	private static Stage primaryStage;
	private static Stage addField;
	private static Stage addConstraint;
	private static Stage compositeConstraint;
	private static Stage tableBuilder;
	private static Stage compositeTable;
	
	private static PrimaryController primaryController;
	private static AddFieldController addFieldController;
	private static ConstraintAddController constraintAddController;
	private static CompositeConstraintController compositeConstraintController;
	private static TableBuilderController tableBuilderController;
	private static CompositeTableController compositeTableController;

	public static void main(String[] args) {
		try {
			DataController.getInstance().getConnectionManager().startConnections();
			launch(args);
		} finally {
			DataController.getInstance().getConnectionManager().closeConnections();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		/***********************************************************************************/
		StageMaster.primaryStage = primaryStage;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/userInterface/xml/Main.fxml"));
		Parent parent = (Parent)loader.load();
		Scene scene = new Scene(parent);
		StageMaster.primaryController = loader.getController();
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Finance Tracker");
		primaryStage.show();
		
		/***********************************************************************************/
		loader = new FXMLLoader(getClass().getResource("/userInterface/xml/FieldAdd.fxml"));
		parent = (Parent)loader.load();
		StageMaster.addFieldController = loader.getController();
		
		scene = new Scene(parent);
		StageMaster.addField = new Stage();
		StageMaster.addField.setTitle("Add Query Field");
		StageMaster.addField.setScene(scene);
		StageMaster.addField.setResizable(false);
		
		/***********************************************************************************/
		loader = new FXMLLoader(getClass().getResource("/userInterface/xml/ConstraintAdd.fxml"));
		parent = (Parent)loader.load();
		StageMaster.constraintAddController = loader.getController();
		
		scene = new Scene(parent);
		StageMaster.addConstraint = new Stage();
		StageMaster.addConstraint.setTitle("Add Constraint");
		StageMaster.addConstraint.setScene(scene);
		StageMaster.addConstraint.setResizable(false);
		
		/***********************************************************************************/
		loader = new FXMLLoader(getClass().getResource("/userInterface/xml/CompositeConstraint.fxml"));
		parent = (Parent)loader.load();
		StageMaster.compositeConstraintController = loader.getController();
		
		scene = new Scene(parent);
		StageMaster.compositeConstraint = new Stage();
		StageMaster.compositeConstraint.setTitle("Add Composite Constraint");
		StageMaster.compositeConstraint.setScene(scene);
		StageMaster.compositeConstraint.setResizable(false);
		/***********************************************************************************/
		loader = new FXMLLoader(getClass().getResource("/userInterface/xml/TableBuilder.fxml"));
		parent = (Parent)loader.load();
		StageMaster.tableBuilderController = loader.getController();
		
		scene = new Scene(parent);
		StageMaster.tableBuilder = new Stage();
		StageMaster.tableBuilder.setTitle("Build table");
		StageMaster.tableBuilder.setScene(scene);
		StageMaster.tableBuilder.setResizable(false);
		/***********************************************************************************/
		loader = new FXMLLoader(getClass().getResource("/userInterface/xml/CompositeTable.fxml"));
		parent = (Parent)loader.load();
		StageMaster.compositeTableController = loader.getController();
		
		scene = new Scene(parent);
		StageMaster.compositeTable = new Stage();
		StageMaster.compositeTable.setTitle("Build composite table");
		StageMaster.compositeTable.setScene(scene);
		StageMaster.compositeTable.setResizable(false);
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
	
	public static Stage compositeConstraint() {
		return compositeConstraint;
	}
	
	public static Stage tableBuilder() {
		return tableBuilder;
	}
	
	public static Stage compositeTable() {
		return compositeTable;
	}
	
	public static PrimaryController getPrimaryController() {
		return primaryController;
	}
	
	public static QueryController getQueryController() {
		return primaryController.getQueryController();
	}
	
	public static ImportController getImportController() {
		return primaryController.getImportController();
	}
	
	public static AddFieldController getAddFieldController() {
		return addFieldController;
	}
	
	public static ConstraintAddController getConstraintAddController() {
		return constraintAddController;
	}
	
	public static CompositeConstraintController getCompositeConstraintController() {
		return compositeConstraintController;
	}
	
	public static TableBuilderController getTableBuilderController() {
		return tableBuilderController;
	}
	
	public static CompositeTableController getCompositeTableController() {
		return compositeTableController;
	}
}