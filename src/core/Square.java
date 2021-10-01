package core;

class Square {
  
  static class Piece { 
    static int NONE = 0;
    static int WHITE = 1;
    static int WHITE_KING = 2;
    static int BLACK = -1;
    static int BLACK_KING = -2;
  }
  
  static class Selection {
    static int NONE = 0;
    static int SELECTED = 1;
    static int VALID_MOVE = 2;
    static int STEP_MOVE = 3;
  }
  
  int background;
  int piece;
  int selection;
  
  Square(int b, int p) {
    background = b;
    piece = p;
    selection = 0;
  }
  
  Square(int b, int p, int s) {
    background = b;
    piece = p;
    selection = s;
  }
  
}
