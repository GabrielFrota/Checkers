package core;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Checkers {
  
  static final AtomicBoolean blackPlays = new AtomicBoolean(true);
  static final AtomicInteger mode = new AtomicInteger();
  
  final Board board = new Board();
  final MainWindow window = new MainWindow(board);
  
  private void random() {
    var r = ThreadLocalRandom.current();
    while (true) {
      int i = r.nextInt(0, 8);
      int j = r.nextInt(0, 8);
      if (board.arr[i][j].piece <= 0)
        continue;
      var p = new Point(i, j);
      var l = board.validMoves(p, 0);
      if (l.isEmpty())
        continue;
      var m = l.get(r.nextInt(0, l.size()));
      board.move(m);
      break;
    }
  }
 
  private void play() throws InterruptedException {
    while (true) {
      synchronized (Checkers.blackPlays) {
        while (Checkers.blackPlays.get())
          Checkers.blackPlays.wait();
      }
      switch (mode.get()) {
      case 1:
        random();
        break;
      default:
        break;
      }
      window.repaintPanel();
    }
  }
  
  public static void main(String[] args) throws InterruptedException {
    var c = new Checkers();
    c.play();
  }
  
}
