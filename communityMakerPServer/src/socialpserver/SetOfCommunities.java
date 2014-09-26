 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import socialpserver.dataio.FeatureLoader;
import socialpserver.dataio.UserFeatureLoader;
import socialpserver.dataio.centroidStrorerDB;

/**
 * Is a Set of Community type objects
 * meant to be used to represent the produced communities
 * @author arix
 */
public class SetOfCommunities{
    private Set<Community> communities;          
    /**
     * all centroid Features
     * every time a Community object calculates a centroid it will store 
     * it's features here so the Inter Similarity can be calculated
     */
    protected Map<Integer, Map<String, Float>> allCentroidFeatures = new HashMap<>();  // needs to be filed by Community.intraSimilarity() method
    
    /**
     * Constructor - Init communities Set
     * create a SetOfCommunities object
     */
    public SetOfCommunities() {
        this.communities = new HashSet<>();
    }
    
    /**
     * Constructor - create an SetOfCommunities object that will
     * contain the given communities
     * @param communities 
     */
    public SetOfCommunities(Set<Set<String>> communities) {
        this.communities = new HashSet<>();
        for (Set<String> community : communities) {
            this.communities.add(new Community(community));            
        }
    }

    
    
    //   java Set handling
    
    /**
     * add a new Community (of users) in this Set
     * @param communityID the community that will be added
     */
    public void addCommunity(Community communityID) {
        communities.add(communityID);
    }
   
    /**
     * remove a Community (of users) from this Set
     * @param communityID the Community to be removed
     */
    public void removeCommunity(Community communityID) {
        communities.remove(communityID);
    }
            
    /**
     * get all contained Community type objects
     * @return a Set of Community type objects
     */
    public Set<Community> getCommunities(){       
        return communities;
    }    
    
    /**
     * #of contained communities
     * (the size of the set)
     * @return #ofCommunities
     */
    public int size() {
        return communities.size();
    }
    
    /**
     * finalize, free memory. 
     */
    public void clear() {
        if (communities.size()>0) {
            communities.clear();
        }
        if (allCentroidFeatures.size()>0) {
            allCentroidFeatures.clear();
        }
    }
    
    
    
    // Communities info
    
    /**
     * Gives useful Info about the contained Communities
     * It calculates maxClusterSize, minClusterSize,
     * averageComminitySize and #ofCommunities, 
     */
    public void communitiesInfo() {
        Integer maxClusterSize = Integer.MIN_VALUE;
        Integer minClusterSize = Integer.MAX_VALUE;
        Float averageComminitySize = 0.0f;   

        Community unclusteredCommunity = new Community(); // Some clustering Algorithms create one extra cluster tha contains all the unclustered Verticles of the social graph

        for (Community tempCom : communities) {   // find unclustered users
            if (tempCom.size() > unclusteredCommunity.size()) {
                unclusteredCommunity = tempCom;
            }            
        }

        for (Community community : communities) {
            if (community == unclusteredCommunity) {  // do not count unclustered users
                continue;
            }
            if (community.size() > maxClusterSize) {  // find max community size
                maxClusterSize = community.size();
            }
            if (community.size() < minClusterSize) {  // find min community size
                minClusterSize = community.size();
            }
            averageComminitySize = averageComminitySize + community.size();
        }

        if (unclusteredCommunity.size() < maxClusterSize * 2) { // in case the algorithm does not produses an Unclusted-users community
            maxClusterSize = unclusteredCommunity.size();
            averageComminitySize = averageComminitySize + unclusteredCommunity.size();
            socialpserver.SocialPServer.algorithmOutputLogger.info("      0 unclustered Users");            
        } else {  // remove Unclusted-users community
            socialpserver.SocialPServer.algorithmOutputLogger.info("      " + unclusteredCommunity.size() + " unclustered Users");               
            communities.remove(unclusteredCommunity);
        }
        socialpserver.SocialPServer.algorithmOutputLogger.info("      found " + communities.size() + " communities");   
        socialpserver.SocialPServer.algorithmOutputLogger.info("      max comminity size = " + maxClusterSize);   
        socialpserver.SocialPServer.algorithmOutputLogger.info("      min comminity size = " + minClusterSize);   
        socialpserver.SocialPServer.algorithmOutputLogger.info("      average comminity size = " + averageComminitySize / communities.size());    // average is the mean value (without the unclustered users)
    }

    
    
