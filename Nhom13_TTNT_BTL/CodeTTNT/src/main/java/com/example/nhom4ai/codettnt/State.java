package com.example.nhom4ai.codettnt;


import java.util.*;

public class State {
    public static int heuristic;
    public static int goal; //Trạng thái đích
    public int[] value; //1 Mảng số nguyên.
    private final int size; // Kich thuoc mảng
    private final int length; //Tong so o của mảng. VD: size =3 =>lemgtj = 3x3
    private int blank; //Vị trí của o trống

    // Truyền vào kích thước của puzzle
    public State(int m) {
        this.size = m;
        this.length = size * size;
        this.value = new int[length];
        this.blank = 0;
    }

    // Truyền vào trạng thái và kích thước của puzzle
    public State(int[] v, int size) {
        this.value = v;
        this.size = size;
        this.length = size * size;
        this.blank = posBlank(this.value);
    }

    public void Init() {
        for (int i = 0; i < length - 1; i++) { //Khởi tạo ô đánh số từ 1 tới length -1
            value[i] = i + 1;
        }
        value[length - 1] = 0;
    }

    // Tạo trang thái đích
    public int[] createGoalArray() {
        Init();
        return value;
    }

    // Tạo trạng thái ngẫu nhiên
    public int[] createRandomArray() {
        Init();
        Random rand = new Random();
        int t = 20 * size; //Số lần di chuyển ngẫu nhiên cho trang thái đầu
        int count = 0;
        int a = 1, b;
        do {
            switch (a) {
                case 1 -> UP();
                case 2 -> RIGHT();
                case 3 -> DOWN();
                case 4 -> LEFT();
            }
            count++;
            while (true) {  //Kiểm tra lần duyệt tiếp theo không quay  lại trạng thái trước
                b = rand.nextInt(4) + 1;
                if (Math.abs(b - a) != 2) {
                    a = b;
                    break;
                }
            }
        } while (count != t);
        return value;
    }

    // Tìm vị trí trống(o so 0)
    public int posBlank(int[] val) {
        int pos = 0;
        for (int i = 0; i < val.length; i++) {
            if (val[i] == 0) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    // Kiểm tra trạng thái có phải trạng thái đích không
    public boolean isGoal(State goalState) {
        int[] goalValue = goalState.value;
        boolean flag = true;
        for (int i = 0; i < length; i++) {
            if (value[i] != goalValue[i]) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    // Vector các trạng thái con
    //rả về một Vector các trạng thái con
    //Di chuyển ô số 0 sang các vị trí
    public Vector<State> successors() {
        Vector<State> states = new Vector<>();
        int blank = posBlank(value); //Vị trí ô số 0 trong mảng
        if (blank / size > 0) {
            addSuccessor(blank, blank - size, states, value); //Di chuyển lên trên
        }
        if (blank / size < size - 1) {
            addSuccessor(blank, blank + size, states, value); //Di chuyển xuống dưới
        }
        if (blank % size > 0) { //Kiểm tra số dư để đảm bảo kh nằm ở cột 1
            addSuccessor(blank, blank - 1, states, value); ////Di chuyển sang trái
        }
        if (blank % size < size - 1) {
            addSuccessor(blank, blank + 1, states, value); //di chuyển sang phải
        }
        return states;
    }

    // Add trạng thái con
    public void addSuccessor(int oldBlank, int newBlank, Vector<State> states, int[] oldVal) {
        int[] newVal = oldVal.clone(); //Sao chep trạng thái
        newVal[oldBlank] = newVal[newBlank]; //Hoán đổi
        newVal[newBlank] = 0;
        states.add(new State(newVal, size));
    }

    //Chon ham heuristic => Se bo sung them cac ham h2,3,4,5,6
    public int estimate(State goalState) {
        int est = 0;
        if (heuristic == 1)
            est = heuristic1(goalState);
        return est;
    }

    // Heuristic 1 - Tổng số ô sai vị trí
    public int heuristic1(State goalState) {
        int[] goalValue = goalState.value;
        int distance = 0; //Dem vi tri sai trong mang
        for (int i = 0; i < length; i++) {
            if (value[i] != 0 && value[i] != goalValue[i])
                distance++;
        }
        return distance;
    }

    // Di chuyển ô trống lên trên
    public void UP() {
        blank = posBlank(value);
        int temp;
        if (blank >= size) {
            temp = value[blank];
            value[blank] = value[blank - size];
            value[blank - size] = temp;
            blank -= size;
        }
    }

    // Di chuyển ô trống sang phải
    public void RIGHT() {
        blank = posBlank(value);
        int temp;
        if (blank % size != size - 1) {
            temp = value[blank];
            value[blank] = value[blank + 1];
            value[blank + 1] = temp;
            blank += 1;
        }
    }

    // Di chuyển ô trống xuống dưới
    public void DOWN() {
        blank = posBlank(value);
        int temp;
        if (blank < length - size) {
            temp = value[blank];
            value[blank] = value[blank + size];
            value[blank + size] = temp;
            blank += size;
        }
    }

    // Di chuyển ô trống sang trái
    public void LEFT() {
        blank = posBlank(value);
        int temp;
        if (blank % size != 0) {
            temp = value[blank];
            value[blank] = value[blank - 1];
            value[blank - 1] = temp;
            blank -= 1;
        }
    }
}
