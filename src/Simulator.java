import java.io.*;
public class Simulator {
	private static final int SUB_INDEX = 2; 
	
	int time; 
	int mainMem;
	int serial;
	int quant; 
	
	public Simulator(int t, int mm, int ser, int q) {
		
		this.time = t; 
		this.mainMem = mm; 
		this.serial = ser; 
		this.quant = q; 
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
		//read file 
		//get next line 
		//Suppose next line = string S 
	
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
