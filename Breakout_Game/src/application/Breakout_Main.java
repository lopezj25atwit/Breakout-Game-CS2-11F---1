package application;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class Breakout_Main extends Application {
    private double paddleX = 250;
    private double ballX = 300, ballY = 300;
    private double ballSpeedX = 3, ballSpeedY = 3;
    private final int PADDLE_WIDTH = 100, PADDLE_HEIGHT = 10;
    private final int BALL_RADIUS = 10;
    private final int ROWS = 5, COLS = 8, BRICK_WIDTH = 70, BRICK_HEIGHT = 20;
    private boolean[][] bricks = new boolean[ROWS][COLS];
    private int score = 0;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(600, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(new javafx.scene.layout.StackPane(canvas));
        
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT && paddleX > 0) {
                paddleX -= 20;
            } else if (e.getCode() == KeyCode.RIGHT && paddleX < 500) {
                paddleX += 20;
            }
        });
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                bricks[i][j] = true;
            }
        }

        new AnimationTimer() {
            public void handle(long now) {
                update();
                draw(gc);
            }
        }.start();

        stage.setTitle("Breakout Game");
        stage.setScene(scene);
        stage.show();
    }

    private void update() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballX <= 0 || ballX >= 590) ballSpeedX *= -1;
        if (ballY <= 0) ballSpeedY *= -1;
        if (ballY >= 390) ballY = 300; // Reset on miss
        
        if (ballY + BALL_RADIUS >= 380 && ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
            ballSpeedY *= -1;
        }
        
        checkBrickCollision();
    }
    
    private void checkBrickCollision() {
        int brickRow = (int) (ballY / BRICK_HEIGHT);
        int brickCol = (int) (ballX / BRICK_WIDTH);
        
        if (brickRow >= 0 && brickRow < ROWS && brickCol >= 0 && brickCol < COLS && bricks[brickRow][brickCol]) {
            bricks[brickRow][brickCol] = false;
            ballSpeedY *= -1;
            score += 10;
        }
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 600, 400);
        gc.setFill(Color.BLUE);
        gc.fillRect(paddleX, 380, PADDLE_WIDTH, PADDLE_HEIGHT);
        gc.setFill(Color.RED);
        gc.fillOval(ballX, ballY, BALL_RADIUS, BALL_RADIUS);
        
        gc.setFill(Color.GREEN);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (bricks[i][j]) {
                    gc.fillRect(j * BRICK_WIDTH, i * BRICK_HEIGHT, BRICK_WIDTH - 5, BRICK_HEIGHT - 5);
                }
            }
        }
        
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 390);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
