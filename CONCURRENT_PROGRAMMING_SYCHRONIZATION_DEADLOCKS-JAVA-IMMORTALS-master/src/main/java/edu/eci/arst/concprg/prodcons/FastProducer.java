
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class FastProducer extends Thread {

	private Queue<Integer> queue = null;

	private int dataSeed = 0;
	private Random rand = null;
	private final long stockLimit;

	public FastProducer(Queue<Integer> queue, long stockLimit) {
		this.queue = queue;
		rand = new Random(System.currentTimeMillis());
		this.stockLimit = stockLimit;
	}

	@Override
	public void run() {
		while (true) {
			if (queue.size() >= stockLimit) {
				synchronized (queue) {
					try {
						queue.wait();						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}else {
			dataSeed = dataSeed + rand.nextInt(100);
			System.out.println("Producer added " + dataSeed);
			queue.add(dataSeed);
			}
		}
	}
}