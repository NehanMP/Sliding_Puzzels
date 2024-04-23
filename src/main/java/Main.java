import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Load file
            Map map = ReadMap.readMapFromFile("example_algorithms/maze30_2.txt");

            // Print the grid
            printGrid(map.getGrid());

            // Print shortest path
            List<String> shortestPath = FindPath.findShortestPath(map);
            if (!shortestPath.isEmpty()) {
                System.out.println("\nShortest Path:");
                int step = 1;
                for (String stepDescription : shortestPath) {
                    System.out.println(step + ". " + stepDescription);
                    step++;
                }
            } else {
                System.out.println("\nNo path found.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
        }
    }

    private static void printGrid(char[][] grid) {
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell);
            }
            System.out.println();
        }
    }
}
