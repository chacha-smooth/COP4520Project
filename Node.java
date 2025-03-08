    import java.util.List;
    import java.util.ArrayList;

    public class Node implements Comparable<Node> {
        Object data;           // The actual data this node represents (could be a position, city, etc.)
        double g;              // Cost from start to this node
        double h;              // Estimated cost from this node to goal
        double f;              // Total cost (g + h)
        Node parent;           // Parent node (for path reconstruction)
        List<Edge> neighbors;  // Connections to other nodes
        
        public Node(Object data) {
            this.data = data;
            this.g = Double.POSITIVE_INFINITY;
            this.h = 0;
            this.f = Double.POSITIVE_INFINITY;
            this.parent = null;
            this.neighbors = new ArrayList<>();
        }
        
        public void addNeighbor(Node neighbor, double distance) {
            neighbors.add(new Edge(neighbor, distance));
        }
        
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }
        
        @Override
        public String toString() {
            return data.toString();
        }
    }
    