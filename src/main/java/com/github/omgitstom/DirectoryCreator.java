package com.github.omgitstom;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.api.ApiKeys;

import java.util.UUID;

/**
 * Created by tom on 4/1/16.
 */

public class DirectoryCreator {

    public static void main(String[] args) throws Exception {

        ApiKey key = ApiKeys.builder().setId("6TQ1PB92U0EAQKJ77RJRVCYVU").setSecret("pw2UHvZM76L16va7lmRC1cCsLFbtAFZR9ZWe7rulKpw").build();

        Client client = Clients.builder().setApiKey(key).setConnectionTimeout(0).build();

        createOrganizationsWithMappings(client);
    }

    private static void createOrganizationsWithMappings(Client client) {


        Application application = client.getDataStore().getResource("https://api.stormpath.com/v1/applications/3j7XJcjlOJCRySKxUzVymw", Application.class);

        Organization organization = client.getDataStore().getResource("https://api.stormpath.com/v1/organizations/4eWLwT5GBEgj85F0gBHG8A", Organization.class);

        for(int i = 0; i < 50; i ++) {
            Directory directory = client.instantiate(Directory.class);
            directory.setName("__deleted__Shared Directory" + UUID.randomUUID());

            directory = client.createDirectory(directory);

            organization.addAccountStore(directory);

            application.addAccountStore(directory);

            System.out.println(i + ". Added and mapped Directory:" + directory.getName());
        }

    }
}
