import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class AStarParallel {

    public interface HeuristicFunction {
        double estimate(Node from, Node to);
    }

    public static List<Node> parallelAStar(Node start, Node goal, HeuristicFunction heuristic, int numThreads) {
        ConcurrentSkipListSet<Node> openList = new ConcurrentSkipListSet<>();
        ConcurrentHashMap<Node, Boolean> closedList = new ConcurrentHashMap<>();

        start.g = 0;
        start.h = heuristic.estimate(start, goal);
        start.f = start.g + start.h;
        start.parent = null;

        openList.add(start);

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();
        AtomicReference<Node> foundGoal = new AtomicReference<>(null);

        while (!openList.isEmpty() && foundGoal.get() == null) {
            Node current = openList.pollFirst();
            if (current == null) continue;

            closedList.put(current, true);

            for (Node neighbor : current.getNeighbors()) {
                if (closedList.containsKey(neighbor)) continue;

                double tentativeG = current.g + current.distanceTo(neighbor);

                if (!openList.contains(neighbor) || tentativeG < neighbor.g) {
                    neighbor.g = tentativeG;
                    neighbor.h = heuristic.estimate(neighbor, goal);
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }

                if (neighbor.equals(goal)) {
                    foundGoal.set(neighbor);
                    break;
                }
            }

            Future<?> future = executorService.submit(() -> {
                for (Node neighbor : current.getNeighbors()) {
                    if (closedList.containsKey(neighbor)) continue;

                    double tentativeG = current.g + current.distanceTo(neighbor);

                    if (!openList.contains(neighbor) || tentativeG < neighbor.g) {
                        neighbor.g = tentativeG;
                        neighbor.h = heuristic.estimate(neighbor, goal);
                        neighbor.f = neighbor.g + neighbor.h;
                        neighbor.parent = current;

                        if (!openList.contains(neighbor)) {
                            openList.add(neighbor);
                        }
                    }

                    if (neighbor.equals(goal)) {
                        foundGoal.set(neighbor);
                        break;
                    }
                }
            });

            futures.add(future);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();

        Node goalNode = foundGoal.get();
        if (goalNode != null) {
            return reconstructPath(goalNode);
        }

        return new ArrayList<>();
    }

    private static List<Node> reconstructPath(Node current) {
        List<Node> path = new ArrayList<>();

        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }

        return path;
    }
}