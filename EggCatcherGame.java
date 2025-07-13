import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class EggCatcherGame extends JPanel implements KeyListener {
    private final int canvasWidth = 800;
    private final int canvasHeight = 400;

    private final ArrayList<Egg> eggs = new ArrayList<>();
    private final Random random = new Random();
    private final Color[] eggColors = {Color.CYAN, Color.GREEN, Color.PINK, Color.YELLOW, Color.ORANGE};

    private int score = 0;
    private int livesRemaining = 3;

    private final int eggWidth = 45;
    private final int eggHeight = 55;
    private final int pointsPerEgg = 10;

    private int eggSpeed = 500;
    private int eggInterval = 4000;
    private final double difficultyScaling = 0.95;

    private final int catcherWidth = 100;
    private final int catcherHeight = 100;
    private int catcherX;
    private int catcherY;

    private Timer eggTimer;
    private Timer moveEggsTimer;
    private boolean gameStarted = false;

    public EggCatcherGame() {
        setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        setBackground(new Color(0, 191, 255)); // "deep sky blue"
        catcherX = canvasWidth / 2 - catcherWidth / 2;
        catcherY = canvasHeight - catcherHeight - 20;

        eggTimer = new Timer(eggInterval, e -> createEgg());
        moveEggsTimer = new Timer(eggSpeed, e -> moveEggs());

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }

    private void startGame() {
        eggTimer.start();
        moveEggsTimer.start();
        gameStarted = true;
    }

    private void createEgg() {
        int x = random.nextInt(canvasWidth - eggWidth);
        eggs.add(new Egg(x, 40, eggWidth, eggHeight, eggColors[random.nextInt(eggColors.length)]));
        if (eggTimer.getDelay() > 100) {
            eggTimer.setDelay((int) (eggTimer.getDelay() * difficultyScaling));
        }
    }

    private void moveEggs() {
        Iterator<Egg> iterator = eggs.iterator();
        while (iterator.hasNext()) {
            Egg egg = iterator.next();
            egg.y += 10;
            if (egg.y > canvasHeight) {
                iterator.remove();
                loseLife();
                if (livesRemaining == 0) {
                    gameOver();
                }
            }
        }
        repaint();
    }

    private void loseLife() {
        livesRemaining--;
    }

    private void gameOver() {
        eggTimer.stop();
        moveEggsTimer.stop();
        JOptionPane.showMessageDialog(this, "Game Over! Final Score: " + score);
        System.exit(0);
    }

    private void checkCatch() {
        Iterator<Egg> iterator = eggs.iterator();
        while (iterator.hasNext()) {
            Egg egg = iterator.next();
            if (egg.x >= catcherX && egg.x + egg.width <= catcherX + catcherWidth
                    && egg.y + egg.height >= catcherY) {
                iterator.remove();
                increaseScore(pointsPerEgg);
            }
        }
        repaint();
    }

    private void increaseScore(int points) {
        score += points;
        if (moveEggsTimer.getDelay() > 50) {
            moveEggsTimer.setDelay((int) (moveEggsTimer.getDelay() * difficultyScaling));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the ground
        g.setColor(new Color(46, 139, 87)); // "sea green"
        g.fillRect(0, canvasHeight - 100, canvasWidth, 100);

        // Draw the sun
        g.setColor(Color.ORANGE);
        g.fillOval(-80, -80, 200, 200);

        // Draw the catcher
        g.setColor(Color.BLUE);
        g.fillArc(catcherX, catcherY, catcherWidth, catcherHeight, 0, 180);

        // Draw the eggs
        for (Egg egg : eggs) {
            g.setColor(egg.color);
            g.fillOval(egg.x, egg.y, egg.width, egg.height);
        }

        // Draw the score and lives
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + livesRemaining, canvasWidth - 100, 20);

        // Draw start message if the game hasn't started
        if (!gameStarted) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            g.drawString("Move to right or left on your keyboard to start the game", canvasWidth / 2 - 300, canvasHeight / 2);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameStarted) {
            startGame();
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT && catcherX > 0) {
            catcherX -= 20;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && catcherX + catcherWidth < canvasWidth) {
            catcherX += 20;
        }
        checkCatch();
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Egg Catcher");
        EggCatcherGame game = new EggCatcherGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static class Egg {
        int x, y, width, height;
        Color color;

        Egg(int x, int y, int width, int height, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
        }
    }
}

