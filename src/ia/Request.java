package ia;

public class Request {

	private final Agent2 agent;
	private boolean processed;

	public Request(Agent2 agent) {
		this.agent = agent;
		this.processed = false;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

}
