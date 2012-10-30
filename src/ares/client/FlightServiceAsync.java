package ares.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FlightServiceAsync {
	
	public void addFlight(String symbol, AsyncCallback<Void> async);
	public void removeFlight(String symbol, AsyncCallback<Void> async);
	public void getFlights(AsyncCallback<String[]> async);
}
