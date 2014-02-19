/**
 *	This file is part of Grph.
 *	
 *  Grph is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Grph is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Grph.  If not, see <http://www.gnu.org/licenses/>. *
 */

package todelete;

import grph.Grph;
import grph.algo.labelling.Incrementlabelling;
import grph.algo.labelling.Relabelling;
import grph.io.AbstractGraphTextWriter;
import grph.io.GraphBuildException;
import grph.io.ParseException;

import java.io.IOException;
import java.io.PrintStream;

public class mWriter extends AbstractGraphTextWriter
{
    @Override
    public void printGraph(Grph g, PrintStream os)
    {
	if (!g.isUndirectedSimpleGraph())
	    throw new IllegalArgumentException();

        os.print(g.getNumberOfVertices());	
	os.print(' ');        
        //os.print("592860");                
	os.print(g.getEdges().size());
	os.print('\n');
        
	if (g.getVertices().contains(0))
	    throw new IllegalArgumentException("graph has vertex 0 which is not supported by metis");

	for (int v = 1; v <= g.getVertices().getGreatest(); ++v)
	{
	    if (g.getVertices().contains(v))
	    {                
		os.print(g.getNeighbours(v).toString_numbers_only()); 
	    }

	    os.print('\n');
	}
    }

    public static void main(String[] args) throws IOException, ParseException, GraphBuildException
    {
	Grph g = new Grph();
	g.grid(3, 3);
	Relabelling rl = new Incrementlabelling(1);
	Grph gg = rl.compute(g);
	String s = new mWriter().printGraph(gg);
	System.out.println(s);
	// gg.display();
    }
}
