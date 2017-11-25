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

	
	public int readInteger(String inputArr[], int index) {
		return Integer.parseInt(inputArr[index].
				substring(SUB_INDEX, inputArr[index].length()));
		
		
	}
	
	public void parseFile(String filename) {
		//read file 
		//get next line 
		//Suppose next line = string S 
	
		String line = ""; 
		String [] words = line.split(" ");
		String firstChar = words[0]; 
		
		switch (firstChar) {
		case "C": 
			int time = Integer.parseInt(words[1]);
			int mainMemory = readInteger(words, 2);
			int serial = readInteger(words, 3);
			int quant = readInteger(words, 4);
			
			
			break;
		case "A": 
			//create job (constructor) 
			int timeArrive = Integer.parseInt(words[1]); 
			int jobNum = readInteger(words, 2);
			int memReq = readInteger(words, 3);
			int serDevUse = readInteger(words, 4); 
			int runTime = readInteger(words, 5);
			int priority = readInteger(words, 6);
			
			//constr here 
			//add to queue (?) 
			
			break;
		case "Q":
			int timeReq = Integer.parseInt(words[1]);
			int jobNumReq = readInteger(words, 2);
			int devReqd = readInteger(words, 3);
			break;
		case "L":
			int timeRel = Integer.parseInt(words[1]);
			int jobNumRel = readInteger(words, 2); 
			int numDevReld = readInteger(words, 3);
			break;
		case "D": 
			//display current system status (?)
			int timeDis = Integer.parseInt(words[1]);
			//display current system params
			break; 
			
			
		
		}
		
		
	}
}
