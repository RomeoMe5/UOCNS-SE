package ru.stepanov.uocns.network.common.generator;

import ru.stepanov.uocns.network.common.generator.utils.DijkstrasAlgorithm;

import java.util.ArrayList;

import static ru.stepanov.uocns.network.common.generator.utils.DijkstrasAlgorithm.shiftRight;

public class Circulant extends Network {
    private int k;
    public int s1, s2;
    public static final int K = 7;

    public Circulant(int k) {
        this.k = k;
        int d = (int) Math.round((Math.sqrt(2 * k - 1) - 1) / 2);
        this.s1 = d;
        this.s2 = d + 1;
    }

    public Circulant(int k, int s1, int s2) {
        this.k = k;
        this.s1 = s1;
        this.s2 = s2;

    }

    private void setNetlist(int[][] netlist) {
        this.netlist = netlist;
    }

    private void setRouting(int[][] routing) {
        this.routing = routing;
    }

    public int[][] getNetlist() {
        return this.netlist;
    }

    public int[][] getRouting() {
        return this.routing;
    }

    public void createNetlist() {
        int[][] netlist = new int[k][PORT_NUMBER];
        for (int id = 0; id < k; id++) {
            int zero, one, zeroPlus, onePlus;
            // изначально задано, что s1 < s2, поэтому:
            zero = id + s1;
            one = id + s2;
            onePlus = one - k;
            zeroPlus = zero - k;
            if ((id + s1) > (k - 1)) {
                netlist[id][0] = zeroPlus;
                netlist[id][1] = onePlus;
                netlist[zeroPlus][3] = id;
                netlist[onePlus][2] = id;

            } else if ((id + s2) > (k - 1)) {
                netlist[id][0] = zero;
                netlist[id][1] = onePlus;
                netlist[zero][3] = id;
                netlist[onePlus][2] = id;

            } else {
                netlist[id][0] = zero;
                netlist[id][1] = one;
                netlist[zero][3] = id;
                netlist[one][2] = id;
            }
        }

        setNetlist(netlist);
    }

    public void createRouting(int[][] adjacencyMatrix, int[][] netlist) {
        int[][] routing = new int[k][k];
        for (int i = 0; i < k; i++) {
            routing[i][i] = 4;
        }

        ArrayList<ArrayList<Integer>> pathForNode;
        pathForNode = DijkstrasAlgorithm.dijkstra(adjacencyMatrix, 0);
//        routing = DijkstrasAlgorithm.fillRouting (0, routing, pathForNode, netlist);
        DijkstrasAlgorithm.fillRouting(0, routing, pathForNode, netlist);

        for (int i = 1; i < k; i++) {
            routing[i] = shiftRight(routing[i - 1]);
        }

        setRouting(routing);
    }

    //матрица смежности
    public int[][] adjacencyMatrix(int[][] netlist, int k) {
        int[][] adjMtx = new int[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                adjMtx[i][j] = 0;
            }
        }
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < 4; j++) {
                adjMtx[i][netlist[i][j]] = 1;
                adjMtx[netlist[i][j]][i] = 1;
            }
        }
        return adjMtx;
    }
}
