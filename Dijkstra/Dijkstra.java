package PJ2;

import java.util.ArrayList;

public class Dijkstra {
    long time;
    public ArrayList<String> getPath(ArrayList<Vertex> vertices , Vertex startV , Vertex endV) {

        this.time = dijkstra(vertices , startV);
        ArrayList<String> path = new ArrayList<>();
        int edge1;
        int edge2 = 0;
        while (endV.getPre() != null) {//加一个标注线路的变量，在存的时候对线路号进行存储，加油！已经很大进步了！
            edge1 = endV.getLine(endV, endV.getPre());
            if (edge1 != edge2) {
                path.add(endV.getName());//换线时输出地铁站
                path.add("line" + edge1);
                edge2 = edge1;
            }
            endV = endV.getPre();
        }
        path.add(startV.getName());
        return path;
    }
    public long dijkstra(ArrayList<Vertex> vertices , Vertex startV){
        long time1 = 0, time2 = 0;
        time1 = System.currentTimeMillis();
        if(vertices.isEmpty()||!vertices.contains(startV)){
            return 0;
        }
        ArrayList<Vertex> unknown = new ArrayList<>();
        ArrayList<Vertex> known = new ArrayList<>();

        unknown.addAll(vertices);
        startV.setTime(0);
        while (!unknown.isEmpty()){
            Vertex v1 = findMin(unknown);
            v1.setKnown(true);
            getKnown(v1,known,unknown);

            for(Vertex v:v1.getVertices()){
                Vertex w = v;
                if(!w.isKnown()){
                    long cvw = 0 ;
                    for(Edge e:v1.getEdges()){
                        Edge edge = e;
                        if(edge.getNewStation().getName().equals(w.getName())||edge.getPreStation().getName().equals(w.getName())){
                            cvw = edge.getTime();
                            break;
                        }
                    }
                    if(v1.getTime() + cvw < w.getTime()){
                        //update w
                        w.setTime(v1.getTime() + cvw);
                        w.setPre(v1);
                    }
                }
            }
        }
        time2 = System.currentTimeMillis();
        return time2 - time1;
    }
    // change ArrayList known and unknown
    public void getKnown(Vertex vertex , ArrayList<Vertex> known , ArrayList<Vertex> unknown){
        if(!known.contains(vertex)&&unknown.contains(vertex)) {
            known.add(vertex);
            unknown.remove(vertex);
        }
    }
    // find the smallest time from all unknown vertices
    public Vertex findMin(ArrayList<Vertex> vertices){
        long min = Integer.MAX_VALUE;
        Vertex minV = new Vertex("min");
        for(Vertex v:vertices){
            if(v.getTime() < min){
                min = v.getTime();
                minV = v;
            }
            else if(v.getTime() == min) {
                if(v.getPre() != null&&v.getPre().getPre() != null) {
                    if (v.getLine(v, v.getPre()) == v.getPre().getLine(v.getPre().getPre(), v.getPre())) {
                        min = v.getTime();
                        minV = v;
                    }
                }
            }
        }
        return minV;
    }
}
