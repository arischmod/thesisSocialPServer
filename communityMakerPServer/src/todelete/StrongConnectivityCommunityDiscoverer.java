/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package todelete;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import socialpserver.SetOfCommunities;
import socialpserver.algorithmic.ClustererAlgorithm;
import socialpserver.dataio.CommunityLoaderLocally;
import socialpserver.dataio.GraphLoader;
import socialpserver.dataio.CommunityStorer;
import socialpserver.dataio.FeatureLoader;
import socialpserver.dataio.UserFeatureLoader;
import socialpserver.dataio.centroidStrorerDB;

/**
 * This class applies the Strong Connectivity algorithm...
 * @author arix
 */
public class StrongConnectivityCommunityDiscoverer implements ClustererAlgorithm {
    protected GraphLoader loader;
    private SetOfCommunities town;
    
        
    /**
     * An algorithm for computing clusters (community structure) in graphs based on
     * Strong Connectivity Community Discoverer. 
     * @param glLoader loads the userAssociations available in pServer 
     * the friendship edges
     * and also has the Location from where the userAssociations will be pulled
     */
    public StrongConnectivityCommunityDiscoverer(GraphLoader glLoader) {
        this.loader = glLoader;
    }
      
    /**
     * Fills a DirectedGraph type graph 
     * @param g the graph instance
     * @return g the graph instance filled with pServers social graph
     */
    private DirectedGraph<String, DefaultEdge> fillGraph(DirectedGraph<String, DefaultEdge> g) {        
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    filling Strong Connectivity graph from given loader");
        Set<String[]> userAssociations = loader.getGraph(); // get user Associations from DB     

        for (String[] FriendsTable : userAssociations) {
            //directed
            g.addVertex(FriendsTable[0]);
            g.addVertex(FriendsTable[1]);
            // add Edge from both directions so basically is an Undirected Graph
            g.addEdge(FriendsTable[0], FriendsTable[1]);
            g.addEdge(FriendsTable[1], FriendsTable[0]);
        }
        return g;
    }

    @Override
    public SetOfCommunities getClusters() {
        DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        g = fillGraph(g);

        socialpserver.SocialPServer.algorithmOutputLogger.info("\n  StrongConnectivity algoritm is trying to find communities...");        
        StrongConnectivityInspector clusterer = new StrongConnectivityInspector(g);
        List<Set<String>> clusterList = clusterer.stronglyConnectedSets();
        
        town = new SetOfCommunities(new HashSet<>(clusterList));                
        socialpserver.SocialPServer.algorithmOutputLogger.info("StrongConnectivity algoritm  ***found " + town.size() + " clusters***");
        
        return town;
    }

    @Override
    public boolean storeCommunities(CommunityStorer store) {
        return store.storeAll(town);
    }

    @Override
    public void evaluate(FeatureLoader fLoader, UserFeatureLoader ufLoader) {
        town.evaluate(fLoader, ufLoader);
    }

    @Override
    public boolean storeCentroidFeatures(centroidStrorerDB storer) {
        return town.storeCentroidFeatures(storer);
    }
    
    @Override
    public void clear() {
        town.clear();
    }
    
}
