package Importer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import utilities.LogWriter;

public class ReaderExcel {

	private static BufferedWriter bw;

	public static void main(String[] args) {
		String pathOut = "D:\\output.txt";
		File write = new File(pathOut);
		FileWriter fw;
		try {
			FileOutputStream fos = new FileOutputStream(pathOut);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "Unicode");

			fw = new FileWriter(write);
			bw = new BufferedWriter(osw);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final String path = "D:\\IN HOA DON 2013.xlsx";
		readFile(path);

		try {
			bw.close();
		} catch (IOException ex) {

		}

		FileReader fr;
		try {
			fr = new FileReader(write);
			BufferedReader br = new BufferedReader(fr);
			while (true) {
				String line = br.readLine();
				if (line != "" && line != null) {
					System.out.println(line);
				} else {
					break;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void readFile(String filePath) {
		try {
			FileInputStream file = new FileInputStream(new File(filePath));

			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Process all sheets
			// for (int i = 0; i < 1; i++) {// workbook.getNumberOfSheets();
			// i++) {
			readSheet(workbook.getSheetAt(0));
			// }

			file.close();
		} catch (FileNotFoundException e) {
			LogWriter.writeException(e);
		} catch (IOException e) {
			LogWriter.writeException(e);
		}
	}

	private static void readSheet(XSSFSheet sheet) {
		// MST : 14, 10
		// Dia chi: 12,9
		// Ten don vi: 11,9
		final int[] MST = { 14, 10 };
		final int[] ADDRESS = { 12, 9 };
		final int[] NAME = { 11, 9 };

		// Iterate through each rows from first sheet
		Iterator<Row> rowIterator = sheet.iterator();

		for (int i = 0; rowIterator.hasNext() && i < 20; i++) {
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			// For row 15, get to column K
			for (int j = 0; cellIterator.hasNext() && j < 20; j++) {
				Cell theCell = cellIterator.next();

				if (i == MST[0] && j == MST[1]) {
					System.out.println("MST : " + theCell.getStringCellValue());
					print("MST : " + theCell.getStringCellValue());
				} else if (i == ADDRESS[0] && j == ADDRESS[1]) {
					System.out.println("Address: " + theCell.getStringCellValue());
					print("Address: " + theCell.getStringCellValue());
				} else if (i == NAME[0] && j == NAME[1]) {
					System.out.println("Name: " + theCell.getStringCellValue());
					print("Name: " + theCell.getStringCellValue());
				}

				// try {
				// System.out.println(i+","+j + ": " +
				// theCell.getStringCellValue());
				// } catch (Exception e) {
				// try {
				// System.out.println(i+","+j + ": " +
				// theCell.getNumericCellValue());
				// } catch (Exception ee) {
				// try {
				// System.out.println(i+","+j + ": " +
				// theCell.getCellFormula());
				// } catch (Exception ex) {
				// System.out.println("Invalid " + theCell.getCellType());
				// }
				// }
				// }
			}
		}
	}

	private static void print(String content) {
		try {
			bw.write(content + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			e.printStackTrace();
		}
	}
}
