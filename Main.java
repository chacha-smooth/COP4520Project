import java.util.List;

public class Main {
        public static void main(String[] args) {
        // Create some nodes
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        
        // Connect the nodes
        nodeA.addNeighbor(nodeB, 1.0);
        nodeA.addNeighbor(nodeC, 3.0);
        nodeB.addNeighbor(nodeD, 5.0);
        nodeB.addNeighbor(nodeE, 2.0);
        nodeC.addNeighbor(nodeE, 4.0);
        
        // Define a simple heuristic (for this example, just returns 0)
        HeuristicFunction heuristic = (from, to) -> 0;

    
        
        // Execute parallel A* algorithm with 4 threads
        List<Node> path = parallelAStar(nodeA, nodeE, heuristic, 4);
        
        // Print the path
        if (path.isEmpty()) {
            System.out.println("No path found!");
        } else {
            System.out.println("Path found: " + path);
        }
    }
}
