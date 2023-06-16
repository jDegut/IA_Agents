package ia;

public class Request {

	private final Agent sender;
	private boolean accepted;

	public Request(Agent sender) {
		this.sender = sender;
		this.accepted = false;
	}

    public Agent getSender() {
        return sender;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted() {
		this.accepted = true;
	}

}
