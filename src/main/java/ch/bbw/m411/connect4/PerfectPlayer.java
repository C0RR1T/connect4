package ch.bbw.m411.connect4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PerfectPlayer extends Connect4ArenaMain.DefaultPlayer {


    private Integer bestPlay = null;
    private final int maxDepth;

    private final static int MAX_REWARD = 100_000;
    private final static int DRAW_REWARD = 0;

    private final HashMap<String, Integer> cache = new HashMap<>();

    public PerfectPlayer(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private int minMax(int beta, int alpha,
                       int depth, Connect4ArenaMain.Stone forColor) {
        if (Connect4ArenaMain.isWinning(board, forColor))
            return MAX_REWARD;
        if (Connect4ArenaMain.isWinning(board, forColor.opponent()))
            return -MAX_REWARD;

        if (depth == 0) {
            return rate(forColor);
        }

        int max = alpha;
        var moves = generateMoves();

        if (moves.size() == 0) {
            return DRAW_REWARD;
        }


        for (var move : moves) {
            board[move] = forColor;
            int value = -minMax(-max, -beta, depth - 1,
                    forColor.opponent());
            board[move] = null;
            if (depth == maxDepth) {
                System.out.printf("At index [%d]: Value %d%n", move, value);
            }
            if (value >= max) {
                max = value;
                if (depth == this.maxDepth) {
                    this.bestPlay = move;
                }
                if (max >= beta) {
                    break;
                }

            }
        }

        return max;
    }

    private List<Integer> generateMoves() {
        var list = new ArrayList<Integer>();
        for (Integer i : List.of(3, 2, 4, 5, 1, 0, 6)) {
            for (int y = i; y < board.length; y += 7) {
                if (board[y] == null) {
                    list.add(y);
                    break;
                }
            }
        }
        return list;
    }


    private int rate(Connect4ArenaMain.Stone forStone) {
        var rating = 0;
        for (int i = 0; i < board.length; i++) {
            // Rate the move based on how close it is to the center
            // i % 7 always goes to 6, then back to 0
            if (board[i] == forStone) {
                switch (i % 7) {
                    case 0, 6 -> rating += 1;
                    case 1, 5 -> rating += 2;
                    case 2, 4 -> rating += 3;
                    case 3 -> rating += 4;
                }
            }
        }

        return rating;
    }


    @Override
    int play() {
        bestPlay = null;
        minMax(Integer.MAX_VALUE, Integer.MIN_VALUE, this.maxDepth,
                myColor);

        return bestPlay;
    }
}
