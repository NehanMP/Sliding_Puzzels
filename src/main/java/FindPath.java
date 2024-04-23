import java.util.*;

public class FindPath {
    public static List<String> findShortestPath(Map map) {
        int rows = map.getGrid().length;
        int cols = map.getGrid()[0].length;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        int[][] distances = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
        }
        distances[map.getStartRow()][map.getStartCol()] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.offer(new int[]{map.getStartRow(), map.getStartCol(), 0});

        int[][][] prev = new int[rows][cols][2];
        prev[map.getStartRow()][map.getStartCol()] = new int[]{-1, -1};

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int row = curr[0];
            int col = curr[1];

            if (row == map.getGoalRow() && col == map.getGoalCol()) {
                break;
            }
            slideToNextNode(map, row, col, distances, pq, prev, directions);
        }

        List<String> shortestPath = new ArrayList<>();
        int[] curr = new int[]{map.getGoalRow(), map.getGoalCol()};
        // Start with the "Done!" step as it's the last one
        shortestPath.add("Done!");

        // We need to add the steps in reverse since we are going from the goal back to the start
        while (curr[0] != -1 && curr[1] != -1) {
            int[] next = prev[curr[0]][curr[1]];
            if (next[0] == -1 && next[1] == -1) {
                // The next step is the start position, add it at the beginning of the list
                shortestPath.add(0, "Start at (" + (curr[1] + 1) + "," + (curr[0] + 1) + ")");
            } else {
                // Add the direction-specific movement to the beginning of the list
                String move = getMoveDirection(next, curr);
                shortestPath.add(0, "Move " + move + " to (" + (curr[1] + 1) + "," + (curr[0] + 1) + ")");
            }
            // Move to the previous step
            curr = next;
        }

        return shortestPath;
    }

    private static void slideToNextNode(Map map, int row, int col, int[][] distances, PriorityQueue<int[]> pq, int[][][] prev, int[][] directions) {
        for (int[] dir : directions) {
            int newRow = row + dir[0], newCol = col + dir[1];
            int distanceTraveled = 0;

            // Keep sliding until a boulder is hit or at the end of the border of the map
            while (isValidPosition(newRow, newCol, map) && map.getGrid()[newRow][newCol] != '0') {
                if (map.getGrid()[newRow][newCol] == 'F' || !isValidPosition(newRow + dir[0], newCol + dir[1], map) ||
                        map.getGrid()[newRow + dir[0]][newCol + dir[1]] == '0') {
                    // Reached finish node or hit a border/boulder
                    newRow += dir[0];
                    newCol += dir[1];
                    distanceTraveled++;
                    break;
                } else {
                    newRow += dir[0];
                    newCol += dir[1];
                    distanceTraveled++;
                }
            }

            // Adding the last valid position just before hitting a boulder
            newRow -= dir[0];
            newCol -= dir[1];
            int newDistance = distances[row][col] + distanceTraveled;

            if (newDistance < distances[newRow][newCol]) {
                distances[newRow][newCol] = newDistance;
                pq.offer(new int[]{newRow, newCol, newDistance});
                prev[newRow][newCol] = new int[]{row, col};
            }
        }
    }

    private static boolean isValidPosition(int row, int col, Map map) {
        boolean isRowValid = (row >= 0) && (row < map.getGrid().length);
        boolean isColumnValid = (col >= 0) && (col < map.getGrid()[0].length);
        return isRowValid && isColumnValid;
    }


    private static String getMoveDirection(int[] from, int[] to) {
        if (from[0] == to[0]) {
            return (from[1] < to[1]) ? "right" : "left";
        } else {
            return (from[0] < to[0]) ? "down" : "up";
        }
    }
}
