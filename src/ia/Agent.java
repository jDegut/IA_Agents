package ia;

import event.EventListener;
import game.Board;
import game.Box;
import game.Direction;
import game.Move;

public class Agent implements Runnable {

    private static int idCounter = 0;
    private final int id;
    private final String name;
    private final int xFinal;
    private final int yFinal;
    private boolean isTerminal;

    private EventListener listener;

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

    public boolean isTerminal() {
        return isTerminal;
    }

    public void setTerminal(boolean terminal) {
        isTerminal = terminal;
    }

    @Override
    public void run() {
        Box box;
        Astar astar;
        while (!isTerminal) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Direction dir = Direction.NONE;
            if(listener instanceof Board board) {
                box = board.getPosition(this);
                astar = new Astar(new Node(box.getX(), box.getY()));
                dir = astar.findPath(board, new Node(xFinal, yFinal));
            }
            sendEvent(new Move(this, dir));
        }
    }
}
