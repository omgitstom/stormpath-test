package com.github.sionin;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.impl.http.ServletHttpRequest;
import com.stormpath.sdk.impl.oauth.authc.AccessTokenAuthenticationRequest;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.OauthAuthenticationResult;
import com.stormpath.sdk.oauth.OauthRequestAuthenticator;
import com.stormpath.sdk.oauth.TokenResponse;
import javafx.util.Pair;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public abstract class StormpathClientWrapper {

    protected Client client;
    protected Application application;

    public StormpathClientWrapper() {
        ApiKey apiKey = ApiKeys.builder().
                setId(System.getProperty("apiKeyId")).
                setSecret(System.getProperty("apiKeySecret")).
                build();
        ClientBuilder clientBuilder = Clients.builder().setApiKey(apiKey);
        String baseUrl = System.getProperty("apiBaseUrl", "");
        if (baseUrl != null && !baseUrl.trim().isEmpty()) {
            clientBuilder.setBaseUrl(baseUrl.trim());
        }

        client = clientBuilder.build();
        application = getApplication(System.getProperty("application"));
    }

    protected Application getApplication(String applicationName) {
        ApplicationList applications = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(applicationName)));
        return applications.iterator().next();
    }

    protected Account getAccount(String username) {
        AccountList accounts = application.getAccounts(Accounts.where(Accounts.username().eqIgnoreCase(username)).withGroups().withCustomData());
        return accounts.iterator().next();
    }

    public Pair<UserWrapper, TokenResponse> authorize(String username, String password) throws Exception {
        AuthenticationRequest usernamePasswordRequest = new UsernamePasswordRequest(username, password);
        AuthenticationResult authenticationResult = application.authenticateAccount(usernamePasswordRequest);
        Account account = authenticationResult.getAccount();

        return new Pair<UserWrapper, TokenResponse>(getUser(account), getToken(account));
    }

    public UserWrapper authorize(String token) throws Exception {
        OauthRequestAuthenticator oauthRequestAuthenticator = application.authenticateOauthRequest(
                getHttpServletRequestMock("Bearer " + token));
        OauthAuthenticationResult authenticationResult = oauthRequestAuthenticator.execute();

        Account account = authenticationResult.getAccount();

        return getUser(account);
    }

    protected TokenResponse getToken(Account account) throws Exception {

        ApiKey apiKey = getApiKey(account);
        String sign = apiKey.getId() + ":" + apiKey.getSecret();

        AuthenticationRequest authenticationRequest = new AccessTokenAuthenticationRequest(
                new ServletHttpRequest(getHttpServletRequestMock("Basic " + new String(Base64.encodeBase64(sign.getBytes())))));
        AccessTokenResult authenticationResult = (AccessTokenResult) application.authenticateAccount(authenticationRequest);
        TokenResponse tokenResponse = authenticationResult.getTokenResponse();

        return tokenResponse;
    }

    private HttpServletRequest getHttpServletRequestMock(String authorization) {
        HttpServletRequest mock = Mockito.mock(HttpServletRequest.class);
        when(mock.getMethod()).thenReturn("POST");
        when(mock.getContentType()).thenReturn("application/x-www-form-urlencoded");
        when(mock.getHeader(eq("Authorization"))).thenReturn(authorization);
        when(mock.getParameter(eq("grant_type"))).thenReturn("client_credentials");
        return mock;
    }


    private ApiKey getApiKey(Account account) {
        ApiKey apiKey;
        ApiKeyList apiKeys = account.getApiKeys();
        Iterator<ApiKey> iterator = apiKeys.iterator();
        if (iterator.hasNext()) {
            apiKey = iterator.next();
        } else {
            apiKey = account.createApiKey();
        }
        return apiKey;
    }

    protected abstract UserWrapper getUser(Account account);
}