package ares.server;

import ares.client.UserInfoService;
import ares.shared.UserInfo;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserInfoServiceImpl extends RemoteServiceServlet implements
		UserInfoService {
	
	public UserInfo getUserInfo(String requestUri) 
	{
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        UserInfo userInfo = new UserInfo();

        if (user != null)
        {
        	userInfo.setNickname(user.getNickname());
        	userInfo.setLoggedIn(true); 
        	userInfo.setEmailAddress(user.getEmail());
        	userInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
        }
        
        else
        {
        	userInfo.setLoggedIn(false);
        	userInfo.setLoginUrl(userService.createLoginURL(requestUri));
        }
        
		return userInfo;
	}

}
