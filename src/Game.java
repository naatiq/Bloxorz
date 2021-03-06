import javafx.geometry.Pos;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by naatiq on 6/7/14.
 */
public class Game {

    private int[][] grid;
    private Position initialPos;
    private Position endPos;

    // Every Grid should have following values
    // 0 for valid positions in play area
    // 9 for positions outside play area
    // 1 for player position
    // 2 for end position
    public Game(int[][] grid, int startX, int startY, int endX, int endY) {
        this.grid = grid;
        this.initialPos = new Position(startX, startY);
        this.endPos = new Position(endX, endY);
    }

    private boolean finished(State state) {
        return state.isStanding() && state.getCurrent().get(0).equals(endPos);
    }

    public ArrayDeque<State> solution() {
        ArrayDeque<State> queue = new ArrayDeque<State>();
        ArrayList<State> visited = new ArrayList<State>();
        HashMap<State, ArrayDeque<State>> paths = new HashMap<State, ArrayDeque<State>>();
        State start = new State(initialPos);
        State end = new State(endPos);

        queue.addFirst(start);

        ArrayDeque<State> path = new ArrayDeque<State>();
        path.addFirst(start);
        paths.put(start, path);

        boolean found = false;
        while (queue.size() != 0 && !found) {
            State currentState = queue.removeFirst();
            visited.add(currentState);
            //System.out.println(currentState);
            for(State state: currentState.nextStates()) {
                path = paths.get(currentState).clone();
                path.addLast(state);
                if(paths.get(state) == null || paths.get(state).size() > path.size())
                    paths.put(state, path);
                if(finished(state)) {
                    end = state;
                    found = true;
                    break;
                }
                else {
                    if(!visited.contains(state)) {
                        queue.addLast(state);
                    }
                }

            }
        }
//        for(State s: paths.get(end)) {
//            System.out.println(s);
//        }
        printMoves(paths.get(end));
        return paths.get(end);
    }
    private class Position {
        int xPos;
        int yPos;

        private Position(int xPos, int yPos) {
            this.xPos = xPos;
            this.yPos = yPos;
        }

        public int getxPos() {
            return xPos;
        }

        public int getyPos() {
            return yPos;
        }

        public boolean isValid() {
            //System.out.println(xPos);
            //System.out.print(yPos);
            return grid[xPos][yPos] != 9;
        }

        public Position nextX() {
            return new Position(xPos + 1, yPos);
        }

        public Position prevX() {
            return new Position(xPos - 1, yPos);
        }

        public Position nextY() {
            return new Position(xPos, yPos + 1);
        }

        public Position prevY() {
            return new Position(xPos, yPos - 1);
        }

        public boolean isXAligned(Position other) {
            return this.xPos == other.xPos;
        }

        public boolean isYAligned(Position other) {
            return this.yPos == other.yPos;
        }

        @Override
        public boolean equals(Object other) {
            return ((Position)other).xPos == this.xPos && ((Position)other).yPos == this.yPos;
        }

        public String toString() {
            return "(" + xPos +", "+yPos + ")";
        }
    }

    private class State {

        private boolean standing;
        private ArrayList<Position> current = new ArrayList<Position>();

        private State(Position pos) {
            standing = true;
            current.add(pos);
        }

        private State(Position pos1, Position pos2) {
            standing = false;
            current.add(pos1);
            current.add(pos2);
        }

        public boolean isStanding() {
            return standing;
        }

        public ArrayList<Position> getCurrent() {
            return current;
        }

        public ArrayList<State> nextStates () {
            ArrayList<State> res = new ArrayList<State>();
            if(isStanding()) {
                Position currentPos = current.get(0);

                // Calculate positions to the right of cube
                Position pos1 = currentPos.nextX();
                Position pos2 = pos1.nextX();
                // if valid add the new State
                checkAdd(res, pos1, pos2);

                // Calculate positions to the left of cube
                pos1 = currentPos.prevX();
                pos2 = pos1.prevX();
                // if valid add the new  State
                checkAdd(res, pos1, pos2);

                // Calculate positions below the cube
                pos1 = currentPos.nextY();
                pos2 = pos1.nextY();
                // if valid add the new State
                checkAdd(res, pos1, pos2);

                // Calculate positions above the cube
                pos1 = currentPos.prevY();
                pos2 = pos1.prevY();
                // if valid add the positions
                checkAdd(res, pos1, pos2);
            }

            else {
                Position currentPos1 = current.get(0);
                Position currentPos2 = current.get(1);

                // Checking if player is lying top to down
                if(currentPos1.isXAligned(currentPos2)) {

                    checkAdd(res, currentPos1.nextX(), currentPos2.nextX());

                    checkAdd(res, currentPos1.prevX(), currentPos2.prevX());

                    if(currentPos1.getyPos() < currentPos2.getyPos()) {
                        checkAdd(res, currentPos1.prevY());
                        checkAdd(res, currentPos2.nextY());
                    }
                    else {
                        checkAdd(res, currentPos1.nextY());
                        checkAdd(res, currentPos2.prevY());
                    }
                }

                //Checking if the player is lying left to right
                else {
                    checkAdd(res, currentPos1.nextY(), currentPos2.nextY());
                    checkAdd(res, currentPos1.prevY(), currentPos2.prevY());

                    if(currentPos1.getxPos() < currentPos2.getxPos()) {
                        checkAdd(res, currentPos1.prevX());
                        checkAdd(res, currentPos2.nextX());
                    }
                    else {
                        checkAdd(res, currentPos1.nextX());
                        checkAdd(res, currentPos2.prevX());
                    }
                }
            }

            return res;
        }

