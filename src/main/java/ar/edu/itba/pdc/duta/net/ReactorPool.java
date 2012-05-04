package ar.edu.itba.pdc.duta.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReactorPool {

	private List<Pair> pool;
	
	private State state;
	
	private int next;
	
	public ReactorPool(int count) throws IOException {
		
		
		pool = new ArrayList<Pair>();
		next = 0;
		for (int i = 0; i < count; i++) {
			
			Pair pair = new Pair();
			pair.reactor = new Reactor();
			pool.add(pair);
		}
		
		state = State.READY;
	}
	
	public void start() {
		
		if (state == State.CLOSED) {
			throw new IllegalStateException("A reactor cant be started twice");
		}
		
		for (Pair pair : pool) {
			pair.thread = new Thread(pair.reactor);
			pair.thread.start();
		}
		
		state = State.RUNNING;
	}
	
	public Reactor get() {
		
		if (state != State.RUNNING || pool.size() <= next) {
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
		
		state = State.CLOSED;
		for (Pair pair : pool) {
			pair.reactor.stop();
		}
	}
	
	private static class Pair {
		Reactor reactor;
		Thread thread;
	}
	
	private static enum State {
		RUNNING, READY, CLOSED
		
	}
}
