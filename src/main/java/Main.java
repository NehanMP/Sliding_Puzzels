// Name - Nehan Perera
// Student ID - 20220606

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Load text file
            Map map = readMapFromFile("example_algorithms/maze30_1.txt");
//            Map map = readMapFromFile("benchmark_series/puzzle_10.txt");

            // Print the grid
            printGrid(map.getPuzzle());

            // Print shortest path
            List<String> shortestPath = FindPath.findShortestPath(map);
            if (!shortestPath.isEmpty()) {
                System.out.println("\nShortest Path:");
                int step = 1;
                for (String stepDescription : shortestPath) {
                    System.out.println(step + ". " + stepDescription);
                    step++;
                }

                System.out.println();
                long startTime = System.nanoTime();
                // Code block to measure
                long endTime = System.nanoTime();
                long duration = (endTime - startTime); // Duration in nanoseconds
                System.out.println("Execution time: " + duration + " nanoseconds");


                Runtime runtime = Runtime.getRuntime();
                runtime.gc(); // Run garbage collection to clear residual memory
                long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

                // Code block to measure
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                long memoryUsed = memoryAfter - memoryBefore;
                System.out.println("Memory used: " + memoryUsed + " bytes");


            } else {
                System.out.println("\nNo path found.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }
    }


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
        List<String> lines = new ArrayList<>();

        int startRow = -1;
        int startCol = -1;
        int goalRow = -1;
        int goalCol = -1;

        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        scanner.close();

        int numRows = lines.size();
        int numCols = lines.stream().mapToInt(String::length).max().orElse(0);

        char[][] puzzle = new char[numRows][numCols]; // grid size set dynamically based on input

        for (int row = 0; row < numRows; row++) {
            String line = lines.get(row);
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                puzzle[row][col] = c;
                if (c == 'S') {
                    startRow = row;
                    startCol = col;
                } else if (c == 'F') {
                    goalRow = row;
                    goalCol = col;
                }
            }
        }

        System.out.println("Rows: " + numRows + " | Columns: " + numCols);
        System.out.println("Found 'S' at Column: " + (startCol + 1) + " Row: " + (startRow + 1));
        System.out.println("Found 'F' at Column: " + (goalCol + 1) + " Row: " + (goalRow + 1) + "\n");

        return new Map(puzzle, startRow, startCol, goalRow, goalCol);
    }


    /**
     * This method prints the grid to the console.
     * Each row of the grid is printed on a new line, and each cell in the row is printed horizontally.
     *
     * @param grid A 2D array of characters representing the grid to be printed.
     */
    private static void printGrid(char[][] grid) {
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell);
            }
            System.out.println();
        }
    }
}
