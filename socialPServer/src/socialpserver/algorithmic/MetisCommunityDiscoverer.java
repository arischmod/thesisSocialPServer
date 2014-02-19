/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socialpserver.algorithmic;

import cnrs.grph.set.IntSet;
import com.carrotsearch.hppc.cursors.IntCursor;
import grph.Grph;
import grph.algo.partitionning.metis.Gpmetis;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import socialpserver.Community;
import socialpserver.SetOfCommunities;
import socialpserver.dataio.GraphLoader;
import socialpserver.dataio.CommunityStorer;
import socialpserver.dataio.FeatureLoader;
import socialpserver.dataio.UserFeatureLoader;
import todelete.metis;

/**
 * NAME gpmetis - manual page for gpmetis 5.1.0 SYNOPSIS gpmetis [options]
 * graphfile nparts DESCRIPTION Required parameters graphfile Stores the graph
 * to be partitioned. nparts The number of partitions to split the graph.
 *
 * Optional parameters -ptype=string Specifies the scheme to be used for
 * computing the k-way partitioning. The possible values are: rb - Recursive
 * bisectioning kway - Direct k-way partitioning [default] -ctype=string
 * Specifies the scheme to be used to match the vertices of the graph during the
 * coarsening. The possible values are: rm - Random matching shem - Sorted
 * heavy-edge matching [default] -iptype=string [applies only when -ptype=rb]
 *
 * Specifies the scheme to be used to compute the initial partitioning of the
 * graph. The possible values are: grow - Grow a bisection using a greedy scheme
 * [default for ncon=1] random - Compute a bisection at random [default for
 * ncon>1] -objtype=string [applies only when -ptype=kway]
 *
 * Specifies the objective that the partitioning routines will optimize. The
 * possible values are: cut - Minimize the edgecut [default] vol - Minimize the
 * total communication volume -no2hop
 *
 * Specifies that the coarsening will not perform any 2-hop matchings when the
 * standard matching fails to sufficiently contract the graph.
 *
 * -contig [applies only when -ptype=kway]
 *
 * Specifies that the partitioning routines should try to produce partitions
 * that are contiguous. Note that if the input graph is not connected this
 * option is ignored.
 *
 * -minconn [applies only when -ptype=kway]
 *
 * Specifies that the partitioning routines should try to minimize the maximum
 * degree of the subdomain graph, i.e., the graph in which each partition is a
 * node, and edges connect subdomains with a shared interface.
 *
 * -tpwgts=filename
 *
 * Specifies the name of the file that stores the target weights for each
 * partition. By default, all partitions are assumed to be of the same size.
 *
 * -ufactor=int
 *
 * Specifies the maximum allowed load imbalance among the partitions. A value of
 * x indicates that the allowed load imbalance is 1+x/1000. For ptype=rb, the
 * load imbalance is measured as the ratio of the
 * 2*max(left,right)/(left+right), where left and right are the sizes of the
 * respective partitions at each bisection. For ptype=kway, the load imbalance
 * is measured as the ratio of max_i(pwgts[i])/avgpwgt, where pwgts[i] is the
 * weight of the ith partition and avgpwgt is the sum of the total vertex
 * weights divided by the number of partitions requested. For ptype=rb, the
 * default value is 1 (i.e., load imbalance of 1.001). For ptype=kway, the
 * default value is 30 (i.e., load imbalance of 1.03).
 *
 * -ubvec=string
 *
 * Applies only for multi-constraint partitioning and specifies the per
 * constraint allowed load imbalance among partitions. The required parameter
 * corresponds to a space separated set of floating point numbers, one for each
 * of the constraints. For example, for three constraints, the string can be
 * "1.02 1.2 1.35" indicating a desired maximum load imbalance of 2%, 20%, and
 * 35%, respectively. The load imbalance is defined in a way similar to ufactor.
 * If supplied, this parameter takes priority over ufactor.
 *
 * -niter=int
 *
 * Specifies the number of iterations for the refinement Algorithm at each stage
 * of the uncoarsening process. Default is 10.
 *
 * -ncuts=int
 *
 * Specifies the number of different partitionings that it will compute. The
 * final partitioning is the one that achieves the best edgecut or communication
 * volume. Default is 1.
 *
 * -nooutput
 *
 * Specifies that no partitioning file should be generated.
 *
 * -seed=int
 *
 * Selects the seed of the random number generator.
 *
 * -dbglvl=int
 *
 * Selects the dbglvl.
 *
 * -help
 *
 * Prints this message.
 *
 * SEE ALSO The full documentation for gpmetis is maintained as a Texinfo
 * manual. If the communitiesInfo and gpmetis programs are properly installed at your site,
 * the command communitiesInfo gpmetis should give you access to the complete manual.
 *
 * @author arix
 */
