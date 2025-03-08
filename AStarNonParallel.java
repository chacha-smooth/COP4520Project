import java.util.*;

public class AStarNonParallel {

    public interface HeuristicFunction {
        double estimate(Node from, Node to);
    }

    public static List<Node> aStar(Node start, Node goal, HeuristicFunction heuristic) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedList = new HashSet<>();

        start.g = 0;
        start.h = heuristic.estimate(start, goal);
        start.f = start.g + start.h;
        start.parent = null;

        openList.add(start);

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            if (current.equals(goal)) {
                return reconstructPath(current);
            }

            closedList.add(current);

            for (Node neighbor : current.getNeighbors()) {
                if (closedList.contains(neighbor)) {
                    continue;
                }

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
            }
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