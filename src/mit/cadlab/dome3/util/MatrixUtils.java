package mit.cadlab.dome3.util;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: caoq
 * Date: Jun 30, 2003
 * Time: 12:14:09 PM
 * To change this template use Options | File Templates.
 */
public class MatrixUtils {

    public static int[][] removeRowCol(int[][] origianlMatrix, ArrayList bad_indeies) {
        if (bad_indeies.size() == 0) return origianlMatrix;
        int[][] result = null;
        for (int i = 0; i < origianlMatrix.length; i++) {
            boolean badrow = false;
            for (int k = 0; k < bad_indeies.size(); k++) {
                if (i == ((Integer) bad_indeies.get(k)).intValue()) {
                    badrow = true;
                    break;
                }
            }
            if (badrow) result = removeOneRowCol(origianlMatrix, i);
        }
        if (result != null) return result;

        return origianlMatrix;
    }

    public static int[][] removeOneRowCol(int[][] originalMatrix, int index) {
        if (index < 0 || index > originalMatrix.length) return originalMatrix;

        int[][] result = new int[originalMatrix.length - 1][originalMatrix.length - 1];

        for (int i = 0; i < originalMatrix.length - 1; i++) { //row
            for (int k = 0; k < originalMatrix.length - 1; k++) {    //col
                if (i < index && k < index)
                    result[i][k] = originalMatrix[i][k];
                else if (i < index && k >= index)
                    result[i][k] = originalMatrix[i][k + 1];
                else if (i >= index && k < index)
                    result[i][k] = originalMatrix[i + 1][k];
                else if (i >= index && k >= index)
                    result[i][k] = originalMatrix[i + 1][k + 1];
            }
        }
        return result;
    }

    public static DoubleMatrix2D removeRowCol(DoubleMatrix2D origianlMatrix, ArrayList bad_indeies) {
        if (bad_indeies.size() == 0) return origianlMatrix;
        DoubleMatrix2D result = origianlMatrix.copy();

        int o_size = origianlMatrix.rows();  //base on assumption it is a square matrix
        int b_size = bad_indeies.size();
        for (int i = 0; i < o_size; i++) {
            boolean badrow = false;
            for (int k = 0; k < b_size; k++) {
                if (i == ((Integer) bad_indeies.get(k)).intValue()) {
                    badrow = true;
                    break;
                }
            }
            if (badrow) result = removeOneRowCol(result, i);
        }

        return result;
    }

    public static DoubleMatrix2D removeOneRowCol(DoubleMatrix2D originalMatrix, int index) {
        if (index < 0 || index > originalMatrix.size()) return originalMatrix;

        //int[][] result = new int[originalMatrix.length - 1][originalMatrix.length - 1];

        int size = originalMatrix.rows();   //base on assumption it is a square matrix

        DoubleMatrix2D result = new DenseDoubleMatrix2D(size - 1, size - 1);
        for (int i = 0; i < size - 1; i++) { //row
            for (int k = 0; k < size - 1; k++) {    //col
                if (i < index && k < index)
                    result.set(i, k, originalMatrix.getQuick(i, k));
                else if (i < index && k >= index)
                    result.set(i, k, originalMatrix.getQuick(i, k + 1));
                else if (i >= index && k < index)
                    result.set(i, k, originalMatrix.getQuick(i + 1, k));
                else if (i >= index && k >= index)
                    result.set(i, k, originalMatrix.getQuick(i + 1, k + 1));
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[][] testMatrix = new int[][]{{1, 2, 3},
                                         {2, 3, 4},
                                         {3, 4, 5}};
        testMatrix = removeOneRowCol(testMatrix, 1);

        for (int i = 0; i < testMatrix.length; i++)
            for (int k = 0; k < testMatrix[i].length; k++)
                System.out.println(testMatrix[i][k]);

    }

    public static void println(int[][] matrix) {
        System.out.println(matrix.length + "X" + matrix.length);
        String row = "";
        for (int i = 0; i < matrix.length; i++) {

            for (int k = 0; k < matrix[i].length; k++)
                row = row + " " + (matrix[i][k]);
            row = row + "\n";
        }
        System.out.println(row);
    }


    public static void println(double[][] matrix) {
        System.out.println(matrix.length + "X" + matrix.length);
        for (int i = 0; i < matrix.length; i++) {
            for (int k = 0; k < matrix[i].length; k++)
                    // System.out.print(matrix[i][k]+" ");
            {
                if (matrix[i][k] == 1)
                    System.out.print("X ");
                else
                    System.out.print("@ ");
            }
            System.out.println("\n");
        }

    }

    public static int[][] toIntArray(DoubleMatrix2D matrix) {
        //make a copy
        int nrow = matrix.rows();
        int ncol = matrix.columns();

        int[][] matrixcopy = new int[nrow][ncol];
        int i,j;
        for (i = 0; i < nrow; i++) {
            for (j = 0; j < ncol; j++) {
                matrixcopy[i][j] = (int) matrix.getQuick(i, j);
            }
        }
        return matrixcopy;
    }
}
