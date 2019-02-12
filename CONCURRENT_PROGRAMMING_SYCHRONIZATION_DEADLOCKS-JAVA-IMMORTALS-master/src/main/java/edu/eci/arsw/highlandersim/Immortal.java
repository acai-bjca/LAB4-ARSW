package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

	private ImmortalUpdateReportCallback updateCallback = null;
	private int health;
	private int defaultDamageValue;
	private final List<Immortal> immortalsPopulation;
	private final String name;
	private final Random r = new Random(System.currentTimeMillis());
	private boolean pausar;
	private boolean isBeingAttacked;
	private boolean iAmAttacking;
	private Immortal atacante;

	public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue,
			ImmortalUpdateReportCallback ucb) {
		super(name);
		this.updateCallback = ucb;
		this.name = name;
		this.immortalsPopulation = immortalsPopulation;
		this.health = health;
		this.defaultDamageValue = defaultDamageValue;
		this.pausar = false;
		this.isBeingAttacked = false;
		this.iAmAttacking = false;
	}

	public void run() {
		while (true) {
			if (!pausar) {
				Immortal im;
				int myIndex = immortalsPopulation.indexOf(this);
				int nextFighterIndex = r.nextInt(immortalsPopulation.size());

				// avoid self-fight
				if (nextFighterIndex == myIndex) {
					nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
				}

				im = immortalsPopulation.get(nextFighterIndex);
				im.isBeingAttacked(this);

				this.fight(im);

				try {
					// Thread.sleep(1);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	//Estan sumando y restando a la misma vez si los dos this y i2 se están atacanco al mismo tiempo
	public void fight(Immortal i2) {
		if (i2.getHealth() > 0) {
			synchronized (this) {//si ningun hilo esta usando a este hilo, entonces se hace esto:
				synchronized (i2) {
					//si ningun hilo esta usando a i2, entonces se hace esto:
					i2.changeHealth(i2.getHealth() - defaultDamageValue);
					this.health += defaultDamageValue;
					updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
				}

			}
		} else {
			updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
		}
	}

	public void changeHealth(int v) {
		health = v;
	}

	public int getHealth() {
		return health;
	}

	public void pausarInmortal(boolean pausar) {
		if (!pausar) {
			synchronized (this) {
				this.notifyAll();
			}
		}
		this.pausar = pausar;
	}

	public void isBeingAttacked(Immortal atacante) {
		this.atacante = atacante;
		this.isBeingAttacked = true;
	}

	@Override
	public String toString() {
		return name + "[" + health + "]";
	}

}
