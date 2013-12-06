package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class Gui_Redo {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui_Redo window = new Gui_Redo();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui_Redo() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Gui_Redo.class.getResource("/img/cisco_small.png")));
		frame.setTitle("Pinpoint PacketLoss");
		frame.setBounds(100, 100, 1400, 1000);
		frame.setMinimumSize(new Dimension(800, 800));
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel inputpanel = new JPanel();
		inputpanel.setBackground(new Color(57, 83, 139));
		inputpanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		frame.getContentPane().add(inputpanel, BorderLayout.NORTH);
		
		try {
			BufferedImage image = ImageIO.read(Gui_Redo.class.getResource("/img/cisco_small.png"));
			GridBagLayout gbl_inputpanel = new GridBagLayout();
			gbl_inputpanel.columnWidths = new int[] {80, 80, 100, 55, 116, 78, 116, 67, 116, 90, 116, 0, 61, 0};
			gbl_inputpanel.rowHeights = new int[]{67, 0, 0};
			gbl_inputpanel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			gbl_inputpanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			inputpanel.setLayout(gbl_inputpanel);
			JLabel lblNewLabel_8 = new JLabel(new ImageIcon(image));
			GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
			gbc_lblNewLabel_8.ipadx = 25;
			gbc_lblNewLabel_8.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_8.gridx = 0;
			gbc_lblNewLabel_8.gridy = 0;
			inputpanel.add(lblNewLabel_8, gbc_lblNewLabel_8);
			
			InputLabel sourceIPLabel = new InputLabel("Source IP");
			sourceIPLabel.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 1;
			gbc_lblNewLabel.gridy = 0;
			inputpanel.add(sourceIPLabel, gbc_lblNewLabel);
			
			InputField sourceIPInput = new InputField();
			GridBagConstraints gbc_txtXxxx = new GridBagConstraints();
			gbc_txtXxxx.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtXxxx.anchor = GridBagConstraints.WEST;
			gbc_txtXxxx.insets = new Insets(0, 0, 5, 5);
			gbc_txtXxxx.gridx = 2;
			gbc_txtXxxx.gridy = 0;
			inputpanel.add(sourceIPInput, gbc_txtXxxx);
			sourceIPInput.setColumns(11);
			
			InputLabel destinationIPLabel = new InputLabel("Destination IP");
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 3;
			gbc_lblNewLabel_1.gridy = 0;
			inputpanel.add(destinationIPLabel, gbc_lblNewLabel_1);
			
			InputField destIPInput = new InputField();
			GridBagConstraints gbc_textField_1 = new GridBagConstraints();
			gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_1.anchor = GridBagConstraints.WEST;
			gbc_textField_1.insets = new Insets(0, 0, 5, 5);
			gbc_textField_1.gridx = 4;
			gbc_textField_1.gridy = 0;
			inputpanel.add(destIPInput, gbc_textField_1);
			destIPInput.setColumns(10);

			InputLabel sourcePortLabel = new InputLabel("Source Port");
			GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
			gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_5.gridx = 5;
			gbc_lblNewLabel_5.gridy = 0;
			inputpanel.add(sourcePortLabel, gbc_lblNewLabel_5);
			
			InputField sourcePortInput = new InputField();
			GridBagConstraints gbc_textField_2 = new GridBagConstraints();
			gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_2.anchor = GridBagConstraints.WEST;
			gbc_textField_2.insets = new Insets(0, 0, 5, 5);
			gbc_textField_2.gridx = 6;
			gbc_textField_2.gridy = 0;
			inputpanel.add(sourcePortInput, gbc_textField_2);
			sourcePortInput.setColumns(10);
			
			InputLabel destinationPortLabel = new InputLabel("Destination Port");
			GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
			gbc_lblNewLabel_6.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_6.gridx = 7;
			gbc_lblNewLabel_6.gridy = 0;
			inputpanel.add(destinationPortLabel, gbc_lblNewLabel_6);
			
			InputField destPortInput = new InputField();
			GridBagConstraints gbc_textField_3 = new GridBagConstraints();
			gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_3.anchor = GridBagConstraints.WEST;
			gbc_textField_3.insets = new Insets(0, 0, 5, 5);
			gbc_textField_3.gridx = 8;
			gbc_textField_3.gridy = 0;
			inputpanel.add(destPortInput, gbc_textField_3);
			destPortInput.setColumns(10);
			
			JToggleButton tglbtnNewToggleButton = new JToggleButton("Start");
			GridBagConstraints gbc_tglbtnNewToggleButton = new GridBagConstraints();
			gbc_tglbtnNewToggleButton.insets = new Insets(0, 0, 5, 5);
			gbc_tglbtnNewToggleButton.anchor = GridBagConstraints.WEST;
			gbc_tglbtnNewToggleButton.gridx = 11;
			gbc_tglbtnNewToggleButton.gridy = 0;
			inputpanel.add(tglbtnNewToggleButton, gbc_tglbtnNewToggleButton);
			
			JComboBox<String> comboBox = new JComboBox<String>();
			comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"TCP", "UDP", "Custom"}));
			GridBagConstraints gbc_comboBox = new GridBagConstraints();
			gbc_comboBox.insets = new Insets(0, 0, 5, 5);
			gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
			gbc_comboBox.gridx = 10;
			gbc_comboBox.gridy = 0;
			inputpanel.add(comboBox, gbc_comboBox);
			
			JLabel protocolLabel = new JLabel("Protocol");
			protocolLabel.setForeground(Color.WHITE);
			protocolLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
			GridBagConstraints gbc_protocolLabel = new GridBagConstraints();
			gbc_protocolLabel.insets = new Insets(0, 0, 5, 0);
			gbc_protocolLabel.gridx = 9;
			gbc_protocolLabel.gridy = 0;
			inputpanel.add(protocolLabel, gbc_protocolLabel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
