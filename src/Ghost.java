import java.util.ArrayList;
import java.util.Comparator;

public class Ghost {
    private Location ghostLocation;
    private Board board;
    private boolean alive;
    public Ghost(Location ghostLocation, Board board) {
        this.ghostLocation = ghostLocation;
        this.board = board;
        this.alive = true;
    }

    public void kill() {
        this.alive = false;
        ghostLocation = null;
    }

    public Location getGhostLocation() {
        return ghostLocation;
    }

    public void move(Direction direction) {
        if (direction == null) return;
        ghostLocation = ghostLocation.move(direction);
    }

    public void move() {
        this.move(this.computeMove());
    }

    public double eval(Direction direction) {
        if (direction == null) {
            return Math.abs(ghostLocation.distance(board.getPacman()));
        }
        return Math.abs(ghostLocation.move(direction).distance(board.getPacman()));
    }

    public Direction computeMove() {
        if (!alive) {
            return null;
        }
        Location pacman = board.getPacman();
        ArrayList<Direction> legalMoves = new ArrayList<>();
        legalMoves.add(null);
        for (Direction direction : Direction.values()) {
            if (board.isEmpty(ghostLocation.move(direction))) {
                legalMoves.add(direction);
            }
        }

        legalMoves.sort((a, b) -> (int) (((this.eval(b) - this.eval(a))*100)));
        if (!board.isImmune()) {
            return legalMoves.isEmpty() ? null : legalMoves.getLast();
        } else {
            return legalMoves.isEmpty() ? null : legalMoves.getFirst();
        }
    }
}
