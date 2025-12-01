import java.awt.*;
import java.util.Random;

public class Location {
    int x; // x-coordinate of the location
    int y; // y-coordinate of the location
    private static final int BLOCK_SIZE = Main.BLOCK_SIZE;
    //initializes the location with x and y values
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Location  RandomLocation(Random random, int size) {
        return new Location(Math.abs(random.nextInt()) % size, Math.abs(random.nextInt()) % size);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location location) {
            return this.x == location.x && this.y == location.y;
        }
        return super.equals(obj);
    }

    public double distance(Location location) {
        return Math.sqrt(Math.pow(this.x - location.x, 2) + Math.pow(this.y - location.y, 2));
    }

    public static Location fromGridCoords(Location l) {
        return new Location(l.getX()/BLOCK_SIZE, l.getY()/BLOCK_SIZE);
    }

    public static Location toGridCoords(Location l) {
        return new Location(l.getX()*BLOCK_SIZE, l.getY()*BLOCK_SIZE);
    }

    public static Point toPoint(Location l) {
        Location l2 = toGridCoords(l);
        return new Point(l2.getX(), l2.getY());
    }

    //method for getting the x-coordinate
    public int getX() {
        return x;
    }
    //method for getting the y-coordinate
    public int getY() {
        return y;
    }

    public Location up() {
        return new Location(x, y - 1);
    }

    public Location down() {
        return new Location(x, y + 1);
    }
    public Location left() {
        return new Location(x - 1, y);
    }
    public Location right() {
        return new Location(x + 1, y);
    }

    public Location move(Direction direction) {
        return switch (direction) {
            case UP -> this.up();
            case DOWN -> this.down();
            case LEFT -> this.left();
            case RIGHT -> this.right();
        };
    }

    //method to check if the location is within the bounds of the board
    public boolean withinBounds(int size) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }
}
