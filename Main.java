import java.util.concurrent.ExecutionException;

public class Main {


    //Testing A star and Parallelism Algorithm
    public static void main(String[] args) throws InterruptedException, ExecutionException {
      
        int[] gridSizes = {10, 50, 100, 200, 500};
        int[] threadCounts = {2, 4, 8, 16};
        int numTrials = 5; 

   
        for (int gridSize : gridSizes) {
            System.out.println("\nGrid Size = " + gridSize);
            System.out.println("----------------------------------------");
            System.out.println("Sequential A*:");
            
        
            double avgSeqTime = 0;
            
            for (int trial = 0; trial < numTrials; trial++) {
                int[][] grid = generateRandomGrid(gridSize);
                int[] start = {0, 0};
                int[] end = {gridSize - 1, gridSize - 1};

                long startTime = System.nanoTime();
                AStarPathfinding aStar = new AStarPathfinding();
                aStar.findPath(grid, start, end);
                long executionTime = (System.nanoTime() - startTime) / 1_000_000; // ms
                avgSeqTime += executionTime;
            }
            avgSeqTime /= numTrials;
            System.out.printf("Execution Time: %d ms%n", Math.round(avgSeqTime));
            
            System.out.println("\nParallel A*:");
            System.out.println("Thread Count | Execution Time (ms)");
            System.out.println("----------------------------------------");

            
            for (int threadCount : threadCounts) {
                double avgParTime = 0;
                for (int trial = 0; trial < numTrials; trial++) {
                    int[][] grid = generateRandomGrid(gridSize);
                    int[] start = {0, 0};
                    int[] end = {gridSize - 1, gridSize - 1};

                    long startTime = System.nanoTime();
                    ParallelizedAStarPathfinding parallelAStar = 
                        new ParallelizedAStarPathfinding(threadCount);
                    parallelAStar.findPath(grid, start, end);
                    long executionTime = (System.nanoTime() - startTime) / 1_000_000; // ms
                    avgParTime += executionTime;
                }
                avgParTime /= numTrials;
                System.out.printf("%11d | %d%n", threadCount, Math.round(avgParTime));
            }
            System.out.println("========================================");
        }
    }

    private static int[][] generateRandomGrid(int size) {
        int[][] grid = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // 10% chance of obstacles
                grid[i][j] = Math.random() < 0.1 ? 1 : 0;
            }
        }
      
        grid[0][0] = 0;
        grid[size-1][size-1] = 0;
        return grid;
    }
}