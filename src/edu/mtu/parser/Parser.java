package edu.mtu.parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import edu.mtu.reaction.ReactionDescription;

/**
 * This class is used to parse the equation(s) that are present in an import file for their reaction.
 */
public class Parser {
	public static final double INVALID_ENTRY_VALUE = -999.999;
	
	/**
	 * Read the chemicals from the file indicated.
	 * 
	 * @param fileName The full path to the file.
	 * @return A list of reactions.
	 */
	public static List<ChemicalDto> parseChemicals(String fileName) throws IOException {
		CSVReader reader = null;
		
		try {
			// Open the file, discard volume and rate
			reader = new CSVReader(new FileReader(fileName));
			reader.readNext();
			reader.readNext();
			
			// Scan until we reach the chemicals header
			String[] entries;
			while ((entries = reader.readNext()) != null) {
				if (entries[0].toUpperCase().equals("NAME")) {
					break;
				}
			}
			if (entries == null) {
				System.err.println("File provided does not contain the chemicals header.");
				throw new IOException("Invalid ChemSim chemicals file.");
			}
			
			// Load the entries
			List<ChemicalDto> results = new ArrayList<ChemicalDto>();
			while ((entries = reader.readNext()) != null) {
				// Check to see if this is an empty line
				if (entries[0].isEmpty()) {
					continue;
				}
				
				results.add(new ChemicalDto(entries[0], entries[1], Double.parseDouble(entries[2])));
			}
			
			// Return the results
			return results;
		} finally {
			if (reader != null) reader.close();	
		}		
	}
	
