package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogWriter {

	private LogWriter() {
	}

	public static void writeLog(String toWrite) {
		System.out.println(toWrite);
	}

	public static void writeException(Exception ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		writeLog(sw.toString());
	}
}
