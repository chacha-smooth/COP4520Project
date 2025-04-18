
package grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Grid {
    private int[][] array;

    
    public Grid(int n) {
        array = generateRandomGrid(n);
    }

    public int[][] getArray() {
        return array;
    }

    public static int[][] generateRandomGrid(int n) {
        int[][] grid = new int[n][n];

    
        for (int i = 0; i < n; i++) {
            Arrays.fill(grid[i], 1);
        }

        // Create random path from (0, 0) to (n-1, n-1)
        int x = 0, y = 0;
        grid[x][y] = 0;

        List<Character> moves = new ArrayList<>();
        for (int i = 0; i < n - 1; i++) {
            moves.add('D');
            moves.add('R');
        }

        Collections.shuffle(moves);

        for (char move : moves) {
            if (move == 'D') {
                x++;
            } else {
                y++;
            }
            grid[x][y] = 0;
        }

        //50 percent of the grid should be 0's
        int totalCells = n * n;
        int targetZeros = totalCells / 2;

    
        int currentZeros = 0;
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == 0) currentZeros++;
            }
        }

        int remaining = targetZeros - currentZeros;

       
        List<int[]> coords = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                coords.add(new int[]{i, j});
            }
        }
        Collections.shuffle(coords);

        for (int[] coord : coords) {
            if (remaining <= 0) break;
            int i = coord[0], j = coord[1];
            if (grid[i][j] == 1) {
                grid[i][j] = 0;
                remaining--;
            }
        }

        return grid;
    }
}
