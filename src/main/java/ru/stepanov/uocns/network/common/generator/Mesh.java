package ru.stepanov.uocns.network.common.generator;

import java.util.ArrayList;
import java.util.List;

public class Mesh extends Network {

    public int dimention;
    protected int n, m; // размерность сетки (n - высота)

    public Mesh(int n, int m) {
        //TODO: check validation
        this.n = n;
        this.m = m;
        dimention = m * n;
    }

    protected void setNetlist(int[][] netlist) {
        this.netlist = netlist;
    }

    protected void setRouting(int[][] routing) {
        this.routing = routing;
    }

    public int[][] getNetlist() {
        return this.netlist;
    }

    public int[][] getRouting() {
        return this.routing;
    }

    public void createNetlist() {
        //TODO: вынести в отдельный метод
        //====================================================================
        // храним и инициализируем "боковые" id роутеров отдельно
        List<Integer> leftList, rightList, upList, downLst;

        //left
        leftList = new ArrayList<>();
        for (int id = m; id < m * (n - 1); id += m) {
            leftList.add(id);
        }

        //right
        rightList = new ArrayList<>();
        for (int id = 2 * m - 1; id < dimention - 1; id += m) {
            rightList.add(id);
        }

        //up
        upList = new ArrayList<>();
        for (int id = 1; id < m - 1; id++) {
            upList.add(id);
        }

        //down
        downLst = new ArrayList<>();
        for (int id = m * (n - 1) + 1; id < dimention - 1; id++) {
            downLst.add(id);
        }
        //====================================================================
        int[][] netlist = new int[dimention][PORT_NUMBER];

        for (int id = 0; id < dimention; id++) {
            // объявление локальных констант для нумеров портов для минимизации ошибок
            int zero = id - 1;
            int one = id + m;
            int two = id + 1;
            int three = id - m;
            int disconnect = -1;

            if (id == 0) { // левый верхний угол 1,2
                netlist[id][0] = disconnect;
                netlist[id][1] = one;
                netlist[id][2] = two;
                netlist[id][3] = disconnect;
            } else if (id == m - 1) { // правый верхний угол 0,1
                netlist[id][0] = zero;
                netlist[id][1] = one;
                netlist[id][2] = disconnect;
                netlist[id][3] = disconnect;
            } else if (id == m * (n - 1)) { // левый нижний угол 2,3
                netlist[id][0] = disconnect;
                netlist[id][1] = disconnect;
                netlist[id][2] = two;
                netlist[id][3] = three;
            } else if (id == dimention - 1) { // правый нижний угол 0,3
                netlist[id][0] = zero;
                netlist[id][1] = disconnect;
                netlist[id][2] = disconnect;
                netlist[id][3] = three;
            } else if (leftList.contains(id)) { // левая сторона 1,2,3
                netlist[id][0] = disconnect;
                netlist[id][3] = three;
                netlist[id][2] = two;
                netlist[id][1] = one;
            } else if (upList.contains(id)) { // верхняя сторона 0,1,2
                netlist[id][0] = zero;
                netlist[id][1] = one;
                netlist[id][2] = two;
                netlist[id][3] = disconnect;
            } else if (rightList.contains(id)) { // правая сторона 0,1,3
                netlist[id][0] = zero;
                netlist[id][1] = one;
                netlist[id][2] = disconnect;
                netlist[id][3] = three;
            } else if (downLst.contains(id)) { // нижняя сторона 0,2,3
                netlist[id][0] = zero;
                netlist[id][1] = disconnect;
                netlist[id][2] = two;
                netlist[id][3] = three;
            } else { // середина
                netlist[id][0] = zero;
                netlist[id][1] = one;
                netlist[id][2] = two;
                netlist[id][3] = three;
            }
        }

        setNetlist(netlist);
    }

    public void createRouting() {
        int[][] routing = new int[dimention][dimention];
        int currentRow, targetRow;
        for (int i = 0; i < dimention; i++) {
            for (int j = 0; j < dimention; j++) {
                currentRow = i / m;
                targetRow = j / m;
                if (i < j) {
                    if (currentRow < targetRow) {
                        routing[i][j] = 1;
                    } else {
                        routing[i][j] = 2;
                    }
                } else if (i > j) {
                    if (currentRow > targetRow) {
                        routing[i][j] = 3;
                    } else {
                        routing[i][j] = 0;
                    }
                } else {
                    routing[i][j] = 4;
                }
            }
        }setRouting(routing);
    }
}
