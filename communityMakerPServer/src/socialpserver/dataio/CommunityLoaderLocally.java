/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import socialpserver.SetOfCommunities;

/**
 * Loads all communities (clusters) that have just been produced
 * from the algorithms (each Community is a Set of users)
 * it is used when we want to load communities that are created at real time 
 * @author arix
 */
public class CommunityLoaderLocally implements ICommunityLoader{
    private SetOfCommunities communities;
    
    /**
     * Creates a Community local loader that will alow the loading of
     * given as a parameter communities
     * @param communities the communities (Set of users) to be loaded
     */ 
    public CommunityLoaderLocally(SetOfCommunities communities) {
        this.communities = communities;
    }

    @Override
    public SetOfCommunities loadCommunities() {
        return communities;
    }
    
}
