package core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class MainWindow extends JFrame {
  private static final long serialVersionUID = 1L;
  
  private class DrawingPanel extends JPanel {  
    private static final long serialVersionUID = 1L;

    public DrawingPanel() {
      super();
      addMouseListener(new MouseAdapter() {
        @Override 
        public void mouseClicked(MouseEvent e) {
          int row = e.getY() / 100;
          int col = e.getX() / 100;
          var sq = board.arr[row][col];
          
          if (Checkers.mode.get() == 0) {
            if (sq.piece != 0) {
              board.selected(new Point(row, col));
            } else if (sq.selection == 2) {
              board.move(board.selected(), new Point(row, col));           
            } else
              return;
            repaint();
          } else {
            if (!Checkers.blackPlays.get())
              return;       
            if (sq.piece == -1 || sq.piece == -2) {
              board.selected(new Point(row, col));
            } else if (sq.selection == 2) {
              board.move(board.selected(), new Point(row, col));
            } else 
              return;
            repaint();
          }
        }
      });
    }
    
    @Override 
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      
      for (int i = 0; i < board.arr.length; i++) {
        int currY = i * 100;
        for (int j = 0; j < board.arr[i].length; j++) {
          int currX = j * 100;
          
          var sq = board.arr[i][j];
          g2d.setColor(sq.background == -1 ? Color.GRAY : Color.LIGHT_GRAY);
          g2d.fillRect(currX, currY, 100, 100);
          if (sq.piece != 0) {
            g2d.setColor(sq.piece < 0 ? Color.BLACK : Color.WHITE);
            g2d.fillOval(currX + 10, currY + 10, 80, 80);
            if (sq.selection == 1) {
              g2d.setColor(Color.RED);
              g2d.setStroke(new BasicStroke(4));
              g2d.drawOval(currX + 10, currY + 10, 80, 80);
            }
            if (sq.piece == 2 || sq.piece == -2) {
              g2d.setColor(Color.RED);
              g2d.setStroke(new BasicStroke(4));
              g2d.drawLine(currX + 30, currY + 30, currX + 70, currY + 70);
            }
          } else if (sq.selection == 2) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(4));
            g2d.fillOval(currX + 40, currY + 40, 20, 20);
          }
        }
      }
      label2.setText(Integer.toString(board.value()));
    }   
  }
  
  private final JMenuBar menuBar = new JMenuBar();
  private final JMenu menuOptions = new JMenu("Options");
  private final JMenuItem menuItem0 = new JMenuItem("Testing");
  private final JMenuItem menuItem1 = new JMenuItem("Random");
  private final JMenuItem menuItem2 = new JMenuItem("Minimax");
  private final JLabel label1 = new JLabel("Board value: ");
  private final JLabel label2 = new JLabel();
  private final Color defaultColor = UIManager.getColor("JMenuItem.background");
  private final DrawingPanel drawPanel = new DrawingPanel();
  private final Board board;
  
  private void itemHandler(JMenuItem item, int mode) {
    for (Component c : item.getParent().getComponents()) {
      if (c instanceof JMenuItem && c.getBackground() == Color.RED) {
        c.setBackground(defaultColor);
        break;
      }
    }
    item.setBackground(Color.RED);
    Checkers.mode.set(mode);
  }
  
  public void repaintPanel() {
    drawPanel.repaint();
  }

  public MainWindow(Board b) {    
    super();
    board = b;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Insets in = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment
      .getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setSize(screenSize.width - in.left - in.right, screenSize.height - in.top - in.bottom);
    getRootPane().setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0)); 
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setTitle("Checkers Game");
    setLocationByPlatform(true);
    setLayout(new FlowLayout());
    
    setJMenuBar(menuBar);
    menuBar.add(menuOptions);
    menuOptions.add(menuItem0);
    menuOptions.add(menuItem1);
    menuOptions.add(menuItem2);
    menuBar.add(label1);
    menuBar.add(label2);
    menuItem0.addActionListener(e -> itemHandler(menuItem0, 0));
    menuItem1.addActionListener(e -> itemHandler(menuItem1, 1));
    menuItem2.addActionListener(e -> itemHandler(menuItem2, 2));
    label1.setFont(label1.getFont().deriveFont(12.0f));
    label2.setFont(label2.getFont().deriveFont(12.0f));
    drawPanel.setPreferredSize(new Dimension(800, 800));
    add(drawPanel);
    setVisible(true);
    menuItem0.doClick();
  }
  
}
