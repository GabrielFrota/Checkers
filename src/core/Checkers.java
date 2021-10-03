package core;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;

public class Checkers {
  
  static final AtomicBoolean blackPlays = new AtomicBoolean(true);
  static final AtomicInteger mode = new AtomicInteger();
  
  final Board board = new Board();
  final MainWindow window = new MainWindow(board);
  
  private int random() {
    var moves = new ArrayList<Move>();     
    for (int i = 0; i < board.arr.length; i++) {
      for (int j = 0; j < board.arr[i].length; j++) {
        if (board.arr[i][j].piece <= 0)
          continue;        
        var p = new Point(i, j);
        var l = board.validMoves(p, 0);
        for (var m : l)
          moves.add(m);
      }
    }
    if (moves.size() == 0)
      return Integer.MIN_VALUE;
    var r = ThreadLocalRandom.current();
    var m = moves.get(r.nextInt(0, moves.size()));
    board.move(m, window);
    return 0;
  }
  
  /*
   * when depth is even white plays for MAX value
   * when depth is odd black plays for MIN value
   * 
   * depth 11 is the maximum depth that my machine can handle in a reasonable time
   */
  private int minimax(Board curr, int depth, int alpha, int beta) {
    if (depth == 11 || curr.blackCnt() == 0)
      return curr.evaluate();
    int maxVal = Integer.MIN_VALUE;
    int minVal = Integer.MAX_VALUE;
    Move maxMove = null;

  outer:
    for (int i = 0; i < curr.arr.length; i++) {
      for (int j = 0; j < curr.arr[i].length; j++) {
        
        if (depth % 2 == 0 && curr.arr[i][j].piece <= 0) 
          continue; // white player skips black pieces 
        if (depth % 2 != 0 && curr.arr[i][j].piece >= 0)
          continue; // black player skips white pieces
        
        var p = new Point(i, j);
        var moves = curr.validMoves(p, 0);
        if (moves.isEmpty())
          continue;
        for (var m : moves) {
          var b = new Board(curr.arr, curr.whiteCnt(), curr.blackCnt());
          b.move(m);
          int val = minimax(b, depth + 1, alpha, beta);
          if (depth % 2 == 0 && val > maxVal) {
            // white player is max when depth is even
            maxVal = val;
            maxMove = m;
            alpha = val;
          }
          if (depth % 2 != 0 && val < minVal) {
            // black player is min when depth is odd
            minVal = val;
            beta = val;
          }
          if (beta <= alpha)
            break outer;
        }
      }
    }
    if (depth != 0 && depth % 2 == 0)
      return maxVal;
    if (depth != 0 && depth % 2 != 0)
      return minVal;  
    
    // only reaches here when returning from the first minimax call from play function.
    // updates the board drawn by the GUI if a move was found.
    if (maxMove != null) 
      curr.move(maxMove, window);
    return maxVal; 
  }

  /*
   * the AI is always the white player, and the human is always the black player.
   * this thread will play the white move and the black move comes from the GUI thread.
   * black player starts the game always, according to checkers rules.
   */
  private void play() throws InterruptedException {
    while (true) {
      synchronized (Checkers.blackPlays) {
        while (Checkers.blackPlays.get())
          Checkers.blackPlays.wait();
      }
      int val = 0;
      switch (mode.get()) {
      case 1:
        val = random();
        break;
      case 2:
        val = minimax(board, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        break;
      }
      if (val == Integer.MIN_VALUE)
        JOptionPane.showMessageDialog(null, "Black wins");
      else if (board.blackCnt() == 0)
        JOptionPane.showMessageDialog(null, "White wins");
      window.repaintPanel();
      Checkers.blackPlays.set(true);
    }
  }
  
  public static void main(String[] args) throws InterruptedException {
    var c = new Checkers();
    c.play();
  }
  
}
