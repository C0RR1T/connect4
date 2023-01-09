package ch.bbw.m411.connect4;

import java.util.ArrayList;
import java.util.List;

public class PerfectPlayer extends Connect4ArenaMain.DefaultPlayer {
    private Integer bestPlay = null;
    private final int maxDepth;

    public PerfectPlayer(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private int minMax(int beta, int alpha,
                       int depth, Connect4ArenaMain.Stone forColor) {
        if (depth == 0) {
            return rate(forColor);
        }
        int max = alpha;
        var moves = generateMoves();
        for (var move : moves) {
            board[move] = forColor;
            int value = -minMax(-max, -beta, depth - 1,
                    forColor.opponent());
            board[move] = null;
            if (value > max) {
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

    private int countEmpty() {
        int count = 0;
        for (Connect4ArenaMain.Stone stone : board) {
            if (stone == null)
                count++;
        }
        return count;
    }

    private int countFilled() {
        return board.length - countEmpty();
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
        if (Connect4ArenaMain.isWinning(board, forStone))
            return Integer.MAX_VALUE;

        if (Connect4ArenaMain.isWinning(board, forStone.opponent()))
            return Integer.MIN_VALUE;

        var rating = 0;
        for (int i = 0; i < board.length; i++) {
            // Rate the move based on how close it is to the center
            // i % 7 always goes to 6, then back to 0
            switch (i % 7) {
                case 0, 6 -> rating += 1;
                case 1, 5 -> rating += 2;
                case 2, 4 -> rating += 3;
                case 3 -> rating += 4;
            }
        }
        return rating;
    }

    private int ratingOfRow(int amount) {
        return amount;
    }

    private int rowCounting(Step step, int origin,
                            Connect4ArenaMain.Stone forColor) {
        int rating = 0;
        var myCount = 0;
        for (int i = origin; i >= 0; i = step.nextStep(i)) {
            var currentStone = board[i];

            if (currentStone == forColor) {
                myCount++;
            } else if (currentStone == forColor.opponent()) {
                rating += ratingOfRow(myCount);
                myCount = 0;
            } else {
                rating += ratingOfRow(myCount);
                myCount = 0;
            }
        }
        return rating;
    }

    interface Step {
        int nextStep(int number);
    }


    @Override
    int play() {
        bestPlay = null;
        int originFilled = countFilled();
        minMax(Integer.MAX_VALUE, Integer.MIN_VALUE, this.maxDepth, myColor);
        return bestPlay;
    }
}
