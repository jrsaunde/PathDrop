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
package guiFX;

import java.io.File;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.web.WebView;

public class Tester implements Runnable {
	public void threadMonitor(ArrayList<Thread>threads) {
		for(Thread thread : threads)
    		System.out.println(thread.isAlive());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
