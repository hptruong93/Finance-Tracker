package queryAgent;



public class ConstraintBuilder {
	
	public static IntegerConstraint getNumberConstraint(String variableName) {
		return new IntegerConstraint(variableName);
	}
	
	public static DoubleConstraint getDoubleConstraint(String variableName) {
		return new DoubleConstraint(variableName);
	}
	
	public static StringConstraint getStringConstraint(String variableName) {
		return new StringConstraint(variableName);
	}
	
	public static DateConstraint getDateConstraint(String variableName) {
		return new DateConstraint(variableName);
	}

	private static abstract class Constraint {
		protected String variableName;
		
		private Constraint(String variableName) {
			this.variableName = variableName;
		}
	}
	
	public static class DoubleConstraint extends Constraint {
		private DoubleConstraint(String variableName) {
			super(variableName);
		}
		
		public String greaterThan(double a) {
			return variableName + " > " + a;
		}
		
		public String lessThan(double a) {
			return variableName + " < " + a;
		}
		
		public String equalTo(double a) {
			return variableName + " = " + a;
		}
	}
	
	public static class IntegerConstraint extends Constraint {
		private IntegerConstraint(String variableName) {
			super(variableName);
		}
		
		public String greaterThan(int a) {
			return variableName + " > " + a;
		}
		
		public String lessThan(int a) {
			return variableName + " < " + a;
		}
		
		public String equalTo(int a) {
			return variableName + " = " + a;
		}
	}
	
	public static class StringConstraint extends Constraint {
		private StringConstraint(String variableName) {
			super(variableName);
		}
		
		public String equalTo(String a) {
			return variableName + " = " + a;
		}
		
		public String like(String a) {
			return variableName + " LIKE " + a;
		}
		
		public String iLike(String a) {
			return variableName + " ILIKE " + a;
		}
		
	}

	public static class DateConstraint extends Constraint {
		private DateConstraint(String variableName) {
			super(variableName);
		}
	}
}
