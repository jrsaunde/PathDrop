package gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;


@SuppressWarnings("serial")
public class InputLabel extends JLabel {
	
	InputLabel(String text){
		setFont(new Font("Tahoma", Font.BOLD, 13));
		setForeground(Color.WHITE);
		setText(text);
		setHorizontalAlignment(SwingConstants.CENTER);
		
	}
	}