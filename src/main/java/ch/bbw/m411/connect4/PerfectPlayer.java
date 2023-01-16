package ch.bbw.m411.connect4;

import java.util.Arrays;

public class PerfectPlayer extends Connect4ArenaMain.DefaultPlayer {


    private int bestPlay = -1;
    private int maxDepth;
    private final int originMaxDepth;

    // Current round to be played
    private int round = 0;


    private int lastMove = -1;

    private final static int MAX_REWARD = 10_000;
    private final static int MIN_REWARD = -MAX_REWARD;

    private final static int WIN_REWARD = 5_000;
    private final static int LOOSE_REWARD = -WIN_REWARD;

    public PerfectPlayer(int maxDepth) {
        this.maxDepth = 3;
        this.originMaxDepth = maxDepth;
    }

    /**
     * Generate the best moves possible based on the current board.
     * If the current function executed is "top level", bestMove gets set to the index of the move which makes most sense
     * to make
     *
     * @param beta     "ceiling" of bound
     * @param alpha    "floor" of bound
     * @param depth    Current depth of the algorithm
     * @param forColor For which player the current move should be made
     * @return a rating of how good the move is (i.E. if it's a winning move -> WIN_REWARD)
     */
    private int minMax(int beta, int alpha,
                       int depth, Connect4ArenaMain.Stone forColor) {
        if (maxDepth == depth) {
            if (Connect4ArenaMain.isWinning(board, forColor.opponent())) {
                return LOOSE_REWARD;
            }

            if (Connect4ArenaMain.isWinning(board, forColor)) {
                return WIN_REWARD;
            }
        } else {

            // Only have to check if the last move made the opponent win, we didn't make the last move, so it can only be
            // our opponent
            if (Connect4ArenaMain.isWinning(board, forColor.opponent(), lastMove)) {
                return LOOSE_REWARD;
            }
        }

        // Max-depth is reached
        if (depth == 0)
            return rate(forColor);

        var moves = generateMoves();

        // No moves are available, must be a draw
        if (moves.length == 0) {
            return rate(forColor);
        }

        int max = alpha;

        for (int move : moves) {
            // Testing out the current move
            board[move] = forColor;
            lastMove = move;
            int value =
                    -minMax(-max,
                            -beta,
                            depth - 1,
                            forColor.opponent());
            board[move] = null;
            // Debugging
            if (depth == maxDepth) {
                System.out.printf("At index [%d]: Value %d%n", move, value);
            }
            if (value > max) {
                max = value;
                // If this function execution is top-level set the best move to this index
                if (depth == this.maxDepth) {
                    this.bestPlay = move;
                }
                // Beta-cutoff
                if (max >= beta) {
                    break;
                }

            }
        }

        return max;
    }

    private final int[] moveOrder = new int[]{3, 2, 4, 5, 1, 0, 6};

    /**
     * Generate moves, prioritising the ones which are closest to the center.
     * `int[]` and {@link Arrays#copyOf(Object[], int)} are more performant than using a list, that's why we're using them
     * here
     *
     * @return an array containing all possible moves
     */
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


    /**
     * Rate the board based on how close the current player is to the center
     *
     * @param forStone for which stone the calculation should be made
     * @return the rating for the player based on the board
     */
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

    // The first SCALING_ROUNDS rounds aren't calculated at 100% max depth, it grows linear until maxDepth is reached
    private final static int SCALING_ROUNDS = 4;


    @Override
    int play() {
        bestPlay = -1;
        lastMove = -1;
        round++;


        // The first few Rounds don't have to be evaluated deeply
        if (round <= SCALING_ROUNDS) {
            maxDepth = (originMaxDepth / SCALING_ROUNDS) * round;
        } else {
            maxDepth = originMaxDepth;
        }

        minMax(MAX_REWARD, MIN_REWARD, this.maxDepth,
                myColor);
        if (bestPlay == -1)
            throw new RuntimeException("bestMove not set");
        return bestPlay;
    }
}
