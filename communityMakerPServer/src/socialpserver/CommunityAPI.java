/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package socialpserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
public class CommunityAPI {

    private DBAccess pServerDB; // PServer Server DB Access object (DB credentials are given on its creation)
    private final String pServerClient; //PServer client name

    /**
     * Constructor
    * @param pServerDB PServer Server DB Access object (DB credentials are given on its creation)
    * @param psClient PServer client name
    */
    public CommunityAPI(DBAccess pServerDB, String psClient) {
        this.pServerDB = pServerDB;
        this.pServerClient = psClient;
    }
    
    /**
     * Produce User Communities
     *
     * @param algorithm Which algorithm will be used to produce user communities
     *   @values weak-> WeakComponent
     *   @values metis-> metis
     *   @values betw-> EdgeBetweennessor
     *   @values bk-> BronKerbosch
     * @param mode com OR soc or ftr (cluster by 'Pserver User Community mode' OR 'SocialPServer' OR 'PServer Feature Group')
     * @param associationType 1 OR 777 OR etc... (loadUserAccosiations by 1='cosine Similarity' OR 777='SocialPServer')
     * @param accosThreshold  Float -> min weight edge that will be taken to account
     * @param parameters All the needed Parameters
     */
    public boolean makeCommunities(String algorithm, String associationType, Map<String, String> parameters) {

        Float accosThreshold;
        if (parameters.get("accosThreshold") == null)
            accosThreshold = new Float(0);
        else    
            accosThreshold = Float.parseFloat(parameters.get("accosThreshold"));
        
        PSocialDBAccess dbAccess;
        dbAccess = new PSocialDBAccess(pServerClient, pServerDB, associationType, algorithm);
        
        GraphLoaderDB graphLoaderDB = new GraphLoaderDB(dbAccess);
        graphLoaderDB.loadGraph(accosThreshold, "user"); // load user Associations to loader (RAM) -> u can retrive them by loader.getGraph()
        boolean toReturn = true;
        switch (algorithm) {
            case "weak":
                ClustererAlgorithm weak = new WeakComponentCommunityDiscoverer(graphLoaderDB);
                weak.getClusters();
                weak.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                if (!weak.storeCommunities(new CommunityStorerDB(dbAccess, "user"))) 
                    toReturn = false;
                if (!weak.storeCentroidFeatures(new centroidStrorerDB(dbAccess))) 
                    toReturn = false;
                        
                weak.clear();
                weak = null;
                System.gc();
                break;
            case "metis":
                ClustererAlgorithm met = new MetisCommunityDiscoverer(graphLoaderDB, parameters.get("nparts"), parameters.get("ptype"), parameters.get("ufactor"), parameters.get("rand"));
                met.getClusters();
                met.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                if (!met.storeCommunities(new CommunityStorerDB(dbAccess, "user")))
                    toReturn = false;
                if (!met.storeCentroidFeatures(new centroidStrorerDB(dbAccess)))
                    toReturn = false;
                met.clear();
                met = null;
                System.gc();
                break;
            case "betw":
                Integer edgesToRemove;
                if (parameters.get("edgesToRemove") == null) // default value
                    edgesToRemove = 5;
                else
                    edgesToRemove = Integer.parseInt(parameters.get("edgesToRemove"));
                ClustererAlgorithm edg = new EdgeBetweennessCommunityDiscoverer(graphLoaderDB, edgesToRemove);
                edg.getClusters();
                edg.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                if (!edg.storeCommunities(new CommunityStorerDB(dbAccess, "user")))
                    toReturn = false;
                if (!edg.storeCentroidFeatures(new centroidStrorerDB(dbAccess)))
                    toReturn = false;
                
                edg.clear();
                edg = null;
                System.gc();
                break;
            case "bk":
                ClustererAlgorithm bk = new BronKerboschCliqueCommunityDiscoverer(graphLoaderDB);
                bk.getClusters();
                bk.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                if (!bk.storeCommunities(new CommunityStorerDB(dbAccess, "user")))
                    toReturn = false;
                if (!bk.storeCentroidFeatures(new centroidStrorerDB(dbAccess)))
                    toReturn = false;
                bk.clear();
                bk = null;
                System.gc();
                break;
            
            default:
                socialPServerOutputLogger.info("* algorithm does not exist *");
                toReturn = false;
                break;
        }
        graphLoaderDB.emptyGraph();
        graphLoaderDB = null;
        dbAccess = null;
        return toReturn;
    }
    
