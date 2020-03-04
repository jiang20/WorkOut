package PJ2;

import java.util.ArrayList;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        String filename = "C:\\Users\\cxz\\Desktop\\Timetable.xls";
        System.out.println("input the stations");
        Scanner input = new Scanner(System.in);
        String string = input.nextLine();
        String[] strings1 = string.split(" ");
        long time = 0;
        for(int i = 0 ; i < strings1.length - 1; i++){
            Navigator navigator = new Navigator();
            navigator.loadMap(filename);
            ArrayList<String> strings = navigator.getPath1(strings1[i],strings1[i+1]);
            time += navigator.getTime();
            for(int j = 0 ; j < strings.size() ; j++){
                if(j != strings.size() - 1) {
                    System.out.print(strings.get(strings.size() - 1 - j) + "-");
                }
                else {
                    if(i == strings1.length - 2)
                        System.out.print(strings.get(strings.size() - 1 - j));
                }
            }
        }
        System.out.print("\n用时"+time+"ms");
    }
}