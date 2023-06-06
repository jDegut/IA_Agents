package game;

import event.Event;
import ia.Agent1;

public class Move implements Event {

    private final Agent1 agent1;
    private Direction dir;


    public Move(Agent1 agent1, Direction dir) {
        this.agent1 = agent1;
        this.dir = dir;
    }

    public Agent1 getAgent() {
        return agent1;
    }

    public Direction getDirection() {
        return dir;
    }

    public void setDirection(Direction path) {
        this.dir = path;
    }
}
