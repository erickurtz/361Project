
public class Job {

	int priority; 
	int time; 
	int memReq; 
	int dev; 
	

	int runTime; 
	int jobNum; 
	String currState; 
	
	public Job(int p, int a, int m, int d, int r, int j) {
		
		this.priority = p; 
		this.time = a;
		this.memReq = m;
		this.dev = d;
		this. runTime = r; 
		this.jobNum = j; 
		this.currState = "None"; 
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int arrivTime) {
		this.time = arrivTime;
	}

	public int getMemReq() {
		return memReq;
	}

	public void setMemReq(int memReq) {
		this.memReq = memReq;
	}

	public int getDev() {
		return dev;
	}

	public void setDev(int dev) {
		this.dev = dev;
	}

	public int getRunTime() {
		return runTime;
	}

	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}
	
	public String getCurrState() {
		return this.currState; 
	}
	
	public void setCurrState(String s, int time) {
		this.currState = s + "At time: " + time;
	}
	
	public int getJobNum() {
		return jobNum;
	}

	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
	}


} 
