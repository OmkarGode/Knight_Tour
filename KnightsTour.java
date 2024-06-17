import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class KnightsTour {

  private static int N;
  private static int M;
  private boolean[][] visited;
  private String[] solution; // holds the solution steps (example: {"0, 0"})
  private Stack<KPanel> stack;
  private Color animationColor;
  private Thread thread;
  private Point[] moves; // Possible knight moves

  // *********************************************************************
  // constructors
  // *********************************************************************

  public KnightsTour(int size) {
    if (size < 1) {
      throw new IllegalArgumentException("Size must be at least 1.");
    }
    N = size;
    M = N * N - 1;
    visited = new boolean[size][size];
    solution = new String[M + 1];
    moves =
      new Point[] {
        new Point(2, 1),
        new Point(1, 2),
        new Point(-1, 2),
        new Point(-2, 1),
        new Point(-2, -1),
        new Point(-1, -2),
        new Point(1, -2),
        new Point(2, -1),
      };
  }

  /**
   * Initialize a default size board of 5 for the KnightsTour (used for testing).
   */
  public KnightsTour() {
    this(5);
  }

  public boolean move(int x, int y, int m) {
    // check if coordinate has passed off the board
    if (x < 0 || x >= N || y < 0 || y >= N) {
      return false;
    }

    // check if coordinate has already been visited
    if (visited[x][y] == true) {
      return false;
    }

    // valid move and knight has now made M moves; finished!
    if (m == M) {
      solution[m] = x + ", " + y;
      visited[x][y] = true;
      return true;
    }
    // this is a valid move, but a tour has not been completed. So, try all
    // moves that can be made from this location recursively.
    else {
      visited[x][y] = true;

      boolean result = false;

      // iterate over all possible knight moves
      for (int i = 0; i < moves.length; i++) {
        int newX = x + (int) moves[i].getX();
        int newY = y + (int) moves[i].getY();
        result = result || move(newX, newY, m + 1);
      }

      // one of the moves led to a completed tour, so this position is
      // part of a successful tour.
      if (result) {
        solution[m] = x + ", " + y;
      }

      // if none of the moves from this position led to a successful tour,
      // backtrack and try a different path
      visited[x][y] = false;
      return result;
    }
  }

  public boolean moveWithAnimation(int x, int y, int m, GUI gui) {
    // check if coordinate has passed off the board
    if (x < 0 || x >= N || y < 0 || y >= N) {
      return false;
    }

    // check if coordinate has already been visited
    if (visited[x][y] == true) {
      return false;
    }

    // valid move and knight has now made M moves; finished!
    if (m == M) {
      solution[m] = x + ", " + y;
      visited[x][y] = true;
      addPanel(gui.getPanels()[x + y * N], String.valueOf(m), gui);
      finishAnimation();
      return true;
    }
    // this is a valid move, but a tour has not been completed. So, try all
    // moves that can be made from this location recursively.
    else {
      visited[x][y] = true;
      addPanel(gui.getPanels()[x + y * N], String.valueOf(m), gui);

      // Get neighbors using Warnsdorff's Rule
      ArrayList<Point> neighbors = getNeighbors(new Point(x, y));
      neighbors.sort((p1, p2) ->
        Integer.compare(getNumNeighbors(p1), getNumNeighbors(p2))
      );

      boolean result = false;

      // iterate over all possible knight moves
      for (Point neighbor : neighbors) {
        int newX = (int) neighbor.getX();
        int newY = (int) neighbor.getY();
        result = result || moveWithAnimation(newX, newY, m + 1, gui);
      }

      // one of the moves led to a completed tour, so this position is
      // part of a successful tour.
      if (result) {
        solution[m] = x + ", " + y;
      }

      // if none of the moves from this position led to a successful tour,
      // backtrack and try a different path
      visited[x][y] = false;
      removePanel(gui.getPanels()[x + y * N], "", gui);
      return result;
    }
  }

  private ArrayList<Point> getNeighbors(Point p) {
    ArrayList<Point> neighbors = new ArrayList<>();
    for (Point move : moves) {
      Point next = new Point(
        (int) p.getX() + (int) move.getX(),
        (int) p.getY() + (int) move.getY()
      );
      if (isValidMove(next) && !visited[(int) next.getX()][(int) next.getY()]) {
        neighbors.add(next);
      }
    }
    return neighbors;
  }

  private int getNumNeighbors(Point p) {
    return getNeighbors(p).size();
  }

  private boolean isValidMove(Point p) {
    return p.getX() >= 0 && p.getX() < N && p.getY() >= 0 && p.getY() < N;
  }

  private void addPanel(final KPanel p, final String s, GUI gui) {
    SwingUtilities.invokeLater(() -> {
      // update previous panel to normal
      if (!stack.isEmpty()) {
        KPanel temp = stack.peek();
        temp.setBackground(temp.getDefaultColor());
        temp.paintAll(temp.getGraphics());
      }
      p.setBackground(animationColor);
      p.repaint(s);
      stack.push(p);
    });
    wait(gui.getWaitTime());
  }

  private void removePanel(final KPanel p, final String s, GUI gui) {
    SwingUtilities.invokeLater(() -> {
      // update prev panel to normal, and remove it.
      if (!stack.isEmpty()) {
        KPanel temp = stack.pop();
        temp.setBackground(temp.getDefaultColor());
        temp.paintAll(temp.getGraphics());
      }
      stack.peek().setBackground(animationColor);
      stack.peek().repaint(null, 0);
      p.repaint(s);
    });
    wait(gui.getWaitTime());
  }

  private void finishAnimation() {
    SwingUtilities.invokeLater(() -> {
      stack.peek().setBackground(stack.peek().getDefaultColor());
      stack.peek().paintAll(stack.peek().getGraphics());
    });
  }

  private void wait(int waitTime) {
    if (waitTime > 0) {
      try {
        Thread.sleep(waitTime);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void solveWithAnimation(
    final int x,
    final int y,
    final GUI gui,
    final JTextArea t
  ) {
    // setup fields
    stack = new Stack<>();
    animationColor = gui.getAnimationColor();

    // append info
    t.append(
      "board size:\t" +
      N +
      " x " +
      N +
      "\nstart position:\t(" +
      x +
      ", " +
      y +
      ")\nAnimating algorithm ...\n"
    );

    // run animation in a separate thread to allow scheduling.
    thread =
      new Thread(() -> {
        boolean solved = moveWithAnimation(x, y, 0, gui);
        if (solved) {
          t.append("a solution was found.");
        } else {
          t.append("no solution was found.");
        }
      });
    thread.start();
  }

  public Thread getAnimationThread() {
    return thread;
  }

  @SuppressWarnings("deprecation")
  public void setAnimationThread(Thread t) {
    if (thread != null) {
      thread.stop();
    }
    thread = t;
  }

  /**
   * Prints the board in the order it visited each vertex to the given
   * JTextArea, for convenience in displaying in a GUI.
   */
  public void printSteps(JTextArea textArea) {
    if (solution[0] == null) {
      textArea.append("(no solution)\n");
    } else {
      for (int m = 0; m <= M; m++) {
        textArea.append((m + 1) + ":\t" + solution[m] + "\n");
      }
      textArea.append("\n");
    }
  }

  public String[] getSolution() {
    return solution;
  }

  public int[][] getTour() {
    try {
      // build a temp array to generate the solution grid
      int[][] tour = new int[N][N];
      for (int m = 0; m <= M; m++) {
        String[] a = solution[m].split(", ");
        int x = Integer.parseInt(a[0]);
        int y = Integer.parseInt(a[1]);
        tour[x][y] = m;
      }
      return tour;
    } catch (NullPointerException e) {
      return new int[0][0];
    }
  }

  // *********************************************************************
  // MAIN
  // *********************************************************************
  public static void main(String[] args) {
    KnightsTour tour;
    int size, x, y;
    if (args.length == 1) {
      size = Integer.parseInt(args[0]);
      x = 0;
      y = 0;
    } else if (args.length == 3) {
      size = Integer.parseInt(args[0]);
      x = Integer.parseInt(args[1]);
      y = Integer.parseInt(args[2]);
    } else {
      size = 5;
      x = 0;
      y = 0;
    }
    tour = new KnightsTour(size);
    tour.solve(x, y, true);
    tour.printSteps();
    tour.printGrid();
  }

  public void solve(int x, int y, boolean print) {
    if (print) {
      System.out.print(
        "board size:\t" +
        N +
        " x " +
        N +
        "\nstart position:\t(" +
        x +
        ", " +
        y +
        ")\nSolving Knight's Tour ..."
      );
      if (move(x, y, 0)) {
        System.out.println("a solution was found.");
      } else {
        System.out.println("no solution was found.");
      }
    } else {
      move(x, y, 0);
    }
  }

  public boolean solve(int x, int y, JTextArea textArea) {
    textArea.append(
      "board size:\t" +
      N +
      " x " +
      N +
      "\nstart position:\t(" +
      x +
      ", " +
      y +
      ")\nSolving Knight's Tour ... "
    );
    if (move(x, y, 0)) {
      textArea.append("a solution was found.\n");
      return true;
    } else {
      textArea.append("no solution was found.\n");
      return false;
    }
  }

  public void printSteps() {
    if (solution[0] == null) {
      System.out.println("(no solution)");
    } else {
      for (int m = 0; m <= M; m++) {
        System.out.println((m + 1) + ":\t" + solution[m]);
      }
    }
  }

  public void printGrid() {
    int[][] tour = getTour();
    // iterate and print it to the terminal.
    for (int y = 0; y < tour.length; y++) {
      for (int x = 0; x < tour[y].length; x++) {
        if (tour[x][y] < 10) {
          System.out.print(" ");
        }
        System.out.print(tour[x][y] + "   ");
      }
      System.out.println("\n");
    }
  }

  /**
   * Prints the board in the order it visited each vertex.
   */
  public void printGrid(JTextArea textArea) {
    int[][] tour = getTour();
    // iterate and print it to the terminal.
    for (int y = 0; y < tour.length; y++) {
      for (int x = 0; x < tour[y].length; x++) {
        if (tour[x][y] < 10) {
          textArea.append(" ");
        }
        textArea.append(tour[x][y] + "   ");
      }
      textArea.append("\n\n");
    }
  }
}
