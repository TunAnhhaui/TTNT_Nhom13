package com.example.nhom4ai.codettnt;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Vector;


public class PuzzleController implements Initializable, Runnable {

    @FXML
    private Label timerLabel;

    @FXML
    private TextField stepField;

    @FXML
    private Button aiBtn, compareBtn, playBtn, jumbleBtn, addImage, addNumber;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private SplitMenuButton sizeMenu, algorithmMenu;

    @FXML
    private Canvas imgCanvas;

    @FXML
    private ImageView imageView;

    @FXML
    private ToggleGroup difficultyToggle;

    @FXML
    private ToggleGroup algorithmToggle;

    @FXML
    private Pane displayPane;

    public Image image;
    public HandleImage handledImage;

    private ThuatToanAStar thuatToanAStar;

    private int size;
    private State state;
    private State goalState;
    private int[] value;
    private Vector<int[]> result;
    private String algorithm;
    private int countStep = 0;
    private boolean isSolve = false;
    private boolean isPlay = false;
    private long startTime;
    private int approvedNodes;
    private int totalNodes;
    private long aiTime;
    private String error;
    private final Vector<Object> compareResults = new Vector<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        State.heuristic = 1;
        State.goal = 2;
        size = 3;
        algorithm = "A*";
        state = new State(size);
        value = state.createGoalArray();
        goalState = new State(size);
        goalState.createGoalArray();
        progressBar.setVisible(false);
        displayImage(null);  // Hiển thị lần đầu
        timerLabel.setText("00:00");
    }

    public void run() {
        int totalStep = result.size() - 1;
        for (int i = 0; i <= totalStep; i++) {
            value = result.get(i);
            state.value = value;
            displayImage(image);
            String step = i + "/" + totalStep;
            Platform.runLater(() -> stepField.setText(step));
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(this::notAI);
    }

    public void notAI() {
        isSolve = false;
        aiBtn.setText("Tự động");
        playBtn.setDisable(false);
        setEnable();
    }

    @FXML
    public void onChangeSize() {
        RadioMenuItem selectedDiff = (RadioMenuItem) difficultyToggle.getSelectedToggle();
        size = switch (selectedDiff.getId()) {
            case "medium" -> 4;
            default -> 3;
        };
        sizeMenu.setText(selectedDiff.getText());
        state = new State(size);
        value = state.createGoalArray();
        goalState = new State(size);
        goalState.createGoalArray();
        handledImage = new HandleImage(image, size, value); // Cập nhật handledImage với kích thước mới
        displayImage(image);
    }

    @FXML
    protected void startTimer() {
        startTime = System.currentTimeMillis();
        Thread timerThread = new Thread(() -> {
            while (isPlay) {
                long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                String time = String.format("%02d:%02d", elapsed / 60, elapsed % 60);
                Platform.runLater(() -> timerLabel.setText(time));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    @FXML
    protected void onAddImageClick() {
        // Thêm logic để chọn và hiển thị ảnh vào `imageView` nếu cần.
    }

    @FXML
    protected void onAddNumberClick() {
        countStep = 0;
        stepField.setText("0");
        value = state.createGoalArray();
    }

    @FXML
    protected void onPlayClick() {
        if (isPlay) {
            notPlay();
        } else {
            playing();
            startTimer();
        }
    }

    @FXML
    protected void onJumbleClick() {
        value = state.createRandomArray();
        countStep = 0;
        stepField.setText("0");
        displayImage(image);
    }

    @FXML
    protected void onAiClick() {
        // Gọi thuật toán AI để giải quyết câu đố.
        countStep = 0;
        if (!isSolve) {
            ThuatToanAStar.stop = false;
            solveThread().start();
            solving();
        } else {
            ThuatToanAStar.stop = true;
            notSolve();
        }
    }


    // Luồng tìm kiếm lời giải
    public Thread solveThread() {
        return new Thread(() -> {
            if (Objects.equals(algorithm, "A*")) {
                solveAStar();
            }
            // Nếu tìm được lời giải
            if (result.size() > 1) {
                Platform.runLater(this::showAlert);
            }
            // Nếu không tìm được lời giải
            else if (result.isEmpty() && error != null) {
                Platform.runLater(this::showWarning);
            }
            // Người chơi chọn dừng tìm kiếm hoặc trạng thái ban đầu là trạng thái đích
            else {
                Platform.runLater(this::notSolve);
            }
        });
    }

    // Giải quyết bài toán bằng thuật toán A*
    public void solveAStar() {
        thuatToanAStar = new ThuatToanAStar();
        thuatToanAStar.startNode = new Node(state, 0);
        thuatToanAStar.goalNode = new Node(goalState, 1);
        thuatToanAStar.solve();
        result = thuatToanAStar.RESULT;
        approvedNodes = thuatToanAStar.approvedNodes;
        totalNodes = thuatToanAStar.totalNodes;
        aiTime = thuatToanAStar.time;
        error = thuatToanAStar.error;
    }

    @FXML
    protected void onCompareClick() {
        // So sánh các thuật toán
    }

    public void onMouseClicked(MouseEvent me) {
        if (isPlay) {
            int blank = state.posBlank(state.value);  // Lấy vị trí của ô trống
            int x = blank % size;  // Cột của ô trống
            int y = blank / size;  // Hàng của ô trống

            // Chuyển đổi tọa độ nhấp chuột thành tọa độ ô
            int mx = (int) (me.getX() / (imgCanvas.getWidth() / size));
            int my = (int) (me.getY() / (imgCanvas.getHeight() / size));

            // Kiểm tra nếu ô nhấp vào nằm cạnh ô trống để thực hiện di chuyển
            if ((mx == x && Math.abs(my - y) == 1) || (my == y && Math.abs(mx - x) == 1)) {
                // Tăng số bước
                countStep++;

                // Di chuyển ô theo hướng phù hợp
                if (mx == x && my == y - 1) {
                    state.UP();
                } else if (mx == x && my == y + 1) {
                    state.DOWN();
                } else if (mx == x - 1 && my == y) {
                    state.LEFT();
                } else if (mx == x + 1 && my == y) {
                    state.RIGHT();
                }

                // Cập nhật trạng thái và vẽ lại bảng
                displayImage(image);
                stepField.setText(String.valueOf(countStep));

                // Kiểm tra xem đã đạt đến trạng thái đích chưa
                if (Arrays.equals(state.value, goalState.value)) {
                    showResult();
                    countStep = 0;
                }
            }
        }
    }


    public void showResult() {
        long time = (System.currentTimeMillis() - startTime) / 1000;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Bạn đã hoàn thành trò chơi!");
        alert.setContentText("Số bước giải: " + countStep + "\n"
                + "Thời gian giải: " + (time >= 60 ? time / 60 + ":" + time % 60 : time) + "s");
        alert.showAndWait().ifPresent(res -> notPlay());
    }

    public void showWarning() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        ButtonType closeTypeBtn = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(closeTypeBtn);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Không tìm được lời giải!");
        alert.setContentText("Nguyên nhân: \n" + error);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        alert.showAndWait().ifPresent(res -> notSolve());
    }

    // Bảng thông báo kết quả tìm kiếm
    public void showAlert() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        ButtonType runTypeBtn = new ButtonType("Chạy", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeTypeBtn = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.setTitle("Thông báo");
        alert.getButtonTypes().setAll(runTypeBtn, closeTypeBtn);
        alert.setHeaderText("Lời giải: ");
        // Kết quả tìm kiếm được
        alert.setContentText("Thuật toán sử dụng: "
                + "A* với Heuristic " + State.heuristic + "\n"
                + "Số node đã duyệt: " + approvedNodes + "\n"
                + "Tổng số node trên cây: " + totalNodes + "\n"
                + "Tổng số bước: " + (result.size() - 1) + "\n"
                + "Thời gian tìm kiếm: " + aiTime + "ms" + "\n"
                + "Bạn có muốn chạy lời giải?");
        alertStyle(alert, closeTypeBtn);
        // Hiển thị kết quả và đợi phải hồi
        alert.showAndWait().ifPresent(res -> {
            if (res == runTypeBtn) {
                aiBtn.setDisable(true);
                Thread runResult = new Thread(this);
                runResult.start();
            } else {
                notSolve();
            }
        });
    }

    // Thêm icon và style cho bảng lời giải và bảng so sánh
    public void alertStyle(Alert alert, ButtonType closeTypeBtn) {
        // Thêm icon
        DialogPane dialogPane = alert.getDialogPane();
        // Thêm css
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        javafx.scene.Node closeBtn = alert.getDialogPane().lookupButton(closeTypeBtn);
        closeBtn.setId("close-btn");
    }

    public void playing() {
        isPlay = true;
        playBtn.setText("Dừng");
        aiBtn.setDisable(true);
        setDisable();
    }

    public void notPlay() {
        isPlay = false;
        playBtn.setText("Chơi");
        aiBtn.setDisable(false);
        setEnable();
    }

    private void setEnable() {
        aiBtn.setDisable(false);
        jumbleBtn.setDisable(false);
        addImage.setDisable(false);
        addNumber.setDisable(false);
        compareBtn.setDisable(false);
        sizeMenu.setDisable(false);
        algorithmMenu.setDisable(false);
        progressBar.setVisible(false);
    }

    private void setDisable() {
        jumbleBtn.setDisable(true);
        addImage.setDisable(true);
        addNumber.setDisable(true);
        compareBtn.setDisable(true);
        sizeMenu.setDisable(true);
        algorithmMenu.setDisable(true);
        progressBar.setVisible(true);
    }

    public void displayImage(Image img) {
        if (img == null) {
            displayPane.setStyle("-fx-background-radius: 20px; -fx-background-color: black");
        } else {
            displayPane.setStyle("");
        }
        handledImage = new HandleImage(img, size, value);
        if (state.isGoal(goalState)) {
            handledImage.win = true;
        }
        GraphicsContext gc = imgCanvas.getGraphicsContext2D();
        handledImage.paint(gc);
    }

    public void onKeyPressed(KeyEvent ke) {
        if (isPlay) {

            int[] tmpValue = Arrays.copyOf(value, size * size);
            switch (ke.getCode()) {
                case S -> state.UP();
                case D -> state.LEFT();
                case W -> state.DOWN();
                case A -> state.RIGHT();
                default -> value = tmpValue;
            }
            countStep++;
            if (Arrays.equals(tmpValue, value)) {
                countStep--;
            }
            if (Arrays.equals(value, goalState.value)) {
                if (countStep != 0) {
                    showResult();
                    countStep = 0;
                }
            }
            stepField.setText(String.valueOf(countStep));
            displayImage(image);
        }
    }

    // Trạng thái đang tìm kiếm
    public void solving() {
        isSolve = true;
        aiBtn.setText("Dừng");
        playBtn.setDisable(true);
        setDisable();
    }

    // Trạng thái không tìm kiếm
    public void notSolve() {
        isSolve = false;
        aiBtn.setText("Tự động");
        playBtn.setDisable(false);
        setEnable();
    }

    public void onChangeAlgorithm() {
        RadioMenuItem selectedAlgorithm = (RadioMenuItem) algorithmToggle.getSelectedToggle();
        switch (selectedAlgorithm.getId()) {
            case "heuristic1" -> {
                State.heuristic = 1;
                algorithm = "A*";
            }
            default -> {
                State.heuristic = 1;
                algorithm = "A*";
            }
        }
        algorithmMenu.setText(selectedAlgorithm.getText());
    }
}
