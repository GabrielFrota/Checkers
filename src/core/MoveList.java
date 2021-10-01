package core;

import java.util.LinkedList;

public class MoveList extends LinkedList<Move> {
  private static final long serialVersionUID = 1L;
 
  public MoveList() {
    super();
  }
  
  @Override
  public boolean add(Move m) {
    var first = peek();
    if (first == null) 
      return super.add(m);
    if (first.steps.size() == m.steps.size()
        && (first.hasJump() == m.hasJump())) 
      return super.add(m);
    if (first.hasJump() && !m.hasJump()) 
      return false;
    if (first.steps.size() > m.steps.size()) 
      return false;
    clear();
    return super.add(m);
  }
}
