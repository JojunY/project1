package src;

import java.io.File;

/**
 * @author:Aurora
 * @create: 2023-07-25 19:56
 * @Description:
 */
public class ChessSteam {
    static class Axis {
        int x;
        int y;

        public Axis(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Axis build(int x, int y) {
            return new Axis(x, y);
        }
    }

    private File outFile;
    private File inFile;
    private String OutFolderPath = "C:\\out";
    private String InFolderPath = "C:\\in";
    private String MyLastMoveTimeTamp;    // 产生Out输出时，会记录这个值


    // 检查是否 Out目录是否存在 对手的New File
    // 超时检测，文件名格式检测，
    public Axis ReadFromOut() {
        File folder = new File(this.OutFolderPath);
        if (folder.exists() && folder.isDirectory()) return null;
        // get all files in out
        File[] files = folder.listFiles();
        int FileCount = files.length;
        // 意味着我们先手, 返回Axis的xy都为-1
        if (FileCount == 0) return Axis.build(-1, -1);
        long MinTime = System.currentTimeMillis() - files[0].lastModified();
        long temp;
        int j = 0;
        for (int i = 0; i < FileCount; i++) {
            // 寻找最近的修改时间，最后修改文件
            temp = System.currentTimeMillis() - files[i + 1].lastModified();
            if (temp < MinTime) {
                MinTime = temp;
                j = i;
            }
        }
        return Axis.build(Integer.parseInt(files[j].getName().substring(0, 1)),Integer.parseInt(files[j].getName().substring(1, 3)));

    }
}
