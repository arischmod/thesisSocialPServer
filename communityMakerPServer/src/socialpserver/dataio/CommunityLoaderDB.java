/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

import java.util.Set;
import socialpserver.Community;
import socialpserver.SetOfCommunities;

/**
 * Loads all communities (clusters) produced from the algorithms 
 * (each Community is a Set of users)
 * from a given database.
 * @author arix
 */
public class CommunityLoaderDB implements ICommunityLoader{
    private PSocialDBAccess dbAccess;
    
    /**
     * Creates a Community db loader that uses a given
     * pServer DBAccess to handle queries.     
     * @param dbAccess pServer DBAccess instance
     */   
    public CommunityLoaderDB(PSocialDBAccess dbAccess) {        
        this.dbAccess = dbAccess;
    }    
    
    @Override
    public SetOfCommunities loadCommunities() {
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    loading Communities from DB..");        
        Set<String> coms = dbAccess.getAllCommunities();
        Set<String[]> userCommunities = dbAccess.getUserCommunities();
        SetOfCommunities communities = new SetOfCommunities();
        
        for (String tempComs : coms) {
            Community community = new Community();            
            for (String[] tempUser : userCommunities) {
                if (tempUser[1].equals(tempComs)) {
                    community.addMember(tempUser[0]);                   
                }
            }    
            communities.addCommunity(community);
        }
        
        return communities;
    }
    
    
}
