package purchases;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utilities.FileUtility;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;

public class Type {
	private static final String CONFIG_FILE_PURCHASE = FileUtility.joinPath("data", "purchase_types.json");
	private static final String CONFIG_FILE_UNIT = FileUtility.joinPath("data", "unit_types.json");
	public static final Type PURCHASE_TYPES_TREE;
	public static final Type UNIT_TYPES_TREE;
	public static final List<String> PURCHASE_TYPES;
	public static final List<String> UNIT_TYPES;
	
	private String name;
	private Set<Type> subtypes;
	
	static {
		JsonRootNode root = FileUtility.readJSON(new File(CONFIG_FILE_PURCHASE));
		PURCHASE_TYPES_TREE = Type.parsePurchaseType("purchase", root);
		PURCHASE_TYPES = Collections.unmodifiableList(PURCHASE_TYPES_TREE.getBottomTypes());
		
		root = FileUtility.readJSON(new File(CONFIG_FILE_UNIT));
		UNIT_TYPES_TREE = Type.parsePurchaseType("unit", root);
		UNIT_TYPES = Collections.unmodifiableList(UNIT_TYPES_TREE.getBottomTypes());
	}
	
	private Type(String name, Set<Type> subtypes) {
		this.name = name;
		this.subtypes = subtypes;
	}
	
	private void addSubTypes(Type newSubType) {
		subtypes.add(newSubType);
	}
	
	private static Type parsePurchaseType(String name, JsonNode foodType) {
		Map<JsonStringNode, JsonNode> subs = foodType.getFields();
		Type toReturn = new Type(name, new HashSet<Type>());
		for (JsonStringNode key : subs.keySet()) {
			toReturn.addSubTypes(parsePurchaseType(key.getText(), subs.get(key)));
		}
		return toReturn;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Find the PurchaseType with the given name in a given PurchaseType
	 * @param name name of the purchase type to find
	 * @param rootTree the current PurchaseType that we are interested in
	 * @return the PurchaseType found, or null if not found
	 */
	public Type findType(String name) {
		Type output = null;
		if (name.equals(this.name)) {
			output = this;
		} else {
			for (Type t : subtypes) {
				output = t.findType(name);
				if (output != null) {
					break;
				}
			}
		}
		return output;
	}

	/**
	 * Check if subtype is a subtype of master. E.g. check if "fish" is subtype of "food"
	 * @param subtype name of the purchase type that would be the subtype
	 * @param master name of the purchase type that would be the larger type
	 * @param root root of the type tree
	 * @return if subtype is actually a subtype of master type.
	 */
	public static boolean isType(String subtype, String master, Type root) {
		Type masterType = root.findType(master);
		
		Type temp = masterType.findType(subtype);
		return temp != null;
	}
	
	/**
	 * Convert the type tree to list. List representation will
	 * lose the tree relations between the components though
	 * @return list contains all the subtypes of this type, and this type itself
	 */
	public List<String> toList() {
		List<String> output = new LinkedList<String>();
		output.add(name);
		
		if (subtypes.isEmpty()) {
			return output;
		} else {
			for (Type subtype : subtypes) {
				output.addAll(subtype.toList());
			}
		}
		
		return output;
	}
	
	public List<String> getBottomTypes() {
		ArrayList<String> output = new ArrayList<String>();
		
		if (this.subtypes.isEmpty()) {
			output.add(this.name);
		} else {
			for (Type t : this.subtypes) {
				output.addAll(t.getBottomTypes());
			}
		}
		
		return output;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
    		return false;
    	}
    	if (object == this) {
    		return true;
    	}
    	if (!(object instanceof Type)) {
    		return false;
    	}
    	return ((Type)object).name.equals(this.name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
