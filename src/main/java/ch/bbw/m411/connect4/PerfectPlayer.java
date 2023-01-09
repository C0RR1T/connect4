package ch.bbw.m411.connect4;

import java.util.*;

public class PerfectPlayer extends Connect4ArenaMain.DefaultPlayer {
    private Integer bestPlay = null;
    private final int maxDepth;

    private final HashMap<Connect4ArenaMain.Stone[], Integer> cache =
            new HashMap<>();

    public PerfectPlayer(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private long countEmpty() {
        return Arrays.stream(board).filter(Objects::isNull).count();
    }

    private int minMax(int beta, int alpha,
                       int depth, Connect4ArenaMain.Stone forColor) {
        if (Connect4ArenaMain.isWinning(board, forColor.opponent()))
            return Integer.MIN_VALUE + 1;
        else if (depth == 0) {
            return rate(forColor);
        } else if (countEmpty() == 0L) {
            return 0;
        }
        int max = alpha;
        var moves = generateMoves();
        for (var move : moves) {
            board[move] = forColor;
            int value = -Optional.ofNullable(cache.get(board))
                    .orElse(minMax(-max, -beta, depth - 1,
                            forColor.opponent()));
            if (!cache.containsKey(board))
                cache.put(board, value);
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
        if (Connect4ArenaMain.isWinning(board, forStone))
            return 1_000;

        if (Connect4ArenaMain.isWinning(board, forStone.opponent()))
            return -1_0000;

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


    @Override
    int play() {
        bestPlay = null;
        minMax(Integer.MAX_VALUE, Integer.MIN_VALUE, this.maxDepth,
                myColor);
        return bestPlay;
    }
}
