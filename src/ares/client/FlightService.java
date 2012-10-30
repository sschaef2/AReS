package ares.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("flight")
public interface FlightService extends RemoteService {

	public void addFlight(String symbol);
	public void removeFlight(String symbol);
	public String[] getFlights();
}
