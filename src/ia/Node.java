package ia;

import game.Box;
import game.Direction;

public class Node extends Box implements Comparable<Node> {
    private int cost;
    private int heuristic;
    private Node parent;
    private Direction dir;

    public Node(int x, int y, int cost, int heuristic, Node parent, Direction dir) {
        super(x, y);
        this.cost = cost;
        this.heuristic = heuristic;
        this.parent = parent;
        this.dir = dir;
    }

    public Node(int x, int y) {
        super(x, y);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public int distance(Node node) {
        return Math.abs(getX() - node.getX()) + Math.abs(getY() - node.getY());
    }

    public void update(int cost, int heuristic) {
        this.cost = cost;
        this.heuristic = heuristic;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(heuristic, o.heuristic);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Node n) {
            return n.getX() == getX() && n.getY() == getY();
        }
        return false;
    }
}
