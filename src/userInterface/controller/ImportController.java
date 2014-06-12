package userInterface.controller;

import importer.ImportVerifier;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import purchases.DataManager;
import purchases.Purchase;
import purchases.PurchaseSet;
import purchases.Type;
import utilities.DateUtility;

public class ImportController implements Initializable {

	@FXML private Label lStatus;
	
	@FXML private Button bImport;
	@FXML private Button bSelectFile;
	
	@FXML private TextField tfLocation;
	@FXML private TextField tfDate;
	
	@FXML private TextField tfDesc0;
	@FXML private TextField tfDesc1;
	@FXML private TextField tfDesc2;
	@FXML private TextField tfDesc3;
	@FXML private TextField tfDesc4;
	@FXML private TextField tfDesc5;
	@FXML private TextField tfDesc6;
	@FXML private TextField tfDesc7;
	@FXML private TextField tfDesc8;
	@FXML private TextField tfDesc9;
	
	@FXML private TextField tfQuantity0;
	@FXML private TextField tfQuantity1;
	@FXML private TextField tfQuantity2;
	@FXML private TextField tfQuantity3;
	@FXML private TextField tfQuantity4;
	@FXML private TextField tfQuantity5;
	@FXML private TextField tfQuantity6;
	@FXML private TextField tfQuantity7;
	@FXML private TextField tfQuantity8;
	@FXML private TextField tfQuantity9;
	
	@FXML private TextField tfUnit0;
	@FXML private TextField tfUnit1;
	@FXML private TextField tfUnit2;
	@FXML private TextField tfUnit3;
	@FXML private TextField tfUnit4;
	@FXML private TextField tfUnit5;
	@FXML private TextField tfUnit6;
	@FXML private TextField tfUnit7;
	@FXML private TextField tfUnit8;
	@FXML private TextField tfUnit9;
	
	@FXML private TextField tfCost0;
	@FXML private TextField tfCost1;
	@FXML private TextField tfCost2;
	@FXML private TextField tfCost3;
	@FXML private TextField tfCost4;
	@FXML private TextField tfCost5;
	@FXML private TextField tfCost6;
	@FXML private TextField tfCost7;
	@FXML private TextField tfCost8;
	@FXML private TextField tfCost9;
	
	@FXML private ComboBox<String> cbbType0;
	@FXML private ComboBox<String> cbbType1;
	@FXML private ComboBox<String> cbbType2;
	@FXML private ComboBox<String> cbbType3;
	@FXML private ComboBox<String> cbbType4;
	@FXML private ComboBox<String> cbbType5;
	@FXML private ComboBox<String> cbbType6;
	@FXML private ComboBox<String> cbbType7;
	@FXML private ComboBox<String> cbbType8;
	@FXML private ComboBox<String> cbbType9;
	
	private ArrayList<ComboBox<String>> types;
	private ArrayList<TextField> descriptions;
	private ArrayList<TextField> quantities;
	private ArrayList<TextField> units;
	private ArrayList<TextField> costs;
	
	private DataManager manager;
	private ImportVerifier verifier;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		manager = new DataManager();
		verifier = new ImportVerifier();
		types = new ArrayList<ComboBox<String>>(Arrays.asList(
				cbbType0, cbbType1, cbbType2, cbbType3, cbbType4, cbbType5, cbbType6, cbbType7, cbbType8, cbbType9));
		descriptions = new ArrayList<TextField>(Arrays.asList(
				tfDesc0, tfDesc1, tfDesc2, tfDesc3, tfDesc4, tfDesc5, tfDesc6, tfDesc7, tfDesc8, tfDesc9));
		quantities = new ArrayList<TextField>(Arrays.asList(
				tfQuantity0, tfQuantity1, tfQuantity2, tfQuantity3, tfQuantity4, tfQuantity5, tfQuantity6, tfQuantity7, tfQuantity8, tfQuantity9));
		units = new ArrayList<TextField>(Arrays.asList(
				tfUnit0, tfUnit1, tfUnit2, tfUnit3, tfUnit4, tfUnit5, tfUnit6, tfUnit7, tfUnit8, tfUnit9));
		costs = new ArrayList<TextField>(Arrays.asList(
				tfCost0, tfCost1, tfCost2, tfCost3, tfCost4, tfCost5, tfCost6, tfCost7, tfCost8, tfCost9));
		
		for (ComboBox<String> cbbType : types) {
			cbbType.getItems().addAll(Type.PURCHASE_TYPES);
		}
	}

	@FXML
	private void importPressed(ActionEvent e) {
		lStatus.setText("");
		
		if (!verifier.verifyLocation(tfLocation.getText())) {
			lStatus.setText("Invalid Location!");
			return;
		} else if (!verifier.verifyDate(tfDate.getText())) {
			lStatus.setText("Invalid date!");
			return;
		}

		HashSet<Purchase> adding = new HashSet<Purchase>();
		for (int i = 0; i < descriptions.size(); i++) {
			String description = descriptions.get(i).getText();
			String type = types.get(i).getValue();
			String quantity = quantities.get(i).getText();
			String unit = units.get(i).getText();
			String cost = costs.get(i).getText();
			
			if (verifier.verifyPurchase(description, type, quantity, unit, cost)) {
				float qtt = Float.parseFloat(quantity);
				float cst = Float.parseFloat(cost);
				
				adding.add(new Purchase(description, type, qtt, unit, cst));
			} else {
				lStatus.setText("Item number " + (i + 1) +" is invalid!!!");
			}
		}
		if (adding.size() > 0) {
			PurchaseSet toAdd = new PurchaseSet(tfLocation.getText(), DateUtility.parseDate(tfDate.getText()), adding);
			manager.addPurchaseSet(toAdd);
		} else if (lStatus.getText().length() == 0) {
			lStatus.setText("Nothing to add");
		}
	}
	
	@FXML
	private void selectFile(ActionEvent e) {
		
	}
}
