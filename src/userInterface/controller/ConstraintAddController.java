package userInterface.controller;

import java.net.URL;
import java.sql.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import userInterface.StageMaster;
import utilities.Mapper;
import utilities.Util;
import dataAnalysis.DataQuery;

public class ConstraintAddController implements Initializable {

	@FXML protected Button bCancel;
	@FXML protected Button bAdd;
	@FXML protected ComboBox<String> cbbField;
	@FXML protected ComboBox<String> cbbField2;
	@FXML protected ComboBox<String> cbbCondition;
	@FXML protected TextField tfValue;
	@FXML protected Label lStatus;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		cbbCondition.getItems().addAll(DataQuery.SUPPORTED_CONDITION);
		cbbField.getItems().add("");
		cbbField.getItems().addAll((new Mapper<String, String>() {
			@Override
			public String map(String input) {
				String[] split = input.split("\\.");
				return split[split.length - 1];
			}
		}).map(DataQuery.FIELD_LIST));
		
		cbbField2.getItems().addAll(cbbField.getItems());
	}

	public void addPressed(ActionEvent e) {
		String field = cbbField.getValue();
		String condition = cbbCondition.getValue();
		String value = tfValue.getText();
		
		Criterion identified = ConstraintParser.parseConstraint(field, condition, value);
		if (identified != null) {
			if (StageMaster.getQueryController().cbAddComposite.isSelected()) {
				StageMaster.getCompositeConstraintController().addConstraint(identified);
			} else {
				int id = MainController.getInstance().dataQuery.addCriterion(identified);
				StageMaster.getQueryController().constraintID.add(id);
				
				StageMaster.getQueryController().lvConstraints.getItems().add(identified.toString());
				StageMaster.getQueryController().cbAddComposite.setDisable(false);
			}
		} else {
			lStatus.setText("Invalid condition!");
		}
	}
	
	public void cancelPressed(ActionEvent e) {
		lStatus.setText("");
		StageMaster.getQueryController().cbAddComposite.setDisable(false);
		StageMaster.addConstraint().hide();
		StageMaster.primaryStage().show();
		StageMaster.primaryStage().toBack();
	}

	private static class ConstraintParser {
		
		private static final HashMap<String, Class<?>> TYPES;
		static {
			TYPES = new HashMap<String, Class<?>>();
			TYPES.put("id", Integer.class);
			TYPES.put("location", String.class);
			TYPES.put("date", Date.class);
			TYPES.put("description", String.class);
			TYPES.put("type", String.class);
			TYPES.put("quantity", Integer.class);
			TYPES.put("unit", String.class);
			TYPES.put("cost", Float.class);
			TYPES.put("purchase_set_id", Integer.class);
		}
		
		/**
		 * Parse the information to give out a hibernate criterion.
		 * This only takes into account simple conditions
		 * 
		 * Special processing for certain information:
		 * EQUAL with multiple values will be joined by OR
		 * NOT_EQUAL with multiple values will be joined by AND
		 * LIKE and ILIKE with multiple values will be joined by OR
		 * 
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
					Disjunction or = Restrictions.disjunction();
					for (Object parsed : parsedValue) {
						or.add(Restrictions.eq(field, parsed));
					}
					out = or;
					break;
				case "NOT_EQUAL":
					Conjunction and = Restrictions.conjunction();
					for (Object parsed : parsedValue) {
						and.add(Restrictions.ne(field, parsed));
					}
					out = and;
					break;
				case "GREATER_THAN":
					out = Restrictions.gt(field, parsedValue[0]);
					break;
				case "LESS_THAN":
					out = Restrictions.lt(field, parsedValue[0]);
					break;
				case "LIKE":
					or = Restrictions.disjunction();
					for (Object parsed : parsedValue) {
						or.add(Restrictions.like(field, parsed));
					}
					out = or;
					break;
				case "ILIKE":
					or = Restrictions.disjunction();
					for (Object parsed : parsedValue) {
						or.add(Restrictions.ilike(field, parsed));
					}
					out = or;
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
