package userInterface;

import queryAgent.QueryBuilder;


public class Test {
	public static void main(String[] args) {
		System.out.println(new QueryBuilder().buildGroupBy("SUM(location)"));
	}
}
