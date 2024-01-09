import java.util.ArrayList;

import java.io.*;

public class Biconnected {
    //Class to find articulation points using algorithm layed out in Algorithms by Sedgewick and Wayne
    private int[] low;
    private int[] pre;
    private int cnt;
    private boolean[] articulation;

    public Biconnected(AdNode[] G, int vertex) {
        low = new int[G.length];
        pre = new int[G.length];
        articulation = new boolean[G.length];
        for (int v = 0; v < G.length; v++) {
            low[v] = -1;
        }
        for (int v = 0; v < G.length; v++) {
            pre[v] = -1;
        }
            
        for (int v = 0; v < G.length; v++) {
            if (pre[v] == -1) {
                dfs(G, v, v, vertex);
            }
        }
    }

    private void dfs(AdNode[] G, int u, int v, int vertex) {
        int children = 0;
        pre[v] = cnt++;
        low[v] = pre[v];
        AdNode curr = G[v].getNext();
        while (curr != null) {
            int w = curr.getVert();
            if (pre[w] == -1) {
                children++;
                dfs(G, v, w, vertex);
                low[v] = Math.min(low[v], low[w]);
                if (low[w] >= pre[v] && u != v && v != vertex) {
                    articulation[v] = true;
                }
            }
            else if (w != u) {
                low[v] = Math.min(low[v], pre[w]);
            }
            curr = curr.getNext();
        }
        if (u == v && children > 1 && v != vertex){
            articulation[v] = true;
        }         
    }

    public boolean anyArticulation() {
        boolean articulationPoint = false;
        for (int i = 0; i < articulation.length; i++) {
            //System.out.println(i);
            if (articulation[i]) {
                articulationPoint = true;
            }
        }
        return articulationPoint; 
    }
    
}