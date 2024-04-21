import com.sun.javafx.scene.traversal.Direction;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Arrays;
import java.util.List;

public class DemoPanel extends JPanel {

    // Screen Settings
    int startCol;
    int startRow;
    int goalCol;
    int goalRow;
    int maxCol;
    int maxRow;
    final int nodeSize = 50;

    // Node
    Node[][] node;
    Node startNode, goalNode, currentNode;
    ArrayList<Node> openList = new ArrayList<>();
    ArrayList<String> lines;

    // Other
    boolean goalReached = false;
    int step = 0;
    Direction currentDirection;

    public DemoPanel() {
        getGridValues();
        node = new Node[maxCol][maxRow];

        final int screenWidth = nodeSize * maxCol;
        final int screenHeight = nodeSize * maxRow;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setLayout(new GridLayout(maxRow, maxCol));
        this.addKeyListener(new KeyHandler(this));
        this.setFocusable(true);

        // Place nodes
        int col = 0;
        int row = 0;

        while (col < maxCol && row < maxRow) {
            node[col][row] = new Node(col, row);
            this.add(node[col][row]);
            col++;

            if (col == maxCol) {
                col = 0;
                row++;
            }
        }

        // Set the start and goal nodes
        setStartNode(startCol, startRow);
        setGoalNode(goalCol, goalRow);

        // Place Solid Nodes
        placeSolidNodes(lines);

        // Set Cost
        setCostOnNodes();
    }

    private void getGridValues() {
        File folder = new File("example_algorithms");
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {

            // Selects a random file from the given folder
            File randomFile = files[new Random().nextInt(files.length)];

            // Just to read lines and store it as a string to print out the graph
            lines = readLinesFromFile(randomFile);

            // Printing out the graph in the String List
            printGraph(lines);

            // Get rows and columns of the graph
            findSpecificNodes(lines);

        } else {
            System.out.println("No files were found in the folder.");
        }
    }

    private static ArrayList<String> readLinesFromFile(File file) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void printGraph(ArrayList<String> lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private void findSpecificNodes(ArrayList<String> lines) {
        // Find number of rows and columns
        int numRows = lines.size();
        int numColumns = numRows > 0 ? lines.get(0).length() : 0;

        maxRow = numRows;
        maxCol = numColumns;

        System.out.println();
        System.out.println("Rows: " + numRows + " | Columns: " + numColumns);

        // Search for 'S' in the graph
        for (int row = 0; row < numRows; row++) {
            String line = lines.get(row);
            int col = line.indexOf('S');
            if (col != -1) {
                startCol = col;
                startRow = row;
                break;
            }
        }
        System.out.println("Found 'S' at Row: " + startRow + " Column: " + startCol);

        // Search for 'F' in the graph
        for (int row = 0; row < numRows; row++) {
            String line = lines.get(row);
            int col = line.indexOf('F');
            if (col != -1) {
                goalCol = col;
                goalRow = row;
                break;
            }
        }
        System.out.println("Found 'F' at Row: " + goalRow + " Column: " + goalCol);
    }

    private void placeSolidNodes(ArrayList<String> lines) {
        for (int row = 0; row < maxRow; row++) {
            String line = lines.get(row);
            for (int col = 0; col < maxCol; col++) {
                if (line.charAt(col) == '0') {
                    setSolidNode(col, row); // Set solid node at this position
                }
            }
        }
    }


    private void setStartNode(int col, int row) {
        node[col][row].setAsStart();
        startNode = node[col][row];
        currentNode = startNode;
    }

    private void setGoalNode(int col, int row) {
        node[col][row].setAsGoal();
        goalNode = node[col][row];
    }

    private void setSolidNode(int col, int row) {
        node[col][row].setAsSolid();
    }

    private void setCostOnNodes() {
        int col = 0;
        int row = 0;

        while (col < maxCol && row < maxRow) {
            getCost(node[col][row]);
            col++;

            if (col == maxCol) {
                col = 0;
                row++;
            }
        }
    }

    private void getCost(Node node) {
        // Get g cost (The distance from the start node)
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);
        node.gCost = xDistance + yDistance;

        // Get h cost (The distance from the start node)
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);
        node.hCost = xDistance + yDistance;

