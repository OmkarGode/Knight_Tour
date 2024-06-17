import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
import java.util.Random;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI implements Runnable {

	private KnightsTour tour;
	private KPanel[] panels;

	private JFrame frame;
	private Container contentPane;
	private JMenuBar menuBar;
	private JPanel mainPanel;
	private Dimension panelDimension;
	public static final Dimension GRID_WINDOW_DIMENSION = new Dimension(500,
	        500);
	public static final Dimension TEXT_WINDOW_DIMENSION = new Dimension(500,
	        550);
	public static final Dimension MINIMUM_WINDOW_DIMENSION = new Dimension(300,
	        300);
	
	// display
	private ButtonGroup displayGroup;
	private JRadioButtonMenuItem textButton;
	private JRadioButtonMenuItem gridButton;

	// color
	private ButtonGroup colorGroup;

	// Text area
	private JTextArea textArea;
	private JScrollPane scrollPane;

	// Time slider
	private JPanel animationSliderPanel;
	private JSlider animationSlider;

	// Other fields
	private final int WINDOW_SIZE = 500;
	private int SIZE = 5;
	private int SCALE = WINDOW_SIZE / SIZE;
	private Color COLOR_1;
	private Color COLOR_2;
	private int KNIGHT_X;
	private int KNIGHT_Y;
	private Color ANIMATION_COLOR;
	private int WAIT_TIME;

	public static void main(String[] args) {
		startGUI(args);
	}

	public GUI() {
		setKnightStartPosition(0, 0);
	}

	public void run() {
		setupFrame();
	}

	public static void startGUI(String[] args) {
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			
		}
		finally {
			SwingUtilities.invokeLater(new GUI());
		}

	}

	public void repaintContents() {
		mainPanel.paintAll(mainPanel.getGraphics()); 
	}

	private void setupFrame() {
		frame = new JFrame("Knight Tour ");
		frame.setIconImage(getIconImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = frame.getContentPane();
		setColors(Color.LIGHT_GRAY, Color.GRAY);
		setAnimationColor(Color.YELLOW);
		WAIT_TIME = 150;
		setupContents();
		setupMenu();
		setWindowSize(GRID_WINDOW_DIMENSION);
		frame.setMinimumSize(MINIMUM_WINDOW_DIMENSION);

		// listener to resize font sizes
		frame.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				for (KPanel p : panels) {
					p.updateLabelFont();
					// p.paintAll(p.getGraphics());
				}

			}

			public void componentMoved(ComponentEvent e) {}

			public void componentShown(ComponentEvent e) {}

			public void componentHidden(ComponentEvent e) {}
		});

		drawGrid();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	private Image getIconImage() {
		int imageSize = 256;
		String key = "\u265E";
		BufferedImage image = new BufferedImage(imageSize, imageSize,
		        BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, imageSize, imageSize);
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif", Font.PLAIN, imageSize));
		g.drawString(key, 0, (imageSize * 4) / 5);

		return image;
	}
	private void setWindowSize(Dimension d) {
		frame.setSize(new Dimension(d));
	}

	private void setupContents() {
		mainPanel = new JPanel();
		mainPanel.setMinimumSize(MINIMUM_WINDOW_DIMENSION);
		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		contentPane.add(mainPanel);
		setupGrid(5);
		setupTextArea();

	}

	@SuppressWarnings("deprecation")
	private void resetState() {
		if (tour != null && tour.getAnimationThread() != null) {
			tour.getAnimationThread().stop();
			try {
				Thread.sleep(200);
			}
			catch (Exception ex) {}
		}
		tour = null;
		textArea.setText("");
		if (animationSliderPanel != null) {
			contentPane.remove(animationSliderPanel);
		}
		panelDimension = new Dimension(SCALE, SCALE);
		for (KPanel p : panels) {
			p.setLabel("");
			p.setPreferredSize(panelDimension);
			p.setBackground(p.getDefaultColor());
		}
		frame.pack();
		frame.paintAll(frame.getGraphics());
		
	}
	private void setKnightStartPosition(int x, int y) {
		if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
			throw new IndexOutOfBoundsException();
		}
		KNIGHT_X = x;
		KNIGHT_Y = y;
	}
	public KPanel[] getPanels() {
		return panels;
	}
	private void setupGrid(int size) {
		setGridSize(size);
		panelDimension = new Dimension(SCALE, SCALE);
		panels = new KPanel[SIZE * SIZE];
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				KPanel panel = new KPanel("");
				panel.setSize(SCALE, SCALE);
				panel.setPreferredSize(panelDimension);
				panel.setLabelFont(new Font("SansSerif", Font.PLAIN,
				        (int) (SCALE / 1.8)));
				panels[x + y * SIZE] = panel;
				// draw step zero where the knight is positioned.
				if (x == KNIGHT_X && y == KNIGHT_Y) {
					panel.setLabel(String.valueOf(0));
				}
			}
		}
	}
	private void setGridSize(int size) {
		if (size != 0) {
			SIZE = size;
			SCALE = WINDOW_SIZE / SIZE;
		}
	}

	private void drawGrid() {
	 	// setWindowSize(500, 500);
		setWindowSize(GRID_WINDOW_DIMENSION);
		mainPanel.removeAll();
		GridLayout layout = new GridLayout(SIZE, SIZE);
		mainPanel.setLayout(layout); // (rows x columns)
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				mainPanel.add(panels[x + y * SIZE]);
			}
		}
		frame.pack();
		updateGridColors();
	}
	private void setupTextArea() {
		textArea = new JTextArea();
		textArea.setEditable(true);
		textArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
		textArea.setTabSize(4);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(TEXT_WINDOW_DIMENSION);
		scrollPane
		        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
		        .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(BorderFactory.createCompoundBorder(
		        // BorderFactory.createEmptyBorder(10, 10, 10, 10),
		        BorderFactory.createEmptyBorder(5, 5, 5, 5),
		        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)));
	}

	private void drawTextArea() {
		mainPanel.removeAll();
		mainPanel.setLayout(new BorderLayout(5, 5));
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		frame.pack();
	}

	private void setColors(Color c1, Color c2) {
		COLOR_1 = c1;
		COLOR_2 = c2;
	}

	private void updateGridColors() {
		updateGridColors(null, null);
	}

	private void updateGridColors(Color c1, Color c2) {
		if (c1 != null && c2 != null) {
			setColors(c1, c2);
		}
		Color firstColor;
		Color secondColor;
		for (int y = 0; y < SIZE; y++) {
			// Alternate starting colors by row
			if (y % 2 == 0) {
				firstColor = COLOR_1;
				secondColor = COLOR_2;
			}
			else {
				firstColor = COLOR_2;
				secondColor = COLOR_1;
			}
			for (int x = 0; x < SIZE; x++) {
				// alternate between firstColor & secondColor
				if (x % 2 == 0) {
					panels[x + y * SIZE].setDefaultColor(firstColor);
				}
				else {
					panels[x + y * SIZE].setDefaultColor(secondColor);
				}
			}
		}
	}

	private void setupMenu() {
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		menuBar.add(createFileMenu());
		menuBar.add(createRunMenu());
		menuBar.add(createDisplayMenu());
		menuBar.add(createColorMenu());
		
	}

	private JMenu createFileMenu() {
		JMenu topMenu = new JMenu("File");
		// *********************************************
		// 1.1) - create "New" dialog to select inputs
		// *********************************************
		JMenuItem menuItem1 = new JMenuItem("New...");
		menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit
		        .getDefaultToolkit().getMenuShortcutKeyMask()));
		menuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JPanel p = new JPanel();
				p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

				JLabel boardLabel = new JLabel("board size:");
				boardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				p.add(boardLabel);
				JTextField sizeField = new JTextField(String.valueOf(5), 2);
				sizeField.setAlignmentX(Component.CENTER_ALIGNMENT);
				sizeField.setHorizontalAlignment(JTextField.CENTER);
				p.add(sizeField);

				JLabel knightLabel = new JLabel("Knight Position:");
				knightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				p.add(knightLabel);

				JPanel posPanel = new JPanel();
				posPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
				posPanel.setLayout(new FlowLayout());
				p.add(posPanel);
				posPanel.add(new JLabel(" x: "));
				JTextField xField = new JTextField(String.valueOf(1), 2);
				xField.setHorizontalAlignment(JTextField.CENTER);
				posPanel.add(xField);


				posPanel.add(new JLabel(" y: "));
				JTextField yField = new JTextField(2);
				// yField.setText(String.valueOf(knightY));
				yField.setText(String.valueOf(1));
				yField.setHorizontalAlignment(JTextField.CENTER);
				posPanel.add(yField);

				int selection = JOptionPane.showConfirmDialog(frame, p,
				        "Create a new board", JOptionPane.OK_CANCEL_OPTION,
				        JOptionPane.PLAIN_MESSAGE);

				if (selection == JOptionPane.OK_OPTION) {
					try {
						int size = Integer.parseInt(sizeField.getText());
						setGridSize(size);
						setKnightStartPosition(
						        Integer.parseInt(xField.getText()) - 1,
						        Integer.parseInt(yField.getText()) - 1);
						resetState();
						setupGrid(size);
						if (gridButton.isSelected()) {
							drawGrid();
						}
						repaintContents();

					}
				
					catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(contentPane,
						        "Input must be a number.", "Error",
						        JOptionPane.ERROR_MESSAGE);
						this.actionPerformed(e);
					}
					catch (IndexOutOfBoundsException ex) {
						JOptionPane.showMessageDialog(contentPane,
						        "x/y values must lie within the board size.",
						        "Error", JOptionPane.ERROR_MESSAGE);
						this.actionPerformed(e);
					}
				}
			}
		});
		topMenu.add(menuItem1);

		JMenuItem menuItem2 = new JMenuItem("Reset");
		menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit
		        .getDefaultToolkit().getMenuShortcutKeyMask()));
		menuItem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetState();
				setupGrid(SIZE);
				if (gridButton.isSelected()) {
					drawGrid();
				}
				repaintContents();
			}
		});
		topMenu.add(menuItem2);
		topMenu.add(new JSeparator());
		JMenuItem menuItem3 = new JMenuItem("Quit");
		menuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit
		        .getDefaultToolkit().getMenuShortcutKeyMask()));
		menuItem3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		topMenu.add(menuItem3);

		return topMenu;
	}
	private JMenu createRunMenu() {
		JMenu topMenu = new JMenu("Run");

		// 1) - create "Knight's Tour
		JMenuItem menuItem1 = new JMenuItem("Solve Knight's Tour");
		menuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, Toolkit
		        .getDefaultToolkit().getMenuShortcutKeyMask()));
		menuItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solveAndPrintKnightsTour();
				repaintContents();
			}
		});
		topMenu.add(menuItem1);
		topMenu.add(new JSeparator());

		// 2) - create "Solve With Animation"
		JMenuItem menuItem2 = new JMenuItem("Animate Algorithm ...");
		menuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit
		        .getDefaultToolkit().getMenuShortcutKeyMask()));
		menuItem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// note: the slider dialog updates WAIT_TIME
				int i = showSliderDialog("Animate Algorithm ...");
				if (i == JOptionPane.OK_OPTION) {
					resetState();
					animateKnightsTour(WAIT_TIME);
				}
			}
		});
		topMenu.add(menuItem2);
		JMenuItem menuItem3 = new JMenuItem("Animate Solution ...");
		menuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit
		        .getDefaultToolkit().getMenuShortcutKeyMask()));
		menuItem3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// note: the slider dialog updates WAIT_TIME
				int i = showSliderDialog("Animate Solution ...");
				if (i == JOptionPane.OK_OPTION) {
					resetState();
					animateSolution();
				}
			}
		});
		topMenu.add(menuItem3);

		return topMenu;
	}

	private int showSliderDialog(String title) {
		return JOptionPane.showConfirmDialog(frame, getTimeSliderPanel(),
		        title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

	private JPanel getTimeSliderPanel() {
		// setup slider panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel label = new JLabel("time for each step (In milliseconds):");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(label);
		final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 100);
		slider.setAlignmentX(Component.CENTER_ALIGNMENT);
		slider.setMinorTickSpacing(50);
		slider.setMajorTickSpacing(250);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setValue(WAIT_TIME);
		final String ms = " ms";
		final JLabel bottomLabel = new JLabel(String.valueOf(WAIT_TIME) + ms);
		bottomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				WAIT_TIME = slider.getValue() + 1;
				bottomLabel.setText(String.valueOf(WAIT_TIME - 1) + ms);
			}

		});
		panel.add(slider);
		panel.add(bottomLabel);

		return panel;
	}

	private void solveAndPrintKnightsTour() {
		resetState();
		if (gridButton.isSelected()) {
			drawGrid();
		}
		frame.pack();
		repaintContents();
		tour = new KnightsTour(SIZE);
		boolean solved = tour.solve(KNIGHT_X, KNIGHT_Y, textArea);
		if (solved) {
			tour.printSteps(textArea);
			tour.printGrid(textArea);
			drawEachStep(0);
		}
	}
	private JMenu createDisplayMenu() {
		JMenu topMenu = new JMenu("Display");
		displayGroup = new ButtonGroup();
		textButton = new JRadioButtonMenuItem("Text");
		textButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawTextArea();
				repaintContents();
			}
		});
		displayGroup.add(textButton);

		gridButton = new JRadioButtonMenuItem("Grid", true);
		gridButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawGrid();
				repaintContents();
			}
		});
		displayGroup.add(gridButton);
		
		final JRadioButtonMenuItem button3 = new JRadioButtonMenuItem(
		        "show visits as " + KPanel.KNIGHT_STRING);
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (button3.isSelected()) {
					KPanel.displayVisitedAsDot();
				}
				else {
					KPanel.displayVisitedAsText();
				}
				if (panels != null) {
					for (KPanel p : panels) {
						p.updateLabelFont();
						p.repaint(p.getLabelText());
					}
				}
			}
		});

		topMenu.add(gridButton);
		topMenu.add(textButton);
		topMenu.add(new JSeparator());
		topMenu.add(button3);

		return topMenu;
	}

	
	private JMenu createColorMenu() {
		JMenu topMenu = new JMenu("Colors");

		// 1) Make "Gray" radio button
		JRadioButtonMenuItem button1 = new JRadioButtonMenuItem("Gray", true);
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (panels != null) {
					updateGridColors(Color.LIGHT_GRAY, Color.GRAY);
					setAnimationColor(Color.YELLOW);
					repaintContents();
				}
			}
		});

		// 2) Make "Random" radio button
		JRadioButtonMenuItem button2 = new JRadioButtonMenuItem("Random");
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (panels != null) {
					Random rand = new Random();
					updateGridColors(new Color(rand.nextInt()),
					        new Color(rand.nextInt()));
					setAnimationColor(new Color(rand.nextInt()));
					repaintContents();
				}
			}
		});

		// 3) - organize buttons and add to containers
		colorGroup = new ButtonGroup();
		colorGroup.add(button1);
		colorGroup.add(button2);
		topMenu.add(button1);
		topMenu.add(button2);

		return topMenu;
	}

	/**
	 * Animate the KnightsTour solve() method, seeing every iteration.
	 */
	private void animateKnightsTour(int waitTime) {
		resetState();
		gridButton.setSelected(true);
		drawGrid();
		frame.pack();
		repaintContents();
		tour = new KnightsTour(SIZE);

		animationSliderPanel = getAnimationSliderPanel();
		contentPane.add(animationSliderPanel, BorderLayout.SOUTH);
		animationSlider.requestFocusInWindow();
		frame.pack();
		tour.solveWithAnimation(KNIGHT_X, KNIGHT_Y, this, textArea);
	}
	public JPanel getAnimationSliderPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createRaisedBevelBorder());
		animationSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000,
		        WAIT_TIME - 1);
		animationSlider.setMinorTickSpacing(25);
		animationSlider.setMajorTickSpacing(250);
		animationSlider.setPaintTicks(true);
		animationSlider.setPaintTrack(true);
		animationSlider.setPaintLabels(true);
		animationSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
		animationSlider.setSnapToTicks(true);
		animationSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				WAIT_TIME = animationSlider.getValue() + 1;
			}
		});

		JLabel fLabel = new JLabel("Faster");
		fLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel sLabel = new JLabel("Slower");
		sLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(fLabel);
		panel.add(animationSlider);
		panel.add(sLabel);

		return panel;
	}

	public int getWaitTime() {
		return WAIT_TIME;
	}

	/**
	 * Draws the solution one step at a time, starting with the Knight's first
	 * position, followed by the second, etc. until every area is visited.
     */
	private void animateSolution() {
		resetState();
		gridButton.setSelected(true);
		drawGrid();

		animationSliderPanel = getAnimationSliderPanel();
		contentPane.add(animationSliderPanel, BorderLayout.SOUTH);
		frame.pack();

		repaintContents();
		tour = new KnightsTour(SIZE);
		boolean solved = tour.solve(KNIGHT_X, KNIGHT_Y, textArea);
		if (solved) {
			tour.printSteps(textArea);
			tour.printGrid(textArea);
			tour.setAnimationThread(new Thread() {
				public void run() {
					drawEachStep();
				}
			});
			tour.getAnimationThread().start();
		}
	}

	private KPanel prev = null;

	private void drawEachStep() {
		drawEachStep(WAIT_TIME);
	}
	private void drawEachStep(int waitTime) {
		String[] solution = tour.getSolution();
		if (solution != null) {
			for (int i = 0; i < solution.length; i++) {
				String[] pos = (solution[i].split(", "));
				int x = Integer.parseInt(pos[0]);
				int y = Integer.parseInt(pos[1]);

				// 1) - Animated drawing (updated every step)
				if (waitTime > 0) {
					final int i2 = i;
					final int x2 = x;
					final int y2 = y;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (i2 != 0) {
								prev.setBackground(prev.getDefaultColor());
								prev.paintAll(prev.getGraphics());
							}
							panels[x2 + y2 * SIZE]
							        .setBackground(ANIMATION_COLOR);
							panels[x2 + y2 * SIZE].repaint(String.valueOf(i2));
							prev = panels[x2 + y2 * SIZE];
						}
					});
					try {
						Thread.sleep(WAIT_TIME);
					}
					catch (Exception e) {}
				}

				// 2) - Regular drawing (updated only at the end)
				else {
					panels[x + y * SIZE].setLabel(String.valueOf(i));
				}
			}
		}
		// 3) - finally, clean up final frame if necessary
		if (prev != null) {
			prev.setBackground(prev.getDefaultColor());
			
		}

	}

	public Color getAnimationColor() {
		return ANIMATION_COLOR;
	}
	public void setAnimationColor(Color c) {
		ANIMATION_COLOR = c;
	}
}