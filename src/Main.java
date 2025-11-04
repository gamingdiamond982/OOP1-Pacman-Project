import java.util.Scanner; // Import the Scanner class for user input
// This is a test comment
public class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        int level = 1;
        int boardSize = 10;
        Location startLocation = new Location(0, 0);
        int cumulativeScore = 0;

        // Outer loop: recreate Board for each level
        while (true) {
            long seed = System.currentTimeMillis() + level; // new seed per level
            Board board = new Board(boardSize, startLocation, seed, level);
            System.out.println("Starting level " + level);
            System.out.println(board.drawBoard());

            // Inner loop: handle moves for current board
            while (true) {
                System.out.print("WASD to move or anything else to quit > ");
                String line = scanner.nextLine();
                Direction direction = switch (line.toUpperCase()) {
                    case "W" -> Direction.UP;
                    case "S" -> Direction.DOWN;
                    case "A" -> Direction.LEFT;
                    case "D" -> Direction.RIGHT;
                    default -> null;
                };
                if (direction == null) {
                    System.out.println("Bye! Final score: " + (cumulativeScore + board.getScore()));
                    scanner.close();
                    return;
                }
                if (board.move(direction)) {
                    System.out.println(board.drawBoard());
                    System.out.println("Your score is : " + board.getScore());
                    System.out.println("Pellets remaining: " + board.getPelletCount());
                    if (board.isImmune()) {
                        System.out.println("You're immune!!!");
                    }
                } else {
                    System.out.println("You cannot move there.");
                }
                if (board.getPelletCount() == 0) {
                    cumulativeScore += board.getScore();
                    System.out.println("Level " + level + " complete! Cumulative score: " + cumulativeScore);
                    level++;
                    break; // break inner loop to start next level
                }
                if (board.isGameOver()) {
                    System.out.println("Game over.");
                    break;
                }
            }
            if (board.isGameOver()) {
                break;
            }
        }
    }
}