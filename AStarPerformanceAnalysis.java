import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AStarPerformanceAnalysis {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Experiment Configuration
        int[][] gridSizes = {
            {5, 5},    // Small grid
            {10, 10},  // Medium grid
            {20, 20},  // Large grid
            {50, 50}   // Very large grid
        };

        int[] threadCounts = {1, 2, 4, 8}; // Different thread configurations
        int iterations = 50; // Number of times to run each algorithm

        // Run sequential A* experiments
        runSequentialExperiments(gridSizes, iterations);

        // Run parallel A* experiments
        runParallelExperiments(gridSizes, threadCounts, iterations);
    }

    private static void runSequentialExperiments(int[][] gridSizes, int iterations) {
        try (FileWriter csvWriter = new FileWriter("sequential_astar_results.csv")) {
            // CSV Header
            csvWriter.append("GridSize,Iteration,ExecutionTime,PathLength,PathCost\n");

            for (int[] gridSize : gridSizes) {
                for (int i = 0; i < iterations; i++) {
                    // Create grid based on size
                    int[][] grid = generateGrid(gridSize[0], gridSize[1]);
                    int[] start = {0, 0};
                    int[] end = {gridSize[0] - 1, gridSize[1] - 1};

                    // Sequential A* Algorithm
                    SequentialAStarPathfinding sequentialAStar = new SequentialAStarPathfinding();
                    
                    long startTime = System.nanoTime();
                    PathResult result = sequentialAStar.findPath(grid, start, end);
                    long endTime = System.nanoTime();

                    long executionTime = endTime - startTime;

                    // Write results to CSV
                    csvWriter.append(String.format("%dx%d,%d,%d,%d,%.2f\n", 
                        gridSize[0], gridSize[1],
                        i, 
                        executionTime, 
                        result.getPath().size(),
                        result.getPathCost()
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runParallelExperiments(int[][] gridSizes, int[] threadCounts, int iterations) {
        try (FileWriter csvWriter = new FileWriter("parallel_astar_results.csv")) {
            // CSV Header
            csvWriter.append("GridSize,ThreadCount,Iteration,ExecutionTime,PathLength,PathCost\n");

            for (int[] gridSize : gridSizes) {
                for (int threadCount : threadCounts) {
                    for (int i = 0; i < iterations; i++) {
                        // Create grid based on size
                        int[][] grid = generateGrid(gridSize[0], gridSize[1]);
                        int[] start = {0, 0};
                        int[] end = {gridSize[0] - 1, gridSize[1] - 1};

                        // Parallel A* Algorithm
                        ParallelAStarPathfinding parallelAStar = new ParallelAStarPathfinding(threadCount);
                        
                        long startTime = System.nanoTime();
                        PathResult result = parallelAStar.findPath(grid, start, end);
                        long endTime = System.nanoTime();

                        long executionTime = endTime - startTime;

                        // Write results to CSV
                        csvWriter.append(String.format("%dx%d,%d,%d,%d,%.2f\n", 
                            gridSize[0], gridSize[1],
                            threadCount,
                            i, 
                            executionTime, 
                            result.getPath().size(),
                            result.getPathCost()
                        ));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Utility method to generate a grid with random obstacles
    private static int[][] generateGrid(int rows, int cols) {
        int[][] grid = new int[rows][cols];
        
        // Randomly place obstacles (1 represents an obstacle)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // 20% chance of an obstacle
                grid[i][j] = Math.random() < 0.2 ? 1 : 0;
            }
        }
        
        // Ensure start and end are clear
        grid[0][0] = 0;
        grid[rows-1][cols-1] = 0;
        
        return grid;
    }
}