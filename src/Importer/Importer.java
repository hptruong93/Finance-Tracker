package Importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Utilities.LogWriter;

public class Importer {

	public static final DateFormat FORMAT_DATE = new SimpleDateFormat("dd/MM/yyyy");
	private static final int COL_INDEX = 0;
	private static final int COL_DATE = 1;
	private static final int COL_DESCRIPTION = 2;
	private static final int COL_TYPE = 3;
	private static final int COL_QUANTITY = 4;
	private static final int COL_SUBCOST = 5;
	private static final int COL_TOTAL_COST = 6;

	private ArrayList<DataSet> purchases;

	public Importer(String filePath) {
		this.purchases = new ArrayList<DataSet>();
		this.purchases = readFile(filePath);
		if (this.purchases == null) {
			LogWriter.writeLog("Cannot load file");
			System.exit(0);
		}

//		System.out.println(this.purchases.size());
//		for (DataSet set : this.purchases) {
//			 System.out.println("Having " + set.size());
//		}
	}

	public Iterator<DataSet> getIterator() {
		return purchases.iterator();
	}
	
	private static ArrayList<DataSet> readFile(String filePath) {
		ArrayList<DataSet> output = new ArrayList<DataSet>();

		try {
			FileInputStream file = new FileInputStream(new File(filePath));

			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Process all sheets
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				output.addAll(readSheet(workbook.getSheetAt(i)));
			}

			file.close();
		} catch (FileNotFoundException e) {
			LogWriter.writeException(e);
		} catch (IOException e) {
			LogWriter.writeException(e);
		}
		return output;
	}

	private static ArrayList<DataSet> readSheet(XSSFSheet sheet) {
		// Key is the starting row, value is the ending row of the merge
		HashMap<Integer, Integer> mergedRegion = new HashMap<>();

		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress mRegion = sheet.getMergedRegion(i);

			int fromRow = mRegion.getFirstRow();
			int toRow = mRegion.getLastRow();

			// System.out.println(fromRow + " to " + toRow);

			if (!mergedRegion.containsKey(fromRow)) {
				mergedRegion.put(fromRow, toRow);
			}
		}

		ArrayList<DataSet> output = new ArrayList<>();
		
		// Iterate through each rows from first sheet
		Iterator<Row> rowIterator = sheet.iterator();
		// Ignore first row since it's the title row
		try {
			rowIterator.next();
		} catch (NoSuchElementException ex) {
			LogWriter.writeException(ex);
			return output;
		}

		int rowCount = 1, startRow = 999, endRow = 999;
		Date currentDate = null;
		double currentTotalCost = -1;
		DataUnit currentDataUnit = null;
		DataSet currentDataSet = null;

		while (rowIterator.hasNext()) {
			// System.out.println("We are at line " + rowCount);
			Row row = rowIterator.next();

			if (mergedRegion.containsKey(rowCount)) {// Start merge
				currentDataSet = new DataSet();
				startRow = rowCount;
				endRow = mergedRegion.get(rowCount);
			} else if (!isInMiddleOfMerge(startRow, rowCount, endRow)) {
				startRow = 999;
			}

			int cellCount = 0;
			Date unitDate = null;
			String unitDescription = null, unitType = null, unitQuantity = null;
			double unitSubCost = 0;

			// For each row, iterate through each columns
			Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				if (cellCount != 0) {
					if (cellCount == COL_DATE) {
						if (isInMiddleOfMerge(startRow, rowCount, endRow)) {
//							System.out.println(FORMAT_DATE.format(currentDate) + " is the date");
						} else {
							currentDataSet = new DataSet();
							if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
								try {
									currentDate = FORMAT_DATE.parse(cell.getStringCellValue());
								} catch (ParseException e) {
									e.printStackTrace();
									System.exit(0);
								}
							} else {
								currentDate = cell.getDateCellValue();
							}
//							System.out.println(FORMAT_DATE.format(currentDate) + " is the date");
						}
						unitDate = currentDate;
					} else if (cellCount == COL_DESCRIPTION) {
						// System.out.println(cell.getStringCellValue() +
						// " is the description");
						unitDescription = cell.getStringCellValue();
					} else if (cellCount == COL_TYPE) {
						// System.out.println(cell.getStringCellValue() +
						// " is the type");
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							unitType = cell.getNumericCellValue() + "";
						} else {
							unitType = cell.getStringCellValue();
						}
					} else if (cellCount == COL_QUANTITY) {
						// System.out.println(cell.getStringCellValue() +
						// " is the quantity");
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							unitQuantity = cell.getNumericCellValue() + "";
						} else {
							unitQuantity = cell.getStringCellValue();
						}
					} else if (cellCount == COL_SUBCOST) {
						// System.out.println(cell.getNumericCellValue() +
						// " is the subcost");
						unitSubCost = cell.getNumericCellValue();
					} else if (cellCount == COL_TOTAL_COST) {
						if (isInMiddleOfMerge(startRow, rowCount, endRow)) {
							// System.out.println(currentTotalCost +
							// " is the total cost");
						} else {
							currentTotalCost = cell.getNumericCellValue();
							// System.out.println(currentTotalCost +
							// " is the total cost");
						}
					}
				} else if (cellCount == COL_INDEX) {
					if (!isInMiddleOfMerge(startRow, rowCount, endRow)
							&& cell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
						for (DataSet set : output) {
							set.conclude();
						}
						return output;
					}
				}
				cellCount++;
			}

			currentDataUnit = new DataUnit(unitDate, unitDescription, unitType, unitQuantity,
					unitSubCost);
			if (isInMiddleOfMerge(startRow, rowCount, endRow)) {
				currentDataSet.addItem(currentDataUnit);
				if (isEndMerge(endRow, rowCount)) {
					output.add(currentDataSet);
				}
			} else {
				currentDataSet = new DataSet(currentDataUnit, currentTotalCost);
				if (!isStartingMerge(startRow, rowCount)) {
					output.add(currentDataSet);
				}
			}

			// System.out.println("");
			rowCount++;
		}

		for (DataSet set : output) {
			set.conclude();
		}

		return output;
	}

	private static boolean isEndMerge(int endRow, int rowCount) {
		return endRow == rowCount;
	}

	private static boolean isStartingMerge(int startRow, int rowCount) {
		return startRow == rowCount;
	}

	private static boolean isInMiddleOfMerge(int startRow, int rowCount, int endRow) {
		return rowCount > startRow && rowCount <= endRow && startRow != -1;
	}
}
