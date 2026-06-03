package bse_engine;

import java.util.*;

public class BSEOrderMatchingEngine {

    /**
     * Executes Kahn's Algorithm to find a safe execution sequence for dependent orders.
     * @param numOrders Number of transaction vertex nodes (V)
     * @param dependencies Matrix representing directed edges from -> to
     * @param orderNames Map translating index integers to their string names
     */
    public static void computeExecutionOrder(int numOrders, int[][] dependencies, Map<Integer, String> orderNames) {
        // 1. Build the Adjacency List representation of the graph
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numOrders; i++) {
            adj.add(new ArrayList<>());
        }

        // 2. Array to store in-degrees of all transaction types
        int[] inDegree = new int[numOrders];

        // 3. Populate graph structures and increment in-degrees
        for (int[] edge : dependencies) {
            int from = edge[0];
            int to = edge[1];
            adj.get(from).add(to);
            inDegree[to]++;
        }

        // 4. Initialize FIFO Queue for tracking dependency-free nodes (in-degree == 0)
        Queue<Integer> queue = new LinkedList<>();
        System.out.println("--- Step 1: Evaluating Initial In-Degrees ---");
        for (int i = 0; i < numOrders; i++) {
            System.out.println(" Order '" + orderNames.get(i) + "' in-degree = " + inDegree[i]);
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        System.out.println("\nInitial Processing Queue (Ready Nodes): " + getQueueNames(queue, orderNames));

        // 5. Processing Loop
        List<String> finalExecutionOrder = new ArrayList<>();
        int step = 1;

        System.out.println("\n--- Step 2: Beginning Kahn's Algorithm Trace Loop ---");
        while (!queue.isEmpty()) {
            int current = queue.poll();
            String currentName = orderNames.get(current);
            finalExecutionOrder.add(currentName);

            System.out.println("\n[Iteration " + step++ + "]");
            System.out.println("  Processing active order: " + currentName);

            // Traverse and decrement neighbors
            List<Integer> neighbors = adj.get(current);
            if (!neighbors.isEmpty()) {
                System.out.print("  Decremented dependencies for: ");
                for (int neighbor : neighbors) {
                    inDegree[neighbor]--;
                    String neighborName = orderNames.get(neighbor);
                    System.out.print(neighborName + "(New In-Degree:" + inDegree[neighbor] + ") ");
                    
                    // If neighbor's in-degree hits 0, add to queue
                    if (inDegree[neighbor] == 0) {
                        queue.add(neighbor);
                    }
                }
                System.out.println();
            } else {
                System.out.println("  No outgoing dependencies to process.");
            }
            System.out.println("  Current Queue status: " + getQueueNames(queue, orderNames));
            System.out.println("  Order Sequence So Far: " + finalExecutionOrder);
        }

        // 6. Verify if the execution sequence is complete or if a deadlock/cycle was found
        System.out.println("\n=======================================================");
        if (finalExecutionOrder.size() == numOrders) {
            System.out.println("SUCCESS: Valid Topological Execution Order Found!");
            System.out.println("Final BSE Engine Safe Processing Sequence:");
            System.out.println("-> " + String.join(" -> ", finalExecutionOrder));
        } else {
            System.out.println("FATAL ERROR: Cyclical dependency deadlock detected in system config.");
        }
        System.out.println("=======================================================");
    }

    // Helper method to look up readable names within the active queue tracker
    private static List<String> getQueueNames(Queue<Integer> q, Map<Integer, String> names) {
        List<String> list = new ArrayList<>();
        for (int node : q) {
            list.add(names.get(node));
        }
        return list;
    }

    public static void main(String[] args) {
        // Enumerate nodes into integer tokens for matrix lookup arrays
        // m1=0, s1=1, b1=2, b2=3, b3=4, c1=5, c2=6
        int numOrders = 7;
        Map<Integer, String> orderNames = new HashMap<>();
        orderNames.put(0, "m1");
        orderNames.put(1, "s1");
        orderNames.put(2, "b1");
        orderNames.put(3, "b2");
        orderNames.put(4, "b3");
        orderNames.put(5, "c1");
        orderNames.put(6, "c2");

        // Input dependency edge connections matrix from case study mapping
        int[][] dependencies = {
            {0, 1}, // m1 -> s1
            {0, 2}, // m1 -> b1
            {0, 3}, // m1 -> b2
            {0, 4}, // m1 -> b3
            {2, 5}, // b1 -> c1
            {3, 5}, // b2 -> c1
            {4, 6}, // b3 -> c2
            {1, 6}  // s1 -> c2
        };

        System.out.println("=== BSE Order-Matching Dependency Engine ===");
        computeExecutionOrder(numOrders, dependencies, orderNames);
    }
}
