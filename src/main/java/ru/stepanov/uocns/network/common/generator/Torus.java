package ru.stepanov.uocns.network.common.generator;

import java.util.ArrayList;
import java.util.List;

public class Torus extends Mesh {
    public Torus(int n, int m) {
        super(n, m);
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
            // "пробрасываемые" порты
            int residualZero = id + (m - 1);
            int residualOne = id - m*(n - 1);
            int residualTwo = id - (m - 1) ;
            int residualThree = id + m*(n - 1);

            if (id == 0) { // левый верхний угол
                netlist[id][0] = residualZero;
                netlist[id][1] = one;
                netlist[id][2] = two;
                netlist[id][3] = residualThree;
            } else if (id == m - 1) { // правый верхний угол
                netlist[id][0] = zero;
                netlist[id][1] = one;
                netlist[id][2] = residualTwo;
                netlist[id][3] = residualThree;
            } else if (id == m * (n - 1)) { // левый нижний угол
                netlist[id][0] = residualZero;
                netlist[id][1] = residualOne;
                netlist[id][2] = two;
                netlist[id][3] = three;
            } else if (id == dimention - 1) { // правый нижний угол
                netlist[id][0] = zero;
                netlist[id][1] = residualOne;
                netlist[id][2] = residualTwo;
                netlist[id][3] = three;
            } else if (leftList.contains(id)) { // левая сторона
                netlist[id][0] = residualZero;
                netlist[id][3] = three;
                netlist[id][2] = two;
                netlist[id][1] = one;
            } else if (upList.contains(id)) { // верхняя сторона
                netlist[id][0] = zero;
                netlist[id][1] = one;
                netlist[id][2] = two;
                netlist[id][3] = residualThree;
            } else if (rightList.contains(id)) { // правая сторона
                netlist[id][0] = zero;
                netlist[id][1] = one;
                netlist[id][2] = residualTwo;
                netlist[id][3] = three;
            } else if (downLst.contains(id)) { // нижняя сторона
                netlist[id][0] = zero;
                netlist[id][1] = residualOne;
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
        int currentRow, targetRow, currentCol, targetCol;
        for (int i = 0; i < dimention; i++) {
            for (int j = 0; j < dimention; j++) {
                currentRow = i / m;
                targetRow = j / m;
                currentCol = i % m;
                targetCol = j % m;

                if (i < j) {
                    if (currentRow < targetRow) {

                        //если по прямому пути (вниз) идти "дольше", чем по "обратному"
                        if (numInterval(currentRow, targetRow, n) >= numInterval(targetRow, currentRow, n)) {
                            routing[i][j] = 3;
                        } else {
                            routing[i][j] = 1;
                        }

                    } else { // если в одной строке

                        //если по прямому пути (направо) идти "дольше", чем по "обратному"
                        if (numInterval(currentCol, targetCol, m) >= numInterval(targetCol, currentCol, m)) {
                            routing[i][j] = 0;
                        } else { //если по прямому пути идти "быстрее"
                            routing[i][j] = 2;
                        }

                    }
                } else if (i > j) {
                    if (currentRow > targetRow) {

                        //если по прямому пути (наверх) идти "дольше", чем по "обратному"
                        if (numInterval(currentRow, targetRow, n) > numInterval(targetRow, currentRow, n)) {
                            routing[i][j] = 3;
                        } else {
                            routing[i][j] = 1;
                        }

                    } else { // если в одной строке

                        //если по прямому пути (налево) идти "дольше", чем по "обратному"
                        if (numInterval(currentCol, targetCol, m) > numInterval(targetCol, currentCol, m)) {
                            routing[i][j] = 0;
                        } else { //если по прямому пути идти "быстрее"
                            routing[i][j] = 2;
                        }
                    }
                } else { // если i == j
                    //передача в IP
                    routing[i][j] = 4;
                }
            }
        }
        setRouting(routing);
    }

    // Функция направленного поиска расстояния между столбцами/строками двух роутеров
    private int numInterval(int start, int end, int max) {
        int interval;
        if (start <= end) {
            interval = end - start;
        } else {
            interval = max - start + end;
        }
        return interval;
    }
}
