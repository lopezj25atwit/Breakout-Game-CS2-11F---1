package application;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.util.*;

// Abstract class to enforce game behavior (Abstract Classes/Interfaces)
abstract class GameObject {
    abstract void update();
    abstract void draw(GraphicsContext gc);
}

// Paddle class utilizing inheritance (Inheritance/Polymorphism)
class Paddle extends GameObject {
    private double x;
    private final int width = 100, height = 10;
    
    public Paddle(double startX) {
        this.x = startX;
    }
    
    public void moveLeft() {
        if (x > 0) x -= 20;
    }
    
    public void moveRight() {
        if (x < 500) x += 20;
    }
    
    public double getX() { return x; }
    
    @Override
    void update() {}
    
    @Override
    void draw(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillRect(x, 380, width, height);
    }
}

public class Breakout_Main extends Application {
    private Paddle paddle = new Paddle(250);
    private double ballX = 300, ballY = 300;
    private double ballSpeedX = 3, ballSpeedY = 3;
    private final int BALL_RADIUS = 10;
    private final int ROWS = 5, COLS = 8, BRICK_WIDTH = 70, BRICK_HEIGHT = 20;
    
    // Using Collections (Generics/Iterators/Collections)
    private Set<String> bricks = new HashSet<>();
    private Map<String, Color> brickColors = new HashMap<>();
    private int score = 0;
    private int lives = 3; // Added lives system
    private boolean gameOver = false;
    private Random random = new Random();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(600, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(new javafx.scene.layout.StackPane(canvas));
        
        scene.setOnKeyPressed(e -> {
            if (gameOver && e.getCode() == KeyCode.R) {
                resetGame();
            }
            if (!gameOver) {
                if (e.getCode() == KeyCode.LEFT) paddle.moveLeft();
                else if (e.getCode() == KeyCode.RIGHT) paddle.moveRight();
            }
        });
        
        initializeBricks();

        new AnimationTimer() {
            public void handle(long now) {
                if (!gameOver) {
                    update();
                }
                draw(gc);
            }
        }.start();

        stage.setTitle("Breakout Game");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeBricks() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                String key = i + "," + j;
                bricks.add(key);
                brickColors.put(key, getRandomColor());
            }
        }
    }

    private void update() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballX <= 0 || ballX >= 590) ballSpeedX *= -1;
        if (ballY <= 0) ballSpeedY *= -1;
        
        if (ballY >= 390) {
            lives--; // Reduce lives when the ball falls below the screen
            if (lives <= 0) {
                gameOver = true; // Set game over state
            } else {
                ballY = 300; // Reset ball position
                ballX = 300;
                ballSpeedY *= -1;
            }
        }
        
        if (ballY + BALL_RADIUS >= 380 && ballX >= paddle.getX() && ballX <= paddle.getX() + 100) {
            ballSpeedY *= -1;
        }
        
        checkBrickCollision();
    }
    
    private void checkBrickCollision() {
        int brickRow = (int) (ballY / BRICK_HEIGHT);
        int brickCol = (int) (ballX / BRICK_WIDTH);
        String key = brickRow + "," + brickCol;
        
        if (bricks.contains(key)) {
            bricks.remove(key);
            brickColors.remove(key);
            ballSpeedY *= -1;
            score += 10;
        }
    }
    
    private void resetGame() {
        lives = 3;
        score = 0;
        bricks.clear();
        brickColors.clear();
        initializeBricks();
        ballX = 300;
        ballY = 300;
        ballSpeedX = 3;
        ballSpeedY = 3;
        gameOver = false;
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 600, 400);
        
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.fillText("GAME OVER! Press R to Restart", 220, 200);
            return;
        }
        
        paddle.draw(gc);
        gc.setFill(Color.RED);
        gc.fillOval(ballX, ballY, BALL_RADIUS, BALL_RADIUS);
        
        for (String key : bricks) {
            String[] parts = key.split(",");
            int i = Integer.parseInt(parts[0]);
            int j = Integer.parseInt(parts[1]);
            gc.setFill(brickColors.get(key));
            gc.fillRect(j * BRICK_WIDTH, i * BRICK_HEIGHT, BRICK_WIDTH - 5, BRICK_HEIGHT - 5);
        }
        
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 390);
        gc.fillText("Lives: " + lives, 550, 390); // Display lives count
    }
    
    private Color getRandomColor() {
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
