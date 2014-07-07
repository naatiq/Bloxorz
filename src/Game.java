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
        ArrayDeque<State> stack = new ArrayDeque<State>();
        ArrayList<State> visited = new ArrayList<State>();
        HashMap<State, ArrayDeque<State>> paths = new HashMap<State, ArrayDeque<State>>();
        State start = new State(initialPos);
        State end = new State(endPos);

        stack.addFirst(start);

        ArrayDeque<State> path = new ArrayDeque<State>();
        path.addFirst(start);
        paths.put(start, path);

        boolean found = false;
        while (stack.size() != 0 && !found) {
            State currentState = stack.removeFirst();
            visited.add(currentState);
            //System.out.println(currentState);
            for(State state: currentState.nextStates()) {
                path = paths.get(currentState).clone();
                path.addLast(state);
                paths.put(state, path);
                if(finished(state)) {
                    end = state;
                    found = true;
                    break;
                }
                else {
                    if(!visited.contains(state)) {
                        stack.addFirst(state);
                    }
                }

            }
        }
        for(State s: paths.get(end)) {
            System.out.println(s);
        }
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
                Position currestPos = current.get(0);

                // Calculate positions to the right of cube
                Position pos1 = currestPos.nextX();
                Position pos2 = pos1.nextX();
                // if valid add the new State
                checkAdd(res, pos1, pos2);

                // Calculate positions to the left of cube
                pos1 = currestPos.prevX();
                pos2 = pos1.prevX();
                // if valid add the new  State
                checkAdd(res, pos1, pos2);

                // Calculate positions below the cube
                pos1 = currestPos.nextY();
                pos2 = pos1.nextY();
                // if valid add the new State
                checkAdd(res, pos1, pos2);

                // Calculate positions above the cube
                pos1 = currestPos.prevY();
                pos2 = pos2.prevY();
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

                    if(currentPos1.getxPos() < currentPos2.getyPos()) {
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
                    return this.current.get(0).equals(otherState.current.get(0)) &&
                            this.current.get(1).equals(otherState.current.get(1));
                }
            }
        }
    }

    public static void main(String[] args) {
        int[][] grid = new int[][]
                {{9,9,9,9,9,9,9,9}
                ,{9,9,9,9,9,9,9,9}
                ,{9,9,0,0,0,0,9,9}
                ,{9,9,0,0,0,0,9,9}
                ,{9,9,0,0,0,0,9,9}
                ,{9,9,0,0,0,0,9,9}
                ,{9,9,9,9,9,9,9,9}
                ,{9,9,9,9,9,9,9,9}};

        Game game = new Game(grid,2,2,5,2);
        game.solution();

    }

}