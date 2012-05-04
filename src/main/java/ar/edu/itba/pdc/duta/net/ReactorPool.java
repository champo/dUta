package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReactorPool {
	
	private List<Pair> pool;
	
	private int next;
	
	public ReactorPool(int count) throws IOException {
		
		pool = new ArrayList<Pair>();
		next = 0;
		for (int i = 0; i < count; i++) {
			
			Pair pair = new Pair();
			pair.reactor = new Reactor();
			pair.thread = new Thread(pair.reactor);
			pair.thread.start();
			
			pool.add(pair);
		}
	}
	
	public Reactor get() {
		
		if (pool.size() <= next) {
			return null;
		}
		
		Reactor reactor = pool.get(next).reactor;
		
		next++;
		if (next >= pool.size()) {
			next = 0;
		}
		
		return reactor;
	}

	public void close() {
		
		for (Pair pair : pool) {
			pair.reactor.stop();
		}
	}
	
	private class Pair {
		Reactor reactor;
		Thread thread;
	}
}
