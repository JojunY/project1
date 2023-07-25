package src;

import java.io.File;
import java.io.IOException;

/**
 * @author:Aurora
 * @create: 2023-07-25 19:56
 * @Description:
 */
public class ChessSteam {
    static class Axis {
        private int x = -1;
        private int y = -1;

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
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

    private String OutFolderPath = "C:\\out"; //记录
    private String InFolderPath = "C:\\in"; //读取对方，删除
    Axis TempInAxis;

    /**
     * 检测对手放在int目录中最新的坐标文件，返回坐标对象
     * 文件和文件夹空检测，
     * In文件检测（当In文件夹存在多个文件时，说明对面没有Cut文件，当In不存在文件时，我方先手或等待读取，当In存在一个文件，对方已经落子，我方会进行算法和Cut处理）
     *
     * @return
     */
    public Axis ReadFromIn() {
        File folder = new File(this.InFolderPath);

        if (!folder.exists() && !folder.isDirectory()) {
            System.out.println("文件夹不存在,请创建文件夹");
            return Axis.build(-1, -1);//文件夹不存在
        }
        File[] files = folder.listFiles();// 取得全部文件
        int FileCount = files.length;
        if (FileCount != 1) {
            System.out.println("请检查对方是否删除历史坐标文件");
            return Axis.build(-1, -1);// In存在2个文件，可能对方没删除
        }

        TempInAxis = Axis.build(Integer.parseInt(files[0].getName().substring(0, 2)), Integer.parseInt(files[0].getName().substring(2, 4)));

        //Cut对方的坐标文件到out中
        String CutFilePath = OutFolderPath + "\\" + files[0].getName();
        File newFile = new File(CutFilePath);
        boolean renamed = files[0].renameTo(newFile);
        if (!renamed){
            System.out.println("Cut失败");
            return Axis.build(-1, -1);
        }

        return TempInAxis;
    }

    /**
     * Out计时器, 入口方法
     * @return
     */
    public Axis ReadFromInTimer() {

        ReadFromIn();

        while (TempInAxis.getX() == -1 || TempInAxis.getY() == -1) {
            System.out.println("正在等待对手的坐标");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return TempInAxis;
    }

    public Boolean WriteMyMoveToIn(int a, int b) {
        if (a == 0 || b == 0)return false;
        String A = "";
        String B = "";
        if (a < 10 && a>0) A = "0"+ a;
        if (b < 10 && b>0) B = "0"+b;

        String MoveInPath = InFolderPath + "\\" + A + B;
        File MyMove = new File(MoveInPath);

        try {
            return MyMove.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
