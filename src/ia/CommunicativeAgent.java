package ia;

import game.Board;
import game.Box;
import game.Direction;
import game.Move;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public class CommunicativeAgent extends Agent {

	/**
	 * Idée : Faire une concurrentHashMap dans Board (boite aux lettres)
	 * Modifier la requête pour mettre le destinataire
	 * Chaque agent avant de bouger regarde s'il a du courrier pour lui
	 * Si oui, il effectue sa manip avec la proba
	 * Si non, il bouge
	 * Ps : probabilité d'être refusé, dans ce cas : il bouge en évitant l'obstacle
	 */

	private static final double ACCEPTING_PROBABILITY = 0.7;
	private Agent lastContacted;

	public CommunicativeAgent(String name, int xFinal, int yFinal) {
		super(name, xFinal, yFinal);
	}

	@Override
	public void run() {
		Box box;
		Direction dir;
		if(listener instanceof Board board) {
			board.initRequestAgent(this);

			while (!isTerminal) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				// D'abord : il regarde s'il a du courrier (communication)
				Queue<Request> requests = board.getRequests(this);
				if(requests.size() > 0) { // Oui ?
					// Il prend la dernière lettre reçue
					Request request = requests.peek();
					if (isAccepting(request)) { // Est-ce que je l'accepte ? (probabilité)
						// Oui : je bouge dans la meilleure direction libre
						dir = board.getRandomEmptyDirection(this);
						System.out.println("Agent2 " + name + " moving " + dir + " to unlock " + request.getSender().getName());
						sendEvent(new Move(this, dir));
						requests.poll();
					} else
						// Non : je l'avertis
						System.out.println("Agent2 " + name + " refusing " + request.getSender().getName());
				} else {
					// J'ai pas de courrier, très bien. Ai-je déjà contacté quelqu'un juste avant ?
					if(lastContacted != null) {
						// Oui : je vérifie si ma demande a été acceptée dans la boite aux lettres de l'autre (illégal ? :) )
						requests = board.getRequests(lastContacted);
						// Il faut que je vérifie tout son courrier pour avoir la dernière lettre que je lui ai posté
						if(requests.size() > 0) {
							Queue<Request> reqTemp = new ArrayDeque<>(requests);
							Request req = reqTemp.poll();
							while (req != null && !req.getSender().equals(this)) {
								req = reqTemp.poll();
							}
							// J'ai fini de parcourir son courrier, ya-t-il une lettre de moi ? (en principe oui puisque lastContacted != null)
							if(req != null && !req.isAccepted()) {
								// Oui ! Et elle a pas été acceptée... Je suis refusé :'(
								System.out.println("Agent2 " + name + " has been refused by " + lastContacted.getName());
								dir = board.getRandomEmptyDirection(this);
								System.out.println("Agent2 " + name + " moving " + dir + " after being refused");
								sendEvent(new Move(this, dir)); // Je bouge donc dans une direction proche et disponible
								requests.remove(req); // Et j'enlève le courrier de sa boite aux lettres (il ne la mérite plus !)
								lastContacted = null; // reset
								continue;
							}
						}
					}
					// Aucun courrier, pas de communication précédente, je peux alors m'occuper de moi-même
					box = board.getPosition(this);
					dir = board.getBestDirection(this);
					Box newPos = board.getBoxDirected(dir, box);
					Agent neighbor = board.getAgent(newPos);
					if (neighbor instanceof CommunicativeAgent a) { // AH ! Je ne peux pas aller là où je veux -> voisin
						board.addRequest(a, new Request(this)); // J'envoie une lettre
						lastContacted = a;
					} else { // Ok top, je bouge
						System.out.println("Agent2 " + name + " moving " + dir);
						sendEvent(new Move(this, dir));
					}
				}
			}
		}
	}

	private boolean isAccepting(Request request) {
		Random r = new Random();
		double p = r.nextDouble();
		if(p < ACCEPTING_PROBABILITY)
			request.setAccepted();
		return request.isAccepted();
	}

}
