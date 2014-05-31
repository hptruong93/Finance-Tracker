package dataAnalysis;

import java.util.ArrayList;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import queryAgent.QueryAgent;

public class Features {
	public static void main(String[] args) {
		try {
			DataQuery a = new DataQuery();
			a.clearFields();
			a.addField(a.DESCRIPTION, a.DATE);
//			 a.setFunction("SUM", "cost");
			// a.addConstraint(Restrictions.eq("location", "provigo"));

			ArrayList<?> temp = (ArrayList<?>) a.query();

			for (Object c : temp) {
				String out = ReflectionToStringBuilder.toString(c);
				System.out.println(interested(out));
			}
		} finally {
			QueryAgent.closeFactory();
		}
	}

	private static String interested(String input) {
		input = input.replace("{", "").replace("}", "");
		int a = input.indexOf("=");

		if (a == -1) {
			int i = input.indexOf(".");
			while (i < input.length()) {
				if (input.charAt(i) == '[') {
					i++;
					break;
				}
				i++;
			}

			return input.substring(i, input.length() - 1).replace(",", ", ");
		} else {
			return input.substring(a + 1, input.length() - 1).replace(",", ", ");
		}
	}
}
