package com.github.sionin;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.oauth.TokenResponse;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class StormpathTest {

    private static final Logger logger = LoggerFactory.getLogger(StormpathTest.class);

    public static final List<UserWrapper> allUsers = new ArrayList<UserWrapper>(1000);
    public static final int E_COUNT = 10;

    public static void main(String[] args) throws Exception {

        System.out.println("\n\n\nTest get user info by access token without user groups iteration");
        StormpathClientWrapper clientWrapper = new StormpathClientWrapper() {
            protected UserWrapper getUser(Account account) {
                return getUserWithoutGroups(account);
            }
        };
        testClient(clientWrapper);

        System.out.println("\n\n\nTest get user info by access token");
        StormpathClientWrapper clientWrapper2 = new StormpathClientWrapper() {
            protected UserWrapper getUser(Account account) {
                return getFullUser(account);
            }
        };
        testClient(clientWrapper2);

    }


    private static void testClient(StormpathClientWrapper clientWrapper) throws Exception {
        String username = System.getProperty("username", "");
        String password = System.getProperty("password", "");
        Pair<UserWrapper, TokenResponse> authority = clientWrapper.authorize(username, password);

        System.out.println("User: " + authority.getKey());
        System.out.println("Token: " + authority.getValue().getAccessToken());

        final UserWrapper sourceUser = authority.getKey();
        final String accessToken = authority.getValue().getAccessToken();

        List<List<Long>> results = new ArrayList<List<Long>>();
        for (int i = 0; i < 10; i++) {
            results.add(profile(clientWrapper, sourceUser, accessToken));
        }

        System.out.println(" # count total   min   max  aver");
        for (int i = 0; i < results.size(); i++) {
            List<Long> times = results.get(i);
            Long total = times.remove(0);
            Long min = Collections.min(times);
            Long max = Collections.max(times);

            System.out.println(String.format("%2d %5d %5d %5d %5d %5d", i, times.size(),
                    total, min, max, total / times.size()));
        }
    }

    private static List<Long> profile(StormpathClientWrapper clientWrapper, UserWrapper sourceUser, String accessToken) throws Exception {
        List<Long> results = new ArrayList<Long>(E_COUNT + 1);
        long totalTime = System.currentTimeMillis();
        for (int i = 0; i < E_COUNT; i++) {
            long time = System.currentTimeMillis();

            UserWrapper userWrapper = authorizeByToken(clientWrapper, sourceUser, accessToken);

            results.add(System.currentTimeMillis() - time);
            allUsers.add(userWrapper);
        }
        totalTime = System.currentTimeMillis() - totalTime;
        results.add(0, totalTime);
        return results;
    }

    private static UserWrapper authorizeByToken(StormpathClientWrapper clientWrapper, UserWrapper sourceUser, String accessToken) throws Exception {
        UserWrapper user = clientWrapper.authorize(accessToken);
        assert sourceUser.toString().equals(user.toString());
        return user;
    }

    private static UserWrapper getFullUser(Account account) {
        Set<String> groups = new HashSet<String>();
        for (Group group : account.getGroups()) {
            groups.add(group.getName());
        }
        return new UserWrapper(
                account.getUsername(),
                account.getEmail(),
                new HashMap<String, Object>(account.getCustomData()),
                groups
        );
    }

    private static UserWrapper getUserWithoutGroups(Account account) {
        Set<String> groups = new HashSet<String>();
        GroupList groupList = account.getGroups();
        assert groupList != null;
        return new UserWrapper(
                account.getUsername(),
                account.getEmail(),
                new HashMap<String, Object>(account.getCustomData()),
                groups
        );
    }

}
