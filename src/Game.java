import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by naatiq on 6/7/14.
 */
public class Game {
    //Set 1 for player positions
    //Set 2 or finish position
    //Set 0 for unoccupied positions
    //Set -1 for positions out of grid

    static int[][] grid;
    private int finishX;
    private int finishY;

    public Game(int[][] grid) {
        this.grid = grid;
    }

    public ArrayList<Game> nextStates() {
        throw new NotImplementedException();
    }

    private int playerPos() {
        throw new NotImplementedException();
    }
}

class Position {
    private int xPos;
    private int yPos;

    Position(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public boolean isValid(int[][] grid) {
        return !(grid[xPos][yPos] == -1);
    }
}

class State {
    private ArrayList<Position> playerPos;

    State(ArrayList<Position> playerPos) {
        this.playerPos = playerPos;
    }

    boolean isStanding() {
        return playerPos.size() == 1;
    }
    ArrayList<State> nextStates() {
        throw new NotImplementedException();
    }
}
