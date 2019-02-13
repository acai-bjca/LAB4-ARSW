package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Immortal extends Thread {

	private ImmortalUpdateReportCallback updateCallback = null;
	private int health;
	private int defaultDamageValue;
	private final List<Immortal> immortalsPopulation;
	private final String name;
	private final Random r = new Random(System.currentTimeMillis());
	private boolean pausar;
	private boolean isBeingAttacked;
	private AtomicBoolean sincronizado;
	private boolean alive;

	public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
		super(name);
		this.updateCallback = ucb;
		this.name = name;
		this.immortalsPopulation = immortalsPopulation;
		this.health = health;
		this.defaultDamageValue = defaultDamageValue;
		this.pausar = false;
		this.isBeingAttacked = false;
		sincronizado = new AtomicBoolean(false);
		alive = true;
	}

	public void run() {
		while (alive) {
			if (pausar) {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			Immortal im;
			int myIndex = immortalsPopulation.indexOf(this);
			int nextFighterIndex = r.nextInt(immortalsPopulation.size());

			// avoid self-fight
			if (nextFighterIndex == myIndex) {
				nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
			}

			im = immortalsPopulation.get(nextFighterIndex);

			this.fight(im);

			try {
				// Thread.sleep(1);
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Estan sumando y restando a la misma vez si los dos this y i2 se están
	// atacanco al mismo tiempo
	public void fight(Immortal i2) {
		if(i2.getAlive()) {
			if (i2.getHealth() > 0) {
				if (this.hashCode() < i2.hashCode()) {
					synchronized (this) {
						// si ningun hilo esta usando a este hilo, entonces se hace esto:
						synchronized (i2) {
							i2.changeHealth(i2.getHealth() - defaultDamageValue);
							this.health += defaultDamageValue;						
						}
					}
				} else {
					synchronized (i2) {
						// si ningun hilo esta usando a este hilo, entonces se hace esto:
						synchronized (this) {
							i2.changeHealth(i2.getHealth() - defaultDamageValue);
							this.health += defaultDamageValue;
							
						}
					}
				}
				updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
				
			} else {
				//muerto = true;			
				updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
				i2.setAlive(false);
			}
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
				this.pausar = pausar;
				this.notifyAll();
			}
		}
		this.pausar = pausar;
	}

	public AtomicBoolean isBeingSinchronized() {
		return sincronizado;
	}

	public void setSinchronized(boolean s) {
		sincronizado.set(s);
	}
	
	public boolean getAlive() {
		return alive;
	}
	
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	@Override
	public String toString() {
		return name + "[" + health + "]";
	}

}
