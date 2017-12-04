import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SimRunner {
	
	
	
	
	
	private static final int SUB_INDEX = 2;
	private static final int P1 = 1; 
	private static final int P2 = 2; 
	private static final int END = 9999;
	
	//State constants for consistency H
	private static final String HQ1 =  "HoldQueue1";
	private static final String HQ2 = "HoldQueue2"; 
	private static final String REJ = "Rejected"; 
	private static final String RED = "ReadyQueue (Proc)"; 
	private static final String WAIT = "WaitQueue(Proc)"; 
	private static final String RUN = "Running(Proc)"; 
	private static final String DONE = "CompleteQueue"; 
	
	
	Simulator s1 = null; 
	int time; 
	String currLine; 
	public SimRunner(String filename) {
		time = 0; 
		currLine = ""; 
		String line = null; 
		try { 
			FileReader filereader = new FileReader(filename); 
			BufferedReader bufferedReader = new BufferedReader(filereader); 
			
			line = bufferedReader.readLine(); 
			while((line != null)) {
				if(this.time == END) {
					this.parseLine(line);
					break;
				}
				this.parseLine(line);
				 if(s1 != null) {
					if(this.time <= s1.time) {
						s1.parseInput(currLine);
						s1.onTick(); 
						line = bufferedReader.readLine();
						
					}else {
						System.out.println("Current time: " + this.s1.time + 
								". Time of next input: " + time + ".");
						s1.onTick(); //if init, handle internal events first
					}
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
		
		String [] words = line.split(" ");
		String firstChar = words[0]; 
		
		switch (firstChar) {
		case "C": 
			int time = Integer.parseInt(words[1]);
			int mainMemory = readInteger(words, 2);
			int serial = readInteger(words, 3);
			int quant = readInteger(words, 4);
			
			this.s1 = new Simulator (time, mainMemory, serial, quant);
			
			System.out.println("Initializing Simulation. Time: " + time + " Memory: " + mainMemory + 
					" Serial Devices: " + serial + " Quant: " + quant);
			
			break;
		case "A":
			int timeArrive = Integer.parseInt(words[1]); 
			int jobNum = readInteger(words, 2);
			int memReq = readInteger(words, 3);
			int serDevUse = readInteger(words, 4); 
			int runTime = readInteger(words, 5);
			int priority = readInteger(words, 6);
			
			//Job currJob = new Job(priority, timeArrive, memReq, serDevUse, runTime, jobNum); 
			//this.s1.addJob(currJob);
			
			//System.out.println("Adding Job. Time arrived: " + timeArrive + " Job Num: " + jobNum + " Mem req'd: "
			//		+ memReq + " Serial Devices used " + serDevUse + " Runtime: " + runTime + " Priority: " + priority);
			this.time = timeArrive; 
			this.currLine = line; 
			
			break;
		case "Q":
			int timeReq = Integer.parseInt(words[1]);
			int jobNumReq = readInteger(words, 2);
			int devReqd = readInteger(words, 3);
			
			this.time = timeReq;
			this.currLine = line; 
			
			//s1.requestQueue.add(new Request(timeReq, jobNumReq, devReqd)); 
			//System.out.println("Requesting devices. TimeReq: " + timeReq + " Job Num: " + jobNumReq + "Devices Req'd: " + devReqd);
			break;
			
		case "L":
			int timeRel = Integer.parseInt(words[1]);
			int jobNumRel = readInteger(words, 2); 
			int numDevReld = readInteger(words, 3);
			
			this.time = timeRel; 
			this.currLine = line; 
			
			//System.out.println("Releasing devices. Time Relased: " + timeRel + " Job Num: " + jobNumRel + " Num Devices: " + numDevReld);
			//break;
		case "D": 
			//display current system status (?)
			int timeDis = Integer.parseInt(words[1]);
			
			
			this.time = timeDis; 
			this.currLine = line; 
			
			System.out.println(s1.printState()); 
			if(timeDis == 9999) {
				//System.out.println("Simulation Display. Time displayed: " + timeDis);
			
				//s1.printState();
				//null pointer?? 
				//dump contents 
				//set s1 = null 
			}else {
				//s1.printState(); 
				//Null Pointer, no idea why 
				//System.out.println("Simulation Display. Time displayed: " + timeDis);
				//don't do that, just print
				System.out.println("Simulation Display. Time displayed: " + timeDis);
				//display current system params
			}
			
			break; 
			
			
		
		}
		
		
	}
	
		
		
}

