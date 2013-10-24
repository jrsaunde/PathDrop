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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;

import topo.JGui;
import topo.NetworkDiscovery;

import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.SessionConfig;
import com.cisco.onep.topology.Graph;

public class SwingGui implements ActionListener{


	public Graph graph = null;
	public List<InetAddress> addresses;
	public NetworkApplication discoveryApplication = NetworkApplication.getInstance();
	public SessionConfig nodeConfig;
	public Collection<String> nodeNames = new TreeSet<String>(Collator.getInstance());
	public ArrayList<String> connectionStrings = new ArrayList();
	public NetworkDiscovery network;
	public String start;
	public String username;
	public String password;

	private JFrame frame;
	private InputField sourceIPInput = new InputField();
	private InputField usernameInput = new InputField();
	private InputField passwordInput = new InputField();
	private JTextField console = new JTextField();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SwingGui window = new SwingGui();
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
	public SwingGui() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
	
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(SwingGui.class.getResource("/img/cisco_small.png")));
		frame.setTitle("Pinpoint PacketLoss");
		frame.setBounds(100, 100, 1400, 1000);
		frame.setMinimumSize(new Dimension(800, 800));
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		InputPanel inputPanel = new InputPanel();
		frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
		
		JPanel consolePanel = new JPanel();
		consolePanel.add(this.console);
		consolePanel.setPreferredSize(new Dimension(800, 200));
		this.console.setEditable(false);
		this.console.setPreferredSize(new Dimension(800, 200));
		frame.getContentPane().add(consolePanel, BorderLayout.SOUTH);
		

		try{
			
			/* Set up the layout of buttons and Labels in inputPanel */
			GridBagLayout inputPanelLayout = new GridBagLayout();
			inputPanelLayout.columnWidths = new int[] {80, 80, 100, 55, 116, 78, 116, 67, 116, 90, 116, 0, 61, 0};
			inputPanelLayout.rowHeights = new int[]{67, 0, 0};
			inputPanelLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			inputPanelLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			inputPanel.setLayout(inputPanelLayout);
			
			/* Add Cisco Logo to top left corner of inputPanel */
			BufferedImage image = ImageIO.read(SwingGui.class.getResource("/img/cisco_small.png"));
			JLabel logo = new JLabel(new ImageIcon(image));
			GridBagConstraints logoConstraints = new GridBagConstraints();
			logoConstraints.ipadx = 25;
			logoConstraints.anchor = GridBagConstraints.NORTHWEST;
			logoConstraints.insets = new Insets(0, 0, 5, 5);
			logoConstraints.gridx = 0;
			logoConstraints.gridy = 0;
			inputPanel.add(logo, logoConstraints);
			
			/* Add Source IP label */
			InputLabel sourceIPLabel = new InputLabel("Source IP");
			
			GridBagConstraints sourceLabelConstraints = createConstraints(1,0);
			inputPanel.add(sourceIPLabel, sourceLabelConstraints);
			
			/* Add Source IP Input */
			GridBagConstraints sourceInputConstraints = createConstraints(2,0);
			inputPanel.add(this.sourceIPInput, sourceInputConstraints);
			
			/* Add Destination IP label */
			InputLabel destIPLabel = new InputLabel("Destination IP");
			
			GridBagConstraints destLabelConstraints = createConstraints(3,0);
			inputPanel.add(destIPLabel, destLabelConstraints);
			
			/* Add Destination IP Input */
			InputField destIPInput = new InputField();
			GridBagConstraints destInputConstraints = createConstraints(4,0);
			inputPanel.add(destIPInput, destInputConstraints);
			
			/* Add username label */
			InputLabel usernameLabel = new InputLabel("Username");
			
			GridBagConstraints usernameLabelConstraints = createConstraints(5,0);
			inputPanel.add(usernameLabel, usernameLabelConstraints);
			
			/* Add username Input */
			GridBagConstraints usernameInputConstraints = createConstraints(6,0);
			inputPanel.add(this.usernameInput, usernameInputConstraints);
			
			/* Add password label */
			InputLabel passwordLabel = new InputLabel("Destination IP");
			
			GridBagConstraints passwordLabelConstraints = createConstraints(7,0);
			inputPanel.add(passwordLabel, passwordLabelConstraints);
			
			/* Add password Input */
			GridBagConstraints passwordInputConstraints = createConstraints(8,0);
			inputPanel.add(this.passwordInput, passwordInputConstraints);
			
			/* Add Protocol Label */
			InputLabel protocolLabel = new InputLabel("Protocol");
			protocolLabel.setForeground(Color.WHITE);
			protocolLabel.setFont(new Font("Tahoma", Font.BOLD, 13));
			GridBagConstraints protocolLabelConstraints = createConstraints(9,0);
			inputPanel.add(protocolLabel, protocolLabelConstraints);
			
			/* Add Protocol Input Field */
			JComboBox protocolInput = new JComboBox();
			protocolInput.setModel(new DefaultComboBoxModel(new String[] {"TCP", "UDP", "Custom"}));
			GridBagConstraints protocolInputConstraints = createConstraints(10, 0);
			inputPanel.add(protocolInput, protocolInputConstraints);
			
			/* Add Start button */
			JToggleButton startButton = new JToggleButton("Start");
			GridBagConstraints startButtonConstraints = createConstraints(11, 0);
			inputPanel.add(startButton, startButtonConstraints);
			startButton.addActionListener(this);
			
			
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Creates GribBagConstraints for input Labels and Inputs
	 * @param x - (int) gridx value 
	 * @param y - (int) gridy value
	 * @return - GridBagConstraints
	 */
	public GridBagConstraints createConstraints(int x, int y){
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		/* Constant values */
		constraints.fill	= GridBagConstraints.HORIZONTAL;
		constraints.anchor 	= GridBagConstraints.WEST;
		constraints.insets 	= new Insets(0, 0, 5, 5);
		
		/* Dynamic Values */
		constraints.gridx = x;
		constraints.gridy = y;
	
		return constraints;
	}
	
	/**
	 * Print to console
	 */
	public void consolePrint(String message){
		this.console.setText(message);
	}
	/**
	 * Listen for start button pushes
	 */
	public void actionPerformed(ActionEvent ae){
		this.start = this.sourceIPInput.getText();
		this.username = this.usernameInput.getText();
		this.password = this.passwordInput.getText();
		try{
			/*Parse input arguments*/
			this.consolePrint("Start Node is " + this.start + " with " + this.username + "/" + this.password);
			
			/* Convert startNodeIP to InetAddress */
			InetAddress startNode = InetAddress.getByName(this.start);
			
			/* Run Network Discovery with input parameters */
			NetworkDiscovery network = new NetworkDiscovery(startNode, this.username, this.password);
			
			/*Add the topology panel with graph drawn from discovered topology */
			JGui topoPanel = new JGui(network.nodeNames,network.connectionStrings);
			
			topoPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
			topoPanel.setPreferredSize(new Dimension(800,800));
			
			/* Add this panel to the window and refresh to display it */
			this.frame.getContentPane().add(topoPanel, BorderLayout.CENTER);
			this.frame.setVisible(true);
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
