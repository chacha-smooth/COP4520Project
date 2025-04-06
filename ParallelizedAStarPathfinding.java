import java.util.*;
import java.util.concurrent.*;

public class ParallelizedAStarPathfinding {

    private final int numThreads;

    public ParallelizedAStarPathfinding(int numThreads) {
        this.numThreads = numThreads;
    }

    private static class Node {
        int row, col, gCost, hCost, fCost;
        Node parent;

        public Node(int row, int col, int gCost, int hCost, Node parent) {
            this.row = row;
            this.col = col;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
            this.parent = parent;
        }
    }

    public void findPath(int[][] grid, int[] start, int[] end) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

       
        PriorityBlockingQueue<Node> openList = new PriorityBlockingQueue<>(11, Comparator.comparingInt(n -> n.fCost));
     
        boolean[][] closedList = new boolean[grid.length][grid[0].length];

        // Add the start node to the open list
        Node startNode = new Node(start[0], start[1], 0, calculateHeuristic(start, end), null);
        openList.add(startNode);

       
        List<Callable<Node>> tasks = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            tasks.add(() -> {
                while (!openList.isEmpty()) {
                    Node current;
                    synchronized (openList) {
                        current = openList.poll();
                        if (current == null)
                            break;
                    }

                    // If the end node is reached, return the node
                    if (current.row == end[0] && current.col == end[1]) {
                        return current;
                    }

                    synchronized (closedList) {
                        closedList[current.row][current.col] = true;
                    }

                    // Explore neighbors
                    for (int[] direction : new int[][] { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } }) {
                        int newRow = current.row + direction[0];
                        int newCol = current.col + direction[1];

                        if (isValid(newRow, newCol, grid, closedList)) {
                            int gCost = current.gCost + 1;
                            int hCost = calculateHeuristic(new int[] { newRow, newCol }, end);
                            Node neighbor = new Node(newRow, newCol, gCost, hCost, current);

                            synchronized (openList) {
                                openList.add(neighbor);
                            }
                        }
                    }
                }
                return null; // No path found by this thread
            });
        }

        // Execute tasks
        Node result = null;
        for (Future<Node> future : executor.invokeAll(tasks)) {
            Node pathNode = future.get(); 
            if (pathNode != null) {
                result = pathNode;
                break;
            }
        }

        executor.shutdown();

    }

    private boolean isValid(int row, int col, int[][] grid, boolean[][] closedList) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length &&
                grid[row][col] == 0 && !closedList[row][col];
    }


    //uses Manhattan distance
    private int calculateHeuristic(int[] point, int[] end) {
        return Math.abs(point[0] - end[0]) + Math.abs(point[1] - end[1]); 
    }

}