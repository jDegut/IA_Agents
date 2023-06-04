package game;

import event.Event;
import ia.Agent;

public class Move implements Event {

    private final Agent agent;
    private Direction dir;


    public Move(Agent agent, Direction dir) {
        this.agent = agent;
        this.dir = dir;
    }

    public Agent getAgent() {
        return agent;
    }

    public Direction getDirection() {
        return dir;
    }

    public void setDirection(Direction path) {
        this.dir = path;
    }
}
