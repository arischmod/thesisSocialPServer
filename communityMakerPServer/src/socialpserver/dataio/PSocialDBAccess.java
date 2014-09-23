/**
 *
 * @author arix
 */
package socialpserver.dataio;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import pserver.data.PServerResultSet;
import socialpserver.Community;
import socialpserver.SetOfCommunities;

/**
 * DB Access methods
 * @author arix
 */
public class PSocialDBAccess {

    private pserver.data.DBAccess dbAccess;
    private pserver.data.PCommunityDBAccess dbPCommunity;
    private final String psClient;
    private PServerResultSet psResultSet;
    private String associationType;
    private String algorithm;
    private String customName = null;
    // private String mode;
    
    
    ////////////////////
    // Constructors  ///
    ////////////////////

    /**
     * creates a socialDBAccess object that is used to handle queries 
     * @param psClient name of pServer Client 
     * used in queries to Locate the records that has to do with the particular client
     * @param db a pServer DBAccess object used to handle queries
     * needed from socialDBAccess to execute pServer type queries 
     *  without caring about the sourceTag and sourceID
     * @param associationType
     * @param algorithm
     */
    public PSocialDBAccess(String psClient, pserver.data.DBAccess db, String associationType, String algorithm) {
        this.psClient = psClient;
        this.dbAccess = db;
//        this.mode = mode;
        this.associationType = associationType;        
        this.algorithm = algorithm;
        try {
            this.dbPCommunity = new pserver.data.PCommunityDBAccess(db);
            dbAccess.connect();
            dbAccess.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    /**
     * creates a socialDBAccess object that is used to handle queries 
     * @param psClient name of pServer Client 
     * used in queries to Locate the records that has to do with the particular client
     * @param db a pServer DBAccess object used to handle queries
     * needed from socialDBAccess to execute pServer type queries 
     *  without caring about the sourceTag and sourceID
     */
    public PSocialDBAccess(String psClient, pserver.data.DBAccess db) {
        this.psClient = psClient;
        this.dbAccess = db;
        try {
            this.dbPCommunity = new pserver.data.PCommunityDBAccess(db);
            dbAccess.connect();
            dbAccess.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * creates a socialDBAccess object that is used to handle queries 
     * @param psClient name of pServer Client 
     * used in queries to Locate the records that has to do with the particular client
     * @param db a pServer DBAccess object used to handle queries
     * needed from socialDBAccess to execute pServer type queries
     * @param associationType
     */
    public PSocialDBAccess(String psClient, pserver.data.DBAccess db, String associationType) {
        this.psClient = psClient;
        this.dbAccess = db;
//        this.mode = mode;
        this.associationType = associationType;
        this.customName = customName;
        try {
            this.dbPCommunity = new pserver.data.PCommunityDBAccess(db);
            dbAccess.connect();
            dbAccess.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Used when adding CUSTOM communities -> creates a socialDBAccess object that is used to handle queries 
     * @param psClient name of pServer Client 
     * used in queries to Locate the records that has to do with the particular client
     * @param db a pServer DBAccess object used to handle queries
     * needed from socialDBAccess to execute pServer type queries
     * @param customName the name for the custom Community
     */
//    public PSocialDBAccess(String psClient, pserver.data.DBAccess db, String customName) {
//        this.psClient = psClient;
//        this.dbAccess = db;
//        this.customName = customName;
//        try {
//            this.dbPCommunity = new pserver.data.PCommunityDBAccess(db);
//            dbAccess.connect();
//            dbAccess.disconnect();
//        } catch (SQLException ex) {
//            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    
    
    
    /**
     * delete all communities stored in pServer DB
     * (for the specific client)
     * @return 
     */
    public boolean deleteAllCommunities() {
        try {
            dbAccess.reconnect();
            // socialPServer method
            if (customName == null) {
                dbAccess.executeUpdate("DELETE FROM communities WHERE community LIKE '"+algorithm+"_"+associationType+"_"+"%' AND FK_psclient = '" + psClient + "' ;");
            }
            else {
                String communityName = "custom_0_" + customName;
                dbAccess.executeUpdate("DELETE FROM communities WHERE community = '"+communityName+"' AND FK_psclient = '" + psClient + "' ;");
            }
            // PServer method
            //dbAccess.clearUserCommunities(psClient);
        } catch (Exception ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                dbAccess.disconnect();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }

    /**
     * delete all user-communities records stored in pServer DB
     * (for the specific client)
     * @return 
     */
    public boolean deleteAllUserCommunities() {
        try {
            dbAccess.reconnect();
            if (customName == null) {
                dbAccess.executeUpdate("DELETE FROM user_community WHERE community LIKE '"+algorithm+"_"+associationType+"_"+"%' AND FK_psclient = '" + psClient + "' ;");
            }
            else {
                String communityName = "custom_0_" + customName;
                dbAccess.executeUpdate("DELETE FROM user_community WHERE community = '"+communityName+"' AND FK_psclient = '" + psClient + "' ;");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                dbAccess.disconnect();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }

    /**
     * Store a Set of Communities into DB 
     * (each Community is a Set of users) 
     * @param communities the Communities that will be stored
     * @return 
     */
    public boolean storeCommunitiesToDB(SetOfCommunities communities) {
        Integer communityNum = 1;  // Name of community               
        try {
            dbAccess.reconnect();
            String communityName = "custom_0_" + customName;
            
            for (Community community : communities.getCommunities()) {
                
                if (customName == null) {
                    communityName = algorithm + "_" + associationType + "_" + communityNum.toString();
                }
                
                for (String user : community.getCommunityMembers()) {
                    dbAccess.executeUpdate("insert into user_community values ('" + user + "', '" + communityName + "', '" + psClient + "');");
                }
                dbAccess.executeUpdate("insert into communities values ('" + communityName + "', '" + psClient + "');");
                communityNum++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                dbAccess.disconnect();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }
    
    
    
    /**
     * get all Features available on PServer for the specific client
     * @return a Map containing all the features and their default values
     */
    public Map<String, Float> getFeatures() {
        Map<String, Float> featureValue = new HashMap<>();

        try {
            dbAccess.reconnect();
            this.psResultSet = dbAccess.executeQuery("SELECT uf_feature, uf_numdefvalue FROM up_features  WHERE FK_psclient = '" + psClient + "';");
            while (psResultSet.next()) {
                featureValue.put(psResultSet.getRs().getString("uf_feature"), Float.parseFloat(psResultSet.getRs().getString("uf_numdefvalue")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return featureValue;
    }
        
    /**
     *  get all Features that a specific user has rated
     * @param user userID
     * @return a Map containing users features and their rating values
     */
    public Map<String, Float> getUserFeatures(String user) {
        Map<String, Float> featureValue = new HashMap<>();

        try {
            dbAccess.reconnect();
            this.psResultSet = dbAccess.executeQuery("SELECT up_feature, up_value FROM user_profiles  WHERE up_user = '" + user + "' AND FK_psclient = '" + psClient + "';");
            while (psResultSet.next()) {
                featureValue.put(psResultSet.getRs().getString("up_feature"), Float.parseFloat(psResultSet.getRs().getString("up_value")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return featureValue;
    }

    
    
    /**
     * get all the Communities that exist in PServer 
     * for the specific Client
     * @return a Set containing all available community IDs
     */
    public Set<String> getAllCommunities() {
        Set<String> communities = new HashSet<>();
        try {
            dbAccess.reconnect();

            this.psResultSet = dbAccess.executeQuery("SELECT community FROM communities WHERE FK_psclient = '" + psClient + "' AND community LIKE '"+algorithm+"_"+associationType+"_"+"%';");
            while (psResultSet.next()) {
                communities.add(psResultSet.getRs().getString("community").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return communities;
    }

    /**
     * get all users and the Community that they belong to
     * @return a Set of String[] array
     * [0]=userID & [1]=communityID
     */
    public Set<String[]> getUserCommunities() {
        Set<String[]> communities = new HashSet<>();

        try {
            dbAccess.reconnect();
            this.psResultSet = dbAccess.executeQuery("SELECT user, community FROM user_community WHERE FK_psclient = '" + psClient + "'AND community LIKE '"+algorithm+"_"+associationType+"_"+"%';");
            while (psResultSet.next()) {
                String[] team = new String[2];
                team[0] = psResultSet.getRs().getString("user");
                team[1] = psResultSet.getRs().getString("community");

                communities.add(team);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return communities;
    }

 
    
   ///////////////////////
   //  User Association //
   ///////////////////////
    
    /**
     * removes all user friendship records
     * @return 
     */
    public boolean deleteAllUserAssociations() {
        try {
            dbAccess.reconnect();
            // socialPServer method
            
            dbAccess.executeUpdate("DELETE FROM user_associations WHERE FK_psclient = '" + psClient + "' AND type = " + Integer.toString(associationType.hashCode()) + " ;");
            // PServer method
            //this.dbPCommunity.deleteUserAccociations(psClient, 1);
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                dbAccess.disconnect();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }

    /**
     * Inserts UserAssociations (friendship links)
     * in DB
     * @param glLoader loads UserAssociations from a given source
     * @return 
     */
    public boolean GraphtoDB(GraphLoader glLoader) {
        try {
            dbAccess.reconnect();
            Set<String[]> userAssociations = glLoader.getGraph(); // get user Associations from loader 
            for (String[] friendship : userAssociations) {                
                dbAccess.executeUpdate("insert into user_associations values (" + friendship[0] + "," + friendship[1] + ", 1 , " + Integer.toString(associationType.hashCode()) + " , '" + psClient + "')");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                dbAccess.disconnect();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }

    /**
     * Get all UserAssociations (friendship links) from DB 
     * for the specific client 
     * @param threshold
     * @param accosThreshold  Float -> min weight edge that will be taken to account
     * @return a Set of String[] array
     * [0]=user1ID & [1]=user2ID
     */
    public Set<String[]> DBtoGraph(Float threshold) {
        Set<String[]> userAssociations = new HashSet<>();

        try {
            dbAccess.reconnect();
            
            //withOUT THershold
//            this.psResultSet = dbAccess.executeQuery("SELECT user_src, user_dst FROM user_associations WHERE FK_psclient = '" + psClient + "' AND type = " + associationType + ";");
            
            //with THershold
            this.psResultSet = dbAccess.executeQuery("SELECT user_src, user_dst FROM user_associations WHERE FK_psclient = '" + psClient + "' AND type = " + Integer.toString(associationType.hashCode()) + " AND weight >= " + threshold + " ;");
            
            while (psResultSet.next()) {
                String[] user = new String[2];
                user[0] = psResultSet.getRs().getString("user_src");
                user[1] = psResultSet.getRs().getString("user_dst");

                userAssociations.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
                psResultSet.close();                
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                
        return userAssociations;
    }

    
    
    //////////////
    // Centroid //
    //////////////
    
    /**
     * removes all Centroid records
     * @return 
     */    
    public boolean deleteAllCentroids() {
        try {
            dbAccess.reconnect();
            //dbAccess.executeUpdate("DELETE FROM centroids WHERE FK_psclient = '" + psClient + "';");
            if (customName == null)
                dbAccess.executeUpdate("DELETE FROM community_profiles WHERE community LIKE '"+algorithm+"_"+associationType+"_"+"%' AND FK_psclient = '" + psClient + "' ;");
            else {
                String communityName = "custom_0_" + customName;
                // delete previus comunity with the same name
                dbAccess.executeUpdate("DELETE FROM community_profiles WHERE community = '"+communityName+"' AND FK_psclient = '" + psClient + "' ;");
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                dbAccess.disconnect();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }

    /**
     * Store all Centroids into DB 
     * (Centroid->featureList->Values) 
     * @param allCentroidFeatures the Centroids and their features-values
     * @return 
    */
    public boolean storeCentroidsToDB(Map<Integer, Map<String, Float>> allCentroidFeatures) {
        try {
            dbAccess.reconnect();
            if (customName == null) {
                for (Integer centroid : allCentroidFeatures.keySet()) {           
                    Map<String,Float> cFeatures= allCentroidFeatures.get(centroid);
                    for( String feature : cFeatures.keySet()) {
                        String communityName = algorithm + "_" + associationType + "_" + centroid.toString();
                        dbAccess.executeUpdate("insert into community_profiles values ('" + communityName + "', '" + feature + "'," + cFeatures.get(feature) + ", '" + psClient + "')");
                    }
                }
            }
            else {
                String communityName = "custom_0_" + customName;                
                for (Integer centroid : allCentroidFeatures.keySet()) {
                    Map<String,Float> cFeatures= allCentroidFeatures.get(centroid);
                    for( String feature : cFeatures.keySet()) {
                        dbAccess.executeUpdate("insert into community_profiles values ('" + communityName + "', '" + feature + "'," + cFeatures.get(feature) + ", '" + psClient + "')");
                    }
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                dbAccess.disconnect();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
    }
    
    /**
     * Given the communityName returns the Centroid feature list (and Weights) of this Community
     * @param communityName the name of the Community
     * @param pattern a pattern of feature names (when * the pattern = null)
     * @return a Map containing features and their values
     */
    public Map<String, Float> getCentroidFeaturesByCommName(String communityName, String pattern) {
        Map<String, Float> featureValue = new HashMap<>();

        try {
            dbAccess.reconnect();
            if (pattern==null || pattern.trim().equals("")) // if pattern = null -> bring all
                this.psResultSet = dbAccess.executeQuery("SELECT feature, feature_value FROM community_profiles  WHERE community = '" + communityName + "'AND FK_psclient = '" + psClient + "';");
            else {
                pattern = pattern.replace("*", "%");
                this.psResultSet = dbAccess.executeQuery("SELECT feature, feature_value FROM community_profiles  WHERE community = '" + communityName + "'AND FK_psclient = '" + psClient + "' AND feature LIKE '" +pattern+ "';");
            }
            while (psResultSet.next()) {
                featureValue.put(psResultSet.getRs().getString("feature"), Float.parseFloat(psResultSet.getRs().getString("feature_value")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return featureValue;
    }
    
    /**
     *  Given a User returns the Centroid Features of his Community
     * @param user userID
     * @return a Map containing features and their values
     */
    public Map<String, Float> getCentroidFeatures(String user) {
        Map<String, Float> featureValue = new HashMap<>();

        try {
            dbAccess.reconnect();
            this.psResultSet = dbAccess.executeQuery("SELECT community FROM user_community WHERE user = '" + user + "' AND FK_psclient = '" + psClient + "' AND community LIKE '"+algorithm+"_"+associationType+"_"+"%';");
            String community=null;
            if (psResultSet.next())
                community = psResultSet.getRs().getString("community");
            else
                System.out.println("Invalid User");
            this.psResultSet = dbAccess.executeQuery("SELECT feature, feature_value FROM community_profiles  WHERE community = '" + community + "'AND FK_psclient = '" + psClient + "';");
            while (psResultSet.next()) {
                featureValue.put(psResultSet.getRs().getString("feature"), Float.parseFloat(psResultSet.getRs().getString("feature_value")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return featureValue;
    }
}
