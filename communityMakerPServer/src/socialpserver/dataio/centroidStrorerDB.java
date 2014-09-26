/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socialpserver.dataio;

import java.util.Map;

/**
 *Store all communities (clusters of users) produced from 
 * to a given database.
 * @author arix
 */
public class centroidStrorerDB {
    
    private PSocialDBAccess dbAccess;    
  
    /** 
    * Store Centroids and it's features 
    * (the Average of features in the community)
    * @author arix
    */
    public centroidStrorerDB(PSocialDBAccess dbAccess) {        
        this.dbAccess = dbAccess;
    }

    /**
     * Store all Centroids of the communities
     * to a given database.
     * @param sourceTag com OR soc (cluster by 'Pserver Community mode' OR 'SocialPServer')
     */   
    public boolean storeAll(Map<Integer, Map<String, Float>>  allCentroidFeatures) {
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    deleting previous Centroids from DB...");        
        try {
            dbAccess.deleteAllCentroids();
            socialpserver.SocialPServer.socialPServerOutputLogger.info("    inserting Centroids to DB...");            
            boolean returnValue = dbAccess.storeCentroidsToDB(allCentroidFeatures);  // insert Centroids to DB               
            socialpserver.SocialPServer.socialPServerOutputLogger.info("    end of DBAccess class Usage !!");   
            return returnValue;
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

