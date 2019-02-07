package blacklist;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchThread extends Thread {
	public int iniSegmento;
	public int finSegmento;
	public String ipaddress;
	public HostBlacklistsDataSourceFacade skds;
	public int BLACK_LIST_ALARM_COUNT;
	public HostBlackListsValidator validator;
	public int numHilo;
	private AtomicInteger ocurrencesCount;
	LinkedList<Integer> blackListOcurrences;
	
	public SearchThread(int iniSegmento, int finSegmento, String ipaddress, int numHilo, HostBlacklistsDataSourceFacade skds, int BLACK_LIST_ALARM_COUNT, AtomicInteger ocurrencesCount) throws InterruptedException {
		super("hilo "+numHilo);
		this.iniSegmento = iniSegmento;
		this.finSegmento = finSegmento;
		this.ipaddress = ipaddress;
		this.skds = skds;
		this.BLACK_LIST_ALARM_COUNT = BLACK_LIST_ALARM_COUNT;
		this.ocurrencesCount = ocurrencesCount;
		this.numHilo = numHilo;
	}	

	public void run() {			
		for (int i=iniSegmento; i<=finSegmento && ocurrencesCount.get()<BLACK_LIST_ALARM_COUNT; i++) {
			validator.setCheckedListsCount();          
            if (skds.isInBlackListServer(i, ipaddress)){
            	blackListOcurrences.add(i);              
            	ocurrencesCount.incrementAndGet();
            }
        }
	}
	
}