        private void checkAdd(ArrayList<State> res, Position pos1, Position pos2) {
            if(pos1.isValid() && pos2.isValid()) {
                res.add(new State(pos1, pos2));
            }
        }

        private void checkAdd(ArrayList<State> res, Position pos1) {
            if(pos1.isValid()) {
                res.add(new State(pos1));
            }
        }

        @Override
        public String toString() {
            String res = "";
            for(Position pos: current){
                res = res + " " + pos;
            }

            return res;
        }

        @Override
        public boolean equals(Object other) {
            State otherState = (State)other;
            if(this.current.size() != otherState.current.size()) {
                return false;
            }
            else {
                if(this.current.size() == 1)
                    return this.current.get(0).equals(otherState.current.get(0));
                else {
                    return (this.current.get(0).equals(otherState.current.get(0)) &&
                            this.current.get(1).equals(otherState.current.get(1))) ||
                            (this.current.get(1).equals(otherState.current.get(0)) &&
                            this.current.get(0).equals(otherState.current.get(1)));
                }
            }
        }
    }

    public static void main(String[] args) {
        int[][] stage4 = new int[][]
                {{9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9}
                ,{9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9}
                ,{9,9,9,9,9,9,9,9,0,0,0,0,0,0,0,9,9,9,9}
                ,{9,9,0,0,0,0,9,9,0,0,0,9,9,0,0,9,9,9,9}
                ,{9,9,0,0,0,0,0,0,0,0,0,9,9,0,0,0,0,9,9}
                ,{9,9,0,0,0,0,9,9,9,9,9,9,9,0,0,0,0,9,9}
                ,{9,9,0,0,0,0,9,9,9,9,9,9,9,0,0,0,0,9,9}
                ,{9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9}
                ,{9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9}};

        int[][] stage5 = new int[][]
                {{9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,0,0,0,0,0,0,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,0,9,9,0,0,0,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,0,9,9,0,0,0,0,0,9,9,9,9},
                 {9,9,0,0,0,0,0,0,9,9,9,9,9,0,0,0,0,9,9},
                 {9,9,9,9,9,9,0,0,0,9,9,9,9,0,0,0,0,9,9},
                 {9,9,9,9,9,9,0,0,0,9,9,9,9,9,0,0,0,9,9},
                 {9,9,9,9,9,9,9,9,0,9,9,0,0,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,0,0,0,0,0,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,0,0,0,0,0,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,0,0,0,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9}};

        int[][] stage61 = new int[][]
                {{9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,0,0,0,0,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,0,0,0,0,9,9,9,9,9},
                 {9,9,0,0,0,9,9,9,9,9,0,9,9,0,0,0,0,9,9},
                 {9,9,0,0,0,0,0,0,0,0,0,9,9,9,0,0,0,9,9},
                 {9,9,0,0,0,9,9,9,9,0,0,0,9,9,0,0,0,9,9},
                 {9,9,0,0,0,9,9,9,9,0,0,0,9,9,0,0,0,9,9},
                 {9,9,9,0,0,9,9,9,9,0,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,0,0,0,0,0,0,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9}};

        int[][] stage62 = new int[][]
                {{9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,0,0,0,0,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,0,0,0,0,9,9,9,9,9},
                 {9,9,0,0,0,9,9,9,9,9,0,9,9,0,0,0,0,9,9},
                 {9,9,0,0,0,0,0,0,0,0,0,9,9,9,0,0,0,9,9},
                 {9,9,0,0,0,9,9,9,9,0,0,0,9,9,0,0,0,9,9},
                 {9,9,0,0,0,9,9,9,9,0,0,0,9,9,0,0,0,9,9},
                 {9,9,9,0,0,0,9,9,9,0,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,0,0,0,0,0,0,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9},
                 {9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9}};

        //Game game  = new Game(stage4,5,3,5,15);
        //Game game = new Game(stage5,5,2,6,15);
        Game game1 = new Game(stage61,5,3,6,11);
        game1.solution();
        Game game2 = new Game(stage62,6,11,5,15);
        game2.solution();

    }

    public static void printMoves(ArrayDeque<State> moves) {
        int n = moves.size();
        for (int i = 0; i < n - 1; i++) {
            State current = moves.removeFirst();
            State next = moves.peek();
            System.out.print(getMove(current, next) + " ,");
        }
    }

    private static String getMove(State current, State next) {
        if(current.current.get(0).getxPos() > next.current.get(0).getxPos())
            return "Up";
        else if(current.current.get(0).getxPos() < next.current.get(0).getxPos())
            return "Down";
        else if(current.current.get(0).getyPos() > next.current.get(0).getyPos())
            return "Left";
        else if(current.current.get(0).getyPos() < next.current.get(0).getyPos())
            return "Right";
        else return "None";

    }
}