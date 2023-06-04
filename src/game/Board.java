package game;

import event.Event;
import event.EventException;
import event.EventListener;
import ia.Agent;
import ia.Astar;
import ia.Node;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Board implements EventListener {

    public static final int BOARD_SIZE = 5;
    public static final int BOARD_HEIGHT = 5;

    private final Game game;
    private final ConcurrentHashMap<Agent,Box> map;

    public Board(Game game) {
        this.game = game;
        this.map = new ConcurrentHashMap<>();
    }

    public List<Agent> getAgents() {
        return new ArrayList<>(map.keySet());
    }

    public synchronized void print() {
        String[][] board = new String[BOARD_SIZE][BOARD_HEIGHT];
        for(Agent a : map.keySet()) {
            Box b = map.get(a);
            board[b.getX()][b.getY()] = a.getName();
        }
        for(int i = 0; i < BOARD_SIZE; i++) {
            System.out.print("|");
            for(int j = 0; j < BOARD_HEIGHT; j++) {
                if(board[j][i] == null)
                    System.out.print(" ");
                else
                    System.out.print(board[j][i]);
                System.out.print("|");
            }
            System.out.print("\n");
        }
    }

    public List<Node> generateNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int x_ = node.getX();
        int y_ = node.getY();
        if(!map.containsValue(new Box(x_, y_ - 1)) && y_ - 1 >= 0)
            neighbors.add(new Node(x_, y_ - 1, node.getCost(), node.getHeuristic(), node, Direction.UP));
        if(!map.containsValue(new Box(x_, y_ + 1)) && y_ + 1 < BOARD_HEIGHT)
            neighbors.add(new Node(x_, y_ + 1, node.getCost(), node.getHeuristic(), node, Direction.DOWN));
        if(!map.containsValue(new Box(x_ - 1, y_)) && x_ - 1 >= 0)
            neighbors.add(new Node(x_ - 1, y_, node.getCost(), node.getHeuristic(), node, Direction.LEFT));
        if(!map.containsValue(new Box(x_ + 1, y_)) && x_ + 1 < BOARD_SIZE)
            neighbors.add(new Node(x_ + 1, y_, node.getCost(), node.getHeuristic(), node, Direction.RIGHT));
        return neighbors;
    }

    public void addAgent(Agent agent, Box box) {
        map.put(agent, box);
    }

    private void updateMap(Move move) throws Exception {
        Agent agent = move.getAgent();
        if(!map.containsKey(agent))
            throw new EventException("Agent not found in map");
        Direction dir = move.getDirection();
        if(dir == Direction.NONE) return;
        Box old = map.get(agent);
        Box box = null;
        switch (dir) {
            case UP -> box = new Box(old.getX(), old.getY() - 1);
            case DOWN -> box = new Box(old.getX(), old.getY() + 1);
            case LEFT -> box = new Box(old.getX() - 1, old.getY());
            case RIGHT -> box = new Box(old.getX() + 1, old.getY());
        }
        map.replace(move.getAgent(), box);
        agent.setTerminal(agent.getXFinal() == box.getX() && agent.getYFinal() == box.getY());
    }

    public List<Direction> getLegalMoves(Agent agent) {
        List<Direction> legalMoves = new ArrayList<>();
        Box box = map.get(agent);
        if(box.getY() - 1 >= 0)
            legalMoves.add(Direction.UP);
        if(box.getY() + 1 < BOARD_HEIGHT)
            legalMoves.add(Direction.DOWN);
        if(box.getX() - 1 >= 0)
            legalMoves.add(Direction.LEFT);
        if(box.getX() + 1 < BOARD_SIZE)
            legalMoves.add(Direction.RIGHT);
        return legalMoves;
    }

    public Box getPosition(Agent agent) {
        return map.get(agent);
    }

    @Override
    public void onEventOccured(Event event) {
        if(event instanceof Move move) {
            try {
                updateMap(move);
                game.updateAgents();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
