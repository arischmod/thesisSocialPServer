/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import socialpserver.SetOfCommunities;

/**
 * Store all communities (clusters of users) produced from 
 * to a given database.
 * @author arix
 */
public class CommunityStorerDB implements ICommunityStorer {
    private PSocialDBAccess dbAccess;    
    private String type;    
  
    /**
     * Creates a community (set of userIDs)
     * db storer that uses a given pServer DBAccess to handle insertions.     
     * @param dbAccess pServer DBAccess instance
     */
    public CommunityStorerDB(PSocialDBAccess dbAccess, String type) {        
        this.dbAccess = dbAccess;
        this.type = type;
    }

    @Override
    public boolean storeAll(SetOfCommunities communities) {
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    deleting previous Communities from DB...");        
        try {
            dbAccess.deleteAllCommunities(type);
            dbAccess.deleteAllUserCommunities(type);   // delete User-Communities from DB
            socialpserver.SocialPServer.socialPServerOutputLogger.info("    inserting Communities to DB...");            
            dbAccess.storeCommunitiesToDB(communities, type);  // insert Communities to DB     
            return true;
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
