package socialpserver.dataio;

import java.util.Set;

/**
 * Loads a graph from a given database.
 *
 * @author arix
 */
public class GraphLoaderDB implements GraphLoader {

    private PSocialDBAccess dbAccess;
    private Set<String[]> userAssociations;

    /**
     * Creates a graph db loader that uses a given pServer DBAccess to handle
     * queries.
     *
     * @param dbAccess pServer DBAccess instance
     */
    public GraphLoaderDB(PSocialDBAccess dbAccess) {
        this.dbAccess = dbAccess;    
    }
    
    @Override
    public Set<String[]> getGraph() {
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    loading User friendship from DB...");
        return userAssociations;
    }

    @Override
    /**
     * NOT USED - too expensive in RAM
     */
    public void loadGraph() {
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    loading User friendship from DB...");
        userAssociations = dbAccess.DBtoGraph();
    }

    @Override
    /**
     * Used by API
     */
    public void emptyGraph() {
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    removing Graph from RAM...");
        userAssociations.clear();
    }
        
}
