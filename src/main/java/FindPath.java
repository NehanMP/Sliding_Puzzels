import java.util.*;

public class FindPath {

    /**
     * Finds the shortest path from a start position to a goal position within a grid using the algorithm.
     * It  performs pathfinding using a priority-based approach to determine the shortest path to the goal.
     * After finding the shortest path, the method returns the path as a list of steps.

     * @param map The Map object that contains the grid which is the puzzle, and the start and goal positions. The grid is a 2D character
     *            array where traversal costs or obstacles can be defined.
     *
     * @return A list of strings that describes each step of the shortest path from the start position to the goal position.
     */
    public static List<String> findShortestPath(Map map) {
        int rows = map.getPuzzle().length;
        int cols = map.getPuzzle()[0].length;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // Initialize data structures for pathfinding
        int[][] distances = new int[rows][cols];
        int[][][] prev = new int[rows][cols][2];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }
        distances[map.getStartRow()][map.getStartCol()] = 0;
        prev[map.getStartRow()][map.getStartCol()] = new int[]{-1, -1};

        // Perform pathfinding to update distances and prev arrays
        findingPath(map, distances, prev, directions);

        // Reconstruct the path based on the results from pathfinding
        return reconstructPath(map, prev);
    }


    /**
     * Dijkstra's algorithm implemented using a priority queue to find the shortest path from the start node to the goal node.
     * The  priority queue always expand the least costly node.

     * It updates distances to each node and records the path taken using predecessor links.

     * @param map The Map object containing the grid, with the start and goal positions.
     *
     * @param distances A 2D array of integers where each element represents the minimum cost (distance) from the start node
     *                  to the node at that position in the grid. This array is updated continuously as the algorithm finds
     *                  potentially shorter paths to each node.

     * @param prev 3D array that tracks the predecessors for each node in the grid.

     * @param directions An array of possible movements up, down, left or right used to explore neighboring nodes from the current node.
     */
    private static void findingPath(Map map, int[][] distances, int[][][] prev, int[][] directions) {
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.offer(new int[]{map.getStartRow(), map.getStartCol(), 0});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int row = curr[0];
            int col = curr[1];

            if (row == map.getGoalRow() && col == map.getGoalCol()) {
                break;
            }
            moveToNextNode(map, row, col, distances, pq, prev, directions);
        }
    }


    /**
     * Reconstructs the shortest path from the start node to the goal node.

     * This method traces the path backwards from the goal to the start by following the predecessor links,
     * adding each step to the path list in reverse order.
     *
     * @param map The Map object containing grid information.
     * @param prev An array where each element holds a two-element array indicating the row and column of the node, to get the shortest path calculation
     *
     * @return A list of strings, each representing a step in the path.
     */
    private static List<String> reconstructPath(Map map, int[][][] prev) {
        List<String> shortestPath = new ArrayList<>();
        int[] curr = new int[]{map.getGoalRow(), map.getGoalCol()};
        shortestPath.add("Done!");

        while (curr[0] != -1 && curr[1] != -1) {
            int[] next = prev[curr[0]][curr[1]];
            if (next[0] == -1 && next[1] == -1) {
                shortestPath.add(0, "Start at (" + (curr[1] + 1) + "," + (curr[0] + 1) + ")");
            } else {
                String move = getMoveDirection(next, curr);
                shortestPath.add(0, "Move " + move + " to (" + (curr[1] + 1) + "," + (curr[0] + 1) + ")");
            }
            curr = next;
        }

        return shortestPath;
    }


    /**
     * Explores all possible movements from a node in the grid and updates the shortest path.
     * calculateNewPosition method is used to find the new position and the distance traveled in each direction.

     * This method iterates through all provided directions, calculates new positions using these directions,
     * then updates the pathfinding data structures if the new position offers a shorter path.
     *
     * @param map The Map object that contains the navigating grid.
     * @param row The current row index of the node from which movements are checked.
     * @param col The current column index of the node from which movements are checked.
     * @param distances A 2D array that records the shortest known distances from the start node to each node in the grid.
     * @param pq A priority queue that prioritizes nodes to explore the shortest distance from the start node.
     * @param prev A 3D array that keeps track of the immediate predecessor of each node.
     * @param directions An array of direction vectors.
     */
    private static void moveToNextNode(Map map, int row, int col, int[][] distances, PriorityQueue<int[]> pq, int[][][] prev, int[][] directions) {
        for (int[] dir : directions) {
            PositionData newPosition = calculateNewPosition(map, row, col, dir);
            updatePosition(newPosition, distances, pq, prev, row, col);
        }
    }


    /**
     * Calculates a new position on the grid by moving in the specified direction from a starting
     * position until an obstacle or the boundary of the map is encountered.

     * The method increments the distance traveled with each step and stops advancing when it reaches
     * the finish node, an obstacle, or the edge of the map.

     * It also backtracks one step to remain at the last valid position before hitting an obstacle or borderline.
     *
     * @param map The Map object containing the grid to navigate.
     * @param startRow The starting row index
     * @param startCol The starting column index
     * @param direction An array of two integers indicating the direction of movement.
     *
     * @return A PositionData object containing the new row and column indices after movement and the total distance
     *         traveled to reach that position.
     */
    private static PositionData calculateNewPosition(Map map, int startRow, int startCol, int[] direction) {
        int newRow = startRow + direction[0];
        int newCol = startCol + direction[1];
        int distanceTraveled = 0;

        // Continue sliding until an obstacle or the border is met
        while (isValidPosition(newRow, newCol, map) && map.getPuzzle()[newRow][newCol] != '0') {
            if (map.getPuzzle()[newRow][newCol] == 'F' || !isValidPosition(newRow + direction[0], newCol + direction[1], map) ||
                    map.getPuzzle()[newRow + direction[0]][newCol + direction[1]] == '0') {
                // Reached finish node or hit a border or obstacle
                newRow += direction[0];
                newCol += direction[1];
                distanceTraveled++;
                break;
            } else {
                newRow += direction[0];
                newCol += direction[1];
                distanceTraveled++;
            }
        }

        // Backtrack to the last valid position before hitting an obstacle
        newRow -= direction[0];
        newCol -= direction[1];

        return new PositionData(newRow, newCol, distanceTraveled);
    }


    /**
     * Updates the shortest known distances to a new position in the grid, adds this new position to the priority queue if
     * it provides a shorter path, and updates the predecessors array to record the path.

     * This method is part of the shortest path finding algorithm,
     * where each move is checked to see if it provides a more efficient route compared to the previously known distances.
     *
     * @param newPosition The new position data containing the row, column, and the distance traveled to reach this position.
     * @param distances A 2D array of integers representing the shortest known distances from the start position to each cell.
     * @param pq priority queue used in pathfinding algorithm to prioritize paths that have shorter distances.
     * @param prev A 3D array to track the row and column of the predecessor for each cell in the pathfinding process.
     * @param row The row index of the current position from which the new position is reached.
     * @param col The column index of the current position from which the new position is reached.
     */
    private static void updatePosition(PositionData newPosition, int[][] distances, PriorityQueue<int[]> pq, int[][][] prev, int row, int col) {
        int newDistance = distances[row][col] + newPosition.distanceTraveled;

        if (newDistance < distances[newPosition.newRow][newPosition.newCol]) {
            distances[newPosition.newRow][newPosition.newCol] = newDistance;
            pq.offer(new int[]{newPosition.newRow, newPosition.newCol, newDistance});
            prev[newPosition.newRow][newPosition.newCol] = new int[]{row, col};
        }
    }


    /**
     * Checks if a given row and column index are within the grid dimensions.
     * This method ensures that the position is not outside the boundaries of the grid
     *
     * @param row The row index to be checked.
     * @param col The column index to be checked.
     * @param map The Map object containing the grid.
     *
     * @return Returns true if both indices are valid, otherwise false.
     */
    private static boolean isValidPosition(int row, int col, Map map) {
        boolean isRowValid = (row >= 0) && (row < map.getPuzzle().length);
        boolean isColumnValid = (col >= 0) && (col < map.getPuzzle()[0].length);
        return isRowValid && isColumnValid;
    }


    /**
     * Determines the direction of movement from one cell to another.
     * The method compares two sets of coordinates and returns the direction either right, left, down, or up.
     *
     * @param from An array of two integers where the first element is the row index and the second is the column index of the starting cell.
     * @param to An array of two integers where the first element is the row index and the second is the column index of the destination cell.
     *
     * @return A string indication either right, left, down, or up.
     */
    private static String getMoveDirection(int[] from, int[] to) {
        if (from[0] == to[0]) {
            return (from[1] < to[1]) ? "right" : "left";
        } else {
            return (from[0] < to[0]) ? "down" : "up";
        }
    }
}
