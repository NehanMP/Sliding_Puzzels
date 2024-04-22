import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    static char[][] map;
     static Node startNode;
    static Node goalNode;

    public static void main(String[] args){
        map = readFile("example_algorithms/maze10_5.txt");
//        map = readFile("benchmark_series/puzzle_10.txt");
        printTheMap(map);

        List<Node> shortestPath = findShortestPath();
        if (shortestPath != null) {
            System.out.println();
            System.out.println("Path found:");
            Node previousNode = null;

            for (int i = shortestPath.size() - 1; i >= 0; i--) {
                Node node = shortestPath.get(i);
                String direction = "";
                if (previousNode != null) {
                    direction = getDirection(previousNode, node);
                }
                if (direction.equals("")){
                    System.out.println((shortestPath.size() - i) + ". Start at" + " (" + (node.getCol() + 1) + "," + (node.getRow() + 1) + ")");

                } else {
                    System.out.println((shortestPath.size() - i) + ". Move " + direction + " to (" + (node.getCol() + 1) + "," + (node.getRow() + 1) + ")");
                }

                previousNode = node;
            }
            System.out.println(shortestPath.size() + 1 + ". Done!");
        } else {
            System.out.println("No path found.");
        }
    }

    /**
     * This method processes a file from the given path and constructs a matrix from its contents,
     * Each character in the file is mapped to a corresponding element in a 2D character array.

     * @param filePath A string representing the path to the target file, which must be a valid and accessible location.
     * @return A matrix of type char[][], where each element corresponds to a character in the file, organized by lines.
     *         Returns null if errors occur during file reading or if the file cannot be found.

     * BufferedReader is used for reading and handles file operations in three main steps:
     * 1. reads the number of lines to determine the number of rows for the matrix.
     * 2. reads the first line to determine the width of the matrix.
     * 3. Inserts characters to the matrix, reading through the file line by line.
     */
    public static char[][] readFile(String filePath){
        char[][] map = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

            // Get the rows and columns count of the map
            int rows = 0;
            int cols = 0;

            while (bufferedReader.readLine() != null){
                rows++;
            }
            bufferedReader.close();

            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line = bufferedReader.readLine();

            if (line != null) {
                cols = line.length();
            }
            bufferedReader.close();

            map = new char[rows][cols];

            // Insert values to the map
            bufferedReader = new BufferedReader(new FileReader(filePath));
            for (int y = 0; y < rows; y++) {
                line = bufferedReader.readLine();
                for (int x = 0; x < cols; x++) {
                    char character = line.charAt(x);
                    map[y][x] = character;
                }
            }
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File does not exist.");
        } catch (IOException e) {
            System.out.println("Error while reading the file.");
        }
        return map;
    }

    /**
     * This method is used to print the 2D character array 'map' to the console.
     * @param map A 2D character array representing the map to be printed.
     */
    public static void printTheMap(char[][] map) {
        for (char[] gridRows : map) {
            for (char node : gridRows) {
                System.out.print(node);
            }
            System.out.println();
        }
    }

    /**
     * This method finds the shortest path from the start node 'S' to the goal node 'F'.
     * It uses Dijkstra's algorithm, which is an algorithm for finding the shortest paths between nodes in a graph.
     * The map is represented as a 2D character array
     * The pathway can be traversed on '.' character but can't traverse in '0' character.

     * A distance map is initialized to keep track of the shortest distance from the start node to all other nodes.
     * It uses a priority queue to process nodes in the order of their current shortest distance. For each node, it checks
     * its adjacent nodes in all directions and updates their distances. Once the goal node is reached, the function
     * reconstructs the path from the goal node back to the start node by following the chain of previous nodes in a back iteration.

     * The output is a list of nodes representing the shortest path from the start node to the goal node.
     * If no path is found, the method returns null.

     * @return List of Nodes representing the shortest path, or null if no path exists.
     */
    public static List<Node> findShortestPath () {
        // Creating Nodes and setting their distances
        HashMap<Node, Integer> distance = new HashMap<>();

        int numRows = map.length;
        int numCols = map.length > 0 ? map[0].length : 0;

        // Print the number of rows and columns
        System.out.println();
        System.out.println("Rows: " + numRows + " | Columns: " + numCols);

        for (int y = 0; y < map.length; y++){
            for (int x = 0; x < map[0].length; x++){
                Node node = new Node(x, y);
                if (map[y][x] == 'S') {
                    startNode = node;
                    distance.put(node, 0);
                    System.out.println("Found 'S' at Column: " + (x + 1) + " Row: " + (y + 1) );

                } else if (map[y][x] != '0') {
                    distance.put(node, Integer.MAX_VALUE);
                }
            }
        }
        // Priority Queue to visit the stored nodes later
        // Arranging the nodes in ascending order based on the distance from the starting position.
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distance::get));
        queue.add(startNode);

        // Applying the Dijkstra Algorithm
        while (!queue.isEmpty()) {

            // Remove the node with the shortest distance from the queue
            Node currentNode = queue.poll();

            // Check if the current node is equal to the finish node
            if (map[currentNode.getRow()][currentNode.getCol()] == 'F') {
                System.out.println("Found 'F' at Column: " + (currentNode.getCol() + 1) + " Row: " + (currentNode.getRow() + 1));
                goalNode = currentNode;
                break;
            }
            // Explore the adjacent nodes that can be visited from the current node
            checkAdjacentNodes(currentNode, distance, queue);
        }
        // Creating the path
        List<Node> shortestPathway = new ArrayList<>();
        Node currentNode = goalNode;
        while (currentNode != null) {
            if (currentNode.getPrevNode() != null) {
                // Store the direction with the node
                getDirection(currentNode.getPrevNode(), currentNode);
            }
            shortestPathway.add(currentNode);
            currentNode = currentNode.getPrevNode();
        }
        return shortestPathway.isEmpty() ? null : shortestPathway;
    }

    /**
     * Explores all adjacent nodes from a given current node and updates their distance from the start node
     * if a shorter path is found. It examines all four directions (up, down, left, right) from the
     * current node and continuously moves in one direction until met with an obstacle
     * Once it cannot move further, the method checks if this new position offers a shorter distance to an
     * adjacent node from the start node compared to previously recorded distances.

     * If a shorter distance is found, the method updates the distance map and sets the current node as the
     * previous node for the adjacent node to track the path while adding the adjacent node to the priority
     * queue to process its adjacent nodes later.

     * @param currentNode The node from which adjacent nodes are being viewed.
     * @param distance    A map that holds the shortest distance from the start node to each node.
     * @param queue       A priority queue that orders nodes based on their distance from the start node,
     *                    used for determining the next node to explore.
     */
    private static void checkAdjacentNodes(Node currentNode, HashMap<Node, Integer> distance, PriorityQueue<Node> queue) {
        // Possible directions that can be travelled from the current position
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        // Moving to all directions one node at a time
        for (int[] direction : directions) {
            int newCol = currentNode.getCol() + direction[0];
            int newRow = currentNode.getRow() + direction[1];
            int distanceTraveled = 0;

            // Keep moving until an obstacle is met
            while (locationIsValid(newCol, newRow) && map[newRow][newCol] != '0') {
                // Traverse the node in the same direction
                newCol += direction[0];
                newRow += direction[1];
                distanceTraveled++;
            }

            // Adding the last node visited before hitting an obstacle
            newCol -= direction[0];
            newRow -= direction[1];
            int newDistance = distance.get(currentNode) + distanceTraveled;

            Node adjacentNode = null;

            // Finding existing node with same coordinates in the map
            for (Map.Entry<Node, Integer> entry : distance.entrySet()) {
                Node node = entry.getKey();
                if (node.getCol() == newCol && node.getRow() == newRow) {
                    adjacentNode = node;
                    break;
                }
            }

            if (adjacentNode != null && newDistance < distance.get(adjacentNode)) {
                distance.put(adjacentNode, newDistance);
                adjacentNode.setPrevNode(currentNode);
                queue.add(adjacentNode);
            }
        }
    }


    /**
     * This method takes two integers representing the col and row coordinates of a location
     * and checks whether these coordinates are within the valid range of the map's dimensions.
     *
     * @param col The x-coordinate (column index) of the location to check.
     * @param row The y-coordinate (row index) of the location to check.
     * @return true if the location is within the boundaries of the map; false otherwise.
     */
    private static boolean locationIsValid(int col, int row) {
        return col >= 0 && col < map[0].length && row >= 0 && row < map.length;
    }


    /**
     * The method takes two nodes as parameters and compares their row and column indices to get
     * the relative directional movement required to go from the 'from' node to the 'to' node.
     * The movement is only in four possible directions (up, down, left, right)

     * @param from The node from which movement is started.
     * @param to   The node towards which movement is made.
     * @return A String representing the direction of movement: "up", "down", "left", "right".
     *         If the nodes are the same or not directly adjacent, an empty string is returned.
     */
    private static String getDirection(Node from, Node to) {
        if (to.getRow() < from.getRow()) return "up";
        if (to.getRow() > from.getRow()) return "down";
        if (to.getCol() < from.getCol()) return "left";
        if (to.getCol() > from.getCol()) return "right";
        return "";
    }
}