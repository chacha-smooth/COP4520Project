package algorithms.parallel;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParallelizedAStarPathfinding {

    private static class Node implements Comparable<Node> {
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

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.fCost, other.fCost);
        }
    }

    private final ExecutorService executor;
    private final int threadCount;

    public ParallelizedAStarPathfinding(int threadCount) {
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    public void findPath(int[][] grid, int[] start, int[] end) {
        int rows = grid.length;
        int cols = grid[0].length;

        PriorityBlockingQueue<Node> openList = new PriorityBlockingQueue<>();
        boolean[][] closedList = new boolean[rows][cols];
        AtomicBoolean pathFound = new AtomicBoolean(false);

        Node startNode = new Node(start[0], start[1], 0, calculateHeuristic(start, end), null);
        openList.add(startNode);


        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    while (!pathFound.get() && !openList.isEmpty()) {
                        Node current = openList.poll(50, TimeUnit.MILLISECONDS);
                        if (current == null) continue;

                        if (closedList[current.row][current.col]) continue;
                        closedList[current.row][current.col] = true;

                        if (current.row == end[0] && current.col == end[1]) {
                            pathFound.set(true);
                            break;
                        }

                        for (int[] dir : new int[][]{{0,1}, {1,0}, {0,-1}, {-1,0}}) {
                            int newRow = current.row + dir[0];
                            int newCol = current.col + dir[1];

                            if (isValid(newRow, newCol, grid, closedList)) {
                                int gCost = current.gCost + 1;
                                int hCost = calculateHeuristic(new int[]{newRow, newCol}, end);
                                Node neighbor = new Node(newRow, newCol, gCost, hCost, current);
                                openList.add(neighbor);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } 
            });
        }


        executor.shutdown();
    }

    private boolean isValid(int row, int col, int[][] grid, boolean[][] closedList) {
        return row >= 0 && row < grid.length &&
               col >= 0 && col < grid[0].length &&
               grid[row][col] == 0 && !closedList[row][col];
    }

    private int calculateHeuristic(int[] point, int[] end) {
        return Math.abs(point[0] - end[0]) + Math.abs(point[1] - end[1]);
    }
}