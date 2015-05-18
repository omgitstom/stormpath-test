package com.github.sionin;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyList;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.*;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryStatus;
import com.stormpath.sdk.impl.http.ServletHttpRequest;
import com.stormpath.sdk.impl.oauth.authc.AccessTokenAuthenticationRequest;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.OauthAuthenticationResult;
import com.stormpath.sdk.oauth.OauthRequestAuthenticator;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.resource.Resource;
import org.apache.commons.lang3.tuple.Pair;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

//        CacheManager cacheManager = Caches.newCacheManager()
//                .withDefaultTimeToLive(10, TimeUnit.MINUTES) //general default
//                .withDefaultTimeToIdle(10, TimeUnit.MINUTES) //general default
//                .withCache(Caches.forResource(GroupList.class) //Application-specific cache settings
//                        .withTimeToLive(10, TimeUnit.MINUTES)
//                        .withTimeToIdle(10, TimeUnit.MINUTES))
//                .withCache(Caches.forResource(Group.class) //Application-specific cache settings
//                        .withTimeToLive(10, TimeUnit.MINUTES)
//                        .withTimeToIdle(10, TimeUnit.MINUTES))
//                .build(); //build the CacheManager
//        clientBuilder.setCacheManager(cacheManager);

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

        return Pair.of(getUser(account), getToken(account));
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

    public <T extends Resource> T getResource(String href, Class<T> aClass) {
        return client.getResource(href, aClass);
    }


    public List<Directory> getDirectories() {
        return getDirectories(Collections.<String>emptySet());
    }

    public List<Directory> getDirectories(Set<String> names) {

        AccountStoreMappingList accountStoreMappings = application.getAccountStoreMappings();

        AccountStoreMappingList storeMappings = accountStoreMappings;
        if (storeMappings == null) {
            return Collections.emptyList();
        }
        Iterator<AccountStoreMapping> storeMappingIterator = storeMappings.iterator();
        if (storeMappingIterator == null || !storeMappingIterator.hasNext()) {
            return Collections.emptyList();
        }

        List<Directory> directories = iterateStoreMappings(names, storeMappings);
        return directories;
    }

    private List<Directory> iterateStoreMappings(Set<String> names, AccountStoreMappingList storeMappings) {
        List<Directory> directories = new ArrayList<Directory>();

        for (AccountStoreMapping storeMapping : storeMappings) {
            AccountStore accountStore = storeMapping.getAccountStore();
            if (accountStore instanceof Directory) {
                Directory directory = (Directory) accountStore;
                if (directory.getStatus() == DirectoryStatus.ENABLED
                        && (names.isEmpty() || names.contains(directory.getName()))
                        ) {
                    directories.add(directory);
                }
            }
        }
        return directories;
    }


    public List<UserWrapper> getUsers(Set<String> customerIds) {
        AccountList accounts = application.getAccounts(getAccountsCriteria());
        List<UserWrapper> users = iterateAccountList(customerIds, accounts);
        return users;
    }

    private List<UserWrapper> iterateAccountList(Set<String> customerIds, AccountList accounts) {
        int size = accounts.getSize();
        List<UserWrapper> users = new ArrayList<UserWrapper>(size);
        Iterator<Account> iterator = accounts.iterator();

        boolean hasNext = iterator.hasNext();
        while (hasNext) {
            Account account = iterator.next();
//            if (customerIds == null || customerIds.isEmpty() || customerIds.contains(account.getDirectory().getName())) {
            users.add(getUser(account));
//            }
//            long htime = System.currentTimeMillis();
            hasNext = iterator.hasNext();
//            htime = System.currentTimeMillis() - htime;
//            if(htime > 0) System.out.println("hasNext time = " + htime);
        }

        return users;

    }


    protected AccountCriteria getAccountsCriteria() {
        return Accounts.criteria().limitTo(100).withDirectory();
    }
}