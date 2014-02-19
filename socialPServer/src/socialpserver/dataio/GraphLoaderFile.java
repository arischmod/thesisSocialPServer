package socialpserver.dataio;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads a graph from a given file.
 *
 * @author arix
 */
public class GraphLoaderFile implements GraphLoader {

    private String UserAssociationFile;
    private Set<String[]> userAssociations = new HashSet<>();

    public GraphLoaderFile(String UserAssociationFile) {
        this.UserAssociationFile = UserAssociationFile;
    }

    @Override
    public Set<String[]> getGraph() {
        return userAssociations;
    }

    @Override
    public void loadGraph() {
        try {
            socialpserver.SocialPServer.socialPServerOutputLogger.info("    loading User friendship from File...");
            FileInputStream fstream = new FileInputStream(UserAssociationFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            strLine = br.readLine(); // first line contains info                    

            while ((strLine = br.readLine()) != null) {   // read line by line

                String[] FriendsTable = strLine.split("	");
                String[] temp = {FriendsTable[0], FriendsTable[1]};
                userAssociations.add(temp);
                /*
                 if (FriendsTable[2].equals("1")) {
                 String[] temp = {FriendsTable[0], FriendsTable[1]};
                 userAssociations.add(temp);
                 }
                 */
            }
            in.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
