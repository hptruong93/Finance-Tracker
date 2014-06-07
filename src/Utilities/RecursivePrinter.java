package utilities;

import java.util.Collection;
import java.util.Map;

public class RecursivePrinter {
	
	private boolean ignoreType; 
	
	public RecursivePrinter(boolean ignoreType) {
		this.ignoreType = ignoreType;
	}
	
	public String print(Object o) {
		StringBuilder output = new StringBuilder(""); 
		print(o, output);
		return output.toString();
	}
	
	private void print(Object o, StringBuilder builder) {
		if (o instanceof Collection<?>) {
			if (!ignoreType) {
				builder.append("Collection: ");
			}
			
			builder.append("[");
			for (Object sub : (Collection<?>) o) {
				print(sub, builder);
				builder.append(", ");
			}
			builder.append("] ");
		} else if (o instanceof Map<?, ?>) {
			if (!ignoreType) {
				builder.append("Map: ");
			}
			builder.append("{");
			for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
				print(entry.getKey(), builder);
				builder.append(":");
				print(entry.getValue(), builder);
				builder.append(", ");
			}
			builder.append("} ");
		} else if (o.getClass().isArray()) {
			if (!ignoreType) {
				builder.append("Array: ");
			}
			builder.append("[");
			for (Object sub : (Object[]) o) {
				print(sub, builder);
				builder.append(", ");
			}
			builder.append("] ");
		} else {
			builder.append(o.toString());
		}
	}
}
