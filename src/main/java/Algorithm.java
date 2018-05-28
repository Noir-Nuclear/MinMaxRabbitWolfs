package main.java;

import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Algorithm {

    public Character[][] ground;
    private Integer levelAI;
    private Position rabbitPosition;

    public Algorithm(int levelAI) {
        rabbitPosition = new Position();
        this.levelAI = levelAI;
        ground = generateGround();
    }

    public Character[][] generateGround() {
        Character[][] newGround = new Character[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                newGround[i][j] = (i + j) % 2 == 0 ? '*' : '0';
            }
        }
        newGround[0][2] = newGround[0][4] = newGround[0][6] = newGround[0][0] = 'W';
        newGround[7][3] = 'R';
        rabbitPosition.i = 7;
        rabbitPosition.j = 3;
        return newGround;
    }

    public HashMap<String, Integer> calculateMoves(Position localRabbitPosition, char animal, Character[][] localGround) {
        List<Position> paths = new ArrayList<>();
        HashMap<String, Integer> moves = new HashMap<>();
        paths.add(localRabbitPosition);
        moves.put(localRabbitPosition.i.toString() + localRabbitPosition.j.toString(), 0);
        while (!paths.isEmpty()) {
            Position currentPosition = paths.remove(0);
            for (int i = 0; i < 4; i++) {
                Position newPosition = currentPosition.clone();
                newPosition.i += new Double(Math.pow(-1, i % 2)).intValue();
                newPosition.j += new Double(Math.pow(-1, i < 2 ? 0 : 1)).intValue();
                if (isAvailable(newPosition, moves, localGround, animal)) {
                    moves.put(newPosition.i.toString() + newPosition.j.toString(),
                            moves.get(currentPosition.i.toString() + currentPosition.j.toString()) + 1);
                    paths.add(newPosition);
                }
            }
        }
        return moves;
    }

    public Position[] getWolfsPosition(Character[][] localGround) {
        Position[] localWolfsPosition = new Position[4];
        int k = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (localGround[i][j] == 'W') {
                    localWolfsPosition[k] = new Position();
                    localWolfsPosition[k].i = i;
                    localWolfsPosition[k].j = j;
                    k++;
                }
            }
        }
        return localWolfsPosition;
    }

    public String isVictory() {
        if (!isFree(rabbitPosition, ground)) {
            return "Волки победили";
        }
        if (rabbitPosition.i == 0) {
            return "Заяц победил";
        }
        return "";
    }

    public Character[][] changeGround(Character[][] changedGround, Position oldPosition, Position newPosition) {
        Character[][] newGround = cloneArrayChar(changedGround);
        char buf = newGround[oldPosition.i][oldPosition.j];
        newGround[oldPosition.i][oldPosition.j] = newGround[newPosition.i][newPosition.j];
        newGround[newPosition.i][newPosition.j] = buf;
        return newGround;
    }

    public void makeStart() {
        runMinMax(0, 'R', rabbitPosition.clone(), cloneArrayChar(ground), Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public String makeMove(int i, Position playerPos) {
        Position newPosition = playerPos.clone();
        Character playerName = ground[playerPos.i][playerPos.j];

        newPosition.i += new Double(Math.pow(-1, i % 2)).intValue();
        newPosition.j += new Double(Math.pow(-1, i < 2 ? 0 : 1)).intValue();
        if (!isAvailable(newPosition, new HashMap<>(), ground, playerName)) {
            return "Нельзя";
        }
        ground = changeGround(ground, playerPos, newPosition);
        playerPos = newPosition.clone();

        if (playerName == 'R') {
            rabbitPosition = playerPos.clone();
        }

        if(!isVictory().equals("")) {
            return isVictory();
        }

        runMinMax(0, playerName == 'W' ? 'R' : 'W', rabbitPosition.clone(), cloneArrayChar(ground), Integer.MIN_VALUE, Integer.MAX_VALUE);
        return isVictory();
    }

    Character[][] cloneArrayChar(Character[][] array) {
        Character[][] newArray = new Character[array.length][];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i].clone();
        }
        return newArray;
    }

    public Integer runMinMax(int recursiveLevel, char animal, Position localRabbitPosition, Character[][] localGround, int alpha, int beta) {
        Position[] localWolfsPosition = getWolfsPosition(localGround);
        Position[] oldNewPosition = new Position[2];
        HashMap<String, Integer> moves = calculateMoves(localRabbitPosition, 'R', localGround);
        if (recursiveLevel >= levelAI * 2)
            return getBestMove(moves);
        int minMax = animal == 'R' ? 254 : 0;
        if (animal == 'R') {
            for (int i = 0; i < 4; i++) {
                Position newPosition = localRabbitPosition.clone();
                newPosition.i += new Double(Math.pow(-1, i % 2)).intValue();
                newPosition.j += new Double(Math.pow(-1, i < 2 ? 0 : 1)).intValue();
                if (isAvailable(newPosition, new HashMap<>(), localGround, 'R')) {
                    Integer testValue = runMinMax(recursiveLevel + 1, 'W',
                            newPosition, changeGround(localGround, localRabbitPosition, newPosition), alpha, beta);
                    if (minMax >= testValue) {
                        minMax = testValue;
                        oldNewPosition[0] = localRabbitPosition.clone();
                        oldNewPosition[1] = newPosition.clone();
                    }
                    beta = beta > testValue ? testValue : beta;
                    if (alpha > beta)
                        break;
                }
            }
        } else {
            boolean isFinal = false;
            boolean isFirst = true;
            for (int i = 0; i < 4; i++) {
                Position newPosition = localWolfsPosition[i].clone();
                newPosition.i += 1;
                newPosition.j -= 1;
                for (int j = 0; j < 2; j++) {
                    newPosition.j += j * 2;
                    if (isAvailable(newPosition, new HashMap<>(), localGround, 'W')) {

                        Integer testValue = runMinMax(recursiveLevel + 1, 'R',
                                localRabbitPosition, changeGround(localGround, localWolfsPosition[i], newPosition), alpha, beta);
                        if (minMax < testValue || isFirst) {
                            minMax = testValue;
                            oldNewPosition[0] = localWolfsPosition[i].clone();
                            oldNewPosition[1] = newPosition.clone();
                            isFirst = false;
                        }
                        alpha = alpha < testValue ? testValue : alpha;
                        if (alpha > beta) {
                            isFinal = true;
                            break;
                        }
                    }
                }
                if (isFinal)
                    break;
            }
        }
        if (recursiveLevel == 0) {
            if(animal == 'R') {
                rabbitPosition = oldNewPosition[1].clone();
            }
            ground = changeGround(ground, oldNewPosition[0], oldNewPosition[1]);
        }
        return minMax;
    }

    public boolean isFree(Position localRabbitPosition, Character[][] localGround) {
        for (int i = 0; i < 4; i++) {
            Position newPosition = localRabbitPosition.clone();
            newPosition.i += new Double(Math.pow(-1, i % 2)).intValue();
            newPosition.j += new Double(Math.pow(-1, i < 2 ? 0 : 1)).intValue();
            if (isAvailable(newPosition, new HashMap<>(), localGround, 'R')) {
                return true;
            }
        }
        return false;
    }

    public int getBestMove(HashMap<String, Integer> moves) {
        int min = 254;
        for (int i = 0; i < 8; i += 2) {
            Integer move = moves.get("0" + i);
            if (move != null && move < min)
                min = move;
        }
        return min;
    }

    public boolean isAvailable(Position newPosition, HashMap<String, Integer> moves, Character[][] localGround, char animal) {
        char antagonist = animal == 'R' ? 'W' : 'R';
        if (newPosition.i > 7 || newPosition.i < 0 || newPosition.j > 7 || newPosition.j < 0)
            return false;
        return !localGround[newPosition.i][newPosition.j].equals(antagonist) &&
                !localGround[newPosition.i][newPosition.j].equals(animal) &&
                moves.get(newPosition.i.toString() + newPosition.j.toString()) == null;
    }

    public Position findRabbit() {
        Position rabbitPosition = new Position();

        for (int i = 0; i < ground.length; i++) {
            Character[] row = ground[i];
            for (int j = 0; j < row.length; j++) {
                Character item = row[j];
                if (item == 'R') {
                    rabbitPosition.i = i;
                    rabbitPosition.j = j;
                    break;
                }
            }
        }

        return rabbitPosition;
    }
}
