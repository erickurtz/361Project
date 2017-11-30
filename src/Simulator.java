//@Author Eric 

//TODO: Bankers algorithm, RoundRobin algorithm to put process on CPU. 

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
		this.availMem = mm;
		this.totalMem = mm; 
	}


	
	//RequestDevices: Request --> void
	//Consumes: Request r, the request being made
	//Produces: void 
	//Takes in a request. checks whether corresponding process is on CPU. if so, fulfills request. 
	public void requestDevices(Request r) {
		if(this.currProcess!= null && r.jobNumReq == this.currProcess.getAjob().jobNum) {
			if(this.serialDev <= r.devReqd) {
				this.currProcess.setCurrResources(this.currProcess.getCurrResources() + r.devReqd);
				this.serialDev -= r.devReqd; 
				//need to remove from queue? check
			}else {
				System.out.println("Not enough devices available.");
			}
		}else {
			System.out.println("Job not on CPU");
		}
	}
	
	
	//ReleaseDevices: Request --> void
	//Consumes: Request r, the request being made
	//Produces: void 
	//Takes in a request. Finds corresponding job based on jobNum of request. Removes resources \
	//if job is found.
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
	
	//inputJob: Job j --> void 
	//consumes: Job j, job to be input
	//produces: void
	//Inputs a job in one of two hold queues.
	public void inputJob(Job j) {
		
		if (j.getMemReq() <= this.availMem) {
			this.availMem-= j.memReq; 
			j.setCurrState(WAIT,this.time);
			this.deviceWaitQueue.add(new Process(j)); 
			checkWaitQueue();
			 
		}else if (j.priority == P1) {
			this.firstHoldQueue.add(j);
			j.setCurrState(HQ1,this.time);
			
		}else {
			this.secondHoldQueue.add(j); 
			j.setCurrState(HQ2,this.time);
		}
	}
	
	//checkHoldQueues
	//consumes: void
	//produces: void 
	//Checks hold queues for whether jobs are ready to be added
	//Checks first queue, then second. 
	
	//IMPORTANT - If first job in queue is not able to be added, should the second job be added if
	//there is enough memory? PDF does not say, or I don't see it. 
	//Code is written as if this is the case.
	 
	void checkHoldQueues() {
		//check first
		boolean reject1 = false;
		
		Job j = this.firstHoldQueue.peekFirst(); 
		while(j!= null && !reject1) {
			if (this.availMem >= j.memReq) {
				this.availMem-= j.memReq; 
				j.setCurrState(WAIT,this.time);
				this.deviceWaitQueue.add(new Process(j)); 
				this.firstHoldQueue.removeFirst();
				j = this.firstHoldQueue.peekFirst(); 
				checkWaitQueue(); 
			}else {
				reject1 = true; 
			}
		}
		//check second
		boolean reject2 = false; 
		j = this.secondHoldQueue.peekFirst(); 
		while(j!= null && !reject2) {
			if (this.availMem >= j.memReq) {
				this.availMem-= j.memReq; 
				j.setCurrState(WAIT,this.time);
				this.deviceWaitQueue.add(new Process(j));
				this.secondHoldQueue.remove(j); 
				j = this.firstHoldQueue.peekFirst();
				checkWaitQueue(); 
			}else {
				reject2 = true; 
			}
		}
	}
	
	
	//deallocateProcess: void -> void
	//consumes: void 
	//produces: void
	public void deallocateProcess() {
		currProcess.getAjob().setCurrState(DONE,this.time);
		this.availMem+= currProcess.getCurrMem(); 
		this.serialDev+= currProcess.getCurrResources();
	}
	
	
	//checkWaitQueue: void --> void
	//consumes: void 
	//produces: void 
	//checks the waitQueue if there are enough resources to add to readyQueue
	
	public void checkWaitQueue() {
		
		for (Process p : this.deviceWaitQueue) {
			if(p.getAjob().getDev() <= this.serialDev) {
				
				int diff = p.getAjob().getMemReq() 
						- p.getCurrResources();
				p.setCurrResources(diff);
				this.serialDev-=diff; 
				this.readyQueue.add(p);
				p.getAjob().setCurrState(RED, this.time);
				
			}
		}
	}
	
	//checkRequestQueue: void -> void 
	//consumes: void 
	//produces: void 
	//checks requestQueue for requests at specific time.
	
	public void checkRequestQueue() {
		for (Request r: requestQueue) {
			if (this.time <= r.timeReq) {
				this.requestDevices(r);
				this.requestQueue.remove(r);
			}
		}
	}
	
	//checkReleaseQueue: void -> void 
	//consumes: void 
	//produces: void 
	//checks releaseQueue for requests at specific time.
	
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
	

	//addJob: Job -> void 
	//adds a job if there is space 
	
	public void addJob(Job j) { 
		this.allJobs.add(j);
		if(j.getMemReq() > this.totalMem) {
			j.setCurrState(REJ, this.time);
		}else {
			j.setCurrState(INP, this.time);
			this.submitQueue.add(j);
		}
	}
	
	public void finishProcess(Process p) {
		deallocateProcess(); 
		this.completeQueue.add(p);
		p.getAjob().setCurrState(DONE, this.time);
		this.currProcess = this.readyQueue.remove(); 
	
	}
	
	
	//onTick: void --> void 
	//consumes: void
	//produces: void 
	//To be run on every tick of the clock.

	public void onTick(String line) {
		//handle internal events first
		checkRequestQueue(); 
		checkReleaseQueue(); 
		

		if (this.currProcess != null) {
			this.currProcess.timeRemaining--; //for the last cycle...

		
			if (this.currProcess.timeRemaining == 0) {
				finishProcess(currProcess); 
				checkWaitQueue(); 
				checkHoldQueues(); 
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
		
		this.time++;
		
	}
	

}
