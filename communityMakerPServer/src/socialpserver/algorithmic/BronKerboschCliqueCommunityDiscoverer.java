/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.algorithmic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import socialpserver.SetOfCommunities;
import socialpserver.dataio.GraphLoader;
import socialpserver.dataio.CommunityStorer;
import socialpserver.dataio.FeatureLoader;
import socialpserver.dataio.UserFeatureLoader;
import socialpserver.dataio.centroidStrorerDB;

/**
 * This class applies the Bron Kerbosch algorithm...
 * @author arix
 */
public class BronKerboschCliqueCommunityDiscoverer implements ClustererAlgorithm {
    protected GraphLoader loader;    
    private SetOfCommunities town;
    
    /**
     * An algorithm for computing clusters (community structure) in graphs based on
     * Bron Kerbosch Clique Discoverer. 
     * @param glLoader loads the userAssociations available in pServer 
     * the friendship edges
     * and also has the Location from where the userAssociations will be pulled
     */
    public BronKerboschCliqueCommunityDiscoverer(GraphLoader glLoader) {
        this.loader = glLoader;
    }
          
    /**
     * Fills a SimpleGraph type graph 
     * @param g the graph instance
     * @return g the graph instance filled with pServers social graph
     */
    private SimpleGraph<String, DefaultEdge> fillGraph(SimpleGraph<String, DefaultEdge> g) {
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    filling Bron Kerboschs graph from given loader");                
        Set<String[]> userAssociations = loader.getGraph(); // get user Associations from Loader

        for (String[] FriendsTable : userAssociations) {
            //undirected
            g.addVertex(FriendsTable[0]);
            g.addVertex(FriendsTable[1]);
            g.addEdge(FriendsTable[0], FriendsTable[1]);
        }
        userAssociations.removeAll(userAssociations);
        return g;
    }

    @Override
    public SetOfCommunities getClusters() {
        long startTime, endTime, totalTime;
        SimpleGraph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        g = fillGraph(g);
                
        BronKerboschCliqueFinder bk = new BronKerboschCliqueFinder(g);        
        socialpserver.SocialPServer.algorithmOutputLogger.info("\n  BronKerboschClique algoritm is trying to find communities..."); 
        startTime = System.currentTimeMillis();                               
        Collection cliques = bk.getAllMaximalCliques();
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        socialpserver.SocialPServer.algorithmOutputLogger.info("BronKerboschClique algoritm  ***found " + cliques.size() + " clusters***");                                
        socialpserver.SocialPServer.algorithmOutputLogger.info(" discovery runTime: " + totalTime);
        town = new SetOfCommunities(new HashSet<Set<String>>(cliques));
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
