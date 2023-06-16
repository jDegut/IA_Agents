package game;

import java.util.Objects;

public class Box{
    private final int x;
    private final int y;

    public Box(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Box box) {
            return box.x == x && box.y == y;
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

//    public int distance(Box b) {
//        return Math.abs(x - b.x) + Math.abs(y - b.y);
//    }

    public int distance(Box b) {
        return (int) Math.sqrt(Math.pow(x - b.x, 2) + Math.pow(y - b.y, 2));
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Box[" +
                "x=" + x + ", " +
                "y=" + y + ']';
    }
}