    /**
     * 
     * @param Algorithm
     * @param TypeAssociation
     * @param Properties 
     */
    public boolean makeFeatureGroups(String algorithm, String associationType, Map<String, String> parameters) {
        Float accosThreshold;
        if (parameters.get("accosThreshold") == null)
            accosThreshold = new Float(0);
        else    
            accosThreshold = Float.parseFloat(parameters.get("accosThreshold"));
        
        PSocialDBAccess dbAccess;
        dbAccess = new PSocialDBAccess(pServerClient, pServerDB, associationType, algorithm);
        
        GraphLoaderDB graphLoaderDB = new GraphLoaderDB(dbAccess);
        graphLoaderDB.loadGraph(accosThreshold, "feature"); // load user Associations to loader (RAM) -> u can retrive them by loader.getGraph()
        boolean toReturn = true;
        switch (algorithm) {
            case "weak":
                ClustererAlgorithm weak = new WeakComponentCommunityDiscoverer(graphLoaderDB);
                weak.getClusters();
                if (!weak.storeCommunities(new CommunityStorerDB(dbAccess, "feature"))) 
                    toReturn = false;
                weak.clear();
                weak = null;
                System.gc();
                break;
            case "metis":
                ClustererAlgorithm met = new MetisCommunityDiscoverer(graphLoaderDB, parameters.get("nparts"), parameters.get("ptype"), parameters.get("ufactor"), parameters.get("rand"));
                met.getClusters();
                if (!met.storeCommunities(new CommunityStorerDB(dbAccess, "feature")))
                    toReturn = false;
                met.clear();
                met = null;
                System.gc();
                break;
            case "betw":
                Integer edgesToRemove;
                if (parameters.get("edgesToRemove") == null) // default value
                    edgesToRemove = 5;
                else
                    edgesToRemove = Integer.parseInt(parameters.get("edgesToRemove"));
                ClustererAlgorithm edg = new EdgeBetweennessCommunityDiscoverer(graphLoaderDB, edgesToRemove);
                edg.getClusters();
                if (!edg.storeCommunities(new CommunityStorerDB(dbAccess, "feature")))
                    toReturn = false;
                edg.clear();
                edg = null;
                System.gc();
                break;
            case "bk":
                ClustererAlgorithm bk = new BronKerboschCliqueCommunityDiscoverer(graphLoaderDB);
                bk.getClusters();
                if (!bk.storeCommunities(new CommunityStorerDB(dbAccess, "feature")))
                    toReturn = false;
                bk.clear();
                bk = null;
                System.gc();
                break;
            
            default:
                socialPServerOutputLogger.info("* algorithm does not exist *");
                toReturn = false;
                break;
        }
        graphLoaderDB.emptyGraph();
        graphLoaderDB = null;
        dbAccess = null;
        return toReturn;
    }
    
    /**
     * Given the communityName returns the Centroid feature list (and Weights) of this Community
     * @param communityName
     * @param pattern a pattern of feature names (when * the pattern = null)
     * @return a Map<String, Float> -> <Feature, Value>  
     */
    public Map<String, Float> getCentroid(String communityName, String pattern) {
        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, pServerDB);
        return dbAccess.getCentroidFeaturesByCommName(communityName, pattern);
    }
    
    /**
     * Add a custom Community
     * @param communityName
     * @param members
     */
    public boolean addCustomCommunity(String communityName, Set<String> members) {
        
        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, pServerDB,
                "custom", "custom", communityName);
        
        Set<Set<String>> customTown = new HashSet<>();
        customTown.add(members);
        SetOfCommunities town = new SetOfCommunities(customTown);
        CommunityStorerDB commStorer = new CommunityStorerDB(dbAccess, "user");
        if ( commStorer.storeAll(town) ) {
            return true;
        } else 
            return false;
    }
    
    /**
     * 
     * @param FeatureGroupName
     * @param Features 
     */
    public boolean addCustomFeatureGroup(String featureGroupName, Set<String> features) {
        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, pServerDB,
                "custom", "custom", featureGroupName);
        
        Set<Set<String>> customTown = new HashSet<>();
        customTown.add(features);
        SetOfCommunities town = new SetOfCommunities(customTown);
        CommunityStorerDB commStorer = new CommunityStorerDB(dbAccess, "feature");
        if ( commStorer.storeAll(town) ) {
            town.intraSimilarityCalculator(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
            town.storeCentroidFeatures(new centroidStrorerDB((dbAccess)));
            return true;
        } else 
            return false;
    }
    
    /**
     * a simple Documentation of the Algorithms and theys default values
     */
    public Map<String, String> algorithmDocumentation() {
       Map<String, String> algosParams = new HashMap<>(); 
       algosParams.put("metis", "nparts->default:100 | ptype->values:(kway OR rb)->default:kway | ufactor->default:100 | rand->default:5 ");
       algosParams.put("bk", "empty");
       algosParams.put("betw", "edgesToRemove->default:5");
       algosParams.put("weak", "empty");
       
       return algosParams;
    }
    
    
   /**
    * Given the userID returns the Centroid feature list (and Weights) of his Community
    *
    * @param user the User
    * @return MAP ... the Centroid feature list of user Community
    */ 
