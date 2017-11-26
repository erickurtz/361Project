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
	
	
	public Simulator(int t, int mm, int ser, int q) {
		
		this.time = t; 
		this.totalMem = mm; 
		this.serialDev = ser; 
		this.quant = q;
		this.availMem = totalMem;
	}

	//Helper function to read lines 
	//produces Strings --> Ints 
	public int readInteger(String inputArr[], int index) {
		return Integer.parseInt(inputArr[index].
				substring(SUB_INDEX, inputArr[index].length()));
		
		
	}
	
	
	//Parses the line of the file
	//Consumes: line -> line denoting what to do 
	//Produces: Nothing, adjusts Simulation based on input
	
	
	public void parseLine(String line) {
	
		String [] words = line.split(" ");
		String firstChar = words[0]; 
		
		switch (firstChar) {
		case "C": 
			int time = Integer.parseInt(words[1]);
			int mainMemory = readInteger(words, 2);
			int serial = readInteger(words, 3);
			int quant = readInteger(words, 4);
			
			System.out.println("Initializing Simulation. Time: " + time + " Memory: " +  " Serial Devices: " + serial + " Quant: " + quant);
			
			break;
		case "A": 
			//create job (constructor) 
			int timeArrive = Integer.parseInt(words[1]); 
			int jobNum = readInteger(words, 2);
			int memReq = readInteger(words, 3);
			int serDevUse = readInteger(words, 4); 
			int runTime = readInteger(words, 5);
			int priority = readInteger(words, 6);
			
			System.out.println("Adding Job. Time arrived: " + timeArrive + " Job Num: " + jobNum + " Mem req'd: "
					+ memReq + " Serial Devices used " + serDevUse + " Runtime: " + runTime + " Priority: " + priority);
			//constr here 
			//add to queue (?) 
			
			break;
		case "Q":
			int timeReq = Integer.parseInt(words[1]);
			int jobNumReq = readInteger(words, 2);
			int devReqd = readInteger(words, 3);
			
			
			System.out.println("Requesting devices. TimeReq: " + timeReq + " Job Num: " + jobNumReq + "Devices Req'd: " + devReqd);
			break;
			
		case "L":
			int timeRel = Integer.parseInt(words[1]);
			int jobNumRel = readInteger(words, 2); 
			int numDevReld = readInteger(words, 3);
			
			System.out.println("Releasing devices. Time Relased: " + timeRel + " Job Num: " + jobNumRel + " Num Devices: " + numDevReld);
			break;
		case "D": 
			//display current system status (?)
			int timeDis = Integer.parseInt(words[1]);
			System.out.println("Simulation Display. Time displayed: " + timeDis);
			//display current system params
			break; 
			
			
		
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

	public void onTick() {
		//handle internal events first
		this.time++; 
		if (this.currProcess != null) 
			this.currProcess.timeRemaining--; //for the last cycle...

		
		if (this.currProcess.timeRemaining == 0) {
			deallocateProcess(); 
			checkWaitQueue(); 
			checkHoldQueues(); 
			this.currProcess = this.readyQueue.remove(); 
			
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
	
	
	public static void main(String args[]) {
		Simulator s1 = new Simulator(0,0,0,0); 
		
		String filename = "test.txt";
		String line = null; 
		
		try { 
			FileReader filereader = new FileReader(filename); 
			BufferedReader bufferedReader = new BufferedReader(filereader); 
			
			while((line = bufferedReader.readLine()) != null) {
				s1.parseLine(line);
			}
		}catch (FileNotFoundException ex){
			System.out.println("File not found");
		}catch (IOException ex) {
			System.out.println("Error reading file"); 
		}
		
	}
}
