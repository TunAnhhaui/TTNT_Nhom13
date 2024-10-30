package com.example.nhom4ai.codettnt;



import java.util.Vector;

public class ThuatToanAStar {
    public Node startNode;  //Trạng thái bắt đầu
    public Node goalNode;  //Trạng thái đích
    public Node currentNode;  //Tìm min f
    private final Vector<Node> FRINGE; //lưu trữ các node đang chờ để duỵêt
    private Vector<Node> CHILD;  //Danh sách các node con của currentNode
    public Vector<Node> CLOSED; // Danh sách chứa các node đã được và xử lý
    public Vector<int[]> RESULT; //Lưu trữ chuỗi các trạng thái từ startNode đến goalNode khi tìm thấy lời giải.
    protected int approvedNodes; //Đếm số node
    protected int totalNodes; //Tổng số node
    protected long time;
    protected static boolean stop = false; //flag dừng thuật toán.
    protected String error;


    //Hàm khởi tạo
    public ThuatToanAStar() {
        FRINGE = new Vector<>();
        CHILD = new Vector<>();
        CLOSED = new Vector<>();
        RESULT = new Vector<>();
    }

    public void solve() {
        RESULT.clear();
        long startTime = System.currentTimeMillis();
        startNode.f = startNode.h = startNode.estimate(goalNode.state);
        startNode.g = 0;
        totalNodes = approvedNodes = 0;
        FRINGE.add(startNode);
        while (!FRINGE.isEmpty()) {
            // Điều kiện dừng thuật toán
            if (System.currentTimeMillis() - startTime > 60000) {
                error = "Thuật toán quá tốn thời gian!";
                approvedNodes = Integer.MAX_VALUE;
                FRINGE.clear();
                CHILD.clear();
                CLOSED.clear();
                return;
            }
            if (stop) {
                FRINGE.clear();
                CHILD.clear();
                CLOSED.clear();
                return;
            }
            // Tìm node có hàm đánh giá - f(n) nhỏ nhất trong FRINGE
            int fMin = FRINGE.get(0).f;
            currentNode = FRINGE.get(0);
            for (Node node : FRINGE) {
                if (node.f < fMin) {
                    fMin = node.f;
                    currentNode = node;
                }
            }
            FRINGE.removeElement(currentNode);
            CLOSED.add(currentNode);
            // Kiểm tra node hiện tại có phải đích hay không
            if (currentNode.h == 0) {
                totalNodes = approvedNodes + FRINGE.size();
                time = System.currentTimeMillis() - startTime;
                addResult(currentNode); // Thêm kết quả vào RESULT
                FRINGE.clear();
                CHILD.clear();
                CLOSED.clear();
                return;
            }
            // Thiết lập các node con
            CHILD = currentNode.successors();
            if (currentNode.parent != null) {
                for (int i = 0; i < CHILD.size(); i++) {
                    Node child = CHILD.get(i);
                    if (child.equals(currentNode.parent)) {
                        CHILD.removeElement(child);
                        break;
                    }
                    // Kiểm tra trạng thái đã được duyệt chưa
                    for (Node node : CLOSED) {
                        if (node.equals(child)) {
                            CHILD.removeElement(child);
                            break;
                        }
                    }
                }
            }
            // Đưa các node con vào FRINGE
            for (Node child : CHILD) {
                child.parent = currentNode;
                child.g = currentNode.g + child.cost;
                child.h = child.estimate(goalNode.state);
                child.f = child.g + child.h;
                // Nếu trạng thái đã tồn tại trong FRINGE và hàm đánh giá tốt hơn => Thay thế
                for (Node node : FRINGE) {
                    if (node.equals(child) && child.f < node.f) {
                        FRINGE.removeElement(node);
                        break;
                    }
                }
                FRINGE.add(0, child);
            }
            CHILD.clear();
            approvedNodes++;
        }
    }
    // Truy vết kết quả
    public void addResult(Node n) {
        if(n.parent!=null) {
            addResult(n.parent);
        }
        RESULT.add(n.state.value);
    }


}