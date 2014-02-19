/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import java.util.Map;

/**
 * Loads all features available in pServer for the specific client
 * from a given database.
 * @author arix
 */
public class FeatureLoaderDB implements FeatureLoader{
    private PSocialDBAccess dbAccess;
    private Map<String, Float> features;

    /**
     * Creates a Feature db loader that uses a given
     * pServer DBAccess to handle queries.     
     * @param dbAccess pServer DBAccess instance
     */    
    public FeatureLoaderDB(PSocialDBAccess dbAccess) {
        this.dbAccess = dbAccess;
    }

    @Override
    public Map<String, Float> getFeatures() {        
        return features;
    }

    @Override
    public void loadFeatures() {
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    loading pServer features from DB...");
        features = dbAccess.getFeatures();
    }
    
}
