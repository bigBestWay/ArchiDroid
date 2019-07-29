package utils;

/**
 * @author - Tanjina Islam
 *
 * @date - 21-06-2019
 */
public class CaseConverter {

	/*
	 * Capitalize First letter of each word in a String
	 * 
	 */
	public static String camelCase(String str)
	{
		StringBuilder builder = new StringBuilder(str);
		// Flag to keep track if last visited character is a 
		// white space or not
		boolean isLastSpace = true;

		// Iterate String from beginning to end.
		for(int i = 0; i < builder.length(); i++)
		{
			char ch = builder.charAt(i);

			if(isLastSpace && ch >= 'a' && ch <='z')
			{
				// Character need to be converted to uppercase
				builder.setCharAt(i, (char)(ch + ('A' - 'a') ));
				isLastSpace = false;
			}
			else if (ch != ' ')
				isLastSpace = false;
			else
				isLastSpace = true;
		}

		return builder.toString();
	}

	/*
	 * Convert a String from ALL_CAPS to CamelCase
	 * Input String = "EXAMPLE_STRING" 
	 * Output String = "ExampleString"
	 */
	public static String allCapsToCamelCase(String input) {
		//String input = "ABC_DEF";
		StringBuilder sb = new StringBuilder();
		for( String oneString : input.split("_") )
		{
			sb.append( oneString.substring(0,1) );
			sb.append( oneString.substring(1).toLowerCase() );
		}
		return sb.toString();

	}

	public static String capatalizeFieldName(String fieldName) {
		final String result;
		if (fieldName != null && !fieldName.isEmpty()
				&& Character.isLowerCase(fieldName.charAt(0))
				&& (fieldName.length() == 1 || Character.isLowerCase(fieldName.charAt(1)))) {
			result =  camelCase(fieldName);
		} else {
			result = fieldName;
		}
		return result;
	}
}
