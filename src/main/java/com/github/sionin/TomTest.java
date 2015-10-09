package com.github.sionin;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;

/**
 * Created by tom on 6/2/15.
 */
public class TomTest {
    public static void main(String[] args) throws Exception {

        ApiKey key = ApiKeys.builder().setId("31E3D6Z3LZ1WEHBFYTPFK1G68").setSecret("TqDETN0b+27MQXdm9W0tNzsd0FNE6gMlcdSzw15we4g").build();

        Client clientStagingA = Clients.builder()
                                        .setApiKey(key)
                                        .setBaseUrl("https://staging-api-a.stormpath.com/v1")
                                        .build();

        Client clientStagingB = Clients.builder()
                                        .setApiKey(key)
                                        .setBaseUrl("https://staging-api-b.stormpath.com/v1")
                                        .build();

        Client clientPublic = Clients.builder()
                .setApiKey(key)
                .build();

        //runTest(clientStagingA, "Staging A");
        //runTest(clientStagingB, "Staging B");

        Application app = clientPublic.getDataStore().getResource("/applications/3QIMlJKKN2whGCYzXXw1t8", Application.class);
        System.out.println(app.getName());

    }

    public static void runTest(Client client, String environment){
        ApplicationList applications = client.getApplications(Applications.where(Applications.name().eqIgnoreCase("stormpath-seed")));
        Application app = applications.iterator().next();


        long totalTime = System.currentTimeMillis();
        AccountList list = app.getAccounts(Accounts.criteria().limitTo(100).withGroups().withCustomData());

        for(Account account:list){
            String email = account.getEmail();
        }

        totalTime = System.currentTimeMillis() - totalTime;

        System.out.println(environment + ": " + totalTime);
    }
}
