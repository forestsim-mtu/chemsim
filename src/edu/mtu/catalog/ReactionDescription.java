package edu.mtu.catalog;

import java.util.ArrayList;
import java.util.List;

import edu.mtu.compound.Species;

/**
 * This class represents a single chemical equation.
 */
public class ReactionDescription {
	
	private List<String> products = new ArrayList<String>();
	private List<String> reactants = new ArrayList<String>();
	
	private double reactionRate = 0.0;
				
	/**
	 * Constructor.
	 */
	public ReactionDescription(List<String> reactants, List<String> products) {
		this.reactants = reactants;
		this.products = products;
	}

	/**
	 * Check to see if the reactants are part of this reaction description.
	 */
	public boolean checkReactants(Species one, Species two) {
		return reactants.contains(one.getFormula()) && reactants.contains(two.getFormula());
	}
	
	/**
	 * Get the products of this equation.
	 */
	public List<String> getProducts() {
		return products;
	}

	/**
	 * Get the reactants of this equation.
	 */
	public List<String> getReactants() {
		return reactants;
	}
	
	/**
	 * Get the reaction rate.
	 */
	public double getReactionRate() {
		return reactionRate;
	}
	
	/**
	 * Set the reaction rate.
	 */
	public void setReactionRate(double value) {
		reactionRate = value;
	}
}
