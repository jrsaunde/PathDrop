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
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;

import topo.JGui;
import topo.NetworkDiscovery;

import com.cisco.onep.element.NetworkApplication;
import com.cisco.onep.element.NetworkElement;
import com.cisco.onep.element.SessionConfig;
import com.cisco.onep.topology.Graph;
import com.cisco.onep.vty.VtyService;

public class SwingGui implements ActionListener{


	public Graph graph = null;
	public List<InetAddress> addresses;
	public NetworkApplication discoveryApplication = NetworkApplication.getInstance();
	public SessionConfig nodeConfig;
	public Collection<String> nodeNames = new TreeSet<String>(Collator.getInstance());
	public ArrayList<String> connectionStrings = new ArrayList<String>();
	public NetworkDiscovery network;
	public String start;
	public String username;
	public String password;

	private JFrame frame;
	private InputField sourceIPInput = new InputField();
	private InputField usernameInput = new InputField();
	private JPasswordField passwordInput = new JPasswordField();
	private JTextField consoleInput = new JTextField();
	private InputField vtyInput = new InputField();
	private JTextArea console = new JTextArea(20,100);
	private JScrollPane consoleScroll = new JScrollPane();
	private DefaultListModel<String> routerList = new DefaultListModel<String>();
	private String newLine       = System.getProperty("line.separator");
	private InputPanel consolePanel;
	private InputPanel listPanel;
	private JGui	topoPanel;
	
