import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class KPanel extends JPanel {
	private static final long serialVersionUID = -6155459401090631351L;
	public static final String KNIGHT_STRING = "\u265E"; // chess piece
	private static boolean DISPLAY_DOT = false;
	private JLabel label;
	private String labelText;
	private Color defaultColor;
	public KPanel(String text) {
		super(new BorderLayout(), true);
		setLabel(text);
	}

	/**
	 * Create a new KPanel with an empty label.
	 */
	public KPanel() {
		this(null);
	}
	public void setLabel(String text) {
		if (text != null) {
			if (label == null) {
				label = new JLabel();
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setVerticalAlignment(SwingConstants.CENTER);
				this.add(label, BorderLayout.CENTER);
			}
			if (text.length() != 0 && DISPLAY_DOT) {
				label.setText(KNIGHT_STRING);
			}
			else {
				label.setText(text);
			}
			labelText = text;
		}
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelFont(Font f) {
		label.setFont(f);
	}

	public Font getLabelFont() {
		return label.getFont();
	}

	/**
	 * Update the label font according to the KPanel's size.
	 */
	public void updateLabelFont() {
		if (DISPLAY_DOT) {
			updateLabelFont(1.8f);
		}
		else {
			updateLabelFont(1.8f);
		}
	}

	
	private void updateLabelFont(float divisor) {
		if (getHeight() < getWidth()) {
			label.setFont(label.getFont().deriveFont(getHeight() / divisor));
		}
		else {
			label.setFont(label.getFont().deriveFont(getWidth() / divisor));
		}
	}

	public void setDefaultColor(Color c) {
		setBackground(c);
		defaultColor = c;
	}

	public Color getDefaultColor() {
		return defaultColor;
	}


	public void repaint(String s) {
		setLabel(s);
		paintAll(getGraphics());
	}

	public void repaint(String s, int waitTime) {
		setLabel(s);
		paintAll(getGraphics());

	}

	public static void displayVisitedAsDot() {
		DISPLAY_DOT = true;
	}

	public static void displayVisitedAsText() {
		DISPLAY_DOT = false;
	}

}
