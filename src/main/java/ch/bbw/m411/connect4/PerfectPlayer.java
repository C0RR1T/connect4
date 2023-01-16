package ch.bbw.m411.connect4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PerfectPlayer extends Connect4ArenaMain.DefaultPlayer {


    private Integer bestPlay = null;
    private final int maxDepth;

    private final static int MAX_REWARD = 10_000;
    private final static int MIN_REWARD = -MAX_REWARD;

    private final static int WIN_REWARD = 1_000;
    private final static int LOOSE_REWARD = -WIN_REWARD;


    private int winningCount = 0;
    private int loosingCount = 0;
    private int drawCount = 0;
    private int ratingCount = 0;

    private final HashMap<String, Integer> cache = new HashMap<>();

    public PerfectPlayer(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    private int minMax(int beta, int alpha,
                       int depth, Connect4ArenaMain.Stone forColor) {
        if (Connect4ArenaMain.isWinning(board, forColor)) {
            winningCount++;
            return WIN_REWARD;
        }
        if (Connect4ArenaMain.isWinning(board, forColor.opponent())) {
            loosingCount++;
            return LOOSE_REWARD;
        }

        var moves = generateMoves();

        if (moves.size() == 0 || depth == 0) {
            ratingCount++;
            return rate(forColor);
        }

        int max = alpha;

        for (var move : moves) {
            board[move] = forColor;
            var boardString =
                    Connect4ArenaMain.toDebugString(board);
            int value =
                    -Optional.<Integer>empty()
                            .orElse(minMax(-max,
                                    -beta,
                                    depth - 1,
                                    forColor.opponent()));
            if (!cache.containsKey(boardString))
                this.cache.put(boardString, value);
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

        if (maxDepth == depth) {
            System.out.printf("Winning Paths: %d%nLoosing Paths: " +
                            "%d%nDrawing Paths: %d%nRating Paths: %d%n",
                    winningCount, loosingCount, drawCount, ratingCount);
            winningCount = 0;
            loosingCount = 0;
            drawCount = 0;
            ratingCount = 0;
        }

        return max;
    }

    // Public because of tests
    public List<Integer> generateMoves() {
        var list = new ArrayList<Integer>();
        var temp = List.of(3, 2, 4, 5, 1, 0, 6);
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
        var opponent = forStone.opponent();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == forStone) {
                rating += checkRow(i, forStone);
            }
            if (board[i] == opponent) {
                rating -= checkRow(i, opponent);
            }
        }

        return rating;
    }

    private int checkRow(int i, Connect4ArenaMain.Stone forStone) {
        int count = 0;
        // check horizontal
        for (int j = i % 7; j < board.length; j += 7) {
            if (board[j] == forStone) {
                count++;
            } else {
                break;
            }
        }

        // check vertical
        for (int j = i; j < board.length; j += 7) {
            if (board[j] == forStone) {
                count++;
            } else {
                break;
            }
        }

        // check diagonal
        for (int j = i; j < board.length; j += 8) {
            if (board[j] == forStone) {
                count++;
            } else {
                break;
            }
        }
        for (int j = i; j < board.length; j += 6) {
            if (board[j] == forStone) {
                count++;
            } else {
                break;
            }
        }

        return count;
    }


    @Override
    int play() {
        bestPlay = null;
        minMax(MAX_REWARD, MIN_REWARD, this.maxDepth,
                myColor);

        return bestPlay;
    }
}