public class MetisCommunityDiscoverer implements ClustererAlgorithm {

    private GraphLoader loader;
    private SetOfCommunities town;
    private Map<String, Integer> UserName_vertexID = new HashMap<>(); 
    private Map<Integer, String> vertexID_UserName = new HashMap<>(); 
    
    /**
     * An algorithm for computing clusters (community structure) in graphs based on
     * Metis Community Discoverer. 
     * @param glLoader loads the userAssociations available in pServer 
     * the friendship edges
     * and also has the Location from where the userAssociations will be pulled
     */
    public MetisCommunityDiscoverer(GraphLoader glLoader) {
        this.loader = glLoader;
        enableJavaAssertions();  // Doing this makes the library more robust and slows downs it a bit
    }

    /**
     * Fills a Grph type graph
     *
     * @param g the graph instance
     * @return g the graph instance filled
     */
    public Grph fillGraph(Grph g) {        
        socialpserver.SocialPServer.socialPServerOutputLogger.info("    filling Metis graph from given loader");                           
        Set<String[]> userAssociations = loader.getGraph(); // get user Associations from Loader
        int vertex=0;  // Metis dose not allow Vertex = 0        
        for (String[] FriendsTable : userAssociations) {
            if (!UserName_vertexID.containsKey(FriendsTable[0])) {
                vertex++;
                g.addVertex(vertex);
                UserName_vertexID.put(FriendsTable[0], vertex);
                vertexID_UserName.put(vertex, FriendsTable[0]);
            }
            if (!UserName_vertexID.containsKey(FriendsTable[1])) {
                vertex++;
                g.addVertex(vertex);
                UserName_vertexID.put(FriendsTable[1], vertex);
                vertexID_UserName.put(vertex, FriendsTable[1]);
            }
            
            if (!g.areVerticesAdjacent(UserName_vertexID.get(FriendsTable[0]), UserName_vertexID.get(FriendsTable[1])) )  {
                g.addUndirectedSimpleEdge(UserName_vertexID.get(FriendsTable[0]), UserName_vertexID.get(FriendsTable[1]));                                                  
            }
        }       
        userAssociations.removeAll(userAssociations);        
        return g;
    }

    @Override
    public SetOfCommunities getClusters() {
        long startTime, endTime, totalTime;
        Grph g = new Grph();
        g  = fillGraph(g);
                
        //metis clusterer = new metis();  // my FIXed version
        Gpmetis clusterer = new Gpmetis();    // Original !!            
        socialpserver.SocialPServer.algorithmOutputLogger.info("\n  Metis algoritm is trying to find communities...");                
        startTime = System.currentTimeMillis();
        //List<IntSet> clusterList = clusterer.compute(g, 50, new Random(5));
        List<IntSet> clusterList = clusterer.compute(g, 10, Gpmetis.Ptype.rb, Gpmetis.Ctype.rm, Gpmetis.Iptype.grow, Gpmetis.Objtype.cut, false, false, 1, 10, 1, new Random(5));
        //List<IntSet> clusterList = clusterer.compute(g, 22, Gpmetis.Ptype.kway, Gpmetis.Ctype.shem, Gpmetis.Iptype.random, Gpmetis.Objtype.cut, false, false, 30, 10, 1, new Random(5));        
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        // tranform communities.DataStructure from grph type to a Global type
        town = new SetOfCommunities();
        for (IntSet tempTeam : clusterList) {
            Community community = new Community();
            for (IntCursor member : tempTeam) {
                community.addMember(vertexID_UserName.get(member.value));
            }            
            town.addCommunity(community);          
        }
        socialpserver.SocialPServer.algorithmOutputLogger.info("Metis algoritm  ***found " + town.size() + " clusters***");         
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
    
    /**
     * Enables the assertions, which are heavily used by Grph. The code of Grph
     * makes an extensive use to assertions. This tends to improve the quality
     * of the source code. Contrarily to the recommendations, Grph uses
     * assertions for method parameters checkings too. This enables the user to
     * disable the assertions, hence disabling all verifications, making the
     * execution sightly faster. The disadvantage is that the user must not
     * forget to include the following line before the first call the the Grph
     * class, in order to enable error verification during the development
     * process.
     */
    public final void enableJavaAssertions() {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    @Override
    public void clear() {
        town.clear();
    }

}
