package edu.mtu.simulation.decay;

public class DecayDto {
	public DecayDto() { } 
	
	public DecayDto(double slope, double mols) {
		this.slope = slope;
		this.mols = mols;
	}
	
	public double slope;
	public double mols;
}