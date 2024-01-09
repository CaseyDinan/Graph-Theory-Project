import java.util.ArrayList;

import java.io.*;

public class NetAnalysis {

    private AdNode[] adjacencyList;

    public NetAnalysis(String networkFile) { //builds network using adjaceny list from input file
        try {
            File net = new File(networkFile);
            BufferedReader netReader = new BufferedReader(new FileReader(net));
            int numVertexs = Integer.parseInt(netReader.readLine());
            adjacencyList = new AdNode[numVertexs];
            String lineInfo;
            while ((lineInfo = netReader.readLine()) != null) {
                String[] info = lineInfo.split(" ");
                int start = Integer.parseInt(info[0]);
                int end = Integer.parseInt(info[1]);
                String type = info[2];
                int bandwidth = Integer.parseInt(info[3]);
                int cableLength = Integer.parseInt(info[4]);

                if (adjacencyList[start] == null) {
                    adjacencyList[start] = new AdNode(start);
                }
                AdNode curr = adjacencyList[start];
                while(curr.getNext() != null) {
                    curr = curr.getNext();
                }
                AdNode endVertex = new AdNode(end, type, bandwidth, cableLength);
                curr.setNext(endVertex);

                if (adjacencyList[end] == null) {
                    adjacencyList[end] = new AdNode(end);
                }
                AdNode curr2 = adjacencyList[end];
                while (curr2.getNext() != null) {
                    curr2 = curr2.getNext();
                }
                AdNode startVertex = new AdNode(start, type, bandwidth, cableLength);
                curr2.setNext(startVertex);
            }
        }
        catch (Exception e) {
            System.out.println("Error: Invalid file input.");
        }
    }

    public ArrayList<Integer> lowestLatencyPath(int u, int w) { //Dykstra's algorithm to find shortest path
        if (u >= adjacencyList.length || w >= adjacencyList.length) {
            return null;
        }
        double[] minLatency = new double[adjacencyList.length];
        int[] minPath = new int[adjacencyList.length];
        boolean[]marked = new boolean[adjacencyList.length];
        for (int i = 0; i < minLatency.length; i++) {
            minLatency[i] = Double.POSITIVE_INFINITY; 
        }
        
        minPath[u] = u;
        latencyHelper(u, 0, minLatency, minPath, marked);
        ArrayList<Integer> path = new ArrayList<Integer>();
        if (marked[w]) {
            path.add(w);
        }
        else {
            return null;
        }
        boolean pathFound = false;
        int loc = w;
        int loops = 0;
        while (!pathFound && loops <= minPath.length) {
            int val = minPath[loc];
            path.add(val);
            loc = val;
            if (loc == u) {
                pathFound = true;
            }
            loops = loops + 1;
        }
        if (!pathFound) {
            return null;
        }
        ArrayList<Integer> rPath = new ArrayList<Integer>();
        for (int i = path.size() - 1; i >= 0; i--) {
            rPath.add(path.get(i));
        }
        //throw exception if path not found
        return rPath;
    }

    private void latencyHelper(int loc, double locDistance, double[] minLat, int[] path, boolean[] marked) {
        marked[loc] = true;
        minLat[loc] = locDistance;
        AdNode curr = adjacencyList[loc].getNext();
        double minDistance = Double.MAX_VALUE;
        int minLoc = Integer.MAX_VALUE;
        while (curr != null) {
            double speed;
            if (curr.getType().equals("copper")) {
                speed = 230000000;
            }
            else {
                speed = 200000000;
            }
            double distance = curr.getLength()/speed;
            distance = distance + locDistance;
            if (distance < minLat[curr.getVert()]) {
                minLat[curr.getVert()] = distance;
                path[curr.getVert()] = loc;
            }
            if (distance < minDistance && !marked[curr.getVert()]) {
                minDistance = distance;
                minLoc = curr.getVert();
            }
            curr = curr.getNext();
        }
        if (minLoc == Integer.MAX_VALUE) {
            return;
        }
        latencyHelper(minLoc, minDistance, minLat, path, marked);
    }

    public int bandwidthAlongPath(ArrayList<Integer> p) throws IllegalArgumentException { //finds bandwith avaliable by traversing the given path in adjacency list
        int minBandwidth = Integer.MAX_VALUE;
        for (int i = 0; i < (p.size() - 1); i++) {
            if (p.get(i) >= adjacencyList.length) {
                throw new IllegalArgumentException();
            }
            AdNode curr = adjacencyList[p.get(i)];
            boolean pathFound = false;
            while (curr != null && !pathFound) {
                if (curr.getVert() == p.get(i+1)) {
                    if (curr.getBand() < minBandwidth)
                    minBandwidth = curr.getBand();
                    pathFound = true;
                }
                else {
                    curr = curr.getNext();
                }
            }
            if (!pathFound) {
                throw new IllegalArgumentException();
            }
        }
        return minBandwidth;
    }

