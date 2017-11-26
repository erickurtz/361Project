
public class Process {
	
	Job ajob; 
	int timeRemaining; 
	int currResources; 
	
	public Process(Job j) {
		this.ajob = j; 
		this.currResources = 0; 
		this.timeRemaining = j.getRunTime(); 
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

}
