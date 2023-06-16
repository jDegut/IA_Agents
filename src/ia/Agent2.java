package ia;

import game.Board;
import game.Box;
import game.Direction;
import game.Move;

import java.util.PriorityQueue;
import java.util.Queue;

public class Agent2 extends Agent {

	private final Queue<Request> requests;

	public Agent2(String name, int xFinal, int yFinal) {
		super(name, xFinal, yFinal);
		this.requests = new PriorityQueue<>();
	}

	public void addRequest(Request request) {
		requests.add(request);
	}

	public boolean canMove() {
		Direction dir = Direction.NONE;
		if(listener instanceof Board b) {
			dir = b.getNearestEmptyDirection(this);
		}
		return dir != Direction.NONE;
	}

	@Override
	public void run() {
		Box box;
		while (!isTerminal) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			if (listener instanceof Board b) {
				box = b.getPosition(this);
				while(requests.size() > 0) {
					// TODO - Il traite chaque requête, s'il peut bouger : il bouge, sinon il attend
				}
				Direction dir = b.getBestDirection(this);
				System.out.println("Agent2 " + name + " moving " + dir);
				Box newPos = b.getBoxDirected(dir, box);
				Agent neighbor = b.getAgent(newPos);
				if(neighbor instanceof Agent2 agent) {
					agent.addRequest(new Request(this));
				}
				sendEvent(new Move(this, dir));
			}
		}
	}
}
