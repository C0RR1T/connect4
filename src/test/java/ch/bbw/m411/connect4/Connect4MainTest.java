package ch.bbw.m411.connect4;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class Connect4MainTest implements WithAssertions {

    protected Connect4ArenaMain newInstance() {
        return new Connect4ArenaMain();
    }

    Connect4ArenaMain.Stone[] fromString(String boardStr) {
        var board = boardStr.codePoints()
                .map(Character::toLowerCase)
                .filter(x -> List.of('x', 'o', '.')
                        .contains((char) x))
                .mapToObj(x -> x == 'x' ? Connect4ArenaMain.Stone.RED : (x == 'o' ? Connect4ArenaMain.Stone.BLUE : null))
                .toArray(Connect4ArenaMain.Stone[]::new);
        assertThat(board).hasSize(Connect4ArenaMain.WIDTH * Connect4ArenaMain.HEIGHT);
        return board;
    }

    AbstractBooleanAssert<?> assertThatXWin(String boardStr) {
        var board = fromString(boardStr);
        return assertThat(newInstance().isWinning(board, Connect4ArenaMain.Stone.RED)).as(Connect4ArenaMain.toDebugString(board));
    }

    @Test
    void isWin() {
        assertThatXWin("xxxx... ....... ....... .......").isTrue();
        assertThatXWin(".xxxx.. ....... ....... .......").isTrue();
        assertThatXWin("..xxxx. ....... ....... .......").isTrue();
        assertThatXWin("...xxxx ....... ....... .......").isTrue();
        assertThatXWin("...x... ...x... ...x... ...x...").isTrue();
        assertThatXWin("......x ......x ......x ......x").isTrue();
        assertThatXWin("xooo... .xoo... ..xo... ...x...").isTrue();
        assertThatXWin(".ooxo.. .oxoo.. .xxxx.. .......").isTrue();
        assertThatXWin(".ooxo.x .oxoo.. .ooxx.. .xxxx..").isTrue();
        assertThatXWin("oooo... xxxx... ....... .......").isTrue();
    }

    @Test
    void noWin() {
        assertThatXWin("....... ....... ....... .......").isFalse();
        assertThatXWin("xxx.xx. ....... ....... .......").isFalse();
        assertThatXWin("xxx.xxx xxx.xxx xxx.xxx .......").isFalse();
        assertThatXWin("xx.x.xx xx.x.xx xx.x.xx .......").isFalse();
        assertThatXWin("ooo.ooo xxx.xxx xxx.xxx xxx.xxx").isFalse();
        assertThatXWin("oo.o.oo xx.x.xx xx.x.xx xx.x.xx").isFalse();
        assertThatXWin("oooo... ....... ....... .......").isFalse();
        assertThatXWin("xxx.xx. xxx.xx. xxx.... o......").isFalse();
        assertThatXWin("xxxo... x.x.... x.o.... o.x....").isFalse();
    }

    @Test
    void inAGreedyBattleTheFirstPlayerWillWin() {
        var red = new Connect4ArenaMain.GreedyPlayer();
        var blue = new Connect4ArenaMain.GreedyPlayer();
        assertThat(newInstance().play(red, blue)).isEqualTo(red);
    }

    @Test
    void noPossibleMoves() {
        var player = new PerfectPlayer(1);
        var board = new Connect4ArenaMain.Stone[27];
        Arrays.fill(board,
                Connect4ArenaMain.Stone.RED);
        player.initialize(board, Connect4ArenaMain.Stone.RED);
        assertThat(player.generateMoves()).isEmpty();
    }

    @Test
    void possibleMoves() {
        var player = new PerfectPlayer(1);
        var board = new Connect4ArenaMain.Stone[27];
        board[3] = Connect4ArenaMain.Stone.RED;
        board[10] = Connect4ArenaMain.Stone.RED;
        board[17] = Connect4ArenaMain.Stone.RED;
        player.initialize(board, Connect4ArenaMain.Stone.RED);
        assertThat(player.generateMoves()).contains(24);
    }
}
