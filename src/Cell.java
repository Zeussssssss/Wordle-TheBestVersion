import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Cell {

	private static String content;
	private static String evaluation;
	private static Font font;
	private static int x, y;
	private static JLabel label;
	private static final Color LIGHT_GRAY = Color.LIGHT_GRAY;
	private static final Color BLACK = Color.black;
	private static final Color WHITE = Color.white;
	private static final int width = 80;
	private static final int height = 110;
	private static final Color GREEN = Color.getColor("", (7 << 16) + (176 << 8) + 52); // 16, 176, 52
	private static final Color YELLOW = Color.getColor("", (204 << 16) + (172 << 8) + 8); // 204, 172, 8
	private static final Color DARK_GRAY = Color.DARK_GRAY;
	private static final Map<String, Color> colorEvaluationMap = Map.of(
			"c", GREEN,
			"p", YELLOW,
			"a", DARK_GRAY
	);
	
	public Cell(String newContent, String newEvaluation, Font newFont,int newX, int newY) {
		content = newContent;
		evaluation = newEvaluation;
		font = newFont;
		x = newX;
		y = newY;
		label = new JLabel("", SwingConstants.CENTER);
		init();
	}
	
	private static void init() {
		label.setFont(font);
		label.setBounds(width+(height*x),width+(height*y),width,width);
		label.setVisible(true);
		label.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
		label.setText(content);
		if (evaluation != null) {
			label.setBackground(colorEvaluationMap.get(evaluation));
			label.setBorder(BorderFactory.createLineBorder(colorEvaluationMap.get(evaluation), 1));
		} else {
			label.setBackground(BLACK);
		}
		label.setOpaque(true);
		label.setForeground(WHITE);
	}
	
	public JLabel getLabel() { return label; }
	
}