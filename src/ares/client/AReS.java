package ares.client;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ares.shared.UserInfo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
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
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/** 
	 * Holds the Google account information for a logged in user. 
	 */
	private UserInfo userInfo = null;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() 
	{
		// Check login status using login service
		UserInfoServiceAsync userService = GWT.create(UserInfoService.class);
		userService.getUserInfo(GWT.getHostPageBaseURL(), new AsyncCallback<UserInfo>()
		{
			// TO DO: 
			// Implement error handling 
			public void onFailure(Throwable error) {
		    }

		      public void onSuccess(UserInfo result) 
		      {
		        userInfo = result;
		        if (userInfo.isLoggedIn())
		        {
		        	// Check if user is an administrator
		        	// Remember to add email addresses for Aya & Leslie
		        	if (userInfo.getEmailAddress() == "sschaef2@asu.edu")
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
		VerticalPanel mainPanel = new VerticalPanel();
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
		
		flightsTable.setText(0, 0, "From");
		flightsTable.setText(0, 1, "To");
		flightsTable.setText(0, 2, "Price");
		flightsTable.setText(0, 3, "Time");
		flightsTable.setText(0, 4, "Class");
		flightsTable.setText(0, 5, "Type");
		flightsTable.setText(0, 6, "Remove");
		
		// TO DO:
		// Get flight information from the Datastore and populate the FlexTable
		
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
		
	}
	
	/**
	 * This page will load if the user is not logged in, and will only display tables
	 * of available domestic and international flights (???). 
	 */
	private void loadAresDisplay()
	{
		
	}
	
	/**
	 * Adds a flight to the table and to the datastore. 
	 */
	private void addFlight()
	{
		
	}
	
	/**
	 * Sends confirmation of the user's booked ticket to their logged in Gmail address. 
	 * @throws UnsupportedEncodingException 
	 */
	private void sendTicketConfirmation() throws UnsupportedEncodingException
	{
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        // TO DO:
        // Setup message body to include information for booked ticket (i.e. To, From, Price, Time)
        String msgBody = "Thank you for booking your flight with AReS. Please see ticket information below:";

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
            //TO DO: 
        	// Implement error handling
        } catch (MessagingException e) {
        	//TO DO: 
        	// Implement error handling
        }
	}
}
