package edu.mtu.simulation;

/**
 * This class contains various properties related to how the model should behave. 
 * These properties are managed by the ChemSim class and may change during model
 * execution or between model runs.
 */
public class ModelProperities {
	// Default time step durations for disproporting species
	private int fastPathway = 1;
	private int mediumPathway = 2;
	private int slowPathway = 3;	
		
	// Reactor volume in liters
	private double reactorVolume;
	
	// How intense the UV is - drives the hydrogen peroxide decay rate
	private double uvIntensity = 0.025;

	// Values needed to ensure hydrogen peroxide exhibits a linear decay based upon the UV intensity
	private double hydrogenPeroxideDecay = 0;
	private long hydrogenPeroxideDecayQuantity = 0;

	
	public int getFastPathway() {
		return fastPathway;
	}
	
	public double getHydrogenPeroxideDecay() {
		return hydrogenPeroxideDecay;
	}
	
	public long getHydrogenPeroxideDecayQuantity() {
		return hydrogenPeroxideDecayQuantity;
	}
	
	public int getMediumPathway() {
		return mediumPathway;
	}

	public double getReactorVolume() {
		return reactorVolume;
	}
	
	public int getSlowPathway() {
		return slowPathway;
	}
	
	public double getUvIntensity() {
		return uvIntensity;
	}
	
	public void setFastPathway(int value) {
		fastPathway = value;
	}
			
	public void setHydrogenPeroxideDecay(double value) {
		hydrogenPeroxideDecay = value;
	}
	
	public void setHydrogenPeroxideDecayQuantity(long value) {
		hydrogenPeroxideDecayQuantity = value;
	}
			
	public void setMediumPathway(int value) {
		mediumPathway = value;
	}

	public void setReactorVolume(double value) {
		reactorVolume = value;
	}
	
	public void setSlowPathway(int value) {
		slowPathway = value;
	}
	
	public void setUvIntensity(double value) {
		this.uvIntensity = value;
	}
}
