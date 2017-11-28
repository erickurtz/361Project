import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SimRunner {
	
	
	
	
	
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
	
	
	Simulator s1 = null; 
	
	
	public SimRunner(String filename) {
		String line = null; 
		try { 
			FileReader filereader = new FileReader(filename); 
			BufferedReader bufferedReader = new BufferedReader(filereader); 
			
			while((line = bufferedReader.readLine()) != null) {
				if(s1 != null) {
					s1.onTick(line); //if init, handle internal events first
					this.parseLine(line); //then accept input (external events)
				}else {
					this.parseLine(line); //else parse the file to initialize the simulator
				}
				
			}
		}catch (FileNotFoundException ex){
			System.out.println("File not found");
		}catch (IOException ex) {
			System.out.println("Error reading file"); 
		}
	}

	public static void main(String[] args) {
		SimRunner s1 = new SimRunner("test.txt"); 

	}
	
	//Helper function to read lines 
	//produces Strings --> Ints 
	public int readInteger(String inputArr[], int index) {
		return Integer.parseInt(inputArr[index].
				substring(SUB_INDEX, inputArr[index].length()));
		
		
	}
	
	//parseLine: Line --> void 
	//Consumes: String line (the line to be consumed
	//Produces: void 
	//Adjusts the simulator based on the line of the given file.
	public void parseLine(String line) {
		
		if(this.s1 != null) {
			s1.onTick(line);
		}
		
		String [] words = line.split(" ");
		String firstChar = words[0]; 
		
		switch (firstChar) {
		case "C": 
			int time = Integer.parseInt(words[1]);
			int mainMemory = readInteger(words, 2);
			int serial = readInteger(words, 3);
			int quant = readInteger(words, 4);
			
			this.s1 = new Simulator (time, mainMemory, serial, quant);
			
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
			
			Job currJob = new Job(priority, timeArrive, memReq, serDevUse, runTime, jobNum); 
			currJob.setCurrState(INP);
			this.s1.allJobs.add(currJob);
			this.s1.submitQueue.add(currJob);
			
			System.out.println("Adding Job. Time arrived: " + timeArrive + " Job Num: " + jobNum + " Mem req'd: "
					+ memReq + " Serial Devices used " + serDevUse + " Runtime: " + runTime + " Priority: " + priority);

			
			break;
		case "Q":
			int timeReq = Integer.parseInt(words[1]);
			int jobNumReq = readInteger(words, 2);
			int devReqd = readInteger(words, 3);
			
			s1.requestQueue.add(new Request(timeReq, jobNumReq, devReqd)); 
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
			
			if(timeDis == 9999) {
				
				//dump contents 
				//set s1 = null 
			}else {
				//don't do that, just print
			}
			System.out.println("Simulation Display. Time displayed: " + timeDis);
			//display current system params
			break; 
			
			
		
		}
		
		
	}
	
		
		
}