    //  Evaluation methods
        
    /**
     * evaluate clusterer quality
     * calculates the Total compactness of the communities (clusters)
     * and the similarity of all Centroids
     * higher TotalCompactness and lower centroidSimilarity 
     * means better clusterer quality     
     * @param fLoader loads all features available in pServer for the specific client
     * and also has the Location from where the features will be pulled
     * @param ufLoader loads the features that are rated by the users
     * and also has the Location from where the features will be pulled
     */
    public void evaluate(FeatureLoader fLoader, UserFeatureLoader ufLoader) {        
        communitiesInfo();  // Info about the communities     
        
        Float intraSimilarity = intraSimilarityCalculator(fLoader, ufLoader);
        //socialpserver.SocialPServer.algorithmOutputLogger.info("    intraSimilarity = " + intraSimilarity + "\n\n");                        
        
        //  Used only  in Thgesis
//        Float interSimilarity = interSimilarityCalculator();
        //socialpserver.SocialPServer.algorithmOutputLogger.info("    interSimilarity = " + interSimilarity + "\n\n");                        
//        Float quality = intraSimilarity / interSimilarity;        
//        socialpserver.SocialPServer.algorithmOutputLogger.info("    clusterer quality = " + quality + "\n\n");                        
    }
    
    /**
     * Calculate the Total compactness of the communities (clusters)
     * TotalCompactness is a sum of every cluster intraSimilarity * weight 
     * @param fLoader loads all features available in pServer for the specific client
     * and also has the Location from where the features will be pulled
     * @param ufLoader loads the features that are rated by the users
     * and also has the Location from where the features will be pulled
     * @return the totalCompactness (all clusters intraSimilarity)
     */
    public Float intraSimilarityCalculator(FeatureLoader fLoader, UserFeatureLoader ufLoader) {        
        Float communityCompactness, compactnessTotal = 0.0f, clusterSizeRatio;    
        int communityID=0;
//        fLoader.loadFeatures();
        for (Community community : communities) {            
            communityCompactness = community.intraSimilarity(fLoader, ufLoader, communityID++, this); // get community intraSimilarity 
            int members = getClusteredUsersNum();
            if (members == 0)
                compactnessTotal = 0.0f;
            else {                
                clusterSizeRatio = (float) community.size() / members; 
                compactnessTotal = compactnessTotal + clusterSizeRatio * communityCompactness;
            }
        }
        socialpserver.SocialPServer.algorithmOutputLogger.info("     "); 
        socialpserver.SocialPServer.algorithmOutputLogger.info("     Total clustering scheme compactness = " + compactnessTotal);        
        return compactnessTotal;
    }

    /**
     * Calculate the centroid Similarity
     * sum the similarity of all centroids
     * @return mean similarity of all centroids
     */    
    private Float interSimilarityCalculator() {        
        Float centroidSim = 0.0f;
        int counter=0;
                
        for (Integer commCentroid : allCentroidFeatures.keySet()) {
            for (Integer commCentroidToCompare : allCentroidFeatures.keySet()) {
                if (commCentroid < commCentroidToCompare) {  //  symetric matrix
                    centroidSim = centroidSim + Community.getCosineVectorSimilarity(allCentroidFeatures.get(commCentroid), allCentroidFeatures.get(commCentroidToCompare));
                    counter++;
                }
            }
        }
        Float interSim;
        if (counter == 0) {            
            interSim = 0.0f;
        }       
        else {
            interSim = centroidSim / counter;
        }
        
        socialpserver.SocialPServer.algorithmOutputLogger.info("     Compactness of all centroids = " + interSim );        
        return interSim;
    }     
        
    /**
     * get the number of all clustered users     
     * @return clusteredUsersNum number of all users contained in 
     * all Community objects
     */
    public int getClusteredUsersNum() {        
        Set<String> clusteredUsersSet = new HashSet<>();  // all clustered users
        
        for (Community tempCom : communities) {   // get the users from every community 
            for (String user : tempCom.getCommunityMembers()) {
                if (!clusteredUsersSet.contains(user)) {
                    clusteredUsersSet.add(user);
                }
            }            
        }        
        return clusteredUsersSet.size();
    }      
    
    /**
     * 
     * @param storer
     * @return 
     */
    public boolean storeCentroidFeatures(centroidStrorerDB storer) {        
        
        return storer.storeAll(allCentroidFeatures);
        
    }
}
