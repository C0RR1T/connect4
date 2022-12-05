package ch.bbw.m411.connect4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        return (int) Arrays.stream(this.board)
                .filter(Objects::isNull)
                .count();
    }

    private List<Integer> generateMoves() {
        var list = new ArrayList<Integer>();
        for (int i = 0; i < 7; i++) {
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
            return Integer.MAX_VALUE - 1;
        else if (Connect4ArenaMain.isWinning(board, forStone.opponent()))
            return Integer.MIN_VALUE + 1;

        var rating = 0;
        var myHorizontal = 0;
        var opponentHorizontal = 0;
        var emptyFields = countEmpty();
        for (int i = board.length - 1; i > 0; i--) {
            // Horizontal reset
            if (i == 20 || i == 13 || i == 6) {
                myHorizontal = 0;
                opponentHorizontal = 0;
                rating -= ratingOfRow(opponentHorizontal);
                rating += ratingOfRow(myHorizontal);
            }

            var currentStone = board[i];

            if (currentStone == forStone) {
                myHorizontal++;
                rating -= ratingOfRow(opponentHorizontal);
                opponentHorizontal = 0;
            } else if (currentStone == forStone.opponent()) {
                opponentHorizontal++;
                rating += ratingOfRow(myHorizontal);
                myHorizontal = 0;
            }


            switch (i % 7) {
                case 0, 6 -> rating += calcRating(currentStone, forStone, 1,
                        emptyFields);
                case 1, 5 -> rating += calcRating(currentStone, forStone, 2,
                        emptyFields);
                case 2, 4 -> rating += calcRating(currentStone, forStone, 3,
                        emptyFields);
                case 3 -> rating +=
                        calcRating(currentStone, forStone, 4, emptyFields);
            }

            if (i > 20) {
                rating += rowCounting((cur) -> cur - 7, i, forStone);

                if (i <= 24) {
                    rating += rowCounting((cur) -> cur - 7 + 1, i, forStone);
                } else {
                    rating += rowCounting((cur) -> cur - 7 - 1, i, forStone);
                }
            }
        }
        return rating;
    }

    private int ratingOfRow(int amount) {
        return (int) Math.pow(amount, 2);
    }

    private int calcRating(Connect4ArenaMain.Stone stone,
                           Connect4ArenaMain.Stone forColor, int amount,
                           int empty) {
        var newAmount = (int) ((1.0 / (empty / 20.0)) * 5.0);
        if (stone == forColor)
            return newAmount;
        else if (stone == forColor.opponent())
            return -amount;
        else return 0;
    }

    private int rowCounting(Step step, int origin,
                            Connect4ArenaMain.Stone forColor) {
        int rating = 0;
        var myCount = 0;
        var opponentCount = 0;
        for (int i = origin; i >= 0; i = step.nextStep(i)) {
            var currentStone = board[i];

            if (currentStone == forColor) {
                myCount++;
                rating -= ratingOfRow(opponentCount);
                opponentCount = 0;
            } else if (currentStone == forColor.opponent()) {
                opponentCount++;
                rating += ratingOfRow(myCount);
                myCount = 0;
            } else {
                rating += ratingOfRow(myCount);
                myCount = 0;
                rating -= ratingOfRow(opponentCount);
                opponentCount = 0;
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
        minMax(Integer.MAX_VALUE, Integer.MIN_VALUE, this.maxDepth, myColor);
        return bestPlay;
    }
}
