import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadMap {
    /**
     * Reads a map from a specified file and identifies key positions within the map.
     * The map is in a text format with each line representing a row of the grid.
     * The method scans each character of every line to construct the map and to find the start
     * and goal positions marked by 'S' and 'F'

     * @param filename The path to the file containing the map.
     * @return Returns a Map object containing the grid and positions for start and goal.
     * @throws FileNotFoundException if file does not exist.

     * file should not exceed 100 characters in a line and the map should not exceed 100 rows.

     * This method assumes a maximum grid size of 100x100; larger grids need adjustments to the array dimensions.
     */
    public static Map readMapFromFile(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        int numRows = 0;
        int numCols = 0;
        int startRow = -1;
        int startCol = -1;
        int goalRow = -1;
        int goalCol = -1;

        char[][] puzzle = new char[100][100]; // Assuming maximum 100x100 grid size

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            numCols = Math.max(numCols, line.length());

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                puzzle[numRows][i] = c;
                if (c == 'S') {
                    startRow = numRows;
                    startCol = i;
                } else if (c == 'F') {
                    goalRow = numRows;
                    goalCol = i;
                }
            }
            numRows++;
        }
        scanner.close();

        System.out.println("Rows: " + numRows + " | Columns: " + numCols);
        System.out.println("Found 'S' at Column: " + (startCol + 1) + " Row: " + (startRow + 1));
        System.out.println("Found 'F' at Column: " + (goalCol + 1) + " Row: " + (goalRow + 1) + "\n");

        char[][] fixedGrid = trimArray(puzzle, numRows, numCols);
        return new Map(fixedGrid, startRow, startCol, goalRow, goalCol);
    }

    /**
     * Trims the char array to the number of rows and columns read from the file.
     *
     * @param puzzle The original char array, might be larger than needed.
     * @param numRows The number of rows to include in the trimmed array.
     * @param numCols The number of columns to include in the trimmed array.
     * @return A new 2D char array containing only the specified number of rows and columns
     */
    private static char[][] trimArray(char[][] puzzle, int numRows, int numCols) {
        char[][] fixedGrid = new char[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(puzzle[i], 0, fixedGrid[i], 0, numCols);
        }
        return fixedGrid;
    }
}
