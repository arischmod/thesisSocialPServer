/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.algorithmic;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.Set;
import socialpserver.SetOfCommunities;
import socialpserver.dataio.GraphLoader;
import socialpserver.dataio.CommunityStorer;
import socialpserver.dataio.FeatureLoader;
import socialpserver.dataio.UserFeatureLoader;
import socialpserver.dataio.centroidStrorerDB;

/**
 * Finds all weak components in a graph as sets of vertex sets. 
 * A weak component is defined as a maximal subgraph in which all pairs 
 * of vertices in the subgraph are reachable from one another in the 
 * underlying undirected subgraph. 
 * This implementation identifies components as sets of vertex sets.
 * To create the induced graphs from any or all of these vertex sets, see
 * Algorithm.filters.FilterUtils.
 *
 * Running time: O(|V| + |E|) where |V| is the number of vertices and |E| 
 * is the number of edges.
 *
 * Author: Scott White
 *
 * @author arix
 */
public class WeakComponentCommunityDiscoverer implements ClustererAlgorithm {
    private SetOfCommunities town;
    protected GraphLoader loader;
        
    /**
     * An algorithm for computing clusters (community structure) in graphs based on
     * Weak Component Community Discoverer. 
     * @param glLoader loads the userAssociations available in pServer 
     * the friendship edges
     * and also has the Location from where the userAssociations will be pulled
     */
    public WeakComponentCommunityDiscoverer(GraphLoader glLoader) {
        this.loader = glLoader;
    }

    /**
     * Fills a UndirectedGraph type graph
     *
     * @param g the graph instance
     * @return g the graph instance filled with pServers social graph
     */
    private UndirectedGraph<String, String> fillGraph(UndirectedGraph<String, String> g) {
        Integer counter = 0;

        socialpserver.SocialPServer.socialPServerOutputLogger.info("    filling Weak Component graph from given loader");               
        Set<String[]> userAssociations = loader.getGraph(); // get user Associations from Loader

        for (String[] FriendsTable : userAssociations) {
            g.addEdge(counter.toString(), FriendsTable[0], FriendsTable[1]);
            counter++;
        }
        userAssociations.removeAll(userAssociations);
        return g;
    }

    @Override
    public SetOfCommunities getClusters() {
        long startTime, endTime, totalTime;
        UndirectedGraph<String, String> g = new UndirectedSparseGraph<>();
        g = fillGraph(g);

        WeakComponentClusterer clusterer = new WeakComponentClusterer();             
        socialpserver.SocialPServer.algorithmOutputLogger.info("\n  WeakComponent algoritm is trying to find communities...");        
        startTime = System.currentTimeMillis();   
        town = new SetOfCommunities(clusterer.transform(g));  // Extracts the weak components from a graph.                
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        socialpserver.SocialPServer.algorithmOutputLogger.info("WeakComponent algoritm  ***found " + town.size() + " clusters***");
        socialpserver.SocialPServer.algorithmOutputLogger.info(" discovery runTime: " + totalTime);
        return town;
    }

    @Override
    public boolean storeCommunities(CommunityStorer storer) {
        return storer.storeAll(town);
    }

    @Override
    public void evaluate(FeatureLoader fLoader, UserFeatureLoader ufLoader) {
        town.evaluate(fLoader, ufLoader);
    }

    @Override
    public void clear() {
        town.clear();
    }    

    @Override
    public boolean storeCentroidFeatures(centroidStrorerDB storer) {

        return town.storeCentroidFeatures(storer);
    }
}
