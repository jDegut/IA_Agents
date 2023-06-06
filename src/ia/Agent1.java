package ia;

import game.Board;
import game.Box;
import game.Direction;
import game.Move;
public class Agent1 extends Agent {

    public Agent1(String name, int xFinal, int yFinal) {
        super(name, xFinal, yFinal);
    }

    @Override
    public void run() {
        Box box;
        Astar astar;
        while (!isTerminal) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Direction dir = Direction.NONE;
            if(listener instanceof Board board) {
                box = board.getPosition(this);
                astar = new Astar(new Node(box.getX(), box.getY()));
                dir = astar.findPath(board, new Node(xFinal, yFinal));
                if(dir == Direction.NONE)
                    dir = board.getNearestEmptyDirection(this);
            }
            System.out.println("Agent1 " + name + " moving " + dir);
            sendEvent(new Move(this, dir));
        }
    }
}
