import java.util.*;

public class AStarPathfinding {

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

    public void findPath(int[][] grid, int[] start, int[] end) {
        int rows = grid.length;
        int cols = grid[0].length;
    
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        boolean[][] closedList = new boolean[rows][cols];
    
        Node startNode = new Node(start[0], start[1], 0, calculateHeuristic(start, end), null);
        openList.add(startNode);
    
        while (!openList.isEmpty()) {
            Node current = openList.poll();
            
            // Path is found.
            if (current.row == end[0] && current.col == end[1]) {
                return; 
            }
    
            closedList[current.row][current.col] = true;
    
            for (int[] direction : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int newRow = current.row + direction[0];
                int newCol = current.col + direction[1];
    
                if (isValid(newRow, newCol, grid, closedList)) {
                    int gCost = current.gCost + 1;
                    int hCost = calculateHeuristic(new int[]{newRow, newCol}, end);
                    Node neighbor = new Node(newRow, newCol, gCost, hCost, current);
    
                    openList.add(neighbor);
                }
            }
        }
        
        System.err.println("No path found from " + Arrays.toString(start) + " to " + Arrays.toString(end));
    }

    private boolean isValid(int row, int col, int[][] grid, boolean[][] closedList) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length &&
               grid[row][col] == 0 && !closedList[row][col];
    }

    // uses Manhattan distance
    private int calculateHeuristic(int[] point, int[] end) {
        return Math.abs(point[0] - end[0]) + Math.abs(point[1] - end[1]); 
    }

    private void printPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(new int[]{node.row, node.col});
            node = node.parent;
        }
        Collections.reverse(path);

        System.out.println("Path found:");
        for (int[] point : path) {
            System.out.println(Arrays.toString(point));
        }
    }
}