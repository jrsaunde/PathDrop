/* PathDrop - Topology Visualizer and Packet Loss Indicator
 * Copyright (c) 2013 
 * Jamie Saunders <jrsaunde@ncsu.edu>
 * Thomas Paradis <tmparadi@ncsu.edu>
 * Hank Liu <hliu9@ncsu.edu>
 * Ryan Coble <rlcoble@ncsu.edu>
 * Isaac Choe <ichoe@ncsu.edu>
 * 
 * All rights reserved
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package packetreplay;

import net.sourceforge.jpcap.capture.*;
import net.sourceforge.jpcap.net.*;


/**
 * This is a future planned class to capture the packets we see in the specificed traffic flow and replay them 
 * passing through the network in an offline mode.
 * 
 * 
 */
public class PacketReplay {
	//private static final int INFINITE = -1;
	private static final int PACKET_COUNT = 10; 

	// BPF filter for capturing any packet
	private static final String FILTER = "";

	private PacketCapture m_pcap;
	private String m_device;

	public PacketReplay() throws Exception {
	  // Step 1:  Instantiate Capturing Engine
	  m_pcap = new PacketCapture();
	  
	  // Step 2:  Check for devices 
	  m_device = m_pcap.findDevice();

	  // Step 3:  Open Device for Capturing (requires root)
	  m_pcap.open(m_device, true);

	  // Step 4:  Add a BPF Filter (see tcpdump documentation)
	  m_pcap.setFilter(FILTER, true);

	  // Step 5:  Register a Listener for Raw Packets
	  m_pcap.addRawPacketListener(new RawPacketHandler());

	  // Step 6:  Capture Data (max. PACKET_COUNT packets)
	  m_pcap.capture(PACKET_COUNT);
	  }

	  @SuppressWarnings("unused")
	public static void main(String[] args) {
	    try {
	      PacketReplay example = new PacketReplay();
	    } catch(Exception e) {
	      e.printStackTrace();
	      System.exit(1);
	    }
	  }
	}


	class RawPacketHandler implements RawPacketListener 
	{
	  private static int m_counter = 0;

	  public void rawPacketArrived(RawPacket data) {
	    m_counter++;
	    System.out.println("Received packet (" + m_counter + ")");
	    //System.out.println(m_pcap.)
	  }
}
