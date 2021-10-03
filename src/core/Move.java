package core;

import java.util.LinkedList;

class Move {  
  
  static class Step {
    final Point src;
    final Point dest;
    final Point jumped;
    
    Step(Point s, Point d) {
      src = s;
      dest = d;
      jumped = null;
    }
    Step(Point s, Point d, Point j) {
      src = s;
      dest = d;
      jumped = j;
    }
  }
  
  final LinkedList<Step> steps;
  
  Move(LinkedList<Step> s) {
    steps = s;
  }
  
  Move(Step s) {
    var l = new LinkedList<Step>();
    l.add(s);
    steps = l;
  }
  
  boolean hasJump() {
    for (var s : steps) {
      if (s.jumped != null)
        return true;
    }
    return false;
  }
  
}
