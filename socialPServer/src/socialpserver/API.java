/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socialpserver;

import java.util.Map;
import pserver.data.DBAccess;
import static socialpserver.SocialPServer.socialPServerOutputLogger;
import socialpserver.algorithmic.BronKerboschCliqueCommunityDiscoverer;
import socialpserver.algorithmic.ClustererAlgorithm;
import socialpserver.algorithmic.EdgeBetweennessCommunityDiscoverer;
import socialpserver.algorithmic.MetisCommunityDiscoverer;
import socialpserver.algorithmic.WeakComponentCommunityDiscoverer;
import socialpserver.dataio.CommunityStorerDB;
import socialpserver.dataio.FeatureLoaderDB;
import socialpserver.dataio.GraphLoaderDB;
import socialpserver.dataio.GraphLoaderFile;
import socialpserver.dataio.PSocialDBAccess;
import socialpserver.dataio.UserAssociationsStorerDB;
import socialpserver.dataio.UserFeatureLoaderDB;
import socialpserver.dataio.centroidStrorerDB;

/**
 * 
 * @author dubdub
 * Using socialPSrever project as an .jar Library
 * Includes the basic functionality 
 */
public class API {

    private DBAccess db; // PServer Server DB Access object (DB credentials are given on its creation)
    private final String pServerClient; //PServer client name
    private String sourceTag; // com OR soc (cluster by 'Pserver Community mode' OR 'SocialPServer')
    private int sourceID;
    
    /**
     * Constructor
    * @param db PServer Server DB Access object (DB credentials are given on its creation)
    * @param psClient PServer client name
    * @param sourceTag com OR soc (cluster by 'Pserver Community mode' OR 'SocialPServer')
    * @param sourceID 1 OR 777 (loadUserAccosiations by 1='Pserver Community mode' OR 777='SocialPServer')
    */
    public API(DBAccess db, String psClient, String sourceTag, int sourceID) {
        this.db = db;
        this.pServerClient = psClient;
        this.sourceTag = sourceTag;
        this.sourceID = sourceID;
    }
    
    /**
    * Loads User Friendship from given File and stores it to DB.
    * The social link is User1id-User2id 
    *
    * @param UserAssociationFile relative path to user-association File
    * @example: "datasets/hetrec2011-lastfm-2k/user_friends.dat"
    */
    public void UserAssociationsFileToDB(String UserAssociationFile) {
        
        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, db, sourceTag, sourceID);
        
        // Insert User Associations info into DB
        UserAssociationsStorerDB uadb = new UserAssociationsStorerDB(dbAccess);
        GraphLoaderFile loader = new GraphLoaderFile(UserAssociationFile);
        uadb.friendshipToDB(loader);
        loader.emptyGraph();
        loader = null;
        uadb = null;
        dbAccess = null;
    }
    
    /**
    * Calculate User Communities
    *
    * @param algorithm Which algorithm will be used to produce user communities
    * @values weak-> WeakComponent
    * @values metis-> metis
    * @values betw-> EdgeBetweennessor
    * @values bk-> BronKerbosch
    */
    public void produceCommunities(String algorithm) {
        
        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, db, sourceTag, sourceID);
        
        GraphLoaderDB graphLoaderDB = new GraphLoaderDB(dbAccess);
        graphLoaderDB.loadGraph(); // load user Associations to loader (RAM) -> u can retrive them by loader.getGraph()

        switch (algorithm) {
            case "weak":
                ClustererAlgorithm weak = new WeakComponentCommunityDiscoverer(graphLoaderDB);
                weak.getClusters();
                weak.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                weak.storeCommunities(new CommunityStorerDB(dbAccess));
                weak.storeCentroidFeatures(new centroidStrorerDB(dbAccess));
                weak.clear();
                weak = null;
                System.gc();
                break;
            case "metis":
                ClustererAlgorithm met = new MetisCommunityDiscoverer(graphLoaderDB);
                met.getClusters();
                met.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                met.storeCommunities(new CommunityStorerDB(dbAccess));
                met.storeCentroidFeatures(new centroidStrorerDB(dbAccess));
                met.clear();
                met = null;
                System.gc();
                break;
            case "betw":
                ClustererAlgorithm edg = new EdgeBetweennessCommunityDiscoverer(graphLoaderDB, 5);
                edg.getClusters();
                edg.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                edg.storeCommunities(new CommunityStorerDB(dbAccess));
                edg.storeCentroidFeatures(new centroidStrorerDB(dbAccess));
                edg.clear();
                edg = null;
                System.gc();
                break;
            case "bk":
                ClustererAlgorithm bk = new BronKerboschCliqueCommunityDiscoverer(graphLoaderDB);
                bk.getClusters();
                bk.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                bk.storeCommunities(new CommunityStorerDB(dbAccess));
                bk.storeCentroidFeatures(new centroidStrorerDB(dbAccess));
                bk.clear();
                bk = null;
                System.gc();
                break;
            
            default:
                socialPServerOutputLogger.info("* algorithm does not exist *");
                break;
        }
        graphLoaderDB.emptyGraph();
        graphLoaderDB = null;
        dbAccess = null;
    }

    /**
    * Given the userID returns the Centroid feature list (and Weights) of his Community
    *
    * @param user the User
    * @return MAP ... the Centroid feature list of user Community
    */ 
    public Map<String, Float> getCentroid(String user) {
        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, db, sourceTag, sourceID);
        return dbAccess.getCentroidFeatures(user);
    }

}
