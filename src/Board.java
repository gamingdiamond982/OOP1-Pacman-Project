import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random; // Import Random class for generating random numbers

public class Board {  
    private int pelletCount; // Number of pellets on the board
    private final char[][] board; // 2D array representing the board layout
    private Location pacman; // current location of pacman
    private final int size; // size of the board (assuming square board)
    private int score; // player's score
    private int level;
    private final Ghost[] ghosts;
    private boolean gameOver;
    private boolean immune = false;
    private int immunityClock = 0;


    public Board(int size, Location pacmanLocation) { 
        // creates a new board with a random seed and default level 1
        this(size, pacmanLocation, (new Random()).nextLong(), 1);
    }

    public Board(int size, Location pacmanLocation, long seed) {
        // default to level 1 when not supplied
        this(size, pacmanLocation, seed, 1);
    }

    /**
     * Create a board using a seed and a level number. Wall probability increases with level,
     * pellet probability decreases with level. Power-pellet probability stays small.
     */
    public Board(int size, Location pacmanLocation, long seed, int level) {
        Random random = new Random(seed);
        score = 0;
        gameOver = false;
        this.size = size;
        this.board = new char[size][size];
        this.pelletCount = 0;
        this.level = level;

        // compute base chances (percent 0..100)
        int baseWall = 10; // starting wall chance at level 1
        int basePellet = 10; // starting pellet chance at level 1
        int basePower = 5; // small constant chance for power pellets
        // adjust by level: walls increase, pellets decrease
        int wallChance = Math.min(80, baseWall + (level - 1) * 6); // increase walls by ~6% per level
        int pelletChance = Math.max(3, basePellet - (level - 1) * 6); // decrease pellets by ~6% per level
        int powerChance = Math.max(1, basePower - (level - 1) / 3); // slight decrease over time

        // normalize so they sum to <= 100
        int total = wallChance + pelletChance + powerChance;
        if (total > 100) {
            float scale = 100f / total;
            wallChance = Math.max(0, Math.round(wallChance * scale));
            pelletChance = Math.max(0, Math.round(pelletChance * scale));
            powerChance = Math.max(0, 100 - wallChance - pelletChance);
        }

        // populate board using a single roll per cell
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int roll = random.nextInt(100); // 0..99
                if (roll < wallChance) {
                    this.board[i][j] = '#';
                } else if (roll < wallChance + powerChance) {
                    this.board[i][j] = 'l';
                } else if (roll < wallChance + powerChance + pelletChance) {
                    this.board[i][j] = '.';
                    pelletCount += 1;
                } else {
                    this.board[i][j] = ' ';
                }
            }
        }

        // Place pacman on the board at the specified starting location
        ghosts = new Ghost[level];
        for (int i = 0; i<level; i++) {
            Location ghostLocation = Location.RandomLocation(random, size);
            if (ghostLocation.equals(pacmanLocation) || !this.isEmpty(ghostLocation)) {
                i--;
                continue;
            }
            ghosts[i] = new Ghost(ghostLocation, this);
        }
        this.pacman = pacmanLocation;
        int py = pacman.getY();
        int px = pacman.getX();
        if (py >= 0 && py < size && px >= 0 && px < size) {
            if (this.board[py][px] == '.') pelletCount = Math.max(0, pelletCount - 1);
            this.board[py][px] = 'P';
        }
    }

    public String drawBoard() {
        // generate the boards ascii representation
        StringBuilder sb = new StringBuilder();
        char[][] tempBoard = board.clone();
        Location currentLocation = new Location(0, 0);
        for (char[] chars : tempBoard) {
            for (char aChar : chars) {
                boolean end = false;
                for (Ghost ghost : ghosts) {
                    if (currentLocation.equals(ghost.getGhostLocation())) {
                        sb.append("\uD83D\uDC7B");
                        end = true;
                    }
                }
                if (end) {

                    currentLocation = currentLocation.right();
                    continue;
                }
                if (aChar != 'P') {
                    sb.append(aChar); //Append regular characters to array
                } else {
                    sb.append("🙃"); // I can't store the emoji in the char array since this is technically a string
                }
                currentLocation = currentLocation.right();
            }
            currentLocation = new Location(0, currentLocation.getY() + 1);
            sb.append('\n'); // New line after each row
        }
        return sb.toString(); // Return the complete board as a string
    }

    public Location getPacman() {
        return pacman;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isEmpty(Location location) {
        if (!location.withinBounds(size)) {
            return false;
        }
        return board[location.getY()][location.getX()] != '#';
    }

    public boolean isImmune() {
        return immune;
    }
    // Attempt to move Pacman in the specified direction
    public boolean move(Direction direction) {
        if (immune) {
            immunityClock--;
            if (immunityClock == 0) {
                immune = false;
            }
        }
        // tries to move pacman in the given direction returns true if successful false if the movement resulted in a collision
        // if moving was successful then ghosts are also moved
        Location newLocation = this.pacman.move(direction);
        // perform some bounds checking and return early if we're out of bounds
        if (!newLocation.withinBounds(size) || board[newLocation.getY()][newLocation.getX()] == '#') return false;
        // if we land on a pellet increase the score   
        if  (board[newLocation.getY()][newLocation.getX()] == '.') {
            score += 1;
            pelletCount -= 1;
            
        }
        if  (board[newLocation.getY()][newLocation.getX()] == 'l') {
            immune = true;
            immunityClock = 5;
        }
        board[pacman.getY()][pacman.getX()] = ' '; // Clear old pacman position
        pacman = newLocation; // Update pacman's location
        board[pacman.getY()][pacman.getX()] = 'P';
        for (Ghost ghost : ghosts) { // calculate the ghost's moves
            ghost.move();
            if (ghost.getGhostLocation().equals(pacman)) {
                if (!immune) {
                    gameOver = true;
                } else {
                    ghost.kill();
                }
            }
        }
        return true;
    }

    public int getScore() { // method to get the current score
        return score;
        
    }

    public int getPelletCount() { // returns the current number of pellets on the board
        return pelletCount;
    }   

    public int leveltracking () { // returns the current level based on score
        level = 1;
        if (pelletCount == 0) {
            return level + 1; 
        }
        return 1;
        
    }
}

