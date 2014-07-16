/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.dataio;

/**
 * Stores user Associations (user-friendship Links(edges)) info 
 * to a given database.
 * @param dbAccess pServer DBAccess instance
 * @author arix
 */
public class UserAssociationsStorerDB{
    private PSocialDBAccess dbAccess;

    /**
     * Creates a user Associations
     * db storer that uses a given pServer DBAccess to handle insertions.     
     * @param dbAccess pServer DBAccess instance
     */
    public UserAssociationsStorerDB(PSocialDBAccess dbAccess) {       
        this.dbAccess = dbAccess;
    }
    
    /**
     * Store user Associations (user-friendship Links(edges)) info 
     * to a given database.
     * @param gloader loads all UserAssociations (social graph) 
     * and also has the Location from where the UserAssociations will be pulled
     */
    public void friendshipToDB(GraphLoaderFile gloader) {        
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    deleting User friendship on DB...");
        dbAccess.deleteAllUserAssociations();   // delete User Associations from DB         
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    storing User friendship into DB...");        
        // load friends from gloader                             
        dbAccess.GraphtoDB(gloader);  // insert Graph to DB  
    }
}
