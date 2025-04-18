import grid.Grid;
import algorithms.AStarPathfinding;
import algorithms.parallel.ParallelizedBidirectional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        int[] gridSizes = new int[11]; // 1 to 2500 with interval of 250
        for (int i = 0; i < gridSizes.length; i++) {
            gridSizes[i] = i * 250;
        }
        gridSizes[0] = 100;

        System.out.println("Example of 10x10 grid:");

        int[][] gridWithPath = {
                { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 1, 0, 1, 0, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 1, 0, 1, 0 },
                { 0, 0, 0, 1, 1, 0, 1, 0, 0, 0 },
                { 1, 1, 0, 0, 1, 0, 1, 1, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 1, 0, 1, 1, 1, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 1, 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
        };

        int[][] gridWithoutPath = {
                { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 1, 0, 1, 0, 1, 0 },
                { 0, 1, 0, 0, 0, 0, 1, 0, 1, 0 },
                { 0, 0, 0, 1, 1, 0, 1, 0, 0, 0 },
                { 1, 1, 0, 0, 1, 0, 1, 1, 1, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 1, 0, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }
        };
        AStarPathfinding aStarExample = new AStarPathfinding();
        ParallelizedBidirectional parallelAStarExample = new ParallelizedBidirectional();

        for (int[] row : gridWithPath) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        // Print out the contents of the path
        System.out.println("A* Path:");
        List<int[]> path = aStarExample.findPath(gridWithPath);
        if (path != null) {
            for (int[] point : path) {
                System.out.print("(" + point[0] + "," + point[1] + ") ");
            }
        }
        System.out.println();

        System.out.println("\nParallel A* Path:");
        List<int[]> parallelPath = parallelAStarExample.findPath(gridWithPath);
        if (parallelPath != null) {
            for (int[] point : parallelPath) {
                System.out.print("(" + point[0] + "," + point[1] + ") ");
            }
        }

        System.out.println("\n-----------------------------------------");
        for (int[] row : gridWithoutPath) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        aStarExample.findPath(gridWithoutPath);

        parallelAStarExample.findPath(gridWithoutPath);

        int numTrials = 100;

        for (int gridId = 0; gridId < gridSizes.length; gridId++) {
            int gridSize = gridSizes[gridId];

            System.out.println("\nGrid Size = " + gridSize);
            System.out.println("----------------------------------------");
            System.out.println("Sequential A*:");

            Grid gridObject = new Grid(gridSize);
            int[][] grid = gridObject.getArray();

            double avgSeqTime = 0;

            for (int trial = 0; trial < numTrials; trial++) {

                AStarPathfinding aStar = new AStarPathfinding();
                long startTime = System.nanoTime();
                aStar.findPath(grid);
                double executionTime = (System.nanoTime() - startTime) / 1_000_000; // ms

                avgSeqTime += executionTime;
            }
            avgSeqTime /= numTrials;
            System.out.printf("Execution Time: %f ms%n", avgSeqTime);

            System.out.println("\nParallel A*:");
            System.out.println("Thread Count | Execution Time (ms)");
            System.out.println("----------------------------------------");

            double avgParTime = 0;
            for (int trial = 0; trial < numTrials; trial++) {

                ParallelizedBidirectional parallelAStar = new ParallelizedBidirectional();

                long startTime = System.nanoTime();

                parallelAStar.findPath(grid);
                double executionTime = (System.nanoTime() - startTime) / 1_000_000; // ms
                avgParTime += executionTime;
            }
            avgParTime /= numTrials;
            System.out.printf("Execution Time %f%n", avgParTime);
            System.out.println("========================================");
        }
    }

}
