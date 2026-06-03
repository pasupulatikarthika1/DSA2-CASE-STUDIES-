package dabbawalla_routing;

import java.util.*;

public class DabbawallaRoutingEngine {

    // Safe boundary representation of infinity that prevents integer overflow during additions
    private static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Runs the Floyd-Warshall algorithm to compute all-pairs shortest paths.
     * @param n Number of station hubs
     * @param edges Adjacency matrix of direct edge travel weights
     * @return Completed shortest path distance matrix
     */
    public static int[][] floydWarshall(int n, int[][] edges) {
        int[][] dp = new int[n][n];
        
        // Initialize the DP lookup table
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dp[i][j] = 0;
                } else if (edges[i][j] != 0) {
                    dp[i][j] = edges[i][j];
                } else {
                    dp[i][j] = INF;
                }
            }
        }

        // Main structural loop tracking intermediate nodes (k)
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Core relaxation formula with built-in overflow protection
                    if (dp[i][k] != INF && dp[k][j] != INF) {
                        if (dp[i][k] + dp[k][j] < dp[i][j]) {
                            dp[i][j] = dp[i][k] + dp[k][j];
                        }
                    }
                }
            }
        }
        return dp;
    }

    /**
     * Utility method to run Floyd-Warshall while maintaining a 'next' matrix for path printing.
     */
    public static int[][] floydWarshallWithTracking(int n, int[][] edges, int[][] next) {
        int[][] dp = new int[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dp[i][j] = 0;
                    next[i][j] = i;
                } else if (edges[i][j] != 0) {
                    dp[i][j] = edges[i][j];
                    next[i][j] = j;
                } else {
                    dp[i][j] = INF;
                    next[i][j] = -1;
                }
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dp[i][k] != INF && dp[k][j] != INF) {
                        if (dp[i][k] + dp[k][j] < dp[i][j]) {
                            dp[i][j] = dp[i][k] + dp[k][j];
                            next[i][j] = next[i][k];
                        }
                    }
                }
            }
        }
        return dp;
    }

    /**
     * Reconstructs the exact path from source hub i to destination j using the tracking matrix.
     */
    public static List<Integer> reconstructPath(int[][] next, int i, int j) {
        List<Integer> path = new ArrayList<>();
        if (next[i][j] == -1) {
            return path; // No path exists
        }
        
        int u = i;
        while (u != j) {
            path.add(u);
            u = next[u][j];
        }
        path.add(j);
        return path;
    }

    public static void main(String[] args) {
        int n = 6;
        String[] names = {"CHC", "DDR", "KRL", "GTR", "VKR", "TNE"};
        
        // Input undirected graph matrix setup
        int[][] graph = new int[n][n];
        graph[0][1] = 10; graph[1][0] = 10; // CHC - DDR
        graph[0][2] = 15; graph[2][0] = 15; // CHC - KRL
        graph[1][2] = 8;  graph[2][1] = 8;  // DDR - KRL
        graph[1][3] = 18; graph[3][1] = 18; // DDR - GTR
        graph[2][3] = 12; graph[3][2] = 12; // KRL - GTR
        graph[2][4] = 5;  graph[4][2] = 5;  // KRL - VKR
        graph[3][4] = 7;  graph[4][3] = 7;  // GTR - VKR
        graph[3][5] = 9;  graph[5][3] = 9;  // GTR - TNE
        graph[4][5] = 6;  graph[5][4] = 6;  // VKR - TNE

        int[][] next = new int[n][n];
        int[][] shortestDistances = floydWarshallWithTracking(n, graph, next);

        System.out.println("=== MUMBAI DABBAWALLA LOGISTICS ENGINE ===");
        System.out.println("Optimized All-Pairs Shortest Travel Time Matrix:\n");
        
        System.out.print("     ");
        for (String name : names) {
            System.out.printf("%-5s", name);
        }
        System.out.println();
        
        for (int i = 0; i < n; i++) {
            System.out.printf("%-5s", names[i]);
            for (int j = 0; j < n; j++) {
                if (shortestDistances[i][j] == INF) {
                    System.out.printf("%-5s", "INF");
                } else {
                    System.out.printf("%-5d", shortestDistances[i][j]);
                }
            }
            System.out.println();
        }

        // Demo path reconstruction
        int start = 0; // CHC
        int end = 5;   // TNE
        System.out.println("\nDynamic Re-routing Example [CHC -> TNE]:");
        System.out.println("Minimum Transit Time: " + shortestDistances[start][end] + " minutes");
        
        List<Integer> routeIds = reconstructPath(next, start, end);
        List<String> routeNames = new ArrayList<>();
        for (int id : routeIds) {
            routeNames.add(names[id]);
        }
        System.out.println("Optimal Node Sequence: " + String.join(" -> ", routeNames));
    }
}
