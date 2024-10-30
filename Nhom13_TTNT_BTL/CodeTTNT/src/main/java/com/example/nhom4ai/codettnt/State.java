package com.example.nhom4ai.codettnt;


import java.util.*;

public class State {
    public static int heuristic;
    public static int goal;
    public int[] value;
    private final int size;
    private final int length;
    private int blank;

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
        for (int i = 0; i < length - 1; i++) {
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
        int t = 20 * size;
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
            while (true) {
                b = rand.nextInt(4) + 1;
                if (Math.abs(b - a) != 2) {
                    a = b;
                    break;
                }
            }
        } while (count != t);
        return value;
    }

    // Tìm vị trí trống
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
    public Vector<State> successors() {
        Vector<State> states = new Vector<>();
        int blank = posBlank(value);
        if (blank / size > 0) {
            addSuccessor(blank, blank - size, states, value);
        }
        if (blank / size < size - 1) {
            addSuccessor(blank, blank + size, states, value);
        }
        if (blank % size > 0) {
            addSuccessor(blank, blank - 1, states, value);
        }
        if (blank % size < size - 1) {
            addSuccessor(blank, blank + 1, states, value);
        }
        return states;
    }

    // Add trạng thái con
    public void addSuccessor(int oldBlank, int newBlank, Vector<State> states, int[] oldVal) {
        int[] newVal = oldVal.clone();
        newVal[oldBlank] = newVal[newBlank];
        newVal[newBlank] = 0;
        states.add(new State(newVal, size));
    }

    public int estimate(State goalState) {
        int est = 0;
        if (heuristic == 1)
            est = heuristic1(goalState);
        return est;
    }

    // Heuristic 1 - Tổng số ô sai vị trí
    public int heuristic1(State goalState) {
        int[] goalValue = goalState.value;
        int distance = 0;
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
