import java.util.ArrayList;

public class NetworkTest {
    public static void main(String[] args) {
        NetAnalysis na = new NetAnalysis("/Users/caseydinan/Documents/cs1501/network_data1.txt");
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(0);
        path.add(2);
        path.add(4);
        path.add(1);
        System.out.println(na.bandwidthAlongPath(path));
        System.out.println(na.copperOnlyConnected());
        System.out.println(na.connectedTwoVertFail());
        System.out.println(na.lowestLatencyPath(0,1));
        System.out.println(na.lowestAvgLatST());
    }
}