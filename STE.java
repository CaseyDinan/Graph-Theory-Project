public class STE {

    
    protected int u; //first endpoint of edge

    
    protected int w; //second edgepoint of edge

    
    public STE(int v1, int v2) {
        u = v1;
        w = v2;
    }

    public boolean equals(STE o) { //Equality comparison, treating edges as undirected
        if (u == o.u && w == o.w) {
            return true;
        } else if (u == o.w && w == o.u) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "(" + String.valueOf(u) + ", " + String.valueOf(w) + ")";
    }
}