	private JToggleButton startButton;
	private JToggleButton startVTY; 
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
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
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(SwingGui.class.getResource("/img/cisco_white.png")));
		frame.setTitle("Pinpoint PacketLoss");
		frame.setBounds(100, 100, 1400, 1000);
		frame.setMinimumSize(new Dimension(800, 800));
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		InputPanel inputPanel = new InputPanel();
		frame.getContentPane().add(inputPanel, BorderLayout.NORTH);
		
		
		this.consolePanel = new InputPanel();
		this.consoleScroll = new JScrollPane(this.console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.consolePanel.add(this.consoleScroll);
		this.consolePanel.setPreferredSize(new Dimension(800, 200));
		this.console.setEditable(false);
		//this.console.setAutoscrolls(true);
		this.console.setRows(9);
		this.console.setPreferredSize(new Dimension(800, 200));
		
		DefaultCaret caret = (DefaultCaret)this.console.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.consoleScroll.setViewportView(this.console);
		this.frame.getContentPane().add(this.consolePanel, BorderLayout.SOUTH);
		
		//Add List Panel
		this.listPanel = new InputPanel();
		this.listPanel.setLayout(new BorderLayout(0, 0));
		InputLabel deviceListLabel = new InputLabel("Device List");
		this.listPanel.add(deviceListLabel, BorderLayout.NORTH);
		frame.getContentPane().add(this.listPanel, BorderLayout.WEST);
		
		try{
			
			/* Set up the layout of buttons and Labels in inputPanel */
			GridBagLayout inputPanelLayout = new GridBagLayout();
			inputPanelLayout.columnWidths = new int[] {80, 80, 100, 55, 116, 78, 116, 67, 116, 90, 116, 0, 61, 0};
			inputPanelLayout.rowHeights = new int[]{67, 0, 0};
			inputPanelLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			inputPanelLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			inputPanel.setLayout(inputPanelLayout);
			
			/*Panel and layout for VTY session*/
			/*GridBagLayout inputVTYPanelLayout = new GridBagLayout();
			inputVTYPanelLayout.columnWidths = new int[] {80, 80, 100, 55, 116, 78, 116, 67, 116, 90, 116, 0, 61, 0};
			inputVTYPanelLayout.rowHeights = new int[]{67, 0, 0};
			inputVTYPanelLayout.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
			inputVTYPanelLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
			inputVTYPanel.setLayout(inputPanelLayout);
			*/
			/* Add Cisco Logo to top left corner of inputPanel */
			BufferedImage image = ImageIO.read(SwingGui.class.getResource("/img/cisco_white.png"));
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
			InputLabel passwordLabel = new InputLabel("Password");
			
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
			JComboBox<String> protocolInput = new JComboBox<String>();
			protocolInput.setModel(new DefaultComboBoxModel<String>(new String[] {"TCP", "UDP", "Custom"}));
			GridBagConstraints protocolInputConstraints = createConstraints(10, 0);
			inputPanel.add(protocolInput, protocolInputConstraints);
			
			/* Add Start button */
			startButton = new JToggleButton("Start");
			GridBagConstraints startButtonConstraints = createConstraints(11, 0);
			inputPanel.add(startButton, startButtonConstraints);
			startButton.addActionListener(this);
			
			/*Add VTY session button*/
			startVTY = new JToggleButton("StartVTY");
			GridBagConstraints startVTYButtonConstraints = createConstraints(11, -1);
			inputPanel.add(startVTY, startVTYButtonConstraints);
			startVTY.addActionListener(this);
			
			/*Add VTY Target IP Input Field*/
			GridBagConstraints vtyInputConstraints = createConstraints(10, -1);
			inputPanel.add(this.vtyInput, vtyInputConstraints);
			
			/* Add VTY label */
			InputLabel vtyInput = new InputLabel("VTY commands");
			
			GridBagConstraints consoleLabelConstraints = createConstraints(8, -1);
			inputPanel.add(vtyInput, consoleLabelConstraints);
			
			/*Add VTY input field*/
			GridBagConstraints consoleInputConstraints = createConstraints(9, -1);
			inputPanel.add(this.consoleInput, consoleInputConstraints);
			consoleInput.addActionListener(this);
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
		this.console.append(newLine + message);
		//this.console.setVisible(true);
	}
	/**
	 * Listen for start button pushes
	 */
	public void actionPerformed(ActionEvent ae){
		
		try{
			if(ae.getSource() == startButton){
				this.start = this.sourceIPInput.getText();
				this.username = this.usernameInput.getText();
				this.password = new String(this.passwordInput.getPassword());	
				
				/*Parse input arguments*/
				this.consolePrint("Start Node is " + this.start + " with " + this.username + "/" + this.password);
				
				/* Convert startNodeIP to InetAddress */
				InetAddress startNode = InetAddress.getByName(this.start);
				
				/* Run Network Discovery with input parameters */
				network = new NetworkDiscovery(startNode, this.username, this.password);
				
				/*Add the topology panel with graph drawn from discovered topology */
				this.topoPanel = new JGui(network.nodeNames,network.connectionStrings);
				
				this.topoPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
				this.topoPanel.setMinimumSize(new Dimension(800,800));
				
				/* Add this panel to the window and refresh to display it */
				this.frame.getContentPane().add(this.topoPanel, BorderLayout.CENTER);
				this.frame.setVisible(true);
				
				/* Add List of routers to RouterList */
				for(String router: network.nodeNames){
					this.routerList.addElement(router);
					//GuiNode routerNode = new GuiNode(router, router);
				}
				
				/* list of connections */
				
				//Add DeviceList Panel
				JList<String> list = new JList<String>(this.routerList);
				list.setMaximumSize(new Dimension( 200, (routerList.size()*20)));
				this.listPanel.add(list, BorderLayout.CENTER);
				//this.listPanel.setMaximumSize(new Dimension(200, 200));
				//frame.getContentPane().add(listPanel, BorderLayout.WEST);
				
				String 	host = "10.192.40.140";
				int 	port = 80;
				
				//TrafficTest test = new TrafficTest(host, port);
				this.consolePrint("Testing traffic with " + host + ":" + port );
			}
			if(ae.getSource() == startVTY){
				System.out.println("VTY Pressed");
				if(network == null){
					this.consolePrint("Must connect to a network before establishing a VTY session");
				}else{
					
					String networkElementName = this.vtyInput.getText();
					InetAddress vtyNodeAddress = InetAddress.getByName(networkElementName);
			        NetworkApplication networkApplication = NetworkApplication.getInstance();
					NetworkElement networkElement =	networkApplication.getNetworkElement(vtyNodeAddress);
					
					VtyService vtyService = new VtyService(networkElement);
		            vtyService.open();
		            
		            //int timeOut = vtyService.getTimeout();
		            //String showOnepStatusCmd = "show onep status";
		            String command = this.consoleInput.getText();
		            //String cliResult = vtyService.write(showOnepStatusCmd);
		            String cliResult = vtyService.write(command);
		            System.out.println(cliResult);
		            System.out.println("VTY successful?");
		            this.consolePrint(cliResult);
		            
		            vtyService.close();
		            vtyService.destroy();
				}
			}
			if(ae.getSource() == consoleInput){
				String input = this.consoleInput.getText();
	            System.out.println(input);
	            this.consolePrint(input);
			}
			
			
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
