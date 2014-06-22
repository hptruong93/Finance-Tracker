package databaseAgent.queryComponents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utilities.IJsonable;
import utilities.functional.Filter;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import databaseAgent.queryBuilder.QueryBuilder;

public class RestrictionTree {

	private static final Set<String> JOINER = new HashSet<String>(Arrays.asList("AND", "OR", "XOR"));
	private static final Set<String> RESTRICTION_FUNCTIONS;
	private static final List<String> SPLITTER = new ArrayList<String>(Arrays.asList(
			">=", "<=", "<>", ">", "<", "=", "IN", "ILIKE", "LIKE"));
	
	static {
		HashSet<String> temp = new HashSet<String>();
		temp.add("NOT");
		RESTRICTION_FUNCTIONS = Collections.unmodifiableSet(temp);
	}
	
	public static void main(String[] args) {
		String a = "( type = :var11 )   OR   (    (  type = :var10  )   OR   (    (  type = :var9  )   OR   (    (  type = :var8  )   OR   (    (  type = :var7  )   OR   (    (  type = :var6  )   OR   (    (  type = :var5  )   OR   (    (  type = :var4  )   OR   (    (  type = :var3  )   OR   (  type = :var2 )  )  )  )  )  )  )  )  ) ";
		RestrictionNode n = parse(a);
		System.out.println(n.preOrder());
	}

	public static RestrictionNode parse(String restriction) {
		restriction = restriction.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ");
		//Remove mistakes caused by function in the condition itself, not the logical functions NOT, AND, XOR, OR
		for (String function : QueryBuilder.SUPPORTED_FUNCTIONS) {
			Matcher m = Pattern.compile(function + " \\(.+? \\) ").matcher(restriction);
			while (m.find()) {
				String found = restriction.substring(m.start(), m.end());
				restriction = restriction.replace(found, found.replaceAll(" ", ""));
			}
		}
		System.out.println(restriction);
		
		List<String> expression = Arrays.asList(restriction.split(" "));
		Collections.reverse(expression);
		Stack<String> temp = new Stack<String>();
		temp.addAll(new Filter<String>(){
			@Override
			public boolean filter(String item) {
				return !item.isEmpty();
			}}.filter(expression));
		
		Stack<String> post = toPostOrder(temp);
		return getNode(post);
	}

	private static RestrictionNode getNode(Stack<String> stack) {
		if (stack.isEmpty()) {
			return null;
		}
		String element = stack.pop();
		
		if (RESTRICTION_FUNCTIONS.contains(element)) {
			return new RestrictionNode(element, getNode(stack), null);
		} else if (JOINER.contains(element)) {
			RestrictionNode left = getNode(stack);
			RestrictionNode right = getNode(stack);
			return new RestrictionNode(element, left, right);
		} else {
			return new RestrictionNode(element);
		}
	}
	
	private static Stack<String> toPostOrder(Stack<String> expression) {
		Stack<String> stack = new Stack<String>();
		Stack<String> output = new Stack<String>();

		while (!expression.isEmpty()) {
			String element = expression.pop();
			if (element.equals("(")) {
				stack.push(element);
			} else if (element.equals(")")) {
				String next = stack.pop();
				while (!next.equals("(")) {
					output.push(next);
					if (stack.isEmpty()) {
						break;
					}
					next = stack.pop();
				}
				if (!stack.isEmpty()) {
					if (RESTRICTION_FUNCTIONS.contains(stack.peek())) {
						output.push(stack.pop());
					}
				}
			} else if (JOINER.contains(element)) {
				if (!stack.isEmpty()) {
					String next = stack.peek();
					while (JOINER.contains(next)) {
						output.push(next);
						if (!stack.isEmpty()) {
							next = stack.pop();
						} else {
							break;
						}
					}
				} 
				stack.push(element);
			} else if (RESTRICTION_FUNCTIONS.contains(element)) {
				stack.push(element);
			} else {
				String val = element;
				while (!expression.isEmpty()) {
					String next = expression.peek();
					if (next.equals("IN")) {
						while (true) {
							if (next.equals(")")) {
								break;
							}
							next = expression.pop();
							val += " " + next;
						}
						break;
					} else if (!next.equals("(") && !next.equals(")") && 
							!RESTRICTION_FUNCTIONS.contains(next) && !JOINER.contains(next)) {
						val += " " + next;
						expression.pop();
					} else {
						break;
					}
				}
				output.push(val);
			}
		}
		
		while (!stack.isEmpty()) {
			String next = stack.pop();
			if (JOINER.contains(next)) {
				output.push(next);
			}
		}
		
		return output;
	}

	public static class RestrictionNode implements IJsonable {
		String value;
		RestrictionNode left, right;

		public RestrictionNode(String value, RestrictionNode left, RestrictionNode right) {
			this.value = value;
			this.left = left;
			this.right = right;
		}

		public RestrictionNode(String value) {
			this.value = value;
		}

		public RestrictionNode(JsonNode node) {
			try {
				value = node.getStringValue("field");
				value += " " + node.getStringValue("condition");
				value += " " + node.getStringValue("value");
			} catch (Exception e) {
				value = node.getStringValue("function");
				if (!RESTRICTION_FUNCTIONS.contains(value)) {
					left = new RestrictionNode(node.getNode("left"));
					right = new RestrictionNode(node.getNode("right"));
				} else {
					left = new RestrictionNode(node.getNode("left"));
					right = null;
				}
			}
		}
		
		public boolean isLeaf() {
			return left == null && right == null;
		}

		public String preOrder() {// Left, right, this
			if (isLeaf()) {
				return value;
			} else {
				String output = value;
				String leftExp = (left == null) ? "" : (", " + left.preOrder());
				String rightExp = (right == null) ? "" : (", " + right.preOrder());
				
				return output + leftExp + rightExp;
			}
		}
		
		@Override
		public String toString() {
			if (isLeaf()) {
				return "(" + value + ")";
			} else {
				String leftExp = (left == null) ? "" : left.toString();
				String rightExp = (right == null) ? "" : right.toString();
				if (!RESTRICTION_FUNCTIONS.contains(value)) {
					return "(" + leftExp + " " + value + " " + rightExp + ")";
				} else {
					return "(" + value + leftExp + ")";
				}
			}
		}

		@Override
		public JsonRootNode jsonize() {
			if (isLeaf()) {
				for (String split : SPLITTER) {
					String[] parts = value.split(" " + split + " ");
					
					if (parts.length != 1) {
						return JsonNodeFactories.object(JsonNodeFactories.field("field", JsonNodeFactories.string(parts[0])),
								JsonNodeFactories.field("condition", JsonNodeFactories.string(split)),
								JsonNodeFactories.field("value", JsonNodeFactories.string(parts[1])));
					}
				}
				return JsonNodeFactories.object(JsonNodeFactories.field("field", JsonNodeFactories.string("")),
						JsonNodeFactories.field("condition", JsonNodeFactories.string("")),
						JsonNodeFactories.field("value", JsonNodeFactories.string(value)));
			} else {
				if (!RESTRICTION_FUNCTIONS.contains(value)) {
					return JsonNodeFactories.object(JsonNodeFactories.field("function", JsonNodeFactories.string(value)),
							JsonNodeFactories.field("left", left.jsonize()),
							JsonNodeFactories.field("right", right.jsonize()));
				} else {
					return JsonNodeFactories.object(JsonNodeFactories.field("function", JsonNodeFactories.string(value)),
							JsonNodeFactories.field("left", left.jsonize()));
				}
			}
		}
	}
}
