package algorithms.parallel;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ParallelizedBidirectional {

    static class SearchState {
        int x, y;
        double gScore;
        double fScore;
        SearchState parent;

        public SearchState(int x, int y, double gScore, double fScore, SearchState parent) {
            this.x = x;
            this.y = y;
            this.gScore = gScore;
            this.fScore = fScore;
            this.parent = parent;
        }
    }

    private static double heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public static List<int[]> findPath(int[][] gridMap) {
        int n = gridMap.length;
        int[] start = {0, 0};
        int[] goal = {n - 1, n - 1};

        Comparator<SearchState> comparator = Comparator.comparingDouble(s -> s.fScore);
        PriorityBlockingQueue<SearchState> forwardQueue = new PriorityBlockingQueue<>(11, comparator);
        PriorityBlockingQueue<SearchState> backwardQueue = new PriorityBlockingQueue<>(11, comparator);

        ConcurrentMap<String, SearchState> forwardVisited = new ConcurrentHashMap<>();
        ConcurrentMap<String, SearchState> backwardVisited = new ConcurrentHashMap<>();

        AtomicReference<int[]> meetingPoint = new AtomicReference<>(null);
        AtomicReference<Double> bestPathCost = new AtomicReference<>(Double.POSITIVE_INFINITY);
        AtomicInteger nodesExplored = new AtomicInteger(0);

        SearchState startState = new SearchState(start[0], start[1], 0, heuristic(start[0], start[1], goal[0], goal[1]), null);
        SearchState goalState = new SearchState(goal[0], goal[1], 0, heuristic(goal[0], goal[1], start[0], start[1]), null);

        forwardQueue.add(startState);
        backwardQueue.add(goalState);
        forwardVisited.put(start[0] + "," + start[1], startState);
        backwardVisited.put(goal[0] + "," + goal[1], goalState);

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        Runnable search = (Runnable) () -> {
            PriorityBlockingQueue<SearchState> queue;
            ConcurrentMap<String, SearchState> visited;
            ConcurrentMap<String, SearchState> oppositeVisited;
            boolean isForward;

            if (Thread.currentThread().getName().equals("forward")) {
                queue = forwardQueue;
                visited = forwardVisited;
                oppositeVisited = backwardVisited;
                isForward = true;
            } else {
                queue = backwardQueue;
                visited = backwardVisited;
                oppositeVisited = forwardVisited;
                isForward = false;
            }

            while (!queue.isEmpty()) {
                SearchState current = queue.poll();
                if (current == null) continue;
                nodesExplored.incrementAndGet();

                if (bestPathCost.get() <= current.fScore) break;

                for (int i = 0; i < 4; i++) {
                    int nx = current.x + dx[i];
                    int ny = current.y + dy[i];

                    if (nx < 0 || ny < 0 || nx >= n || ny >= n || gridMap[nx][ny] == 1) continue;

                    double tentativeG = current.gScore + 1;
                    String key = nx + "," + ny;

                    SearchState prev = visited.get(key);
                    if (prev == null || tentativeG < prev.gScore) {
                        SearchState nextState = new SearchState(nx, ny, tentativeG, tentativeG + heuristic(nx, ny, goal[0], goal[1]), current);
                        visited.put(key, nextState);
                        queue.add(nextState);

                        if (oppositeVisited.containsKey(key)) {
                            double pathCost = tentativeG + oppositeVisited.get(key).gScore;
                            if (pathCost < bestPathCost.get()) {
                                bestPathCost.set(pathCost);
                                meetingPoint.set(new int[]{nx, ny});
                            }
                        }
                    }
                }
            }
        };

        Thread forwardThread = new Thread(search);
        forwardThread.setName("forward");
        Thread backwardThread = new Thread(search);
        backwardThread.setName("backward");

        forwardThread.start();
        backwardThread.start();

        try {
            forwardThread.join();
            backwardThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int[] meet = meetingPoint.get();
        if (meet != null) {
            return reconstructPath(forwardVisited.get(meet[0] + "," + meet[1]), backwardVisited.get(meet[0] + "," + meet[1]));
        }
        System.err.println("No path found.");
        return Collections.emptyList();
    }

    private static List<int[]> reconstructPath(SearchState forwardState, SearchState backwardState) {
        List<int[]> path = new ArrayList<>();
        SearchState current = forwardState;
        while (current != null) {
            path.add(new int[]{current.x, current.y});
            current = current.parent;
        }
        Collections.reverse(path);

        current = backwardState.parent;
        while (current != null) {
            path.add(new int[]{current.x, current.y});
            current = current.parent;
        }
    // Print the path
      
        return path;
    }
}
