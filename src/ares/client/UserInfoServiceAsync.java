package ares.client;

import ares.shared.UserInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserInfoServiceAsync {

	public void getUserInfo(String requestUri, AsyncCallback<UserInfo> callback);

}
