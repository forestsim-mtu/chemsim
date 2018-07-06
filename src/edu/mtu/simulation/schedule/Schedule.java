package edu.mtu.simulation.schedule;

import java.util.ArrayDeque;

/**
 * The schedule is based upon a ring buffer, but modified so that there is always
 * a marker node that ensures a callback is made to the simulation.  
 */
public class Schedule {
		
	// Flags to indicate shutdown
	private boolean halt;				// Shut down now
	private boolean stopping;			// Shut down at end of time ste	
	private boolean stopped;			// Schedule is complete
	
	// Current time step of the schedule
	private int timeStep;
	private int runTill;
	
	// Pointer to the simulation
	private Simulation simulation;
	
	private ArrayDeque<Steppable> schedule;
	
	/**
	 * Constructor.
	 */
	public Schedule() {
		schedule = new ArrayDeque<Steppable>();
		stopped = true;
	}
	
	/**
	 * Get the count of nodes in the schedule.
	 */
	public int getCount() {
		return schedule.size();
	}
	
	/**
	 * Get the current time step number.
	 */
	public int getTimeStep() {
		return timeStep;
	}
	
	/**
	 * Clears the schedule completely, simulation finish will be called.
	 */
	public void halt() {
		halt = true;
		stopped = true;
		schedule.clear();
		simulation.finish(halt);
	}
	
	/**
	 * Insert a new steppable at the beginning of the next time step.
	 */
	public void insert(Steppable steppable) {
		schedule.add(steppable);
	}
		
	/**
	 * Remove the node indicated from the schedule.
	 */
	public void remove(Steppable steppable) {
		steppable.deactivate();
	}
	
	/**
	 * 
	 * @param simulation
	 * @param runTill
	 */
	public void start(Simulation simulation, int runTill) {
		// Check to make sure a simulation was provided
		if (simulation == null) {
			throw new IllegalArgumentException("The simulation cannot be null");
		}
		
		// Set the relevant flags and pointers
		halt = false;
		stopped = false;
		stopping = false;
		timeStep = 0;
		this.runTill = runTill;
		this.simulation = simulation;
		
		// Add the escapement as the final entry
		schedule.add(new Escapement());
		
		// Run the schedule
		while (schedule.size() > 0) {			
			Steppable steppable = schedule.remove();			
			if (steppable.isActive()) {
				steppable.doAction(timeStep);
				schedule.add(steppable);
			}
		}
	
		// Perform clean-up operations
		if (!halt) {
			simulation.finish(halt);
		}
		stopped = true;
	}
	
	/**
	 * Signals the schedule to that it should stop at the end of the current time step.
	 */
	public void stop() {
		stopping = true;
	}
	
	/**
	 * Returns true if the schedule is stopped, false otherwise.
	 */
	public boolean stopped() {
		return stopped;
	}
		
	/**
	 * Wrapper for the steppable that represents the end of a single time step.
	 * 
	 * This offers us a very minor performance gain under very large schedules
	 * by avoiding the instanceof check
	 */
	private class Escapement extends Steppable {
		@Override
		public void doAction(int step) { 
			timeStep++;
			simulation.step(timeStep, runTill);
			if (timeStep == runTill || stopping) {
				schedule.clear();
				deactivate();
			}
		} 
	}
}
