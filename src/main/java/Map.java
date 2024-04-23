public class Map {
    private final char[][] puzzle;
    private final int startRow, startCol, goalRow, goalCol;

    public Map(char[][] puzzle, int startRow, int startCol, int goalRow, int goalCol) {
        this.puzzle = puzzle;
        this.startRow = startRow;
        this.startCol = startCol;
        this.goalRow = goalRow;
        this.goalCol = goalCol;
    }

    public char[][] getPuzzle() {
        return puzzle;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getGoalRow() {
        return goalRow;
    }

    public int getGoalCol() {
        return goalCol;
    }
}
