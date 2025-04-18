import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import algorithms.AStarPathfinding;
import algorithms.parallel.ParallelizedBidirectional;
import grid.Grid;



public class PerformanceAnalysis {

    public static void main(String[] args) {
        try {
            // Create directory if it doesn't exist
            java.io.File directory = new java.io.File("../analysis/csv");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Setup CSV file
            FileWriter csvWriter = new FileWriter("../analysis/csv/performance_data.csv");
            csvWriter.append("GridSize,ThreadCount,Algorithm,ExecutionTime_ms\n");
            
            // Parameters
            int[] gridSizes = new int[41]; // 0 to 10000 with interval of 250 needs 41 values
            for (int i = 1; i < gridSizes.length; i++) {
                gridSizes[i] = i * 250;
            }

            gridSizes[0] = 100;

            int numTrials = 1000; // Number of trials for each grid size
            
            for (int gridSize : gridSizes) {
                System.out.println("\nAnalyzing Grid Size = " + gridSize);
                
                // Generate a consistent grid for all algorithms
                Grid grid = new Grid(gridSize);
                int[][] gridArray = grid.getArray();
                
                // Sequential A* (single thread)
                double avgSeqTime = runSequentialTrials(gridArray, gridSize, numTrials);
                
                // Record sequential data (as 1 thread)
                csvWriter.append(String.format("%d,%d,%s,%.2f\n", 
                    gridSize, 1, "Sequential", avgSeqTime));
                
                System.out.println("Sequential A* Average Time: " + avgSeqTime + " ms");
                
                // Parallel A* with different thread counts
              
                    
                    
                    double avgParTime = runParallelTrials(gridArray, gridSize, 2, numTrials);
                    
                    // Record parallel data
                    csvWriter.append(String.format("%d,%d,%s,%.5f\n", 
                        gridSize, 2, "Parallel", avgParTime));
                    
                    System.out.println("Parallel A* with " + 2 + " threads: " + avgParTime + " ms");
                
            }
            
            csvWriter.flush();
            csvWriter.close();
            System.out.println("\nPerformance data saved to ../analysis/astar_performance_data.csv");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static double runSequentialTrials(int[][] grid, int gridSize, int numTrials) {
        double totalTime = 0;
        
        for (int trial = 0; trial < numTrials; trial++) {       
            
            AStarPathfinding aStar = new AStarPathfinding();
            long startTime = System.nanoTime();
            aStar.findPath(grid);
            double executionTime = (System.nanoTime() - startTime) / 1_000_000; // ms
            
            totalTime += executionTime;
        }
        
        return totalTime / numTrials;
    }
    
    private static double runParallelTrials(int[][] grid, int gridSize, int threadCount, int numTrials) {
        double totalTime = 0;
        
        for (int trial = 0; trial < numTrials; trial++) {

            ParallelizedBidirectional parallelAStar = new ParallelizedBidirectional();
  
            long startTime = System.nanoTime();
            


            parallelAStar.findPath(grid);
            double executionTime = (System.nanoTime() - startTime) / 1_000_000; // ms
            
            totalTime += executionTime;
        }
        
        return totalTime / numTrials;
    }
}