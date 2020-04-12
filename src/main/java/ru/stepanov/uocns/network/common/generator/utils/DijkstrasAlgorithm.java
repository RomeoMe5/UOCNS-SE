package ru.stepanov.uocns.network.common.generator.utils;

import java.util.ArrayList;

public class DijkstrasAlgorithm {
    private static final int NO_PARENT = -1;

    // Function that implements Dijkstra's
    // single source shortest path
    // algorithm for a graph represented
    // using adjacency matrix
    // representation
    public static ArrayList<ArrayList<Integer>> dijkstra(int[][] adjacencyMatrix,
                                                         int startVertex) {
        int nVertices = adjacencyMatrix[0].length;

        // shortestDistances[i] will hold the
        // shortest distance from src to i
        int[] shortestDistances = new int[nVertices];

        // added[i] will true if vertex i is
        // included / in shortest path tree
        // or shortest distance from src to
        // i is finalized
        boolean[] added = new boolean[nVertices];

        // Initialize all distances as
        // INFINITE and added[] as false
        for (int vertexIndex = 0; vertexIndex < nVertices;
             vertexIndex++) {
            shortestDistances[vertexIndex] = Integer.MAX_VALUE;
            added[vertexIndex] = false;
        }

        // Distance of source vertex from
        // itself is always 0
        shortestDistances[startVertex] = 0;

        // Parent array to store shortest
        // path tree
        int[] parents = new int[nVertices];

        // The starting vertex does not
        // have a parent
        parents[startVertex] = NO_PARENT;

        // Find shortest path for all
        // vertices
        for (int i = 1; i < nVertices; i++) {

            // Pick the minimum distance vertex
            // from the set of vertices not yet
            // processed. nearestVertex is
            // always equal to startNode in
            // first iteration.
            int nearestVertex = -1;
            int shortestDistance = Integer.MAX_VALUE;
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++) {
                if (!added[vertexIndex] &&
                        shortestDistances[vertexIndex] <
                                shortestDistance) {
                    nearestVertex = vertexIndex;
                    shortestDistance = shortestDistances[vertexIndex];
                }
            }

            // Mark the picked vertex as
            // processed
            added[nearestVertex] = true;

            // Update dist value of the
            // adjacent vertices of the
            // picked vertex.
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++) {
                int edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];

                if (edgeDistance > 0
                        && ((shortestDistance + edgeDistance) <
                        shortestDistances[vertexIndex])) {
                    parents[vertexIndex] = nearestVertex;
                    shortestDistances[vertexIndex] = shortestDistance +
                            edgeDistance;
                }
            }
        }

        return getPathsForVertex(startVertex, shortestDistances, parents);
    }

    // A utility function to print
    // the constructed distances
    // array and shortest paths
    private static ArrayList<ArrayList<Integer>> getPathsForVertex(int startVertex,
                                                                   int[] distances,
                                                                   int[] parents) {
        int nVertices = distances.length;
        System.out.print("Vertex\t Distance\tPath");
        ArrayList<ArrayList<Integer>> pathForNode = new ArrayList<>();
        for (int vertexIndex = 0; vertexIndex < nVertices; vertexIndex++) {
            ArrayList<Integer> path = new ArrayList<>();
            if (vertexIndex != startVertex) {

                System.out.print("\n" + startVertex + " -> ");
                System.out.print(vertexIndex + " \t\t ");
                System.out.print(distances[vertexIndex] + "\t\t");
                printPath(vertexIndex, parents, path);

            }
            pathForNode.add(path);
        }
        System.out.println();
        return pathForNode;
    }

    // Function to print shortest path
    // from source to currentVertex
    // using parents array
    private static void printPath(int currentVertex, int[] parents, ArrayList<Integer> path) {

        // Base case : Source node has
        // been processed
        if (currentVertex == NO_PARENT) {
            return;
        }
        printPath(parents[currentVertex], parents, path);
        path.add(currentVertex);
        System.out.print(currentVertex + " ");
    }

    //заполнение первой строчки роутинга
    public static int[][] fillRouting(int currentNode, int[][] routing, ArrayList<ArrayList<Integer>> pathForNode, int[][] netlist) {
        for (int i = 0; i < pathForNode.size(); i++) {
            if (pathForNode.get(i).size() > 0) {
                //из пути берем первую пару портов
                //и по ней определяем порт, который
                //указываем в матрице маршрутизации
                int x1 = pathForNode.get(i).get(0);
                int x2 = pathForNode.get(i).get(1);
                int port = findPort(netlist, x1, x2);
                routing[currentNode][i] = port;
            }
        }
        return routing;
    }

    //нахождение порта, по которому надо двигаться, чтобы из sourse попасть в target
    private static int findPort(int[][] netlist, int sourse, int target) {
        int port = -1;
        for (int j = 0; j < 4; j++) {
            if (netlist[sourse][j] == target) {
                port = j;
            }
        }
        return port;
    }

    //сдвиг значений вектора вправо
    public static int[] shiftRight(int[] row) {
        int[] newRow = new int[row.length];
        int last = row[row.length - 1];          // save off first element

        // shift right
        System.arraycopy(row, 0, newRow, 1, row.length - 2 + 1);

        // wrap last element into first slot
        newRow[0] = last;
        return newRow;
    }
}
