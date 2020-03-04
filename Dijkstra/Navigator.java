package PJ2;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Navigator {
    private ArrayList<Vertex> vertices = new ArrayList<>();

    public void loadMap(String path) {//对地铁线路进行处理，得到最终的结果
        File input = new File(path);
        try {
            Workbook book = Workbook.getWorkbook(input);
            int sheetSize = book.getNumberOfSheets();
            String name;
            String stationName;
            String time1;
            String time2 = "";
            for(int k = 0; k < sheetSize; k++) {
                name = book.getSheetNames()[k];
                Sheet sheet = book.getSheet(k);
                if(k == 9 || k == 10) {
                    //第一个站点
                    Vertex newStation;
                    stationName = sheet.getCell(0, 2).getContents();
                    newStation = exist(stationName);
                    if(newStation == null){
                        newStation = new Vertex(stationName);
                        vertices.add(newStation);
                    }
                    if(!newStation.getLine().contains(k+1)) {
                        newStation.getLine().add(k+1);
                    }
                    time1 = sheet.getCell(1, 2).getContents();
                    Vertex preStation;
                    Vertex binStation = null;//两个方向的站点
                    int j = 0;//两个方向的站点对应的索引
                    String time3 = "";
                    //导入中间站点
                    for(int i = 3;  i < sheet.getRows(); i++) { //第一个方向和第二个方向的部分站点
                        preStation = newStation;
                        stationName = sheet.getCell(0, i).getContents();
                        time2 = sheet.getCell(1, i).getContents();

                        if(time2.equals("--") || sheet.getCell(2, i).getContents().equals("--")){ //跳过某些站点
                            if(binStation == null){
                                binStation = preStation;
                                j = i;
                                time3 = time1;
                            }
                            if(time2.equals("--"))
                                continue;
                        }

                        newStation = exist(stationName);
                        if (newStation == null) {
                            newStation = new Vertex(stationName);
                            vertices.add(newStation);
                        }
                        if(!newStation.getLine().contains(k+1)) {
                            newStation.getLine().add(k+1);
                        }
                        if(!preStation.getLine().contains(k+1)) {
                            preStation.getLine().add(k+1);
                        }
                        preStation.getVertices().add(newStation);
                        newStation.getVertices().add(preStation);
                        Edge edge = new Edge(preStation, newStation, name, getTime(time1, time2));
                        preStation.getEdges().add(edge);
                        newStation.getEdges().add(edge);
                        edge = null; //我想释放资源
                        time1 = time2;
                    }

                    preStation = binStation;
                    time1 = time3;
                    for(; j < sheet.getRows(); j++) { //第二个方向的分站点
                        stationName = sheet.getCell(0, j).getContents();
                        time2 = sheet.getCell(2, j).getContents();

                        if(time2.equals("--")){ //跳过某些站点
                            continue;
                        }

                        newStation = exist(stationName);
                        if (newStation == null) {
                            newStation = new Vertex(stationName);
                            vertices.add(newStation);
                        }
                        assert preStation != null;
                        preStation.getVertices().add(newStation);
                        newStation.getVertices().add(preStation);
                        if(!preStation.getLine().contains(k+1)) {
                            preStation.getLine().add(k+1);
                        }
                        if(!newStation.getLine().contains(k+1)) {
                            newStation.getLine().add(k+1);
                        }
                        Edge edge = new Edge(preStation, newStation, name, getTime(time1, time2));
                        preStation.getEdges().add(edge);
                        newStation.getEdges().add(edge);
                        edge = null; //我想释放资源
                        time1 = time2;
                        preStation = newStation;
                    }

                }else {

                    //第一个站点
                    Vertex newStation;
                    stationName = sheet.getCell(0, 1).getContents();
                    newStation = exist(stationName);
                    if(newStation == null){
                        newStation = new Vertex(stationName);
                        vertices.add(newStation);
                    }
                    if(!newStation.getLine().contains(k+1)) {
                        newStation.getLine().add(k+1);
                    }
                    time1 = sheet.getCell(1, 1).getContents();
                    Vertex preStation;
                    //导入中间站点
                    for(int i = 2; i < sheet.getRows(); i++) {
                        preStation = newStation;
                        stationName = sheet.getCell(0, i).getContents();
                        time2 = sheet.getCell(1, i).getContents();
                        newStation = exist(stationName);
                        if(newStation == null) {
                            newStation = new Vertex(stationName);
                            vertices.add(newStation);
                        }
                        if(!newStation.getLine().contains(k+1)) {
                            newStation.getLine().add(k+1);
                        }
                        if(!preStation.getLine().contains(k+1)) {
                            preStation.getLine().add(k+1);
                        }
                        preStation.getVertices().add(newStation);
                        newStation.getVertices().add(preStation);
                        Edge edge = new Edge(preStation,newStation,name, getTime(time1, time2));
                        preStation.getEdges().add(edge);
                        newStation.getEdges().add(edge);
                        edge = null; //我想释放资源
                        time1 = time2;
                    }
                }
            }
//            System.out.println(vertices.size() + "  " + sheetSize);
        }catch (BiffException | IOException e) {
            e.printStackTrace();
        }
    }

    private Vertex exist(String station) {  //判断是否已经导入了点
        for (Vertex vertex : vertices) {
            if (vertex.getName().equals(station))
                return vertex;
        }
        return  null;
    }
    private long getTime(String start, String end) { //返回分钟数
        int length1 = start.length();
        int length2 = end.length();
        long minute = (int)end.charAt(length2 - 1) - (int)start.charAt(length1 - 1);
        long ten = (int)end.charAt(length2 - 2) - (int)start.charAt(length1 - 2);
        long hour = (int)end.charAt(length2 - 4) - (int)start.charAt(length1 - 4);
        hour = hour >= 0? hour : hour + 4;
        return hour * 60 + ten * 10 + minute;
    }
    private long time;
    public ArrayList<String> getPath1(String start, String end){
        Dijkstra dijkstra = new Dijkstra();
        Vertex startV = exist(start);
        Vertex endV = exist(end);
        if(startV == null || endV == null) {
            return null; //抱歉没有这个站点
        }
        ArrayList<String> path = new ArrayList<>();
        if(startV == endV){
            path.add(startV.getName());
            return path;
        }//ok
        path = dijkstra.getPath(vertices,startV,endV);
        this.time = dijkstra.time;
        return path;
    }
    public long getTime(){
        return time;
    }

}
class Vertex {
    private String name;
    private long time;
    private boolean known;
    private ArrayList<Integer> line;
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    private Vertex pre;

