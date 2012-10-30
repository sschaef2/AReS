package ares.client;

import ares.shared.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userInfo")
public interface UserInfoService extends RemoteService {
	public UserInfo getUserInfo(String requestUri);
}
