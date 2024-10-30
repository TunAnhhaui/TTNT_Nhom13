package com.example.nhom4ai.codettnt;

import java.util.Vector;

public class Node {
    public State state;
    public int f;  //Giá trị ước lượng (heuristic) từ node hiện tại đến trạng thái mục tiêu.
    public int g;  //Chi phí từ điểm bắt đầu đến node hiện tại.
    public int h; // Tổng chi phí ước lượng (f = g + h).
    public int cost; //Chi phí để đi từ node cha đến node hiện tại.
    public Node parent;  //Truy vết lại đường đi từ đích đến nguồn sau khi tìm thấy giải pháp.

    // Phương thức khởi tạo Node nhận 2 giá trị state và cost
    public Node(State state, int cost) {
        this.state = state; //Lưu trữ trạng thái của node hiện tại, thuộc kiểu State
        this.cost = cost;
    }
    // Kiểm tra value có giống value của n không
    public boolean equals(Node n) {
        boolean flag = true; //Đặt flag để chạy vòng lặp
        int[] val = state.value;
        int[] newVal = n.state.value;
        for (int i = 0; i < val.length; i++) {
            if (val[i] != newVal[i]) {
                flag = false;// Phát hiện sự khác biệt
                break; // Thoát khỏi vòng lặp ngay lập tức
            }
        }
        return flag;
    }
    // Tính ước lượng h(x)
    public int estimate(State goalState) {
        return state.estimate(goalState);
    }

    // Vector các Node con
    public Vector<Node> successors() {
        Vector<Node> nodes = new Vector<>(); // Tạo danh sách Node con
        Vector<State> states = state.successors(); // Lấy danh sách trạng thái con từ trạng thái hiện tại
        for (State value : states) {   // Duyệt qua từng trạng thái con
            nodes.add(new Node(value, 1));   // Tạo Node mới từ mỗi trạng thái con và thêm vào danh sách
        }
        return nodes;  // Trả về danh sách các Node con
    }
}