    public boolean copperOnlyConnected() { //checks if graph would remain connected if only copper wires were considered using DFS
        boolean[] marked = new boolean[adjacencyList.length];
        marked = copperHelper(adjacencyList, 0, marked);
        boolean connected = true;
        for (int i = 0; i < marked.length; i++) {
            if (!marked[i]) {
                connected = false;
            }
        }
        return connected;
    }

    private boolean[] copperHelper(AdNode[] graph, int vertex, boolean[] marked) {
        marked[vertex] = true;
        AdNode curr = graph[vertex].getNext();
        while (curr != null) {
            if (!marked[curr.getVert()] && curr.getType().equals("copper")) {
                copperHelper(graph, curr.getVert(), marked);
            }
            curr = curr.getNext();
        }
        return marked;
    }

    public boolean connectedTwoVertFail() { //checks for articulation points in second degree
        boolean connected = true;
        for (int i = 0; i < adjacencyList.length; i++) {
            AdNode[] subGraph = deleteVert(i);
            Biconnected twoVerts = new Biconnected(subGraph, i);
            if(twoVerts.anyArticulation()) {
                connected = false;
            }
        }
        return connected;
    }

    private AdNode[] deleteVert(int vertex) {
        AdNode[] newGraph = new AdNode[adjacencyList.length];
        for (int i = 0; i < adjacencyList.length; i++) {
            //System.out.println(adjacencyList[i].getVert());
            newGraph[i] = new AdNode(adjacencyList[i].getVert());
            if (newGraph[i].getVert() != vertex) {
                AdNode curr = adjacencyList[i].getNext();
                AdNode curr1 = newGraph[i];
                while (curr != null) {
                    if (curr.getVert() != vertex) {
                        AdNode temp = new AdNode(curr.getVert());
                        curr1.setNext(temp);
                        curr1 = curr1.getNext();
                    }
                    curr = curr.getNext();
                }     
            }
        }
        return newGraph;
    }
    

    public ArrayList<STE> lowestAvgLatST() { //uses Prim's to find lowest average spanning tree
        //make sure tree is connected first
        int[] visited = new int[adjacencyList.length];
        ArrayList<STE> spanning = new ArrayList<STE>();
        boolean[] marked = new boolean[adjacencyList.length];
        marked[0] = true;
        int treesize = 0;
        int startEdge;
        double minDistance;
        STE currEdge = null;
        for(int i = 0; i < visited.length - 1; i++) {
            treesize = treesize + 1;
            minDistance = Double.MAX_VALUE;
            for(int j = 0; j < treesize; j++) {
                startEdge = visited[j];
                AdNode curr = adjacencyList[startEdge].getNext();
                while (curr != null) {
                    double speed;
                    if (curr.getType().equals("copper")) {
                        speed = 230000000;
                    }
                    else {
                        speed = 200000000;
                    }
                    double distance = curr.getLength()/speed;
                    if (distance < minDistance && !containsEdge(spanning, startEdge, curr.getVert()) && !(marked[startEdge] && marked[curr.getVert()])) {
                        minDistance = distance;
                        currEdge = new STE(startEdge, curr.getVert());
                        if (treesize < visited.length) {
                            visited[treesize] = curr.getVert();
                        }         
                    }
                    curr = curr.getNext();
                }
            }
            if (currEdge != null && !containsEdge(spanning, currEdge)) {
                spanning.add(currEdge);
                marked[visited[treesize]] = true;
            } 
        }
        if (spanning.size() != adjacencyList.length - 1) {
            return null;
        }
        return spanning;
    }

    private boolean containsEdge(ArrayList<STE> spanning, int startVert, int endVert) {
        STE edge = new STE(startVert, endVert);
        for (int i = 0; i < spanning.size(); i++) {
            if (edge.equals(spanning.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsEdge(ArrayList<STE> spanning, STE edge) {
        for (int i = 0; i < spanning.size(); i++) {
            if (edge.equals(spanning.get(i))) {
                return true;
            }
        }
        return false;
    }
}


class AdNode {

    private int vertex; //vertex ID

    private String edgeType; //Copper or Fiber Optic cable

    private int bandwidth; //Bandwidth capability of cable

    private int length; //length of cable

    private AdNode next;

    public AdNode(int vert) {
        this.vertex = vert;
        this.edgeType = null;
        this.bandwidth = 0;
        this.length = 0;
        this.next = null;
    }

    public AdNode(int vert, String type, int width, int len) {
        this.vertex = vert;
        this.edgeType = type;
        this.bandwidth = width;
        this.length = len;
        this.next = null;
    }

    public int getVert() {
        return vertex;
    }

    public String getType() {
        return edgeType;
    }

    public int getBand() {
        return bandwidth;
    }

    public int getLength() {
        return length;
    }

    public AdNode getNext() {
        return next;
    }

    public void setNext(AdNode n) {
        this.next = n;
    }
}