package ares.client;

import ares.shared.UserInfo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

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
		// Check login status using login service.
		UserInfoServiceAsync userService = GWT.create(UserInfoService.class);
		userService.getUserInfo(GWT.getHostPageBaseURL(), new AsyncCallback<UserInfo>()
		{
			public void onFailure(Throwable error) {
		    }

		      public void onSuccess(UserInfo result) 
		      {
		        userInfo = result;
		        if (userInfo.isLoggedIn())
		        {
		        	// Check if it is an administrator
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
}
