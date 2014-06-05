package userInterface.controller;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import userInterface.StageMaster;
import utilities.Util;

public class ConstraintAddController implements Initializable {

	@FXML private Button bCancel;
	@FXML private Button bAdd;
	@FXML private ComboBox<String> cbbField;
	@FXML private ComboBox<String> cbbCondition;
	@FXML private TextField tfValue;
	@FXML private Label lStatus;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}

	public void addPressed(ActionEvent e) {
		String field = cbbField.getValue();
		String condition = cbbCondition.getValue();
		String value = tfValue.getText();
		
		Criterion identified = ConstraintParser.parseConstraint(field, condition, value);
		if (identified != null) {
			MainController.getInstance().dataQuery.addConstraint(identified);
		} else {
			lStatus.setText("Invalid condition!");
		}
	}
	
	public void cancelPressed(ActionEvent e) {
		lStatus.setText("");
		StageMaster.addConstraint().hide();
	}
	
	private static class ConstraintParser {
		private static final List<String> SUPPORTED_CONDITION = 
				new ArrayList<String>(Arrays.asList("BETWEEN", "EQUAL", "NOT_EQUAL", "GREATER_THAN", "LESS_THAN", 
						"LIKE", "IS_EMPTY",
						"IS_NOT_EMPTY", "IS_NOT_NULL", "IS_NULL"));
		private static final HashMap<String, Class> TYPES;
		static {
			TYPES = new HashMap<String, Class>();
			TYPES.put("ID", Integer.class);
			TYPES.put("Location", String.class);
			TYPES.put("Date", Date.class);
			TYPES.put("Description", String.class);
			TYPES.put("Type", String.class);
			TYPES.put("Quantity", Integer.class);
			TYPES.put("Unit", String.class);
			TYPES.put("Cost", Float.class);
			TYPES.put("Purchase set id", Integer.class);
		}
		
		/**
		 * Parse the information to give out a hibernate criterion.
		 * This only takes into account simple conditions
		 * @param field name of the field
		 * @param condition condition type
		 * @param value value of the condition
		 * @return a hibernate criterion. Null if there is any error in input
		 */
		private static Criterion parseConstraint(String field, String condition, String value) {
			Criterion out;
			String[] values;
			if (value.contains(",")) {
				values = value.split(",");
			} else {
				values = new String[]{value};
			}
			Object[] parsedValue = new Object[values.length];
			Class<?> toParse = TYPES.get(field);
			try {			
				if (toParse == Date.class) {
					for (int i = 0; i < values.length; i++) {
						parsedValue[i] = Util.parseDate(values[i]);
					}
				} else if (toParse == Integer.class) {
					for (int i = 0; i < values.length; i++) {
						parsedValue[i] = Integer.parseInt(values[i]);
					}
				} else if (toParse == Float.class) {
					for (int i = 0; i < values.length; i++) {
						parsedValue[i] = Float.parseFloat(values[i]);
					} 
				} else if (toParse == String.class) {
					for (int i = 0; i < values.length; i++) {
						parsedValue[i] = values[i];
					} 
				}
			} catch (Exception e) {
				parsedValue = null;
			}
			
			try {
				switch (condition) {
				case "BETWEEN":
					out = Restrictions.between(field, parsedValue[0], parsedValue[1]);
					break;
				case "EQUAL":
					out = Restrictions.eq(field, parsedValue[0]);
					break;
				case "NOT_EQUAL":
					out = Restrictions.ne(field, parsedValue[0]);
					break;
				case "GREATER_THAN":
					out = Restrictions.gt(field, parsedValue[0]);
					break;
				case "LESS_THAN":
					out = Restrictions.lt(field, parsedValue[0]);
					break;
				case "LIKE":
					out = Restrictions.like(field, parsedValue[0]);
					break;
				case "IS_EMPTY":
					out = Restrictions.isEmpty(field);
					break;
				case "IS_NOT_EMPTY":
					out = Restrictions.isNotEmpty(field);
					break;
				case "IS_NOT_NULL":
					out = Restrictions.isNotNull(field);
					break;
				case "IS_NULL":
					out = Restrictions.isNull(field);
					break;
				default:
					return null;
				}
				return out;
			} catch (Exception e) {
				return null;
			}
		}
	}
}
