package game;

import event.Event;
import ia.Agent;

public class Move implements Event {

    private final Agent agent;
    private final Direction dir;


    public Move(Agent agent1, Direction dir) {
        this.agent = agent1;
        this.dir = dir;
    }

    public Agent getAgent() {
        return agent;
    }

    public Direction getDirection() {
        return dir;
    }
}
