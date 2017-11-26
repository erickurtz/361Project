import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.*;

public class Simulator {
	private static final int SUB_INDEX = 2;
	private static final int P1 = 1; 
	private static final int P2 = 2; 
	
	//State constants for consistency 
	private static final String INP = "InputQueue"; 
	private static final String HQ1 =  "HoldQueue1";
	private static final String HQ2 = "HoldQueue2"; 
	private static final String REJ = "Rejected"; 
	private static final String RED = "ReadyQueue (Proc)"; 
	private static final String WAIT = "WaitQueue(Proc)"; 
	private static final String RUN = "Running(Proc)"; 
	private static final String DONE = "CompleteQueue"; 
	
	int time; 
	int totalMem;
	int serialDev;
	int quant; 
	int availMem;
	Process currProcess = null; 
	LinkedList<Job> submitQueue = new LinkedList<Job>(); 
	LinkedList<Job> allJobs = new LinkedList<Job>(); 
	LinkedList<Job> firstHoldQueue = new LinkedList<Job>(); 
	LinkedList<Job> secondHoldQueue = new LinkedList<Job>(); 
	LinkedList<Process> completeQueue = new LinkedList <Process>(); 
	LinkedList<Process> deviceWaitQueue = new LinkedList<Process>();
	Queue<Process> readyQueue = new LinkedList<Process>(); 
	Queue<Request> requestQueue = new LinkedList<Request>(); 
	Queue<Request> releaseQueue = new LinkedList<Request>(); 
	
	public Simulator(int t, int mm, int ser, int q) {
		
		this.time = t; 
		this.serialDev = ser; 
		this.quant = q;
		this.availMem = totalMem;
	}


	
	//Parses the line of the file
	//Consumes: line -> line denoting what to do 
	//Produces: Nothing, adjusts Simulation based on input
	
	
	public void requestDevices(Request r) {
		
		
		
		
		if(this.currProcess!= null && r.jobNumReq == this.currProcess.getAjob().jobNum) {
			if(this.serialDev <= r.devReqd) {
				this.currProcess.setCurrResources(this.currProcess.getCurrResources() + r.devReqd);
				this.serialDev -= r.devReqd; 
			}else {
				System.out.println("Not enough devices available.");
			}
		}else {
			System.out.println("Job not on CPU");
		}
	}
	
	public void releaseDevices(Request r) {
		Job j1 = null; 
		if(this.currProcess != null && r.jobNumReq == this.currProcess.getAjob().getJobNum()) {
			j1 = this.currProcess.getAjob(); 
		}else { 
			for(Process p : readyQueue) {
				if(p.getAjob().getJobNum() == r.jobNumReq) {
					j1 = p.getAjob(); 
					break; 
				}
			} 
			for(Process p: deviceWaitQueue) {
				if(p.getAjob().getJobNum() == r.jobNumReq) {
					j1 = p.getAjob(); 
					break; 
				}
			}	
		}
		if(j1 != null) {
			j1.setDev(r.devReqd);
			this.serialDev+= r.devReqd;
		}else {
			System.out.println("Error: Process not found. Cannot Release devices");
		}
	}
	
	
	public void inputJob(Job j) {
		 if (j.priority == P1) {
			this.firstHoldQueue.add(j);
			j.setCurrState(HQ1);
			
		}else {
			this.secondHoldQueue.add(j); 
			j.setCurrState(HQ2);
		}
	}
	
	
	public void checkHoldQueues() {
		for(Job j : this.firstHoldQueue) {
			if (this.availMem >= j.memReq) {
				this.availMem-= j.memReq; 
				j.setCurrState(WAIT);
				this.deviceWaitQueue.add(new Process(j)); 
				checkWaitQueue(); 
			}
		}
		
	}
	
	
	public void deallocateProcess() {
		currProcess.getAjob().setCurrState(DONE);
		this.availMem+= currProcess.getCurrMem(); 
		this.serialDev+= currProcess.getCurrResources();
	}
	
	
	
	public void checkWaitQueue() {
		
		for (Process p : this.deviceWaitQueue) {
			if(p.getAjob().getDev() <= this.serialDev) {
				
				int diff = p.getAjob().getMemReq() 
						- p.getCurrResources();
				p.setCurrResources(diff);
				this.serialDev-=diff; 
				this.readyQueue.add(p);
				p.getAjob().setCurrState(RED);
				
			}
		}
	}
	
	public void checkRequestQueue() {
		for (Request r: requestQueue) {
			if (this.time <= r.timeReq) {
				this.requestDevices(r);
				this.requestQueue.remove(r);
			}
		}
	}
	
	public void checkReleaseQueue() {
		for (Request r: releaseQueue) {
			if (this.time <= r.timeReq) {
				this.releaseDevices(r);
				this.requestQueue.remove(r);
			}
		}
	}
	
	//Todo: handle process scheduling in round robin way 
	//implement Bankers algorithm 
	

	public void onTick(String line) {
		//handle internal events first
		checkRequestQueue(); 
		checkReleaseQueue(); 
		this.time++;

		if (this.currProcess != null) {
			this.currProcess.timeRemaining--; //for the last cycle...

		
			if (this.currProcess.timeRemaining == 0) {
				deallocateProcess(); 
				checkWaitQueue(); 
				checkHoldQueues(); 
				this.currProcess = this.readyQueue.remove(); 
			
			}
		}
		
		for (Job j : submitQueue) {
			if (j.getArrivTime() == this.time) {
				inputJob(j);
			}
					
		}
		Collections.sort(firstHoldQueue, new Comparator <Job> () {

			@Override
			public int compare(Job o1, Job o2) {
				
				return o1.runTime-o2.runTime;
			}
			
		});
		
	}
	

}
