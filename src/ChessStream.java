package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:Aurora
 * @create: 2023-07-25 19:56
 * @Description:
 */
public class ChessStream {
    Axis TempInAxis = Axis.build(-1, -1);
    String TempPath = null;
    int ReadTimerFlag = -1;

    public int ReadFromInput(String FolderPath) {

        ReadTimerFlag = -1;

        TempPath = FolderPath;

        File folder = null;
        if (!FolderPath.equals("")) folder = new File(FolderPath);
        else return 1;

        if (!folder.exists() && !folder.isDirectory()) return 1;

        File[] files = folder.listFiles();// Obtain all files
        assert files != null;
        int FileCount = files.length;

        if (FileCount != 1) return 2;
        TempInAxis = Axis.build(Integer.parseInt(files[0].getName().substring(0, 2)), Integer.parseInt(files[0].getName().substring(2, 4)));
        // Del their file
        files[0].delete();
        return 0;
    }

    public Axis ReadTimer(String FolderPath) {

        TempInAxis = Axis.build(-1, -1);

        Thread Checker = new Thread(() -> {
            synchronized (this){
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                        // When Checker have be interrupted Unexpectedly
                    }
                    if (ReadTimerFlag == 1) {
                        System.out.println("folder no exists");
                        break;
                    } else if (ReadTimerFlag == 2) {
                        System.out.println("Waiting for opposite's reply,Please check only one file in folder " + FolderPath);
                        break;
                    }
                    ReadTimerFlag = -1;
                }
            }
        });
        Checker.start();
        while (TempInAxis.getX() == -1 || TempInAxis.getY() == -1) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ReadTimerFlag = ReadFromInput(FolderPath);
        }
        Checker.interrupt();
        return TempInAxis;
    }

    public void WriteMyMove(int a, int b, String FolderPath) {
        String A;
        String B;
        if (a == 0 || b == 0) return;
        if (a < 10 && a > 0) A = "0" + a;
        else A = "" + a;
        if (b < 10 && b > 0) B = "0" + b;
        else B = "" + b;
        String MoveInPath = FolderPath + "\\" + A + B;
        File MyMove = new File(MoveInPath);
        try {
            MyMove.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String GetPath() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String s;
        try {
            s = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    static class Axis {
        private int x = -1;
        private int y = -1;

        public Axis(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Axis build(int x, int y) {
            return new Axis(x, y);
        }

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

        @Override
        public String toString() {
            return "Axis{" + "x=" + x + ", y=" + y + '}';
        }
    }

}
