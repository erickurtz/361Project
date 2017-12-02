
public class Process implements Comparable<Process> {
	
	Job ajob; 
	int timeRemaining; 
	int currResources; 
	int currMem;
	
	public Process(Job j) {
		this.ajob = j; 
		this.currMem = 0;
		this.currResources = 0; 
		this.timeRemaining = j.getRunTime(); 
	}
	
	public int getResReqDiff() {
		return this.getAjob().getDev() - this.currResources; 
	}

	public int getCurrResources() {
		return currResources;
	}

	public void setCurrResources(int currResources) {
		this.currResources = currResources;
	}

	public int getCurrMem() {
		return currMem;
	}

	public void setCurrMem(int currMem) {
		this.currMem = currMem;
	}

	public Job getAjob() {
		return ajob;
	}

	public void setAjob(Job ajob) {
		this.ajob = ajob;
	}

	public int getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(int timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	@Override
	public int compareTo(Process o) {
		// TODO Auto-generated method stub
		return this.getAjob().getJobNum() - o.getAjob().getJobNum();
	}
	
	public String toString() {
		return "Process for " + this.getAjob().toString(); 
	}

}