    Vertex(String name) {
        this.name = name;
        this.time = Integer.MAX_VALUE;
        this.known = false;
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        line = new ArrayList<>();
    }

    Vertex(String name, long time) {
        this.name = name;
        this.time = time;
        this.known = false;
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        line = new ArrayList<>();
    }

    // get name 0f station
    public String getName() {
        return name;
    }

    // set name of station
    public void setName(String name) {
        this.name = name;
    }

    // get time
    public long getTime() {
        return time;
    }

    // set time
    public void setTime(long time) {
        this.time = time;
    }

    // get known
    public boolean isKnown() {
        return known;
    }

    // set known
    public void setKnown(boolean known) {
        this.known = known;
    }

    public Vertex getPre() {
        return pre;
    }

    public void setPre(Vertex pre) {
        this.pre = pre;
    }

    public ArrayList<Vertex> getVertices(){
        return vertices;
    }
    public ArrayList<Edge> getEdges(){
        return edges;
    }
    public ArrayList<Integer> getLine(){
        return line;
    }

    public int getLine( Vertex vertex , Vertex vertex1){
        int edgeLine = 0;
        bbb:for(int i = 0; i < vertex.getLine().size() ; i++){
            for(int j = 0; j < vertex1.getLine().size() ; j++){
                if(vertex.getLine().get(i) == vertex1.getLine().get(j)) {
                    edgeLine = vertex.getLine().get(i);
                    break bbb;
                }
            }
        }
        return edgeLine;
    }
}

class Edge{
    private Vertex preStation , newStation;
    private String name ;
    private long time ;
    private int line;
    Edge(Vertex preStation , Vertex newStation , String name , long time){
        this.preStation = preStation ;
        this.newStation = newStation ;
        this.name = name ;
        this.time = time ;
        int i = 0 , k = 0 ;
        a:for( ; i < this.preStation.getLine().size() ; i ++){//get number of line
            for( ; k < this.newStation.getLine().size() ; k ++){
                if(this.preStation.getLine().get(i).equals(this.newStation.getLine().get(k))){
                    this.line = this.preStation.getLine().get(i);
                    break a;//用标记的方法将break用于外部循环
                }
            }
        }
    }
    //used in dijkstra algorithm
    public Vertex getPreStation(){
        return preStation;
    }

    public Vertex getNewStation() {
        return newStation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
