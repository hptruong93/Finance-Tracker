package userProfile;

import java.util.Date;
import java.util.UUID;

import utilities.DateUtility;
import utilities.IJsonable;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class UserProfile implements IJsonable {
	private UUID id;
	private String name;
	private int queryMade;
	private Date lastActivity;
	
	public UserProfile(String name, UUID id) {
		this.id = id;
		this.name = name;
		lastActivity = DateUtility.getToday();
		queryMade = 0;
	}
	
	public UserProfile(String name) {
		this(name, UUID.randomUUID());
	}
	
	public UserProfile(JsonNode node) {
		this.name = node.getStringValue("name");
		this.queryMade = Integer.parseInt(node.getNumberValue("query_made"));
		this.lastActivity = DateUtility.parseDate(node.getStringValue("last_activity"));
		this.id = UUID.fromString(node.getStringValue("id"));
	}
	
	public void touch() {
		lastActivity = DateUtility.getToday();
	}
	
	public void query() {
		touch();
		queryMade++;
	}
	
	public void reset() {
		touch();
		queryMade = 0;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string(name)),
				JsonNodeFactories.field("id", JsonNodeFactories.string(id.toString())),
				JsonNodeFactories.field("last_activity", JsonNodeFactories.string(DateUtility.dateToString(lastActivity))),
				JsonNodeFactories.field("query_made", JsonNodeFactories.number(queryMade)));
	}
}
