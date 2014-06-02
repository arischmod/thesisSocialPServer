package socialpserver;

import org.apache.commons.collections15.*;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import java.io.IOException;
import socialpserver.algorithmic.ClustererAlgorithm;
import socialpserver.algorithmic.BronKerboschCliqueCommunityDiscoverer;
import socialpserver.algorithmic.EdgeBetweennessCommunityDiscoverer;
import socialpserver.algorithmic.MetisCommunityDiscoverer;
import todelete.StrongConnectivityCommunityDiscoverer;
import socialpserver.algorithmic.WeakComponentCommunityDiscoverer;
import socialpserver.dataio.FeatureLoaderDB;
import socialpserver.dataio.PSocialDBAccess;
import socialpserver.dataio.GraphLoaderDB;
import socialpserver.dataio.GraphLoaderFile;
import socialpserver.dataio.UserAssociationsStorerDB;
import socialpserver.dataio.UserFeatureLoaderDB;

import java.util.logging.Logger;
import socialpserver.dataio.CommunityStorerDB;
import socialpserver.dataio.GraphLoader;
import socialpserver.dataio.LoggerInit;

/**
 * The main Class of the SocialPServer project
 *
 * @startuml "java.lang.Enum<ImageSize>" <|-- enum ImageSize ImageSize :
 * +ImageSize BIG ImageSize : +ImageSize LARGE ImageSize : +ImageSize MEDIUM
 * ImageSize : +ImageSize THUMB ImageSize : +getHeight() ImageSize : +getWidth()
 * @enduml
 */
public class SocialPServer {

    private static final String UserAssociationFile = "datasets/hetrec2011-lastfm-2k/user_friends.dat"; //"datasets/flixster/ratings.txt"; // "datasets/epinions/user_rating.txt"; //"datasets/hetrec2011-lastfm-2k/user_friends.dat"

    public static final Logger algorithmOutputLogger = Logger.getLogger("algorithmOutput");  // create Loggers 
    public static final Logger socialPServerOutputLogger = Logger.getLogger("socialPServerOutput");

    /**
     * main method of SocialPServer project
     *
     * @author arix
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        LoggerInit.initLogger();
        
        String psClient = args[0]; //  "flixster"  "LastFM"
        psClient = "LastFM";
        
        // to antikhmeno db tha mou dhnetai apo ton pServer se run time
        pserver.data.DBAccess db = new pserver.data.DBAccess("jdbc:mysql://127.0.0.1:3306/pserver?", "root", "!8kbx78qb");  // 83.212.125.37 okeanos DB // 127.0.0.1 local
        
        API api = new API(db, psClient, "soc", 777);
        api.UserAssociationsFileToDB(UserAssociationFile);
        api.produceCommunities("weak");
        
        //execute(psClient,db);
        socialPServerOutputLogger.info("process finished *");
    }

    // Basic Functionality
    private static void execute(String psClient, pserver.data.DBAccess db) {
        PSocialDBAccess dbAccess = new PSocialDBAccess(psClient, db, "soc", 777);
        //at DB --> create index name ON up_features (FK_psclient);   // for beter performance    

        // Insert User Associations info into DB
        //UserAssociationsToDB uadb = new UserAssociationsStorerDB(dbAccess);
        //uadb.friendshipToDB(new GraphLoaderFile(UserAssociationFile));

        GraphLoaderDB graphLoaderDB = new GraphLoaderDB(dbAccess);
        graphLoaderDB.loadGraph(); // load user Associations to loader (RAM) -> u can retrive them by loader.getGraph()

        //socialUtils(graphLoaderDB); // info about the social graph                         
        //String algorithm = args[1];
        String algorithm = "weak";
        switch (algorithm) {
            case "weak":
                ClustererAlgorithm weak = new WeakComponentCommunityDiscoverer(graphLoaderDB);
                weak.getClusters();
                weak.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                //weak.storeCommunities(new CommunityStorerDB(dbAccess));
                weak.clear();
                weak = null;
                System.gc();
                break;
            case "metis":
                ClustererAlgorithm met = new MetisCommunityDiscoverer(graphLoaderDB);
                met.getClusters();
                met.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                //met.storeCommunities(new CommunityStorerDB(dbAccess));
                met.clear();
                met = null;
                System.gc();
                break;
            case "betw":
                ClustererAlgorithm edg = new EdgeBetweennessCommunityDiscoverer(graphLoaderDB, 5);
                edg.getClusters();
                edg.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                //edg.storeCommunities(new CommunityStorerDB(dbAccess));
                edg.clear();
                edg = null;
                System.gc();
                break;
            case "bk":
                ClustererAlgorithm bk = new BronKerboschCliqueCommunityDiscoverer(graphLoaderDB);
                bk.getClusters();
                bk.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
                //bk.storeCommunities(new CommunityStorerDB(dbAccess));
                bk.clear();
                bk = null;
                System.gc();
                break;

            /*   case "strcon":
             ClustererAlgorithm str = new StrongConnectivityCommunityDiscoverer(graphLoaderDB);
             startTime = System.currentTimeMillis();
             str.getClusters();
             endTime = System.currentTimeMillis();
             totalTime = endTime - startTime;
             socialpserver.SocialPServer.algorithmOutputLogger.info("  runTime: " + totalTime);
             str.evaluate(new FeatureLoaderDB(dbAccess), new UserFeatureLoaderDB(dbAccess));
             //str.storeCommunities(new CommunityStorerDB(dbAccess));
             str.clear();
             */
            default:
                socialPServerOutputLogger.info("* algorithm does not exist *");
                break;
        }
    }
    
    /**
     * useful info about the Social Graph. an EdgeBetweenness graph will be used
     * to calculate the size of the social graph
     *
     * @return #Vertex and #Edge
     */
    private static void socialUtils(GraphLoader glLoader) {
        algorithmOutputLogger.info("an EdgeBetweenness graph will be used to calculate the size of the social graph");
        EdgeBetweennessCommunityDiscoverer temp = new EdgeBetweennessCommunityDiscoverer(glLoader, 0);
        UndirectedGraph<String, String> g = new UndirectedSparseGraph<>();
        g = temp.fillGraph(g);
        // the following statement is used to log messages
        algorithmOutputLogger.info("**Social Graph graph has " + g.getVertexCount() + " users and " + g.getEdgeCount() + " friendship edges**");
    }
}