	/**
	 * Parse the reaction rate from the file indicated.
	 * 
	 * @param fileName The full path to the file.
	 * @return The reaction rate (i.e., slope) from the file, assumed to be in mols/L
	 */
	public static double parseRate(String fileName) throws IOException {
		CSVReader reader = null;
		
		try {
			// Open the file, discard volume
			reader = new CSVReader(new FileReader(fileName));
			reader.readNext();
			
			// Second entry should be the rate
			String[] entries = reader.readNext();
			if (!entries[0].toUpperCase().equals("RATE")) {
				System.err.println("File provided does not contain the rate on line two.");
				throw new IOException("Invalid ChemSim chemicals file.");
			}
			
			// Return the result
			return Double.parseDouble(entries[1]);
		} finally {
			if (reader != null) reader.close();
		}
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static double parseIntercept(String fileName) throws IOException {
		CSVReader reader = null;
		
		try {
			// Open the file, discard volume
			reader = new CSVReader(new FileReader(fileName));
			reader.readNext();
			
			// Second entry should be the rate and intercept
			String[] entries = reader.readNext();
			if (!entries[0].toUpperCase().equals("RATE")) {
				System.err.println("File provided does not contain the rate on line two.");
				throw new IOException("Invalid ChemSim chemicals file.");
			}
			
			// Check for the intercept, return null if one isn't present
			if (entries.length != 3){
				return INVALID_ENTRY_VALUE;
			}
			
			// Return the result
			return Double.parseDouble(entries[2]);
		} finally {
			if (reader != null) reader.close();
		}
	}
	
	/**
	 * Parse the percentage of hydroxyl radicals that should be retained.
	 * 
	 * @param fileName The full name and path of the file.
	 * @return The value as a decimal percentage (ex., 0.50).
	 * 
	 * @exception IllegalArgumentException Thrown if the file does not contain a "Percentage" field.
	 */
	public static double parseHydroxylPercentage(String fileName) throws IOException, IllegalArgumentException {
		CSVReader reader = null;
		
		try {
			// Open the file
			reader = new CSVReader(new FileReader(fileName));
			
			// Read until we find the value, or the "Name" marker for the chemicals listing
			String[] entries;
			while ((entries = reader.readNext()) != null) {
				// Match found
				if (entries[0].toUpperCase().equals("PERCENTAGE")) {
					return Double.parseDouble(entries[1]);
				}
				
				// Start of chemicals found
				if (entries[0].toUpperCase().equals("NAME")) {
					throw new IllegalArgumentException("File does not appear to contain 'percentage' entry.");
				}
			}
			
			throw new IOException("Invalid ChemSim chemicals file.");
		} finally {
			if (reader != null) reader.close();
		}
	}
	
	/**
	 * Parse the volume of the experiment from the file indicated.
	 * 
	 * @param fileName The full path to the file.
	 * @return The value in the units from the file, assumed to be liters.
	 */
	public static double parseVolume(String fileName) throws IOException {
		CSVReader reader = null;

		try {
			// Open the file
			reader = new CSVReader(new FileReader(fileName));

			// First entry should be the volume
			String[] entries = reader.readNext();
			if (!entries[0].toUpperCase().equals("VOLUME")) {
				System.err.println("File provided does not contain the volume on line one.");
				throw new IOException("Invalid ChemSim chemicals file.");
			}
			
			// Return the result
			return Double.parseDouble(entries[1]);
		} finally {
			if (reader != null)	reader.close();
		}
	}
	
	/**
	 * Read the reactions from the file indicated.
	 * 
	 * @param fileName The full path to the file.
	 * @return A list of reactions.
	 */
	// TODO Update this to ignore empty last columns
	public static List<ReactionDescription> parseReactions(String fileName) throws IOException {
		// Open the file
		CSVReader reader = new CSVReader(new FileReader(fileName));
		
		// Read the header to note the number of items
		String[] enteries = reader.readNext();
		int reactants = 0;
		while (enteries[reactants].toUpperCase().equals("REACTANT")) {
			reactants++;
		}
		int products = 0;
		while (enteries[reactants + products].toUpperCase().equals("PRODUCT")) {
			products++;
		}
		
		// Check to see if there are odds associated with the file
		boolean oddsColumn = enteries[enteries.length - 1].toUpperCase().equals("RATIO");
		
		List<ReactionDescription> results = new ArrayList<ReactionDescription>();
		while ((enteries = reader.readNext()) != null) {
			// Check to see if the line is commented out
			if (enteries[0].startsWith("#")) {
				continue;
			}
			
			// Check to see if this is an empty line
			if (enteries[0].isEmpty()) {
				continue;
			}
			
			// Process the reactants
			List<String> reactant = new ArrayList<String>();
			for (int ndx = 0; ndx < reactants; ndx++) {
				if (!enteries[ndx].isEmpty()) {
					reactant.add(enteries[ndx].trim());	
				}
			}
						
			// Process the products
			List<String> product = new ArrayList<String>();
			for (int ndx = 0; ndx < products; ndx++) {
				product.addAll(parseProduct(enteries[reactants + ndx].trim()));
			}				
			
			// Note the reaction rate
			double reactionRate = Double.parseDouble(enteries[reactants + products]);
						
			if (oddsColumn) {
				double reactionOdds = Double.parseDouble(enteries[reactants + products + 1]);
				results.add(new ReactionDescription(reactant, product, reactionRate, reactionOdds));				
			} else {
				results.add(new ReactionDescription(reactant, product, reactionRate));	
			}			
		}
		
		// Close and return
		reader.close();
		return results;
	}
	
	/**
	 * Parse the given product into multiples, if appropriate.
	 */
	private static List<String> parseProduct(String product) {
		List<String> results = new ArrayList<String>();
		
		// Do we have any work to do?
		if (product.isEmpty()) {
			return results;
		}
		
		// Is there only a single product?
		if (!Character.isDigit(product.charAt(0))) {
			results.add(product);
			return results;
		}

		// Parse the number and return the appropriate count of products
		int count = 0;
		while (Character.isDigit(product.charAt(count))) { 
			count++;
		}
		String formula = product.substring(count);
		count = Integer.parseInt(product.substring(0, count));
		for (int ndx = 0; ndx < count; ndx++) {
			results.add(formula);
		}		
		return results;
	}
}
