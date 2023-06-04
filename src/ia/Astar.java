package ia;

import game.Board;
import game.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Astar {

    private final Node start;

    public Astar(Node start) {
        this.start = start;
    }

    public Direction findPath(Board board, Node objectif) {
        List<Node> closed = new ArrayList<>();
        Queue<Node> open = new PriorityQueue<>();
        open.add(start);
        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.equals(objectif)) {
                Node firstChild = backPropagate(current);
                return firstChild.getDir();
            }
            closed.add(current);
            for (Node neighbor : board.generateNeighbors(current)) {
                if(!(closed.contains(neighbor)
                        || (open.contains(neighbor) && neighbor.getCost() < current.getCost()))) {
                    int cost = current.getCost() + 1;
                    int heuristic = cost + neighbor.distance(objectif);
                    neighbor.update(cost, heuristic);
                    open.add(neighbor);
                }
            }
            closed.add(current);
        }
        return Direction.NONE;
    }

    private Node backPropagate(Node node) {
        if(node.getParent() == null) {
            return node;
        }
        if(node.getParent().getParent() == null) {
            return node;
        }
        return backPropagate(node.getParent());
    }

}