//    public Map<String, Float> getCentroid(String user) {
//        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, pServerDB, sourceTag, sourceID);
//        return dbAccess.getCentroidFeatures(user);
//    }
    
    
    /**
    * Loads User Friendship from given File and stores it to DB.
    * The social link is User1id-User2id 
    *
    * @param UserAssociationFile relative path to user-association File
    * @example: "datasets/hetrec2011-lastfm-2k/user_friends.dat"
    */
//    public void UserAssociationsFileToDB(String UserAssociationFile) {
//        
//        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, db, sourceTag, sourceID);
//        
//        // Insert User Associations info into DB
//        UserAssociationsStorerDB uadb = new UserAssociationsStorerDB(dbAccess);
//        GraphLoaderFile loader = new GraphLoaderFile(UserAssociationFile);
//        uadb.friendshipToDB(loader);
//        loader.emptyGraph();
//        loader = null;
//        uadb = null;
//        dbAccess = null;
//    }
    
    /**
    * Calculate User Communities
    *
    * @param algorithm Which algorithm will be used to produce user communities
    * @values weak-> WeakComponent
    * @values metis-> metis
    * @values betw-> EdgeBetweennessor
    * @values bk-> BronKerbosch
    */
//    public void produceCommunities(String algorithm) {
//        
//        PSocialDBAccess dbAccess = new PSocialDBAccess(pServerClient, db, sourceTag, sourceID);
//        
//        GraphLoaderDB graphLoaderDB = new GraphLoaderDB(dbAccess);
//        graphLoaderDB.loadGraph(); // load user Associations to loader (RAM) -> u can retrive them by loader.getGraph()
//
//        switch (algorithm) {
//            case "weak":
//                ClustererAlgorithm weak = new WeakComponentCommunityDiscoverer(graphLoaderDB);
//                weak.getClusters();
//                weak.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
//                weak.storeCommunities(new CommunityStorerDB(dbAccess));
//                weak.storeCentroidFeatures(new centroidStrorerDB(dbAccess));
//                weak.clear();
//                weak = null;
//                System.gc();
//                break;
//            case "metis":
//                ClustererAlgorithm met = new MetisCommunityDiscoverer(graphLoaderDB);
//                met.getClusters();
//                met.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
//                met.storeCommunities(new CommunityStorerDB(dbAccess));
//                met.storeCentroidFeatures(new centroidStrorerDB(dbAccess));
//                met.clear();
//                met = null;
//                System.gc();
//                break;
//            case "betw":
//                ClustererAlgorithm edg = new EdgeBetweennessCommunityDiscoverer(graphLoaderDB, 5);
//                edg.getClusters();
//                edg.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
//                edg.storeCommunities(new CommunityStorerDB(dbAccess));
//                edg.storeCentroidFeatures(new centroidStrorerDB(dbAccess));
//                edg.clear();
//                edg = null;
//                System.gc();
//                break;
//            case "bk":
//                ClustererAlgorithm bk = new BronKerboschCliqueCommunityDiscoverer(graphLoaderDB);
//                bk.getClusters();
//                bk.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
//                bk.storeCommunities(new CommunityStorerDB(dbAccess));
//                bk.storeCentroidFeatures(new centroidStrorerDB(dbAccess));
//                bk.clear();
//                bk = null;
//                System.gc();
//                break;
//            
//            default:
//                socialPServerOutputLogger.info("* algorithm does not exist *");
//                break;
//        }
//        graphLoaderDB.emptyGraph();
//        graphLoaderDB = null;
//        dbAccess = null;
//    }



}
