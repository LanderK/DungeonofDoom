package dod;

import java.text.ParseException;


/**
 * Class to handle command line arguments and initialise the correct instances.
 */
public class Program {
	/**
	 * Main method, used to parse the command line arguments.
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {

		try {
		
			Server server = null;

			switch (args.length) {
				case 0 :
					// No Command line arguments - default map
					server = new Server("defaultMap");
					break;

				case 1 :
					//1 Argument that shoud be the name of the map to be loaded
					server = new Server(args[0]);
					break;

				default :
					System.err
							.println("The wrong number of arguments have been provided, you can either specify "
									+ System.getProperty("line.separator")
									+ " the name of the map you want to play on, or followed by nothing to play the defaultMap");
					break;
			}

			//user.run();
		} catch (final ParseException e) {
			System.err.println("Syntax error on line " + e.getErrorOffset()
					+ ":" + System.getProperty("line.separator")
					+ e.getMessage());
			System.exit(2);
		} catch (final Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
