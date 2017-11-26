import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.*;

public class Simulator {
	private static final int SUB_INDEX = 2; 
	
	int time; 
	int totalMem;
	int serial;
	int quant; 
	int availMem;
	Process currProcess = null; 
	LinkedList<Job> submitQueue = new LinkedList<Job>(); 
	LinkedList<Job> allJobs = new LinkedList<Job>(); 
	LinkedList<Job> firstHoldQueue = new LinkedList<Job>(); 
	LinkedList<Job> secondHoldQueue = new LinkedList<Job>(); 
	LinkedList<Process> completeQueue = new LinkedList <Process>(); 
	LinkedList<Process> deviceWaitQueue = new LinkedList<Process>();
	LinkedList<Process> readyQueue = new LinkedList<Process>(); 
	
	
	public Simulator(int t, int mm, int ser, int q) {
		
		this.time = t; 
		this.totalMem = mm; 
		this.serial = ser; 
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
	
	//run for every tick of the clock 
	

	public void onTick() {
		//handle internal events first 
		this.time++; 
		if (this.currProcess != null) 
			this.currProcess.timeRemaining--; 
		
		//sort the first Holdqueue in order to get it in SJF
		Collections.sort(firstHoldQueue, new Comparator <Job> () {

			@Override
			public int compare(Job o1, Job o2) {
				
				return o1.runTime-o2.runTime;
			}
			
		});
		
		//it's unnecessary to sort the other list -> jobs will 
		//simply be added to the front of the queue. 
		
		
		
		
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
