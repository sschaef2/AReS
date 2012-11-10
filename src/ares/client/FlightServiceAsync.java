package ares.client;

import java.util.List;

import ares.shared.Flight;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FlightServiceAsync {
	
	public void addFlight(Flight flight, AsyncCallback<Void> async);
	public void removeFlight(Flight flight, AsyncCallback<Void> async);
	public void getFlights(AsyncCallback<List<Flight>> async);
	public void getFlights(String type, AsyncCallback<List<Flight>> async);
}
