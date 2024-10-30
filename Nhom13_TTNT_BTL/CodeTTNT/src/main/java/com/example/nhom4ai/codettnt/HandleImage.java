package com.example.nhom4ai.codettnt;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HandleImage {
    private final int Size;
    private final int Length;
    protected int blank;
    protected int[] Value;
    private final Image img;
    private final double canvasSize = 550;  // Kích thước Canvas cố định 550x550
    private double cw1, ch1; // Kích thước của mỗi ô

    public boolean win = false;

    public HandleImage(Image img, int size, int[] val) {
        this.img = img;
        this.Size = size;
        Length = Size * Size;
        this.Value = val;
        InitImage();
    }

    public void InitImage() {
        // Tính kích thước của mỗi ô dựa trên kích thước của Canvas và số ô trong bảng
        cw1 = canvasSize / Size;
        ch1 = canvasSize / Size;
        blank = posBlank(Value);
    }

    public int posBlank(int[] Value) {
        int pos = 0;
        for (int i = 0; i < Length; i++) {
            if (Value[i] == 0) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    public void paint(GraphicsContext g) {
        // Tạo nền đen cho Canvas
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvasSize, canvasSize);

        // Vẽ từng ô số trong bảng
        for (int i = 0; i < Length; i++) {
            double x = (i % Size) * cw1;
            double y = (i / Size) * ch1;

            if (Value[i] != 0) {
                // Vẽ ô màu trắng
                g.setFill(Color.WHITE);
                g.fillRect(x + 0.5, y + 0.5, cw1 - 1, ch1 - 1);

                // Vẽ số trong ô với kích thước tự động điều chỉnh
                g.setFill(Color.BLACK);
                g.setFont(Font.font("Roboto", FontWeight.BOLD, canvasSize / (Size * 2.5)));

                String text = String.valueOf(Value[i]);

                // Điều chỉnh căn giữa số trong ô
                double textX = x + (cw1 - g.getFont().getSize()) / 2;
                double textY = y + (ch1 + g.getFont().getSize()) / 2;

                g.fillText(text, textX, textY);
            } else {
                // Vẽ ô trống màu xám nhạt
                g.setFill(Color.rgb(181, 178, 172));
                g.fillRect(x + 0.5, y + 0.5, cw1 - 1, ch1 - 1);
            }
        }
    }
}
