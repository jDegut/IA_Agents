package game;

import event.Event;
import event.EventException;
import event.EventListener;
import ia.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Board implements EventListener {

    public static final int BOARD_SIZE = 5;
    public static final int BOARD_HEIGHT = 5;

    private final Game game;
    private final Map<Agent,Box> map;

    private final Map<Agent, Queue<Request>> requests;

    public Board(Game game) {
        this.game = game;
        this.map = new ConcurrentHashMap<>();
        this.requests = new ConcurrentHashMap<>();
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

    public Map<Box, Direction> getAllNeighbors(Box box) {
        Map<Box, Direction> neighbors = new HashMap<>();
        int x_ = box.getX();
        int y_ = box.getY();
        if(y_ - 1 >= 0) neighbors.put(new Box(x_, y_ - 1), Direction.UP);
        if(y_ + 1 < BOARD_HEIGHT) neighbors.put(new Box(x_, y_ + 1), Direction.DOWN);
        if(x_ - 1 >= 0) neighbors.put(new Box(x_ - 1, y_), Direction.LEFT);
        if(x_ + 1 < BOARD_SIZE) neighbors.put(new Box(x_ + 1, y_), Direction.RIGHT);
        return neighbors;
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
        Box box = getBoxDirected(dir, old);
        map.replace(move.getAgent(), box);
        agent.setTerminal(agent.getXFinal() == box.getX() && agent.getYFinal() == box.getY());

        for(Box b : map.values()) {
            if(b.equals(box)) continue;
            if(b.getX() == box.getX() && b.getY() == box.getY()) {
                throw new Exception("Collision detected");
            }
        }
    }

    public Box getBoxDirected(Direction dir, Box old) {
        Box box = null;
        switch (dir) {
            case UP -> box = new Box(old.getX(), old.getY() - 1);
            case DOWN -> box = new Box(old.getX(), old.getY() + 1);
            case LEFT -> box = new Box(old.getX() - 1, old.getY());
            case RIGHT -> box = new Box(old.getX() + 1, old.getY());
        }
        return box;
    }

    public Box getPosition(Agent agent) {
        return map.get(agent);
    }

    public Agent getAgent(Box box) {
        for(Agent a : map.keySet()) {
            if(map.get(a).equals(box))
                return a;
        }
        return null;
    }

    public Direction getNearestEmptyDirection(Agent agent) {
        Box box = map.get(agent);
        List<Node> neighbors = generateNeighbors(new Node(box.getX(), box.getY(), 0, 0, null, Direction.NONE));
        return neighbors.stream().min((n1, n2) -> {
            int d1 = Math.abs(n1.getX() - agent.getXFinal()) + Math.abs(n1.getY() - agent.getYFinal());
            int d2 = Math.abs(n2.getX() - agent.getXFinal()) + Math.abs(n2.getY() - agent.getYFinal());
            return Integer.compare(d1, d2);
        }).orElseThrow().getDir();
    }

    /**
     * Méthodes pour la map requests (boite aux lettres de Agent2)
     */

    public Direction getBestDirection(Agent agent) {
        Box box = map.get(agent);
        Map<Box, Direction> neighbors = getAllNeighbors(box);
        return neighbors.get(neighbors.keySet().stream()
                        .min(Comparator.comparingDouble(b -> b.distance(new Box(agent.getXFinal(), agent.getYFinal()))))
                        .orElseThrow());
    }

    public Direction getRandomEmptyDirection(Agent agent) {
        Box box = map.get(agent);
        Map<Box, Direction> neighbors = getAllNeighbors(box);
        Box random = neighbors.keySet().stream()
                .filter(b -> !map.containsValue(b))
                .skip(new Random().nextInt(neighbors.size()))
                .findFirst()
                .orElse(null);
        return neighbors.getOrDefault(random, Direction.NONE);
    }

    public void initRequestAgent(Agent agent) {
        requests.put(agent, new ConcurrentLinkedQueue<>());
        System.out.println("Agent " + agent.getName() + " initialized");
    }

    /**
     * Retourne la requête la plus ancienne de l'agent
     * => L'agent est forcément dans les clés puisqu'il est init avant
     * @param agent
     * @param request
     */
    public void addRequest(Agent agent, Request request) {
        if(!requests.containsKey(agent))
            initRequestAgent(agent);
        requests.get(agent).add(request);
    }

    public Queue<Request> getRequests(Agent agent) {
        return requests.get(agent);
    }

    /**
     * FIN
     */

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
