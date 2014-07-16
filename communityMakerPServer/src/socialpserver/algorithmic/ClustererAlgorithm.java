/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.algorithmic;

import socialpserver.SetOfCommunities;    //  Data Structure
import socialpserver.dataio.CommunityStorer;   // Data IO
import socialpserver.dataio.FeatureLoader;   // Data IO
import socialpserver.dataio.UserFeatureLoader;   // Data IO
import socialpserver.dataio.centroidStrorerDB;

/**
 * An Clustering Algorithm used to cluster the social graph
 * @author arix
 */
public interface ClustererAlgorithm {
    
    /**
     * Returns the calculated clusters as communities.
     * The method run the algorithm to the social graph and produces communities
     * @return A Set of Communities (set of all clusters). 
     * Each Community subset shows the users that forms this community.
     */
    public SetOfCommunities getClusters();
    
    /**
     * Calculate the quality of the produced communities (clusters)
     * found by the algorithm
     * @param fLoader loads all features available in pServer for the specific client
     * and also has the Location from where the features will be pulled
     * @param ufLoader loads the features that are rated by the users
     * and also has the Location from where the features will be pulled
     */
    public void evaluate(FeatureLoader fLoader, UserFeatureLoader ufLoader);
    
    /**
    * Store the the Centroids of all communities (and their features)
    * @param store is an object that contains the methods to store 
    * and also the Location where the centroids will be stored
    */
    public void storeCentroidFeatures(centroidStrorerDB store); 
    
    /**
     * Store the produced communities (into DB, File, etc)
     * @param store is an object that contains the methods to store 
     * and also the Location where the communities will be stored
     */
    public void storeCommunities(CommunityStorer store);    
    
    /**
     * finalize, free memory. 
     */
    public void clear();    
    
}
