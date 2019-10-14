# ChemSim
ChemSim is an agent-based modeling (ABM) approach to computational chemistry. ChemSim is primarily a Java application uses a custom scheduler to manage agents, although still contains some code based upon the use of [MASON Multiagent Simulation Toolkit](http://cs.gmu.edu/~eclab/projects/mason/).

# User Guide
## Execution
The simulation makes use of premain instrumentation and needs to be launched with the following parameters:

  -javaagent:lib/SizeOf.jar 

Depending upon the molecule count, tuning of the JVM will be needed to either ensure enough space or prevent overcollection by the GC. One starting point is the following, but they are by no means guaranteed to be ideal:

 -Xms4G  -XX:+UseParallelGC -XX:NewRatio=4

## Command Line Parameters
The following command line parameters are supproted by the simulation:

| Short | Long | Required | Description |
| --- | --- | --- | --- | 
| -c \[file] | --chemicals \[file] | Yes | CSV file with compounds present at start of experiment | 
| -r \[file] | --reactions \[file] | Yes | CSV file with reactions to be modeled | 
| -dt \[number] | | No | The delta T in seconds, default | 
| -l \[number] | --limit \[number] | No | The maximum number of molecules to generate at initlization. |
| -n \[number] |--run \[number] | No | The run number to apply to results files |
| -p \[number] | --padding \[number] | No | The number of seconds to pad the estimated time by, default 900 seconds |
| -w \[number] | --write \[number] | No | The report interval to print / save status on, default 60 iterations |
| -t \[number] | --terminate \[formula] | No | Terminate the model when the given molecule has zero entities |
| -v \[number] | --verify \[file] \[file] | No | Verify the \[reactions] and \[chemicals] files are properly formatted and has balanced reactions |

### Launch Examples
With linear decay of hydrogen peroxide,
> java -javaagent:lib/SizeOf.jar -jar ChemSim.jar -c experiment/chemicals.csv -r experiment/reactions.csv -l 1000000

## CSV File Format
### Input
Two CSV files need to be supplied to the simulation via the command line upon execution. The first defines the state of the reactor at the start of the simulation. This file starts with three header lines, followed by the header for the compound listing, followed by at least one starting compound.

**chemicals.csv** - Inputs defining the state of the reactor
<pre>
Volume, 1.0
Rate, -1.0E02
Percentage, 1	
Name, Formula, Mols
Hydrogen Peroxide, H2O2, 1.0
Acetone, CH3COCH3, 0.1
</pre>

The `Volume` of the reactor is assumed to be in liters. The `Rate` is the rate of decay of hydrogen peroxide when it undergoes photolysis and is assumed to be in mM/L/min. The `Percentage` field relates to the caging effect is and an unsupported feature. The default value of one is recommended.

The second file expected is the reactions list. This file should contain a header similar to the following:

<pre>
Reactant, Reactant, Product, Product, k, Ratio, pKa
</pre>

Where `Reactant` is one or more reactants for a given reaction, which results in one or more `Product`s. The value `k` defines the reaction rate constant. In the case of multiple possible pathways, `Ratio` indicates the ratio that they are expected to appear (implemented as a probability) with one (1) being the default ratio if one is not supplied. Finally, `pKa` is the negative base-10 logarithmic acid dissociation constant for the reaction. When the `pKa` is given, `k` and `Ratio` should be left blank, and vice versa, as shown in the example:

**reactions.csv** - Example reactions file, as a table for clarity

| Reactant | Reactant | Product | Product | k | Ratio | pKa |
| --- | --- | --- | --- | --- | --- | --- | 
| A |	B |	C |  |  1.00E+10 | | |
| A |	C |	D |  |  1.40E+09 | 0.75 | |
| A |	C	| 2E | F | 1.40E+09	| 0.25 | |
| D | | G | H+ | | | 11.6 |

When parsing the reactions, the simulation will assume that numeric prefixes to products should indicate that it represents the quantity of the product to be generated (i.e., A + B -> 2C is the same as A + B -> C + C).

### Output
Three files are generated as output by the simulation. First, the console is always echoed to the file `console.txt`, which can be found in the same location as the JAR file. Upon start-up the directory `molecules` is created in the same location as the JAR file. This allows the count of molecules to be tracked at the path `molecules/results-*.csv` where the asterisk is replaced by the supplied run number, or defaults to one (1). This file begins with the time stamp of model execution, followed by a header, and finally the numeric data. The first column is the `Time` step, in seconds past simulation start. Upon termination of the model, the folder `mols` is created and the `molecules/results-*.csv` file is converted from molecule counts into mols. 

Users should be aware that the number of molecules is determined by the number of molecules permitted upon model initialization. As such, the user of the molecule to mol scaler (displayed on the console) is needed to convert between molecules and mols.

# Developer Guide
## Development Environment
The following is the development environment:

- Eclipse IDE Neon Release (4.6.0 or 4.6.1)
- Java SE SDK 8 (JavaSE-1.8)

The SizeOf JAR file is included and is responsible for benchmarking the size of objects when the model starts. This is used to control how many agents are created. All other dependences are managed through the MAVEN POM file.

The following Eclipse plug-ins are recommended for developers wishing to see all documentation:

- ObjectAid UML Explorer for Eclipse (1.1.11)

## Branches
Two branches currently exist: `spatial` is the primary branch that experiments are run against. New development should branch `spatial` and request a merge to it. The `master` branch is retained in the repository, but is currently inactive for development and experimental work.
