package src;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Aurora
 * @create: 2023-07-25 19:56
 * @Description:
 */
public class ChessSteam {
// true first , false second
    static class Axis {
        private int x = -1;
        private int y = -1;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public List<Integer> getXY() {
            List<Integer> list = new ArrayList<Integer>();
            list.add(getX());
            list.add(getY());
            return list;
        }


        public Axis(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Axis build(int x, int y) {
            return new Axis(x, y);
        }

        @Override
        public String toString() {
            return "Axis{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }



    // full path, FirstRound


    Axis TempInAxis;


    public Axis ReadFromInput(String FolderPath) {
        File folder = null;
        if (!FolderPath.equals("")) {
            folder = new File(FolderPath);
        }

        if (!folder.exists() && !folder.isDirectory()) {
            System.out.println("The folder does not exist, please create a folder");
            return Axis.build(-1, -1);// The folder does not exist
        }
        File[] files = folder.listFiles();// Obtain all files
        int FileCount = files.length;
        if (FileCount != 1) {
            System.out.println("Please check if the folder is only in one file");
            return Axis.build(-1, -1);// In has 2 files, which may not have been deleted by the other party
        }

        TempInAxis = Axis.build(Integer.parseInt(files[0].getName().substring(0, 2)), Integer.parseInt(files[0].getName().substring(2, 4)));

        // Del their file
        files[0].delete();

        return TempInAxis;
    }

    public Axis ReadFromInTimer(String FolderPath) {

        ReadFromInput(FolderPath);

        while (TempInAxis.getX() == -1 || TempInAxis.getY() == -1) {
            System.out.println("Waiting for opposite's reply");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return TempInAxis;
    }

    public Boolean WriteMyMoveToIn(int a, int b, String FolderPath) {
        if (a == 0 || b == 0) return false;
        String A = "";
        String B = "";
        if (a < 10 && a > 0) A = "0" + a;
        if (b < 10 && b > 0) B = "0" + b;

        String MoveInPath = FolderPath + "\\" + A + B;
        File MyMove = new File(MoveInPath);

        try {
            boolean newFileBoolean = MyMove.createNewFile();
            return newFileBoolean;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
