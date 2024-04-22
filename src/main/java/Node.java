public class Node {
    private Node prevNode;
    private final int col;
    private final int row;

    public Node(int col, int row) {
        this.prevNode = null;
        this.col = col;
        this.row = row;
    }

    // Getters
    public Node getPrevNode() {
        return prevNode;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    //Setters
    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }

}
