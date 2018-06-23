package edu.mtu.compound;

import java.util.List;

import edu.mtu.reaction.ReactionDescription;
import edu.mtu.reaction.ReactionRegistry;
import edu.mtu.primitives.Int3D;
import edu.mtu.reactor.Reactor;
import edu.mtu.simulation.ChemSim;

public class MoleculeFactory {

	/**
	 * Create a molecule of the given type and ensure it is scheduled (or not) appropriately.
	 * 
	 * @param formula of the molecule to be created.
	 * @param location of the molecule to be created.
	 */
	public static void create(String formula, Int3D location) {
		// First update out count
		ChemSim.getTracker().update(formula, 1);
		
		// Return if it doesn't have any reactants
		if (!ReactionRegistry.getInstance().hasReactants(formula)) {
			return;
		}
		
		// Create and schedule the molecule
		Molecule entity = new Molecule(formula);
		ChemSim.getSchedule().insert(entity);
		Reactor.getInstance().insert(entity, location);
	}
	
	/**
	 * Process the list of formulas and create new molecules from them.
	 * 
	 * @param formulas of the molecules to be created.
	 * @param location of the molecule to be created.
	 */
	public static void create(String[] formulas, Int3D location) {
		for (String formula : formulas) {
			create(formula, location);
		}
	}

	/**
	 * Create a disproportionating molecule and ensure it is add to the schedule.
	 * 
	 * @param reactions associated with the molecule.
	 * @param location of the molecule.
	 */
	public static void create(Molecule one, Molecule two, List<ReactionDescription> reactions, Int3D location) {
		ReactionDescription[] rd = new ReactionDescription[reactions.size()];
		rd = reactions.toArray(rd);
		Molecule entity = DisproportionatingMolecule.create(one, two, rd);
		ChemSim.getSchedule().insert(entity);
		Reactor.getInstance().insert(entity, location);
		ChemSim.getTracker().update(entity.getFormula(), 1);
	}
}