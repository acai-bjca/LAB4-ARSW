package edu.eci.arsw.highlandersim;

import java.util.List;

public class ImmortalGarbage extends Thread {
	private List immortals;
	public  ImmortalGarbage(List<Immortal> immortals) {
		this.immortals = immortals;
	}
	
	public void run() {
		while(true) {
			int i = 0;
			while (!immortals.isEmpty()) {
				if(i == immortals.size())  i =0;
				Immortal im = (Immortal) immortals.get(i);
				
				if (!im.getAlive()) {
					immortals.remove(i);
					i = -1;
				}
				i++;
			}
		}	
	}
}