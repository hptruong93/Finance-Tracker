package userInterface.controller.visualizer;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import userInterface.StageMaster;
import utilities.Log;
import utilities.RecursivePrinter;

public class TableVisualizer implements IDataVisualizer {

	private TableView<String> tool;
	private RecursivePrinter printer;
	
	public TableVisualizer(TableView<String> tool) {
		this.tool = tool;
		printer = new RecursivePrinter(true);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void visualize(Object data) {
		tool.getColumns().clear();
		tool.getItems().clear();
		final ArrayList<?> rowList = (ArrayList<?>) data;
		int column = 1;

		//If query has no result
		if (rowList.size() == 0) {
			column = 0;
		}

		//First row always exists unless there is no result, which is caught above
		Object firstRow = rowList.get(0);
		
		if (firstRow.getClass().isArray()) {//If there are multiple columns in the row
			Object[] toPrint = (Object[]) firstRow;
			column = toPrint.length;
		} else {
			// One column. We can safely deduce that there is one row only.
			// If there were two or more rows with one column in each row, 
			// the result would have been in arrays (of 1 element),
			// which is the above case
			column = 1;
		}
		
		/***********Finish getting number of column*****************************************/
		List<String> columnNames = null;
		//Now double check with query fields list that the number of column is correct
		if (column != StageMaster.getQueryController().lvFields.getItems().size()) {
			Log.exception(new IllegalArgumentException("Result inconsistent with query..."));
			return;
		} else {//Get column info
			 columnNames = StageMaster.getQueryController().lvFields.getItems();
		}
		
		/**********************************
         * ADD TABLE COLUMN DYNAMICALLY *
         **********************************/
		
		for (int i = 0 ; i < column; i++) {
            //We are using non property style for making dynamic table
            final int currentColumn = i;               
            TableColumn tableColumn = new TableColumn(columnNames.get(i));
            tableColumn.setCellValueFactory(
            new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {                   
                @Override
				public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {                                                                                             
                    return new SimpleStringProperty(param.getValue().get(currentColumn).toString());                       
                }                   
            });
            
            tool.getColumns().addAll(tableColumn);
        }
		
        /********************************
         * Adding data to ObservableList *
         ********************************/
		ObservableList rowData = FXCollections.observableArrayList();
        for (Object singleRow : rowList) {
        	ObservableList row = FXCollections.observableArrayList();
        	if (singleRow.getClass().isArray()) {
        		Object[] real = (Object[]) singleRow;
				// Iterate Row
				for (int i = 0; i < column; i++) {
					// Iterate Column
					row.add(printer.print(real[i]));
				}
        	} else {
        		row.add(printer.print(singleRow));
        	}
        	rowData.add(row);
        }

        //FINALLY ADDED TO TableView
        tool.setItems(rowData);
	}	
}
