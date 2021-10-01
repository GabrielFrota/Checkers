package core;

class Point {
  final int row;
  final int col;
  
  Point(int r, int c) {
    row = r;
    col = c;
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == this) 
      return true;
    if (!(o instanceof Point)) 
      return false;
    var p = (Point) o;
    return (this.row == p.row && this.col == p.col);
  }
}
