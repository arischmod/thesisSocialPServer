/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.algorithmic;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.util.Set;
import socialpserver.SetOfCommunities;
import socialpserver.dataio.GraphLoader;
import socialpserver.dataio.CommunityStorer;
import socialpserver.dataio.FeatureLoader;
import socialpserver.dataio.UserFeatureLoader;

/**
 * An algorithm for computing clusters (community structure) in graphs based on
 * edge betweenness. The betweenness of an edge is defined as the extent to
 * which that edge lies along shortest paths between all pairs of nodes. This
 * algorithm works by iteratively following the 2 step process:
 *
 * Compute edge betweenness for all edges in current graph Remove edge with
 * highest betweenness
 *
 * Running time is: O(kmn) where k is the number of edges to remove, m is the
 * total number of edges, and n is the total number of vertices. For very sparse
 * graphs the running time is closer to O(kn^2) and for graphs with strong
 * community structure, the complexity is even lower.
 *
 * This algorithm is a slight modification of the algorithm discussed below in
 * that the number of edges to be removed is parameterized.
 *
 * Author: Scott White, Tom Nelson (converted to jung2) See Also: "Community
 * structure in social and biological networks by Michelle Girvan and Mark
 * Newman"
 *
 * @author arix
 */
public class EdgeBetweennessCommunityDiscoverer implements ClustererAlgorithm {

    protected GraphLoader loader;
    private SetOfCommunities town;
    int numEdgesToRemove; //numEdgesToRemove: is the number of edges to be progressively removed from the graph

    /**
     * An algorithm for computing clusters (community structure) in graphs based on
     * edge betweenness. 
     * @param glLoader loads the userAssociations available in pServer 
     * the friendship edges
     * and also has the Location from where the userAssociations will be pulled
     * @param numEdgesToRemove the number of edges to be progressively removed
     * from the graph The more edges removed the smaller and more cohesive the
     * clusters.
     */
    public EdgeBetweennessCommunityDiscoverer(GraphLoader glLoader, int numEdgesToRemove) {
        this.loader = glLoader;
        this.numEdgesToRemove = numEdgesToRemove;
    }

    /**
     * Fills a UndirectedGraph type graph
     * @param g the graph instance
     * @return g the graph instance filled with pServers social graph
     */
    public UndirectedGraph fillGraph(UndirectedGraph<String, String> g) {
        Integer counter = 0;

        socialpserver.SocialPServer.socialPServerOutputLogger.info("    filling Edge Betweenness graph from given loader");          
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
        
        EdgeBetweennessClusterer clusterer = new EdgeBetweennessClusterer(numEdgesToRemove);             
        socialpserver.SocialPServer.algorithmOutputLogger.info("\n  EdgeBetweenness algoritm is trying to find communities...");        
        startTime = System.currentTimeMillis();        
        town = new SetOfCommunities(clusterer.transform(g));  // Finds the set of clusters which have the strongest "community structure".         
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;  
        socialpserver.SocialPServer.algorithmOutputLogger.info("EdgeBetweenness algoritm  ***found " + town.size() + " clusters***");        
        socialpserver.SocialPServer.algorithmOutputLogger.info(" discovery runTime: " + totalTime);
        return town;
    }

    @Override
    public void storeCommunities(CommunityStorer store) {
        store.storeAll(town);
    }

    @Override
    public void evaluate(FeatureLoader fLoader, UserFeatureLoader ufLoader) {
        town.evaluate(fLoader, ufLoader);
    }

    @Override
    public void clear() {
        town.clear();
    }
}
