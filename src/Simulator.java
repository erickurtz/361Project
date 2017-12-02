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
	private static final int NUM_RES_TYPE = 1; 
	
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
	int totalSerDev;
	int quant; 
	int availMem;
	
	Process currProcess = null; 
	LinkedList<Job> allJobs = new LinkedList<Job>(); 
	LinkedList<Job> firstHoldQueue = new LinkedList<Job>(); 
	LinkedList<Job> secondHoldQueue = new LinkedList<Job>(); 
	LinkedList<Process> allActiveProcess = new LinkedList<Process>(); 
	LinkedList<Process> completeQueue = new LinkedList <Process>(); 
	LinkedList<Process> deviceWaitQueue = new LinkedList<Process>();
	Queue<Process> readyQueue = new LinkedList<Process>(); 
	public Simulator(int t, int mm, int ser, int q) {
		
		this.time = t; 
		this.serialDev = ser; 
		this.totalSerDev = ser; 
		this.quant = q;
		this.availMem = mm;
		this.totalMem = mm; 
	}


	
	//RequestDevices: Request --> void
	//Consumes: Request r, the request being made
	//Produces: void 
	//Takes in a request. checks whether corresponding process is on CPU. if so, fulfills request. 
	public void requestDevices(Request r) {
		Process theProc = null; 
		for(Process p: deviceWaitQueue) {
			if(p.getAjob().getJobNum() == r.jobNumReq) {
				theProc = p; 
				break; 
			}
		}
		
		if(theProc == null) {
			System.out.println("Process is not in waitQueue or has all"
					+ "devices needed");
			return; 
		}
		
		
		if(runBankersAlg(r)) { //bankersAlg
			int tba = theProc.getAjob().getDev() 
					- theProc.getCurrResources(); 
			theProc.setCurrResources( theProc.getAjob().getDev());
			this.serialDev -= tba; 
			readyQueue.add(theProc); 
		}else {
			System.out.println("Request denied for r.tostring()");
		}
	}
	
	
	
	
	//ReleaseDevices: Request --> void
	//Consumes: Request r, the request being made
	//Produces: void 
	//Takes in a request. Finds corresponding job based on jobNum of request. Removes resources \
	//if job is found.
	public void releaseDevices(Request r) {
		Process p1 = null; 
		if(this.currProcess != null && r.jobNumReq == this.currProcess.getAjob().getJobNum()) {
			p1 = this.currProcess; 
		}else { 
			for(Process p : readyQueue) {
				if(p.getAjob().getJobNum() == r.jobNumReq) {
					p1 = p;
					break; 
				}
			} 
			for(Process p: deviceWaitQueue) {
				if(p.getAjob().getJobNum() == r.jobNumReq) {
					p1 = p;
					break; 
				}
			}	
		}
		if(p1 != null) {
			p1.setCurrResources(p1.currResources- r.devReqd);
			this.serialDev+= r.devReqd;
		}else {
			System.out.println("Error: Process not found. Cannot Release devices");
		}
	}
	
	
	public Boolean runBankersAlg(Request r) {
		
		if(currProcess == null) {
			return true; //should never happen 
		}
		int totalProcesses = deviceWaitQueue.size();
		int available [] = {this.serialDev}; 
		int[][] max = new int [NUM_RES_TYPE] [totalProcesses];
		int [] [] allocation = new int [NUM_RES_TYPE] [totalProcesses];
		int [] [] need = new int [NUM_RES_TYPE] [totalProcesses];
		
		
		//NOTE: Writing as 2d arrays for "code maintainablity"
	
			for(int j = 0; j< max[1].length; j++) {
				max[1][j] = this.deviceWaitQueue.get(j).getAjob().getDev(); 
			}
		
		
	
			for(int j = 0; j< max[1].length; j++) {
				allocation[1][j] = this.deviceWaitQueue.get(j).getCurrResources();
				
			}
		
	
			for(int j = 0; j< max[1].length; j++) {
				need[1][j] = max[1][j] - allocation[1][j];
			}
		

		 
		int devAfterReq = this.serialDev-r.devReqd; 
		
		boolean canSatisfy = false; 
		
		//because there's only one resource in the entire system, I'm hardcoding
		//it in. 
		for(int i = 0; i <need[0].length; i++) {
			if (devAfterReq <= need[0][i]) {
				canSatisfy = true; 
			}
		}
		
		/* for ( Process p: this.deviceWaitQueue) {
			if(p.getResReqDiff() <= devAfterReq) {
				canSatisfy = true; 
			}
		} */ 
		
		return canSatisfy;
		
	}
	
	
	//inputJob: Job j --> void 
	//consumes: Job j, job to be input
	//produces: void
	//Inputs a job in one of two hold queues.
	public void inputJob(Job j) {
		
		if(j.getMemReq()> this.totalMem || j.getDev() > this.totalSerDev) {
			j.setCurrState(REJ, time);
		}else if (j.getMemReq() <= this.availMem) {
			this.availMem-= j.memReq; 
			j.setCurrState(WAIT,this.time);
			Process p = new Process(j);
			this.deviceWaitQueue.add(p); 
			this.allActiveProcess.add(p);
			checkWaitQueue();		 
		}else if (j.priority == P1) {
			this.firstHoldQueue.add(j);
			j.setCurrState(HQ1,this.time);
			
		}else {
			this.secondHoldQueue.add(j); 
			j.setCurrState(HQ2,this.time);
		}
		this.allJobs.add(j); 
		return; 
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

	//Todo: handle process scheduling in round robin way 
	//implement Bankers algorithm 
	

	
	public void finishProcess(Process p) {
		deallocateProcess(); 
		this.completeQueue.add(p);
		this.allActiveProcess.remove(p); 
		p.getAjob().setCurrState(DONE, this.time);
		this.currProcess = this.readyQueue.remove(); 
	
	}
	
	
	//onTick: void --> void 
	//consumes: void
	//produces: void 
	//To be run on every tick of the clock.

	public void onTick() {
		//handle internal events first

		if (this.currProcess != null) {
			this.currProcess.timeRemaining--; //for the last cycle...

		
			if (this.currProcess.timeRemaining == 0) {
				finishProcess(currProcess); 
				checkWaitQueue(); 
				checkHoldQueues(); 
			}else {
				
				//handle switching of processes, ROUND ROBIN ALG HERE
				
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
	
	public int readInteger(String inputArr[], int index) {
		return Integer.parseInt(inputArr[index].
				substring(SUB_INDEX, inputArr[index].length()));
		
		
	}
	
	public Process findProcess(Job j) {
		
		if( currProcess!= null && currProcess.getAjob().getJobNum() == j.getJobNum()) {
			return currProcess; 
		}else {
			for (Process p: readyQueue) {
				if(p.getAjob().getJobNum() == j.getJobNum()) {
					return p; 
				}
			}
			
			for (Process p: deviceWaitQueue) {
				if(p.getAjob().getJobNum() == j.getJobNum()) {
					return p; 
				}
			}
			
			System.out.println("Error: Process not found, check Job State");
			return null; 
		}
		
	}
	
	public int getWeightedTurnaround(Process p) {
		return 0; 
	}
	
	public int getTurnaround(Process p) {
		
		return 0; 
	}
	
	public String printState() {
		String tbr = "State of All Jobs Entered: \n"; 
		
		//first print all the jobs. Then print all Processes corresponding jobs
		
		
		for(Job j: this.allJobs) {
			String addTo = ""; 
			int wTTime = j.getRunTime(); 
			int TTime = j.getRunTime(); 
			
			switch (j.getCurrState()) {
			case(HQ1): 
				addTo = j.toString();
				break;
			case(HQ2): 
				addTo = j.toString();
			case(REJ): 
				wTTime = -1; 
				TTime = -1; 
				addTo = j.toString();
				break; 
			case(WAIT): 
			case(RED):
			case(RUN): 
				Process p = this.findProcess(j);
				addTo = p.toString(); 
				wTTime = getWeightedTurnaround(p);
				TTime = getTurnaround(p); 
				break; 
			case(DONE): 
				addTo = j.toString(); 
				
				
			}
			tbr += addTo + "Turnaround: " + TTime + ". Weighted Turnaround: " + wTTime + ".\n"; 
			
		}
		
		
		return tbr + "\n" + "HoldQueue 1 Contents: " + this.firstHoldQueue.toString() +
				"\nHoldQueue 2 Contents: " + this.secondHoldQueue.toString() + 
				"\nWaitQuee Contents: " + this.deviceWaitQueue.toString() + 
				"\nReadyQueue Contents: " + this.readyQueue.toString() + 
				"\nCompleteQueue Contents: "  + this.completeQueue.toString() + 
				"\nCurrent Time: " + this.time; 
	}
	
	public void parseInput(String s1) {
		
		String line = s1;  
		String [] words = line.split(" ");
		String firstChar = words[0]; 
		
		switch (firstChar) {
		case "A": 
			//create job (constructor) 
			int timeArrive = Integer.parseInt(words[1]); 
			int jobNum = readInteger(words, 2);
			int memReq = readInteger(words, 3);
			int serDevUse = readInteger(words, 4); 
			int runTime = readInteger(words, 5);
			int priority = readInteger(words, 6);
		
			Job currJob = new Job(priority, timeArrive, memReq, serDevUse, runTime, jobNum); 
			
			
			this.inputJob(currJob);

			System.out.println("Adding Job. Time arrived: " + timeArrive + " Job Num: " + jobNum + " Mem req'd: "
				+ memReq + " Serial Devices used " + serDevUse + " Runtime: " + runTime + " Priority: " + priority);
				
		
			break;	
		case "Q":
			int timeReq = Integer.parseInt(words[1]);
			int jobNumReq = readInteger(words, 2);
			int devReqd = readInteger(words, 3);
			
			Request r = new Request(timeReq, jobNumReq, devReqd); 
			
			//TRY TO VALIDATE REQUEST HERE. DONT PUT IN QUEUE. 
			
			this.requestDevices(r);
			

			//BANKERS ALG?????????????

			System.out.println("Requesting devices. TimeReq: " + timeReq + " Job Num: " + jobNumReq + " Devices Req'd: " + devReqd);
			break;
			
		case "L":
			int timeRel = Integer.parseInt(words[1]);
			int jobNumRel = readInteger(words, 2); 
			int numDevReld = readInteger(words, 3);
			
			this.releaseDevices(new Request(timeRel, jobNumRel,numDevReld));
			System.out.println("Releasing devices. Time Relased: " + timeRel + " Job Num: " + jobNumRel + " Num Devices: " + numDevReld);
			break;
		case "D": 
			//display current system status (?)
			int timeDis = Integer.parseInt(words[1]);
			//this.printState(); 
			if(timeDis == 9999) {
				//this.printState();
				//dump contents 
				//set s1 = null 
			}else {
				//this.printState(); 
			}
			//System.out.println("Simulation Display. Time displayed: " + timeDis);
			//display current system params
			break; 
		}
		
	}
	

}