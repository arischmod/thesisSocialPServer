/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import java.util.Map;

/**
 * Loads all features that are rated by the users from a given database.
 * @author arix
 */
public class UserFeatureLoaderDB implements UserFeatureLoader{
    private PSocialDBAccess dbAccess;
  
    /**
     * Creates a User-Feature db loader that uses a given
     * pServer DBAccess to handle queries.     
     * @param dbAccess pServer DBAccess instance
     */
    public UserFeatureLoaderDB(PSocialDBAccess dbAccess) {
        this.dbAccess = dbAccess;
    }

    @Override
    public Map<String, Float> getUserFeatures(String user) {
        //socialpserver.SocialPServer.socialPServerOutputLogger.info("    geting User friendship data from DB...");
        return dbAccess.getUserFeatures(user);
    }
    
}
