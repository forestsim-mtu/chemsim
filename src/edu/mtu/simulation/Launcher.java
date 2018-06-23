package edu.mtu.simulation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.mtu.system.EchoStream;
import net.sourceforge.sizeof.SizeOf;

public final class Launcher {
	/**
	 * Main entry point for the simulation.
	 */
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, IOException {

		// Echo to the console file 
		String filename = "console.txt";
		if (args.length > 0 && args[0].equals("-timestamp")) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss.SSSZ");
			filename = String.format("console.%s.txt", formatter.format(new Date()));
		}
		FileOutputStream out = new FileOutputStream(filename);
		EchoStream echo = new EchoStream(out);
		System.setOut(echo);
		
		// Configure SizeOf, note that the program MUST be invoked with -javaagent:lib/SizeOf.jar
		SizeOf.skipStaticField(true);
		SizeOf.setMinSizeToLog(10);

		// Load the arguments
		ParseArguments(args);
		SimulationProperties properties = SimulationProperties.getInstance();
		if (properties.getChemicalsFileName().equals("")) {
			System.err.println("Chemicals file not provided!");
			System.exit(-1);
		}
		if (properties.getReactionsFileName().equals("")) {
			System.err.println("Reactions file not provided!");
			System.exit(-1);
		}
		
		// Initialize the simulation
		long seed = System.currentTimeMillis();
		ChemSim instance = ChemSim.getInstance();
		instance.initialize(seed);
				
		// Run the simulation and exit
		int timeSteps = ChemSim.getProperties().getTimeSteps();
		instance.start(timeSteps);
	}

	private static void ParseArguments(String[] args) {
		boolean chemicals = false, reactions = false;
		boolean experimentalDecay = false;
		
		SimulationProperties properties = SimulationProperties.getInstance();
		String iteration = "";
		
		// Parse out the arguments
		for (int ndx = 0; ndx < args.length; ndx+=2) {
			switch(args[ndx]) {
			case "-c":
			case "--chemicals":
				properties.setChemicalsFileName(args[ndx + 1]);
				chemicals = true;
				break;
			case "-e":
			case "--experimental":
				properties.setExperimentalDataFileName(args[ndx + 1]);
				experimentalDecay = true;
				break;
			case "-p":
			case "--print":
				int interval = Integer.parseInt(args[ndx + 1]);
				properties.setReportInterval(interval);
				break;
			case "-r":
			case "--reactions":
				properties.setReactionsFileName(args[ndx + 1]);
				reactions = true;
				break;
			case "-n":
			case "--run":
				iteration = "-" + args[ndx + 1];
				break;
			case "-l":
			case "--limit":
				int limit = Double.valueOf(args[ndx + 1]).intValue();
				properties.setMoleculeLimit(limit);
				break;
			case "-s":
			case "--step":
				int value = Integer.parseInt(args[ndx + 1]);
				if (value > 60) {
					System.err.println("Time step cannot exceed 60 seconds.");
					System.exit(-1);
				}
				properties.setTimeStepLength(value);
				break;
			case "-t":
			case "--terminate":
				properties.addTerminationOn(args[ndx + 1]);
				break;
			default:
				System.err.println("Unknown argument, " + args[ndx]);
				printUsage();
				System.exit(-1);				
			}
		}
		
		// Make sure we have the parameters to run
		if (!(chemicals && reactions)) {
			printUsage();
			System.exit(-1);
		}
		
		// Apply the settings
		properties.setExperimentalDecay(experimentalDecay);
		properties.setMolarFileName(String.format(properties.getMolarFileName(), iteration));
		properties.setResultsFileName(String.format(properties.getResultsFileName(), iteration));
	}
	
	private static void printUsage() {
		String format = "%-25s %s\n";
		
		System.err.println("Usage: [ChemSim] [Parameters]");
		System.err.println("\nRequired:");
		System.err.printf(format, "-c, --chemicals [file]", "CSV file with compounds present at start of experiment");
		System.err.printf(format, "-r, --reactions [file]", "CSV file with reactions to be modeled");
		System.err.println("\nOptional: ");
		System.err.printf(format, "-e, --experimental [file]", "CSV file with the known experimental results for photolysis decay");
		System.err.printf(format, "-l, --limit [number]", "The maximum number of molecules to generate at initlization.");
		System.err.printf(format, "-n, --run [number]", "The run number to apply to results files");
		System.err.printf(format, "-p, --print [number]", "The minute interval to print / save status on, default 100");
		System.err.printf(format, "-s, --step [number", "The duration of the time step in seconds, default 60");
		System.err.printf(format, "-t, --terminate [formula]", "Terminate the model when the given molecule has zero entities");
		System.err.println("\nNOTE:");
		System.err.println("JAVAGENT initialization is required, -javaagent:lib/SizeOf.jar");
	}
}
