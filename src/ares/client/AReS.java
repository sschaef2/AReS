package ares.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/*
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
*/

import ares.shared.Flight;
import ares.shared.UserInfo;
/*
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
*/
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AReS implements EntryPoint {
	
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	@SuppressWarnings("unused")
	private static final String SERVER_ERx	ROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/** 
	 * Holds the Google account information for a logged in user. 
	 */
	private UserInfo userInfo = null;
	
	/**
	 * This is the entry point method.
	 */
	
	/**
	 * GWT Components.
	 */
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable flightsTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox toInputBox = new TextBox();
	private TextBox fromInputBox = new TextBox();
	private TextBox priceInputBox = new TextBox();
	private TextBox timeInputBox = new TextBox();
	private ListBox classComboBox = new ListBox();
	private ListBox typeComboBox = new ListBox();
	private Button addFlightButton = new Button("Add");
	private Label toLabel = new Label("To");
	private Label fromLabel = new Label("From");
	private Label priceLabel = new Label("Price");
	private Label timeLabel = new Label("Time");
	private ArrayList<Flight> flights = new ArrayList<Flight>();
	
	private final FlightServiceAsync flightService = GWT.create(FlightService.class);
	
	public void onModuleLoad() 
	{
		// Check login status using login service
		UserInfoServiceAsync userService = GWT.create(UserInfoService.class);
		userService.getUserInfo(GWT.getHostPageBaseURL(), new AsyncCallback<UserInfo>()
		{
			public void onFailure(Throwable error) {
				handleError(error);
		    }

		      public void onSuccess(UserInfo result) 
		      {
		        userInfo = result;
		        if (userInfo.isLoggedIn())
		        {
		        	// Check if user is an administrator
		        	// Remember to add email addresses for Aya & Leslie
		        	if (userInfo.getEmailAddress() == "sean.schaefer2@gmail.com")
		        			loadAresAdmin();
		        	else
		        		loadAresSearch();
		        }
		        
		        else
		        {
		        	loadAresDisplay();
		        }
		    }
		});
	}
	
	/**
	 * This page will load if the user is logged in as an administrator, and then can 
	 * add and remove flights from the persistence Datastore. 
	 */
	private void loadAresAdmin()
	{
		// Init GWT components
		// May need these declared outside of this method, for use in addFlight()
		/*VerticalPanel mainPanel = new VerticalPanel();
		FlexTable flightsTable = new FlexTable();
		HorizontalPanel addPanel = new HorizontalPanel();
		TextBox toInputBox = new TextBox();
		TextBox fromInputBox = new TextBox();
		TextBox priceInputBox = new TextBox();
		TextBox timeInputBox = new TextBox();
		ListBox classComboBox = new ListBox();
		ListBox typeComboBox = new ListBox();
		Button addFlightButton = new Button();
		Label toLabel = new Label("To");
		Label fromLabel = new Label("From");
		Label priceLabel = new Label("Price");
		Label timeLabel = new Label("Time");
		*/
		
		flightsTable.setText(0, 0, "From");
		flightsTable.setText(0, 1, "To");
		flightsTable.setText(0, 2, "Price");
		flightsTable.setText(0, 3, "Time");
		flightsTable.setText(0, 4, "Class");
		flightsTable.setText(0, 5, "Type");
		flightsTable.setText(0, 6, "Remove");
		flightsTable.getRowFormatter().addStyleName(0, "flightListHeader");
		flightsTable.addStyleName("watchList");
		
		loadFlights();
		
		classComboBox.addItem("Economy");
		classComboBox.addItem("Business");
		typeComboBox.addItem("Domestic");
		typeComboBox.addItem("International");
		
		// Add the parameters for adding a flight to the HorizontalPanel
		addPanel.add(fromLabel);
		addPanel.add(fromInputBox);addPanel.add(toLabel);
		addPanel.add(toInputBox);
		addPanel.add(priceLabel);
		addPanel.add(priceInputBox);
		addPanel.add(timeLabel);
		addPanel.add(timeInputBox);
		addPanel.add(classComboBox);
		addPanel.add(typeComboBox);
		addPanel.add(addFlightButton);
		
		// Setup the VerticalPanel
		mainPanel.add(addPanel);
		mainPanel.add(flightsTable);
		
		// Associate the RootPanel to the HTML page
		RootPanel.get("flightList").add(mainPanel);
		toInputBox.setFocus(true);
		
		// Add Handler for when addFlightButton is clicked
		addFlightButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addFlight();
			}
		});
	}

	/**
	 * This page will load if the user is logged in, and will allow for searching/booking of 
	 * available domestic and international flights. 
	 */
	private void loadAresSearch() 
	{
		loadAresAdmin();
	}
	
	/**
	 * This page will load if the user is not logged in, and will only display tables
	 * of available domestic and international flights (???). 
	 */
	private void loadAresDisplay()
	{
		// Assemble login panel.
		Anchor signInLink = new Anchor("Sign In");
		VerticalPanel loginPanel = new VerticalPanel();
		Label loginLabel = new Label("Please sign in to your Google Account to access the AReS application.");
	    signInLink.setHref(userInfo.getLoginUrl());
	    loginPanel.add(loginLabel);
	    loginPanel.add(signInLink);
	    RootPanel.get("flightList").add(loginPanel);
	}
	
	/**
	 * Adds a flight to the table and to the datastore. 
	 */
	private void addFlight()
	{
		 String location = fromInputBox.getText().trim();
		 if (location.isEmpty())
		 {
		 		Window.alert(location + " is not a valid entry for starting location.");
		 		fromInputBox.selectAll();
		 		return;
		 }
		 String destination = toInputBox.getText().trim();
		 if (destination.isEmpty())
		 {
		 		Window.alert(destination + " is not a valid entry for destination.");
		 		toInputBox.selectAll();
		 		return;
		 }
		 String time = timeInputBox.getText().trim();
		 if (time.isEmpty())
		 {
		 		Window.alert(time + " is not a valid entry for time.");
		 		timeInputBox.selectAll();
		 		return;
		 }
		 double price = new Double(priceInputBox.getText().trim());
		 if (price <= 0.0)
		 {
		 		Window.alert(price + " is not a valid entry for price.");
		 		priceInputBox.selectAll();
		 		return;
		 }
		 
		 String seatClass = classComboBox.getItemText(classComboBox.getSelectedIndex());
		 String type = typeComboBox.getItemText(typeComboBox.getSelectedIndex());
		 
		 Flight flight = new Flight(location, destination, time, price, seatClass, type);
		 if (flights.contains (flight))
			 return;
		 
		//Add the flight to the Datastore
		addFlight(flight);
	}
	
	
	private void addFlight(final Flight flight)
	{
		flightService.addFlight(flight, 
				new AsyncCallback<Void>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Void ignore) {
				displayFlight(flight);
			}
		});
	}
	
	private void loadFlights()
	{
		flightService.getFlights(new AsyncCallback<List<Flight>>(){
			public void onFailure(Throwable error) {
				handleError(error);
		      }
		      public void onSuccess(List<Flight> flights) {
		        if (flights.isEmpty())
		        	Window.alert("All flights are empty");
		        displayFlights(flights);
		      }
		});
	}
	
	private void displayFlights(List<Flight> flights)
	{
		for (Flight flight : flights) {
			displayFlight(flight);
		}	
	}
	
	private void displayFlight(final Flight flight){
		int row = flightsTable.getRowCount();
		flights.add(flight);
		
		flightsTable.setText(row, 0, flight.getLocation());
		flightsTable.setText(row, 1, flight.getDestination());
		flightsTable.setText(row, 2, Double.toString(flight.getPrice()));
		flightsTable.setText(row, 3, flight.getTime());
		flightsTable.setText(row, 4, flight.getSeatClass());
		flightsTable.setText(row, 5, flight.getType());
		
		// Add a button to remove this flight from the table.
	    Button removeFlightButton = new Button("x");
	    removeFlightButton.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	          removeFlight(flight);
	        }
	      });
	    
	      flightsTable.setWidget(row, 6, removeFlightButton);	
	}
	
	private void removeFlight(final Flight flight) {
	    flightService.removeFlight(flight, new AsyncCallback<Void>() {
	      public void onFailure(Throwable error) {
	      }
	      public void onSuccess(Void ignore) {
	        undisplayFlight(flight);
	      }
	    });
	  }
	
	private void undisplayFlight(Flight flight)
	{
		int removedIndex = flights.indexOf(flight);
	    flights.remove(removedIndex);
	    flightsTable.removeRow(removedIndex+1);
	}
	
	/**
	 * Sends confirmation of the user's booked ticket to their logged in Gmail address. 
	 * @throws UnsupportedEncodingException 
	 */
	private void sendTicketConfirmation(String flightTo, String flightFrom, String flightPrice, String flightTime,
			String flightClass, String flightType) throws UnsupportedEncodingException
	{
		/*
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "Thank you for booking your flight with AReS. Please see ticket information below:"
        		+ "\n\nFrom: " + flightFrom + "\nTo: " + flightTo + "\nTime: " + flightTime + "\nPrice: "
        		+ flightPrice + "\nClass: " + flightClass + "\nType: " + flightType + 
        		"\n\nIf you have any questions about your reservation, please feel free to "
        		+ "contact us at sschaef2@asu.edu.";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("sschaef2@asu.edu", "AReS Admin"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(userInfo.getEmailAddress(), 
                            		 userInfo.getNickname()));
            msg.setSubject("Your Ticket Information with AReS");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (AddressException e) {
        	handleError(e);
        } catch (MessagingException e) {
        	handleError(e);
        }
        
		String msgBody = "Thank you for booking your flight with AReS. Please see ticket information below:"
        		+ "\n\nFrom: " + flightFrom + "\nTo: " + flightTo + "\nTime: " + flightTime + "\nPrice: "
        		+ flightPrice + "\nClass: " + flightClass + "\nType: " + flightType + 
        		"\n\nIf you have any questions about your reservation, please feel free to "
        		+ "contact us at sschaef2@asu.edu.";
		
			MailService service = MailServiceFactory.getMailService();
			MailService.Message msg = new MailService.Message();
	       
	        msg.setSender("sschaef2@asu.edu");
	        msg.setTo(userInfo.getEmailAddress());
	       
	        msg.setSubject("Your Ticket Information with AReS");
	        msg.setTextBody(msgBody);
	       
	        try {
	            service.send(msg);
	        } catch (IOException e) {
	            handleError(e);
	            e.printStackTrace();
	        }
	        */
	}
	
	/**
	 * Sends confirmation of cancellation of a ticket to the user's Gmail address.
	 * @throws UnsupportedEncodingException
	 */
	private void sendCancelConfirmation(String flightTo, String flightFrom, String flightPrice, String flightTime,
			String flightClass, String flightType) throws UnsupportedEncodingException
	{
		/*Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "Your flight with AReS has been cancelled, as detailed below:" + 
        		"\n\nFrom: " + flightFrom + "\nTo: " + flightTo + "\nTime: " + flightTime + "\nPrice: "
        		+ flightPrice + "\nClass: " + flightClass + "\nType: " + flightType + 
        		"\n\nIf you have any questions about your cancellation, please feel free to "
        		+ "contact us at sschaef2@asu.edu.";

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("sschaef2@asu.edu", "AReS Admin"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(userInfo.getEmailAddress(), 
                            		 userInfo.getNickname()));
            msg.setSubject("Your Ticket Cancellation with AReS");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (AddressException e) {
        	handleError(e);
        } catch (MessagingException e) {
        	handleError(e);
        }
        
		
		 String msgBody = "Your flight with AReS has been cancelled, as detailed below:" + 
	        		"\n\nFrom: " + flightFrom + "\nTo: " + flightTo + "\nTime: " + flightTime + "\nPrice: "
	        		+ flightPrice + "\nClass: " + flightClass + "\nType: " + flightType + 
	        		"\n\nIf you have any questions about your cancellation, please feel free to "
	        		+ "contact us at sschaef2@asu.edu.";
		
		MailService service = MailServiceFactory.getMailService();
		MailService.Message msg = new MailService.Message();
       
        msg.setSender("sschaef2@asu.edu");
        msg.setTo(userInfo.getEmailAddress());
       
        msg.setSubject("Your Ticket Cancellation with AReS");
        msg.setTextBody(msgBody);
       
        try {
            service.send(msg);
        } catch (IOException e) {
            handleError(e);
            e.printStackTrace();
        }
        */
	}
	
	private void displayHelp()
	{
		/* final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("AReS Help");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		closeButton.getElement().setId("closeButton");
		
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>To request assistance with AReS, please contact the " +
				"<a href='mailto:sschaef2@asu.edu'>AReS Support Email</a></b>"));
		
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
		
		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		*/
		
		Window.alert("To request assistance with AReS, please contact the " +
				"AReS Support Email at sschaef2@asu.edu");
	}
	
	private void displayTicketInformation(String flightTo, String flightFrom, String flightPrice, String flightTime,
			String flightClass, String flightType)
	{
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Your Ticket Information");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		closeButton.getElement().setId("closeButton");
		
		// TODO: 
		// Finish method
		VerticalPanel dialogVPanel = new VerticalPanel();
		
	}
	
	private void handleError(Throwable error) 
	{
	    Window.alert(error.getMessage());
	 
	}
}
