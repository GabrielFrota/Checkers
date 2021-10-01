package core;

class Board {
  
  final Square[][] arr = new Square[8][8];
    
  private Point selected;
  private MoveList moves;
  
  private void odd(Square[] dest, int piece) {
    for (int col = 0; col < dest.length; col++) {
      if (col % 2 != 0)
        dest[col] = new Square(-1, piece);
      else
        dest[col] = new Square(1, 0);
    }
  }
  
  private void even(Square[] dest, int piece) {
    for (int col = 0; col < dest.length; col++) {
      if (col % 2 == 0) 
        dest[col] = new Square(-1, piece);
      else
        dest[col] = new Square(1, 0);
    }
  }
  
  Board() {
    odd(arr[0], -1);
    even(arr[1], -1);
    odd(arr[2], -1);
    even(arr[3], 0);
    odd(arr[4], 0);
    even(arr[5], 1);
    odd(arr[6], 1);
    even(arr[7], 1);
  }
  
  private void clearSelection() {
    if (selected != null) {
      arr[selected.row][selected.col].selection = 0;
      selected = null;
    }
    for (var m : moves) {
      for (var s : m.steps) 
        arr[s.dest.row][s.dest.col].selection = 0;   
    }
    moves.clear();
  }
  
  private void setSelection() {
    for (var m : moves) {
      var s = m.steps.getLast(); 
      arr[s.dest.row][s.dest.col].selection = 2;   
    }
    arr[selected.row][selected.col].selection = 1;
  }
  
  public void selected(Point p) {
    if (selected != null) 
      clearSelection();
    selected = p;
    moves = validMoves(p, 0);
    setSelection();
  }
  
  public Point selected() {
    return selected;
  }
  
  public int value() {
    int cnt = 0;
    for (int i = 0; i < arr.length; i++) {
      for (int j = 0; j < arr[i].length; j++) {
        var p = arr[i][j].piece;
        if (p != 0)
          cnt += p;
      }
    }
    return -cnt;
  }
      
  public void move(Move m) {
    for (var s : m.steps) {
      var sqSrc = arr[s.src.row][s.src.col];
      var sqDest = arr[s.dest.row][s.dest.col];
      if (s.jumped != null) 
        s.jumped.piece = 0;
      sqDest.piece = sqSrc.piece;
      sqSrc.piece = 0;
      sqSrc.selection = 0;
      if (sqDest.piece < 0 && s.dest.row == 7) sqDest.piece = -2;
      if (sqDest.piece > 0 && s.dest.row == 0) sqDest.piece = 2;
    }
    clearSelection();
    synchronized (Checkers.blackPlays) { 
      Checkers.blackPlays.set(!Checkers.blackPlays.get());
      Checkers.blackPlays.notify();
    }
  }
  
  public void move(Point src, Point dest) {
    for (var m : moves) {
      if (m.steps.getFirst().src.equals(src) 
          && m.steps.getLast().dest.equals(dest)) {
            move(m);
            return;
      }
    }
    throw new IllegalArgumentException("src and dest Points are not in the moves list. Something is wrong");
  }
  
  private boolean tryMoves(MoveList l, Point p, int rowDest, int colDest) {
    if (rowDest < 0 || rowDest > 7
        || colDest < 0 || colDest > 7)
      return false;
    
    if (arr[p.row][p.col].piece != 0 && arr[rowDest][colDest].piece == 0) {
      var s = new Move.Step(p, new Point(rowDest, colDest));
      l.add(new Move(s));
      return true;
    }
    
    int rowJump = rowDest - Integer.signum(p.row - rowDest);
    int colJump = colDest - Integer.signum(p.col - colDest);
    if (rowJump < 0 || rowJump > 7
        || colJump < 0 || colJump > 7)
      return false;
    if (Integer.signum(arr[p.row][p.col].piece) == Integer.signum(arr[rowDest][colDest].piece))
      return false;
    
    if (arr[rowDest][colDest].piece != 0 && arr[rowJump][colJump].piece == 0) {
      var s = new Move.Step(p, new Point(rowJump, colJump), arr[rowDest][colDest]);
      l.add(new Move(s));
    }    
    return false;
  }
  
  public MoveList validMoves(Point p, int piece) {
    var sq = arr[p.row][p.col];
    var prev = sq.piece;
    if (piece != 0) sq.piece = piece;
    var l = new MoveList();
    
    if (sq.piece == -1) {
      tryMoves(l, p, p.row + 1, p.col - 1);
      tryMoves(l, p, p.row + 1, p.col + 1);     
    } 
    else if (sq.piece == 1) {
      tryMoves(l, p, p.row - 1, p.col - 1);
      tryMoves(l, p, p.row - 1, p.col + 1);
    }   
    else if (sq.piece == -2 || sq.piece == 2) {
      for (int i = p.row - 1, j = p.col + 1; i >= 0 && j <= 7; i--, j++) {
        if (!tryMoves(l, p, i, j))
          break;
      }
      for (int i = p.row + 1, j = p.col + 1; i <= 7 && j <= 7; i++, j++) {
        if (!tryMoves(l, p, i, j))
          break;
      }
      for (int i = p.row + 1, j = p.col - 1; i <= 7 && j >= 0; i++, j--) {
        if (!tryMoves(l, p, i, j))
          break;
      }
      for (int i = p.row - 1, j = p.col - 1; i >= 0 && j >= 0; i--, j--) {
        if (!tryMoves(l, p, i, j))
          break;
      }
    }
    var ll = new MoveList();
    for (var m : l) {
      if (m.hasJump()) {
        var jumped = m.steps.getLast().jumped;
        int val = jumped.piece;
        for (var s : m.steps)
          s.jumped.piece = 0;
        var lll = validMoves(m.steps.getLast().dest, sq.piece);
        for (var s : m.steps)
          s.jumped.piece = val;
        for (var mm : lll) {
          if (mm.hasJump()) {
            for (var s : m.steps)
              mm.steps.addFirst(s);
            ll.add(mm);
          }      
        } 
      }
    }
    if (!ll.isEmpty()) l = ll;
    if (piece != 0) sq.piece = prev;
    return l;
  }
  
}
