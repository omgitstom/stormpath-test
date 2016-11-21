package com.github.omgitstom;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.directory.Directories;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.directory.DirectoryList;
import com.stormpath.sdk.organization.OrganizationAccountStoreMapping;
import com.stormpath.sdk.organization.OrganizationAccountStoreMappingList;

import java.util.ArrayList;

/**
 * Created by tom on 4/1/16.
 */

public class Deleter {

    public static void main(String[] args) throws Exception {

        ApiKey key = ApiKeys.builder().setId("YOUR_API_KEY").setSecret("YOUR_API_SECRET").build();

        Client client = Clients.builder().setApiKey(key).setConnectionTimeout(0).build();

        deleteDirectories(client);
    }

    private static void deleteDirectories(Client client) {


        DirectoryList directoryList = client.getCurrentTenant().getDirectories(Directories.where(Directories.name().eqIgnoreCase("__deleted__*")));
        ArrayList<Directory> listToDelete = new ArrayList<Directory>();
        int i = 1;

        for(Directory directory : directoryList){
            listToDelete.add(directory);
        }

        for(Directory directory : listToDelete) {
            //Delete Organization Account Store Mappings
            OrganizationAccountStoreMappingList organizationMappingList = directory.getOrganizationAccountStoreMappings();

            for (OrganizationAccountStoreMapping mapping : organizationMappingList) {
                mapping.delete();
            }

            System.out.println(i + ". Deleting directory: " + directory.getName());
            directory.delete();
            i++;
        }
    }
}
