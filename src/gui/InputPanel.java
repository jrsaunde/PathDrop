package gui;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class InputPanel extends JPanel{

	InputPanel(){
		setBackground(new Color(57, 83,139));
		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		}
}
