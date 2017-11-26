
public class Request implements Comparable<Request>{
	public int timeReq; 
	public int jobNumReq;
	public int devReqd; 
	
	public Request(int t, int j, int d) {
		this.timeReq = t; 
		this.jobNumReq = j; 
		this.devReqd = d;
	}

	@Override
	public int compareTo(Request o) {
		return this.timeReq - o.timeReq; 
	}

	
	
	
}
