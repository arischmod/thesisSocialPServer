/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import socialpserver.SetOfCommunities;

/** 
 * Loads all communities (clusters) produced from the algorithms 
 * each Community is a Set of users
 * @author arix
 */
public interface ICommunityLoader {
    
    /**
     * Loads all communities (clusters) produced from the algorithms 
     * each Community is a Set of users
     * @return a SetOfCommunities object which contains all the produced Clusters
     * each Community Set is a Set of UserIDs that form that every Community
     */
    public SetOfCommunities loadCommunities();
    
}
