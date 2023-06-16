package ia;

import event.EventListener;
import game.Move;

public abstract class Agent implements Runnable {

	private static int idCounter = 0;
	private final int id;
	protected final String name;
	protected final int xFinal;
	protected final int yFinal;
	protected boolean isTerminal;

	protected EventListener listener;

	public Agent(String name, int xFinal, int yFinal) {
		this.id = idCounter++;
		this.name = name;
		this.xFinal = xFinal;
		this.yFinal = yFinal;
		this.isTerminal = false;
	}

	public void setListener(EventListener listener) {
		this.listener = listener;
	}

	public void sendEvent(Move move) {
		if(listener != null)
			listener.onEventOccured(move);
		else
			System.out.println("WARNING - Listener not set");
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getXFinal() {
		return xFinal;
	}

	public int getYFinal() {
		return yFinal;
	}

	public void setTerminal(boolean terminal) {
		isTerminal = terminal;
	}

}
