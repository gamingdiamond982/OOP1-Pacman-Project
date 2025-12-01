import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;


// Mock PacMan class for demonstration


public class Main extends JFrame {
    public static final int BLOCK_SIZE = 50;
    private ArrayList<Point> obstacles;
    private ArrayList<Point> pellets;
    private ArrayList<Point> ghosts;
    private ArrayList<Point> powerups;
    private JPanel gamePanel;
    private JLabel scoreLabel;
    private Board board;
    private Image ghostImage;
    private JPanel currentPanel;


    public Main() {
        setTitle("Pac-Man Game");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        showMainMenu();
        setVisible(true);
    }

    private void showMainMenu() {
        // Clear current panel if exists
        if (currentPanel != null) {
            remove(currentPanel);
        }
        removeKeyListener(getKeyListeners().length > 0 ? getKeyListeners()[0] : null);

        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.BLACK);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.add(Box.createVerticalGlue());

        JLabel titleLabel = new JLabel("PAC-MAN GAME");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(titleLabel);

        menuPanel.add(Box.createVerticalStrut(50));

        JButton newGameButton = new JButton("New Game");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.setPreferredSize(new Dimension(150, 50));
        newGameButton.setFont(new Font("Arial", Font.PLAIN, 20));
        newGameButton.addActionListener(e -> startNewGame(1));
        menuPanel.add(newGameButton);

        menuPanel.add(Box.createVerticalStrut(20));

        JButton quitButton = new JButton("Quit");
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setPreferredSize(new Dimension(150, 50));
        quitButton.setFont(new Font("Arial", Font.PLAIN, 20));
        quitButton.addActionListener(e -> System.exit(0));
        menuPanel.add(quitButton);

        menuPanel.add(Box.createVerticalGlue());

        currentPanel = menuPanel;
        add(menuPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }


    private void startNewGame(int level) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        try {
            ghostImage = ImageIO.read(new URL("https://www.pngall.com/wp-content/uploads/15/Pacman-Ghost-PNG-Images.png"));
        } catch (IOException e) {
            ghostImage = null;
        }
        setTitle("Game Interface Demo");
        setSize(500, 597);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        board = new Board(10, new Location(0, 0), System.currentTimeMillis(), level);
        // Initialize game elements
        obstacles = board.getObstacles();
        pellets = board.getPellets();
        ghosts = board.getGhosts();
        powerups = board.getPowerups();
        gamePanel = createGamePanel();
        add(gamePanel, BorderLayout.CENTER);
        add(createScorePanel(), BorderLayout.NORTH);
        add(createControlPanel(), BorderLayout.SOUTH);
        currentPanel = gamePanel;

        // Remove old key listeners
        for (KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }

        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {

                Direction direction = switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> Direction.UP;
                    case KeyEvent.VK_DOWN -> Direction.DOWN;
                    case KeyEvent.VK_LEFT -> Direction.LEFT;
                    case KeyEvent.VK_RIGHT -> Direction.RIGHT;
                    default -> null;
                };
                if (direction == null) {
                    return;
                }
                board.move(direction);
                pellets = board.getPellets();
                ghosts = board.getGhosts();
                powerups = board.getPowerups();
                scoreLabel.setText("Score: " + board.getScore());
                if (board.getPelletCount() == 0) {
                    startNewGame(level + 1);
                }
                gamePanel.repaint();
            }
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyTyped(KeyEvent e) {}
        });
        revalidate();
        repaint();
        requestFocusInWindow();
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(Color.BLACK);
                int blockSize = 50;
                // Draw grid
                g.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i < getWidth(); i += blockSize) {
                    for (int j = 0; j < getHeight(); j += blockSize) {
                        g.drawRect(i, j, blockSize, blockSize);
                    }
                }
                // Draw Pac-Man centered in block
                g.setColor(Color.YELLOW);
                if (board.isImmune()) {
                    g.setColor(Color.GREEN);
                }
                int pacmanSize = 30;
                Location pacmanLocation = Location.toGridCoords(board.getPacman());
                int pacmanX = pacmanLocation.getX() + (blockSize - pacmanSize) / 2;
                int pacmanY = pacmanLocation.getY() + (blockSize - pacmanSize) / 2;
                g.fillArc(pacmanX, pacmanY, pacmanSize, pacmanSize, 45, 270);
                // Draw obstacles
                g.setColor(Color.BLUE);
                for (Point obstacle : obstacles) {
                    g.fillRect(obstacle.x, obstacle.y, blockSize, blockSize);
                }
                // Draw pellets
                g.setColor(Color.WHITE);
                int pelletSize = 10;
                for (Point pellet : pellets) {
                    int pelletX = pellet.x + (blockSize - pelletSize) / 2;
                    int pelletY = pellet.y + (blockSize - pelletSize) / 2;
                    g.fillOval(pelletX, pelletY, pelletSize, pelletSize);
                }

                // Draw powerups

                g.setColor(Color.YELLOW);
                pelletSize = 15;
                for (Point pellet : powerups) {
                    int pelletX = pellet.x + (blockSize - pelletSize) / 2;
                    int pelletY = pellet.y + (blockSize - pelletSize) / 2;
                    g.fillOval(pelletX, pelletY, pelletSize, pelletSize);
                }
                // Draw ghosts

                g.setColor(Color.RED);
                for (Point ghost: ghosts) {
                    if (ghostImage != null) {
                        g.drawImage(ghostImage, ghost.x, ghost.y, 50, 50, null);
                    } else {
                        g.fillOval(ghost.x, ghost.y, 50, 50);
                    }
                }

            }
        };
        panel.setPreferredSize(new Dimension(500, 500));
        return panel;
    }

    private JPanel createScorePanel() {
        JPanel scorePanel = new JPanel();
        scoreLabel = new JLabel("Score: " + board.getScore());
        scorePanel.add(scoreLabel);
        scorePanel.setBackground(Color.GRAY);
        return scorePanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        JButton menuButton = new JButton("Menu");
        JButton resetButton = new JButton("Reset");
        menuButton.addActionListener(_ -> showMainMenu());
        resetButton.addActionListener(_ -> startNewGame(1));
        controlPanel.add(menuButton);
        controlPanel.add(resetButton);
        controlPanel.setBackground(Color.LIGHT_GRAY);
        return controlPanel;
    }

    public static void main(String[] args) {
        new Main();
    }
}
