/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import socialpserver.SetOfCommunities;

/** 
 * Store all communities (clusters of users) produced from the algorithms 
 * each Community is a Set of users
 * @author arix
 */
public interface CommunityStorer {
    
    /**
     * Store all communities (clusters of users) produced from the algorithms 
     * to a given database.
     * @param communities is the a SetOfCommunities object that contains all 
     * the communities (set of users) that we want to store
     * @param sourceTag com OR soc (cluster by 'Pserver Community mode' OR 'SocialPServer')
     */
    public void storeAll(SetOfCommunities communities);
   
}
