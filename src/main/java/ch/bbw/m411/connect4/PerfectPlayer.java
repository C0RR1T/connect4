package ch.bbw.m411.connect4;

import java.util.Arrays;

public class PerfectPlayer extends Connect4ArenaMain.DefaultPlayer {


    private int bestPlay = -1;
    private final int maxDepth;

    private final static int MAX_REWARD = 10_000;
    private final static int MIN_REWARD = -MAX_REWARD;

    private final static int WIN_REWARD = 5_000;
    private final static int LOOSE_REWARD = -WIN_REWARD;

    public PerfectPlayer(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private int minMax(int beta, int alpha,
                       int depth, Connect4ArenaMain.Stone forColor) {
        if (Connect4ArenaMain.isWinning(board, forColor.opponent())) {
            return LOOSE_REWARD;
        }

        if (Connect4ArenaMain.isWinning(board, forColor)) {
            return WIN_REWARD;
        }

        if (depth == 0)
            return rate(forColor);

        var moves = generateMoves();

        if (moves.length == 0) {
            return rate(forColor);
        }

        int max = alpha;

        for (int move : moves) {
            board[move] = forColor;
            int value =
                    -minMax(-max,
                            -beta,
                            depth - 1,
                            forColor.opponent());
            board[move] = null;
            if (depth == maxDepth) {
                System.out.printf("At index [%d]: Value %d%n", move, value);
            }
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

    private final int[] moveOrder = new int[]{3, 2, 4, 5, 1, 0, 6};

    // Public because of tests
    public int[] generateMoves() {
        int[] moves = new int[7];
        int moveSize = 0;
        for (int i : moveOrder) {
            for (int y = i; y < board.length; y += 7) {
                if (board[y] == null) {
                    moves[moveSize++] = y;
                    break;
                }
            }
        }
        return Arrays.copyOf(moves, moveSize);
    }

    private final int[] values = new int[]{1, 2, 3, 4, 3, 2, 1};

    private int rate(Connect4ArenaMain.Stone forStone) {
        var rating = 0;

        for (int i = 0; i < board.length; i++) {
            if (board[i] == forStone)
                rating += values[i % 7];
            else if (board[i] != null)
                rating -= values[i % 7];
        }

        return rating;
    }


    @Override
    int play() {
        bestPlay = -1;
        minMax(MAX_REWARD, MIN_REWARD, this.maxDepth,
                myColor);

        return bestPlay;
    }
}
