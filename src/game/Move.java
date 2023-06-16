package game;

import event.Event;
import ia.Agent;
import ia.Agent1;

public class Move implements Event {

    private final Agent agent;
    private Direction dir;


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

    public void setDirection(Direction path) {
        this.dir = path;
    }
}
