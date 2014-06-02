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
    private String psClient;
    private PServerResultSet psResultSet;
    private String sourceTag;
    private int sourceID;

    /**
     * creates a socialDBAccess object that is used to handle queries 
     * @param psClient name of pServer Client 
     * used in queries to Locate the records that has to do with the particular client
     * @param db a pServer DBAccess object used to handle queries
     * needed from socialDBAccess to execute pServer type queries 
     * @param sourceTag com OR soc (cluster by 'Pserver Community mode' OR 'SocialPServer')
     * @param sourceTag 1 OR 777 (loadUserAccosiations by 1='Pserver Community mode' OR 777='SocialPServer')
     */
    public PSocialDBAccess(String psClient, pserver.data.DBAccess db, String sourceTag, int sourceID) {
        this.psClient = psClient;
        this.dbAccess = db;
        this.sourceTag = sourceTag;
        this.sourceID = sourceID;
        try {
            this.dbPCommunity = new pserver.data.PCommunityDBAccess(db);
            dbAccess.connect();
            dbAccess.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * delete all communities stored in pServer DB
     * (for the specific client)
     */
    public void deleteAllCommunities() {
        try {
            dbAccess.reconnect();
            // socialPServer method
            dbAccess.executeUpdate("DELETE FROM communities WHERE FK_psclient = '" + psClient + "' AND community LIKE '%"+sourceTag+"%';");
            // PServer method
            dbAccess.clearUserCommunities(psClient);
        } catch (Exception ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * delete all user-communities records stored in pServer DB
     * (for the specific client)
     */
    public void deleteAllUserCommunities() {
        try {
            dbAccess.reconnect();
            dbAccess.executeUpdate("DELETE FROM user_community WHERE FK_psclient = '" + psClient + "'AND community LIKE '%"+sourceTag+"%';");
        } catch (Exception ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Store a Set of Communities into DB 
     * (each Community is a Set of users) 
     * @param communities the Communities that will be stored
     */
    public void storeCommunitiesToDB(SetOfCommunities communities) {
        Integer communityNum = 1;  // Name of community               
        try {
            dbAccess.reconnect();
            for (Community community : communities.getCommunities()) {
                String communityName = sourceTag + communityNum.toString();
                for (String user : community.getCommunityMembers()) {
                    dbAccess.executeUpdate("insert into user_community values (" + user + ", '" + communityName + "', '" + psClient + "');");
                }
                dbAccess.executeUpdate("insert into communities values ('" + communityName + "', '" + psClient + "');");
                communityNum++;
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

            this.psResultSet = dbAccess.executeQuery("SELECT community FROM communities WHERE FK_psclient = '" + psClient + "' AND community LIKE '%"+sourceTag+"%';");
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
            this.psResultSet = dbAccess.executeQuery("SELECT user, community FROM user_community WHERE FK_psclient = '" + psClient + "'AND community LIKE '%"+sourceTag+"%';");
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

    /**
     * removes all user friendship records
     */
    public void deleteAllUserAssociations() {
        try {
            dbAccess.reconnect();
            // socialPServer method
            dbAccess.executeUpdate("DELETE FROM user_associations WHERE FK_psclient = '" + psClient + "' AND type = " + sourceID + " ;");
            // PServer method
            //this.dbPCommunity.deleteUserAccociations(psClient, 1);
        } catch (SQLException ex) {
            Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dbAccess.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(PSocialDBAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Inserts UserAssociations (friendship links)
     * in DB
     * @param glLoader loads UserAssociations from a given source
     */
    public void GraphtoDB(GraphLoader glLoader) {
        try {
            dbAccess.reconnect();
            Set<String[]> userAssociations = glLoader.getGraph(); // get user Associations from loader 
            for (String[] friendship : userAssociations) {                
                dbAccess.executeUpdate("insert into user_associations values (" + friendship[0] + "," + friendship[1] + ", 1 , " + sourceID + " , '" + psClient + "')");
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
    }

    /**
     * Get all UserAssociations (friendship links) from DB 
     * for the specific client 
     * @return a Set of String[] array
     * [0]=user1ID & [1]=user2ID
     */
    public Set<String[]> DBtoGraph() {
        Set<String[]> userAssociations = new HashSet<>();

        try {
            dbAccess.reconnect();
            this.psResultSet = dbAccess.executeQuery("SELECT user_src, user_dst FROM user_associations WHERE FK_psclient = '" + psClient + "' AND type = " + sourceID + " ;");

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

}
