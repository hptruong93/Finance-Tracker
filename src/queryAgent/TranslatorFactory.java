package queryAgent;

public class TranslatorFactory {
	
	public static final int STANDARD_TRANSLATOR = 0;
	public static final int FEATURED_TRANSLATOR = 1;

	private static final HQLTranslator standardTranslator = new HQLTranslator();
	private static final FeaturedHQLTranslator featuredTranslator = new FeaturedHQLTranslator();
	
	public static HQLTranslator getTranslator(int flavor) {
		if  (flavor == STANDARD_TRANSLATOR) {
			return standardTranslator;
		} else if (flavor == FEATURED_TRANSLATOR) {
			return featuredTranslator;
		}
		return null;
	}
}
