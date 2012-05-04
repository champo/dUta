package ar.edu.itba.pdc.duta.http.parser;

@SuppressWarnings("serial")
public class ParseException extends Exception {

	int line = -1;

	public ParseException(String msg) {
		
		super(msg);
	}
	
	public ParseException(String msg, int line) {
		
		super(msg);
		this.line = line;
	}

	public ParseException(Throwable e, int line) {
	
		super(e);
		this.line = line;
	}
	
	public int getLine() {

		return line;
	}
	
	@Override
	public String toString() {
		
		if (line == -1) {
			return super.toString();
		}

		String msg = getLocalizedMessage();

		if (msg == null) {
			return this.getClass().getName() + " at line " + line;
		}

		return this.getClass().getName() + " at line " + line + ": " + msg;
	}
}
