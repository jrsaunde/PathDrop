package gui;


import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.List;

import javax.swing.JTextPane;
import javax.swing.JProgressBar;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.border.BevelBorder;

import java.awt.Color;

import javax.swing.JToggleButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.SystemColor;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.Font;


public class Gui {

	private JFrame frmPinpointPacketloss;
	private JTextField txtXxxx;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;

	//private BufferedImage image;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
					Gui window = new Gui();
					
					window.frmPinpointPacketloss.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPinpointPacketloss = new JFrame();
		URL imageUrl = this.getClass().getResource("img/cisco_small.png");
		frmPinpointPacketloss.setIconImage(Toolkit.getDefaultToolkit().getImage(imageUrl));
		frmPinpointPacketloss.setTitle("Pinpoint PacketLoss");
		frmPinpointPacketloss.setBounds(100, 100, 1400, 1000);
		frmPinpointPacketloss.setMinimumSize(new Dimension(800, 800));
		frmPinpointPacketloss.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPinpointPacketloss.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel inputpanel = new JPanel();
		inputpanel.setBackground(new Color(57, 83, 139));
		inputpanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		frmPinpointPacketloss.getContentPane().add(inputpanel, BorderLayout.NORTH);
		
		try {
			URL backgroundimg = this.getClass().getResource("/img/cisco_small.png");
			BufferedImage image = ImageIO.read(backgroundimg);
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
			
			txtXxxx = new JTextField();
			GridBagConstraints gbc_txtXxxx = new GridBagConstraints();
			gbc_txtXxxx.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtXxxx.anchor = GridBagConstraints.WEST;
			gbc_txtXxxx.insets = new Insets(0, 0, 5, 5);
			gbc_txtXxxx.gridx = 2;
			gbc_txtXxxx.gridy = 0;
			inputpanel.add(txtXxxx, gbc_txtXxxx);
			txtXxxx.setColumns(11);
			
			InputLabel destinationIPLabel = new InputLabel("Destination IP");
			GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
			gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_1.gridx = 3;
			gbc_lblNewLabel_1.gridy = 0;
			inputpanel.add(destinationIPLabel, gbc_lblNewLabel_1);
			
			textField_1 = new JTextField();
			GridBagConstraints gbc_textField_1 = new GridBagConstraints();
			gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_1.anchor = GridBagConstraints.WEST;
			gbc_textField_1.insets = new Insets(0, 0, 5, 5);
			gbc_textField_1.gridx = 4;
			gbc_textField_1.gridy = 0;
			inputpanel.add(textField_1, gbc_textField_1);
			textField_1.setColumns(10);

			InputLabel sourcePortLabel = new InputLabel("Source Port");
			GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
			gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_5.gridx = 5;
			gbc_lblNewLabel_5.gridy = 0;
			inputpanel.add(sourcePortLabel, gbc_lblNewLabel_5);
			
			textField_2 = new JTextField();
			GridBagConstraints gbc_textField_2 = new GridBagConstraints();
			gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_2.anchor = GridBagConstraints.WEST;
			gbc_textField_2.insets = new Insets(0, 0, 5, 5);
			gbc_textField_2.gridx = 6;
			gbc_textField_2.gridy = 0;
			inputpanel.add(textField_2, gbc_textField_2);
			textField_2.setColumns(10);
			
			InputLabel destinationPortLabel = new InputLabel("Destination Port");
			GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
			gbc_lblNewLabel_6.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_6.gridx = 7;
			gbc_lblNewLabel_6.gridy = 0;
			inputpanel.add(destinationPortLabel, gbc_lblNewLabel_6);
			
			textField_3 = new JTextField();
			GridBagConstraints gbc_textField_3 = new GridBagConstraints();
			gbc_textField_3.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField_3.anchor = GridBagConstraints.WEST;
			gbc_textField_3.insets = new Insets(0, 0, 5, 5);
			gbc_textField_3.gridx = 8;
			gbc_textField_3.gridy = 0;
			inputpanel.add(textField_3, gbc_textField_3);
			textField_3.setColumns(10);
			
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
		
		JPanel consolePanel = new JPanel();
		consolePanel.setBackground(new Color(57, 83, 139));
		//consolePanel.setBackground(UIManager.getColor("ToolBar.shadow"));
		consolePanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		frmPinpointPacketloss.getContentPane().add(consolePanel, BorderLayout.EAST);
		GridBagLayout gbl_consolePanel = new GridBagLayout();
		gbl_consolePanel.columnWidths = new int[] {109, 146};
		gbl_consolePanel.rowHeights = new int[] {16, 0, 0, 16};
		gbl_consolePanel.columnWeights = new double[]{0.0, 0.0};
		gbl_consolePanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		consolePanel.setLayout(gbl_consolePanel);
		
		JLabel lblNewLabel_4 = new JLabel("Devices Discovered");
		lblNewLabel_4.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 0;
		consolePanel.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		JProgressBar progressBar = new JProgressBar();
		//progressBar.setForeground(new Color(51, 204, 51));
		//progressBar.setForeground("nimbusRed");
		progressBar.setValue(80);
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.anchor = GridBagConstraints.WEST;
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 0;
		consolePanel.add(progressBar, gbc_progressBar);
		
		JLabel lblNewLabel_3 = new JLabel("Traffic Sent");
		lblNewLabel_3.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 1;
		consolePanel.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		JProgressBar progressBar_1 = new JProgressBar();
		progressBar_1.setForeground(SystemColor.info);
		progressBar_1.setValue(50);
		GridBagConstraints gbc_progressBar_1 = new GridBagConstraints();
		gbc_progressBar_1.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar_1.anchor = GridBagConstraints.WEST;
		gbc_progressBar_1.gridx = 1;
		gbc_progressBar_1.gridy = 1;
		consolePanel.add(progressBar_1, gbc_progressBar_1);
		
		JLabel lblNewLabel_7 = new JLabel("Total Packet Loss");
		lblNewLabel_7.setForeground(Color.WHITE);
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 2;
		consolePanel.add(lblNewLabel_7, gbc_lblNewLabel_7);
		
		JProgressBar progressBar_2 = new JProgressBar();
		progressBar_2.setValue(20);
		progressBar_2.setForeground(UIManager.getColor("ToolBar.dockingForeground"));
		GridBagConstraints gbc_progressBar_2 = new GridBagConstraints();
		gbc_progressBar_2.gridx = 1;
		gbc_progressBar_2.gridy = 2;
		consolePanel.add(progressBar_2, gbc_progressBar_2);
		
		JPanel devicesPanel = new JPanel();
		devicesPanel.setBackground(new Color(57, 83, 139));
		//devicesPanel.setBackground(UIManager.getColor("ToolBar.shadow"));
		devicesPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		devicesPanel.setPreferredSize(new Dimension(150, 550));
		frmPinpointPacketloss.getContentPane().add(devicesPanel, BorderLayout.WEST);
		GridBagLayout gbl_devicesPanel = new GridBagLayout();
		gbl_devicesPanel.columnWidths = new int[] {140, 0};
		gbl_devicesPanel.rowHeights = new int[] {30, 560, 0};
		gbl_devicesPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_devicesPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		devicesPanel.setLayout(gbl_devicesPanel);
	
		//devicesPanel.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel lblNewLabel_2 = new JLabel("Discovered Devices");
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setPreferredSize(new Dimension(130, 30));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 0;
		devicesPanel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		List list = new List();
		list.setPreferredSize(new Dimension(500, 800));
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.anchor = GridBagConstraints.NORTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 1;
		devicesPanel.add(list, gbc_list);
		
		JTabbedPane vtyPanel = new JTabbedPane(JTabbedPane.BOTTOM);
		vtyPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		vtyPanel.setBackground(new Color(57, 83, 139));
		//vtyPanel.setBackground(UIManager.getColor("ToolBar.shadow"));
		vtyPanel.setPreferredSize(new Dimension(200, 150));
		frmPinpointPacketloss.getContentPane().add(vtyPanel, BorderLayout.SOUTH);
		
		JTextPane textPane_2 = new JTextPane();
		textPane_2.setBackground(Color.LIGHT_GRAY);
		vtyPanel.addTab("Console", null, textPane_2, null);
		vtyPanel.setForegroundAt(0, Color.WHITE);
		
		JTextPane textPane = new JTextPane();
		textPane.setBackground(Color.LIGHT_GRAY);
		vtyPanel.addTab("Device2", null, textPane, null);
		vtyPanel.setForegroundAt(1, Color.WHITE);
		
		JTextPane textPane_1 = new JTextPane();
		textPane_1.setBackground(Color.LIGHT_GRAY);
		vtyPanel.addTab("Device1", null, textPane_1, null);
		vtyPanel.setForegroundAt(2, Color.WHITE);
		
		JPanel topologyPanel = new JPanel();
		topologyPanel.setBackground(SystemColor.menu);
		frmPinpointPacketloss.getContentPane().add(topologyPanel, BorderLayout.CENTER);
		
		GridBagLayout gbl_topologyPanel = new GridBagLayout();
		gbl_topologyPanel.columnWidths = new int[] {60, 800, 60};
		gbl_topologyPanel.rowHeights = new int[] {60, 600, 60};
		gbl_topologyPanel.columnWeights = new double[]{0.0, 0.0, 1.0};
		gbl_topologyPanel.rowWeights = new double[]{0.0, 0.0, 1.0};
		topologyPanel.setLayout(gbl_topologyPanel);
		
		JLabel leftArrow = new JLabel("");
		leftArrow.setIcon(new ImageIcon("C:\\Users\\Jamie\\Documents\\workspace_java\\demo\\src\\arrow_left.png"));
		GridBagConstraints gbc_leftArrow = new GridBagConstraints();
		gbc_leftArrow.insets = new Insets(0, 0, 5, 5);
		gbc_leftArrow.gridx = 0;
		gbc_leftArrow.gridy = 1;
		topologyPanel.add(leftArrow, gbc_leftArrow);
		
		JLabel rightArrow = new JLabel("");
		rightArrow.setHorizontalAlignment(SwingConstants.CENTER);
		rightArrow.setIcon(new ImageIcon("C:\\Users\\Jamie\\Documents\\workspace_java\\demo\\src\\arrow_right.png"));
		GridBagConstraints gbc_rightArrow = new GridBagConstraints();
		gbc_rightArrow.insets = new Insets(0, 0, 5, 0);
		gbc_rightArrow.gridx = 2;
		gbc_rightArrow.gridy = 1;
		topologyPanel.add(rightArrow, gbc_rightArrow);
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.menu);
		panel.setPreferredSize(new Dimension(800, 600));
		try {
			BufferedImage image = ImageIO.read(new File("C:\\Users\\Jamie\\Documents\\workspace_java\\demo\\src\\topo.png"));
			JLabel picLabel = new JLabel(new ImageIcon(image));
			picLabel.setBackground(SystemColor.menu);
			panel.add(picLabel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		topologyPanel.add(panel, gbc_panel);
		
		JLabel upArrow = new JLabel("");
		upArrow.setIcon(new ImageIcon("/img/arrow_up.png"));
		GridBagConstraints gbc_upArrow = new GridBagConstraints();
		gbc_upArrow.insets = new Insets(0, 0, 5, 5);
		gbc_upArrow.gridx = 1;
		gbc_upArrow.gridy = 0;
		topologyPanel.add(upArrow, gbc_upArrow);
		
		JLabel downArrow = new JLabel("");
		downArrow.setIcon(new ImageIcon("C:\\Users\\Jamie\\Documents\\workspace_java\\demo\\src\\arrow_down.png"));
		GridBagConstraints gbc_downArrow = new GridBagConstraints();
		gbc_downArrow.insets = new Insets(0, 0, 0, 5);
		gbc_downArrow.gridx = 1;
		gbc_downArrow.gridy = 2;
		topologyPanel.add(downArrow, gbc_downArrow);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.menu);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 2;
		gbc_panel_1.gridy = 0;
		topologyPanel.add(panel_1, gbc_panel_1);
		
		JButton zoomInButton = new JButton("");
		zoomInButton.setContentAreaFilled( false );
		zoomInButton.setIcon(new ImageIcon("C:\\Users\\Jamie\\Documents\\workspace_java\\demo\\src\\zoomin.png"));
		zoomInButton.setPreferredSize(new Dimension(16,16));
		panel_1.add(zoomInButton);
		
		JButton zoomOutButton = new JButton("");
		zoomOutButton.setContentAreaFilled( false );
		zoomOutButton.setIcon(new ImageIcon("C:\\Users\\Jamie\\Documents\\workspace_java\\demo\\src\\zoomout.png"));
		zoomOutButton.setPreferredSize(new Dimension(16, 16));
		panel_1.add(zoomOutButton);
			}

}
