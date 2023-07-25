package src;

import java.io.File;

/**
 * @author:Aurora
 * @create: 2023-07-25 19:56
 * @Description:
 */
public class ChessSteam {
    static class Axis {
        private int x;
        private int y;

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

    private File outFile;
    private File inFile;
    private String OutFolderPath = "C:\\out";
    private String InFolderPath = "C:\\in";
    private long MyLastMoveTimeTamp = 0;    // 产生Out输出时，会记录这个值


    /**
     * 检测对手放在Out目录中最新的坐标文件，返回坐标对象
     * 附带坐标名格式检测, 文件夹检测, 文件检测, 对方文件检测
     * @return
     */
    public Axis ReadFromOut() {
        int FileCount = 0;
        File folder = new File(this.OutFolderPath);

        if (!folder.exists() && !folder.isDirectory()){
            System.out.println("文件夹不存在,请创建文件夹");
            return Axis.build(-1, -1);//文件夹不存在
        };
        File[] files = folder.listFiles();// get all files in out
        if (files != null) FileCount = files.length;
        if (FileCount == 0) {
            System.out.println("Out文件夹内为空");
            return Axis.build(-1, -1);// 意味着我们先手, 返回Axis的xy都为-1
        }


        long MinTime = System.currentTimeMillis() - files[0].lastModified();
        long temp;
        int j = 0; //目标文件
        int i = 0;

        while (i + 1 <= FileCount) {
            if (files[i].getName().matches("[0-9]\\{4}")){
                System.out.println("您的文件名存在问题,暂时不支持本程序读入");
                return Axis.build(-1, -1);
            }
            // 寻找最近的修改时间，最后修改文件
            temp = System.currentTimeMillis() - files[i].lastModified();
            if (temp < MinTime) {
                MinTime = temp;
                j = i;
            }
            i++;
        }
        // 判断 对方最新文件是否存在, 与 自己的 In文件全局时间戳比较（存在于Out的）
        if (MyLastMoveTimeTamp == files[j].lastModified()){
            System.out.println("最新的Out文件夹文件为我方提供, 请对方移动并输出");
            return Axis.build(-1, -1);
        }
        return Axis.build(Integer.parseInt(files[j].getName().substring(0, 2)),Integer.parseInt(files[j].getName().substring(2, 4)));
    }

    /**
     *  Out计时器, 入口方法
     * @return
     */
    public Axis ReadFromOutTimer(){

        while (ReadFromOut().getX() == -1 || ReadFromOut().getY() == -1) {
            System.out.println("正在等待对手的坐标");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return ReadFromOut();
    }
}
