package trougao;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class TriangleNew {
    
    private static final int X_MAX = 100;
    private static int[][] values = new int[X_MAX][X_MAX];
    
    public TriangleNew() {
    }

    public static void main(String[] args) {
        try {
            FileInputStream fis = new FileInputStream("d:/triangle.txt");
            InputStreamReader in = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(in);
            for (int i = 0; i < X_MAX; i++) {
                String string = br.readLine();
                String[] strings = string.split(" ");
                for (int j = 0; j <= i; j++) {
                    values[i][j] = Integer.parseInt(strings[j]);
                }
            }
            
            for (int i = X_MAX - 1; i >= 0; i--) {
                for (int j = 0; j <= i; j++) {
                    if (i == X_MAX - 1) {
                        values[i][j] = values[i][j];
                    } else {
                        values[i][j] = values[i][j] + (values[i + 1][j] > values[i + 1][j + 1] ? values[i + 1][j] : values[i + 1][j + 1]);
                    }
                }
            }
            System.out.println(values[0][0]);
            
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}

