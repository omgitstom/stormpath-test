package com.github.sionin;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupList;
import com.stormpath.sdk.oauth.TokenResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class StormpathTest {

    private static final Logger logger = LoggerFactory.getLogger(StormpathTest.class);

    public static final List<Object> garbage = new ArrayList<Object>(1000);
    public static final int E_COUNT = 1;

    public static void main(String[] args) throws Exception {

        System.out.println("Test without any additional user data");
        StormpathClientWrapper clientWrapper0 = new StormpathClientWrapper() {
            protected UserWrapper getUser(Account account) {
                return getUserWithoutAnyData(this, account);
            }

            protected AccountCriteria getAccountsCriteria() {
                return Accounts.criteria().limitTo(50);
            }
        };
        testGetAllUsers(clientWrapper0, 2);

        System.out.println("\n\nTest full user info");
        StormpathClientWrapper clientWrapper2 = new StormpathClientWrapper() {
            protected UserWrapper getUser(Account account) {
                return getFullUser(this, account);
            }

            protected AccountCriteria getAccountsCriteria() {
                return Accounts.criteria().limitTo(50).withCustomData().withGroups().withDirectory();
            }
        };
        testGetAllUsers(clientWrapper2, 2);

//        System.out.println("\n\nTest without user groups iteration");
//        StormpathClientWrapper clientWrapper = new StormpathClientWrapper() {
//            protected UserWrapper getUser(Account account) {
//                return getUserWithoutGroups(this, account);
//            }
//
//            protected AccountCriteria getAccountsCriteria() {
//                return Accounts.criteria().limitTo(50).withCustomData().withDirectory();
//            }
//        };
//        testGetAllUsers(clientWrapper, 2);
//
//        System.out.println("\n\nTest full user info with groups cache");
//        StormpathClientWrapper clientWrapper3 = new StormpathClientWrapper() {
//            protected UserWrapper getUser(Account account) {
//                return getUserWithCachedGroups(this, account);
//            }
//        };
//        testGetAllDirectories(clientWrapper3, 5);
//        testGetAllUsers(clientWrapper3, 5);
//        testAuthorization(clientWrapper3, 10);




    }


    private static void testAuthorization(final StormpathClientWrapper clientWrapper, int testCount) throws Exception {
        String username = System.getProperty("username", "");
        String password = System.getProperty("password", "");
        Pair<UserWrapper, TokenResponse> authority = clientWrapper.authorize(username, password);

        System.out.println("Test Authorization");
        System.out.println("User: " + authority.getKey());
        System.out.println("Token: " + authority.getValue().getAccessToken());

        final UserWrapper sourceUser = authority.getKey();
        final String accessToken = authority.getValue().getAccessToken();

        List<List<Long>> results = new ArrayList<List<Long>>();
        for (int i = 0; i < testCount; i++) {
            results.add(profile(new Callable() {
                @Override
                public Object call() throws Exception {
                    return authorizeByToken(clientWrapper, sourceUser, accessToken);
                }
            }));
            TimeUnit.SECONDS.sleep(1);
        }

        printResults(results);
        garbage.clear();
    }

    private static void testGetAllUsers(final StormpathClientWrapper clientWrapper, int testCount) throws Exception {

        System.out.println("Test get all users");
        List<List<Long>> results = new ArrayList<List<Long>>();
        for (int i = 0; i < testCount; i++) {
            results.add(profile(new Callable<List<UserWrapper>>() {
                @Override
                public List<UserWrapper> call() throws Exception {
                    return clientWrapper.getUsers(Collections.EMPTY_SET);
                }
            }));
            TimeUnit.SECONDS.sleep(1);
        }

        printResults(results);
        garbage.clear();
    }

    private static void testGetAllDirectories(final StormpathClientWrapper clientWrapper, int testCount) throws Exception {

        System.out.println("Test get all directories");
        List<List<Long>> results = new ArrayList<List<Long>>();
        for (int i = 0; i < testCount; i++) {
            results.add(profile(new Callable<List<Directory>>() {
                @Override
                public List<Directory> call() throws Exception {
                    return clientWrapper.getDirectories();
                }
            }));
            TimeUnit.SECONDS.sleep(1);
        }

        printResults(results);
        garbage.clear();
    }

    private static void printResults(List<List<Long>> results) {
        System.out.println(" # count  total    min    max   aver");
        for (int i = 0; i < results.size(); i++) {
            List<Long> times = results.get(i);
            Long total = times.remove(0);
            Long min = Collections.min(times);
            Long max = Collections.max(times);

            System.out.println(String.format("%2d %5d %6d %6d %6d %6d",
                    i, times.size(), total, min, max, total / times.size()));
        }
    }

    private static List<Long> profile(Callable callable) throws Exception {
        List<Long> results = new ArrayList<Long>(E_COUNT + 1);
        results.add(0L);
        long totalTime = System.currentTimeMillis();
        for (int i = 0; i < E_COUNT; i++) {
            long time = System.currentTimeMillis();

            Object value = callable.call();

            results.add(System.currentTimeMillis() - time);
            garbage.add(value);
        }
        totalTime = System.currentTimeMillis() - totalTime;
        results.set(0, totalTime);
        return results;
    }

    private static UserWrapper authorizeByToken(StormpathClientWrapper clientWrapper, UserWrapper sourceUser, String accessToken) throws Exception {
        UserWrapper user = clientWrapper.authorize(accessToken);
        assert sourceUser.toString().equals(user.toString());
        return user;
    }

    private static UserWrapper getFullUser(StormpathClientWrapper clientWrapper, Account account) {
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

    private static UserWrapper getUserWithoutAnyData(StormpathClientWrapper clientWrapper, Account account) {
        return new UserWrapper(
                account.getUsername(),
                account.getEmail(),
                new HashMap<String, Object>(),
                new HashSet<String>()
        );
    }

    private static UserWrapper getUserWithoutGroups(StormpathClientWrapper clientWrapper, Account account) {
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

    private static CustomData getCustomData(StormpathClientWrapper clientWrapper, Account account) {
        return clientWrapper.getResource(account.getCustomData().getHref(), CustomData.class);
    }

    private static Map<String, Set<String>> groupsCache = new HashMap<String, Set<String>>();

    private static UserWrapper getUserWithCachedGroups(StormpathClientWrapper clientWrapper, Account account) {
        GroupList groupList = account.getGroups();
        assert groupList != null;
        String href = groupList.getHref();

        Set<String> groups = getGroupsFromCache(clientWrapper, href);
        return new UserWrapper(
                account.getUsername(),
                account.getEmail(),
                new HashMap<String, Object>(getCustomData(clientWrapper, account)),
                groups
        );
    }

    private static Set<String> getGroupsFromCache(StormpathClientWrapper clientWrapper, String href) {
        Set<String> groups = groupsCache.get(href);
        if (groups != null) {
            return groups;
        }
        groups = new HashSet<String>();

        GroupList groupList = clientWrapper.getResource(href, GroupList.class);
        for (Group group : groupList) {
            groups.add(group.getName());
        }

        groupsCache.put(href, Collections.unmodifiableSet(groups));
        return groups;
    }

}
