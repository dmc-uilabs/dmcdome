package dsm;

import java.lang.*;

public class DSMPartition
{

	int n;                   //dsm Size
	int[][] orig_Matrix;     //Original Matrix
	int[][] reach_Matrix;    //Reachability Matrix
	int[][] result_Matrix;   //Result Reachablity Matrix
	int[][] R;               //Reachability Set
	int[][] A;               //Antecedent Set
	int[] total_R;           //the totalnumber of Reachability Set
	int[] total_A;           //the totalnumber of Antecedent Set
	int levels;              //the total number of Levels
	int[] count;             //the total number of Elements in each Level
	int[][] resultorder;     //Elements in each Level
	int[] elementorder;


   public DSMPartition(int[][] nMatrix)
   {
	   n = nMatrix.length;
	   orig_Matrix = new int[n][n];
	   reach_Matrix = new int[n][n];
	   result_Matrix = new int[n][n];
	   R = new int[n][n];
	   A = new int[n][n];
	   total_R = new int[n];
	   total_A = new int[n];
	   count = new int[n];
	   resultorder = new int[n][n];
	   elementorder = new int[n];
	   //initialize:
	   int i,j;
	   for (i = 0; i < n; i++)
	   {
		   for (j = 0; j < n; j++)
		   {
			   orig_Matrix[i][j] = nMatrix[i][j];
			   reach_Matrix[i][j] = 0;
			   result_Matrix[i][j] = 0;
			   R[i][j] = 0;
			   A[i][j] = 0;
			   resultorder[i][j] = 0;
		   }
		   count[i] = 0;
		   total_R[i] = 0;
		   total_A[i] = 0;
		   elementorder[i] = 0;
	   }
   }

	public void Calculate_Reachability_Matrix()
	{
		int[][] temp1 = new int[n][n];
		int[][] temp2 = new int[n][n];
		int i,j,k,l;
		//initialize
		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				temp2[i][j] = orig_Matrix[i][j];

		//calculate Reachability_Matrix
		for (i = 0; i < n - 1; i++)
		{
			for (j = 0; j < n; j++)
				for (k = 0; k < n; k++)
					temp1[j][k] = temp2[j][k];
			for (j = 0; j < n; j++)
				for (k = 0; k < n; k++)
					for (l = 0; l < n; l++)
					{
						temp2[j][k] += temp1[j][l] * orig_Matrix[l][k];
						if (temp2[j][k] > 1) temp2[j][k] = 1;
					}
		}

		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				reach_Matrix[i][j] = temp2[i][j];

	}

	public void Matrix_Partition()
	{
		int i,j,k;
		for (i = 0; i < n; i++)
		{
			k = 0;
			for (j = 0; j < n; j++)
			{
				if (reach_Matrix[i][j] != 0)
				{
					R[i][k] = j;
					k++;
				}
			}
			total_R[i] = k;
			k = 0;
			for (j = 0; j < n; j++)
			{
				if (reach_Matrix[j][i] != 0)
				{
					A[i][k] = j;
					k++;
				}
			}
			total_A[i] = k;
		}
	}

//Macro recorded 8/17/98 by Qi Dong
//This macro partitions the matrix based on the reachability matrix result

	public void Find_Levels()
	{
		int taken[] = new int[n];
		int temp[] = new int[n];
		int i,j,k,m,total;
		int signal;
		//initialize
		for (i = 0; i < n; i++)
		{
			taken[i] = 0;
			temp[i] = 0;
		}

		total = n;
		levels = 0;

		while (total > 0)
		{
			for (i = 0; i < n; i++)
			{
				signal = 0;
				for (j = 0; j < total_R[i]; j++)
				{
					if (taken[R[i][j]] != 1)
					{
						for (k = 0; k < total_A[i]; k++)
						{
							if (R[i][j] == A[i][k])
							{
								signal = 1;
								break;
							}
							else
							{
								signal = 0;
							}
						}

					}
					else if (taken[R[i][j]] == 1)
					{
						signal = 1;
					}
					if (signal == 0) break;
				}
				m = 0;
				if (signal == 1)
				{
					for (j = 0; j < total_R[i]; j++)
					{
						if (taken[R[i][j]] == 0) //means this element is not taken out before
						{
							temp[m] = R[i][j];
							m++;
							total--;
							taken[R[i][j]] = 2;

						}
					}
					for (j = 0; j < m; j++)
					{
						resultorder[levels][count[levels] + j] = temp[j];
						//Cells(elevel + 2, c(elevel) + j).Value = temp(j)
					}
					count[levels] += m;
				}
			}
			for (i = 0; i < n; i++)
			{
				if (taken[i] == 2)
				{
					taken[i] = 1;
				}
			}
			levels++;
		}
	}

	public void Get_Result()
	{
		int i,j,k;
		k = 0;
		for (i = 0; i < levels; i++)
			for (j = 0; j < count[i]; j++)
			{
				elementorder[k] = resultorder[i][j];
				k++;
			}

		for (i = 0; i < n; i++)
			for (j = 0; j < n; j++)
				result_Matrix[i][j] = orig_Matrix[elementorder[i]][elementorder[j]];

	}


	public static void display_Matrix(int[][] nMatrix)
	{
		int nSize = nMatrix.length;
		int i,j;
		for (i = 0; i < nSize; i++)
		{
			for (j = 0; j < nSize; j++)
			{
				System.out.print(nMatrix[i][j]);
			}
			System.out.print("\n");
		}
	}

	public static void display_Array(int[] nArray)
	{
		int nSize = nArray.length;
		int i;
		for (i = 0; i < nSize; i++)
		{
			System.out.print(nArray[i] + " ");
		}
		System.out.print("\n");
	}


}


//a1=Integer.parseInt(s1);
//s1=Integer.toString(a1);