        // Get f Cost
        node.fCost = node.gCost + node.hCost;

        // Display the cost on Node
        if (node != startNode && node != goalNode) {
            node.setText("<html>F:" + node.fCost + "<br>G:" + node.gCost + "</html>");
        }
    }

    public void search() {
        System.out.println("Starting search...");
        if (currentDirection == null) currentDirection = Direction.RIGHT; // Initial direction is arbitrary

        Stack<Pair<Node, Direction>> decisionPoints = new Stack<>();
        decisionPoints.push(new Pair<>(currentNode, currentDirection));

        // while (!goalReached && step < 300) {
        if (!goalReached && step < 300) {

            System.out.println("Current node: " + currentNode.col + ", " + currentNode.row);
            currentNode.setAsChecked();
            if (!moveToNextNode()) {
                if (decisionPoints.isEmpty()) {
                    System.out.println("No path found!");
                    // break;
                } else {
                    boolean foundNewPath = false;
                    while (!foundNewPath && !decisionPoints.isEmpty()) {
                        Pair<Node, Direction> lastDecisionPoint = decisionPoints.pop();
                        currentNode = lastDecisionPoint.getKey();
                        currentDirection = lastDecisionPoint.getValue();

                        Direction newDirection = findAlternativeDirection(currentDirection);
                        if (newDirection != null) {
                            currentDirection = newDirection;
                            System.out.println("Backtracking to node: " + currentNode.col + ", " + currentNode.row + " - New direction: " + newDirection);
                            decisionPoints.push(new Pair<>(currentNode, currentDirection));
                            foundNewPath = true;
                        }
                    }
                    if (!foundNewPath) {
                        System.out.println("No path found!");
                        // break;
                    }
                }
            } else {
                decisionPoints.push(new Pair<>(currentNode, currentDirection));
            }
            step++;
        }
    }


    private boolean canMove(Direction direction) {
        int col = currentNode.col;
        int row = currentNode.row;
        return switch (direction) {
            case UP -> row > 0 && !node[col][row - 1].solid;
            case DOWN -> row < maxRow - 1 && !node[col][row + 1].solid;
            case LEFT -> col > 0 && !node[col - 1][row].solid;
            case RIGHT -> col < maxCol - 1 && !node[col + 1][row].solid;
            default -> false;
        };
    }

    private boolean moveToNextNode() {
        Node nextNode = getNextNode(currentDirection);
        if (nextNode != null && !nextNode.solid) {
            openNode(nextNode);
            currentNode = nextNode;
            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
                return true;
            }
            return true;
        }
        return false;
    }

    private Node getNextNode(Direction direction) {
        int col = currentNode.col;
        int row = currentNode.row;
        return switch (direction) {
            case UP -> row > 0 ? node[col][row - 1] : null;
            case DOWN -> row < maxRow - 1 ? node[col][row + 1] : null;
            case LEFT -> col > 0 ? node[col - 1][row] : null;
            case RIGHT -> col < maxCol - 1 ? node[col + 1][row] : null;
            default -> null;
        };
    }

    private Direction findAlternativeDirection(Direction lastTriedDirection) {
        List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT));
        directions.remove(lastTriedDirection); // Avoid the last tried direction
        directions.remove(oppositeDirection(lastTriedDirection)); // Avoid the opposite of the last tried direction as well

        for (Direction newDirection : directions) {
            if (canMove(newDirection)) {
                return newDirection;
            }
        }
        return null; // Return null if no valid direction found
    }


    private Direction oppositeDirection(Direction direction) {
        return switch (direction) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
            default -> null;
        };
    }

    private void openNode(Node node) {
        if (!node.open && !node.checked && !node.solid) {
            // If the node is not yet visited then add it to the open list.
            node.setAsOpen();
            node.parent = currentNode;
            openList.add(node);
        }
    }

    private void trackThePath() {
        // Back track and draw the best path
        Node current = goalNode;

        while (current != startNode) {
            current = current.parent;

            if (current != startNode) {
                current.setAsPath();
            }
        }
    }
}