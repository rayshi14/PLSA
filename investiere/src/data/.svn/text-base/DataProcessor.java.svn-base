package data;



public abstract class DataProcessor {

	public DataProcessor(){

	}

	/*
	 * calculate sum of a vector int
	 */
	public static int vectorSum(int [] vector){
		int sum = 0;
		for(int i=0; i<vector.length; i++){
			sum += vector[i];
		}
		return sum;
	}

	/*
	 * calculate sum of a vector double
	 */
	public static double vectorSum(double [] vector){
		double sum = 0;
		for(int i=0; i<vector.length; i++){
			sum += vector[i];
		}
		return sum;
	}

	/*
	 * get the traverse of a matrix
	 */
	public static double[][] matrixTraverse(double[][] mat){
		double[][] tMat = new double[mat[0].length][mat.length];
		for(int i=0; i<tMat.length; i++){
			for(int j=0; j<tMat[0].length; j++){
				tMat[i][j] = mat[j][i];
			}
		}
		return tMat;
	}

	/*
	 * print all entries in a double matrix
	 */
	public static void matrixPrintOut(double[][] mat){
		int row = mat.length;
		int col = mat[0].length;
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				System.out.format("%6.2f", mat[i][j]);
			}
			System.out.println();
		}
	}

	/*
	 * print all entries in an int matrix
	 */
	public static void matrixPrintOut(int[][] mat){
		int row = mat.length;
		int col = mat[0].length;
		for(int i=0; i<row; i++){
			for(int j=0; j<col; j++){
				System.out.format("%4d", mat[i][j]);
			}
			System.out.println();
		}
	}

	/*
	 * print all the entries in a double vector
	 */
	public static void vectorPrintOut(double[] vec){
		int len = vec.length;
		for(int i=0; i<len; i++){
			System.out.format("%12.8f", vec[i]);
		}
		System.out.println();
	}
	
	public static void vectorPrintOut(int[] vec){
		int len = vec.length;
		for(int i=0; i<len; i++){
			System.out.format("%4d", vec[i]);
		}
		System.out.println();
	}

	/*
	 * if str is a number, return true
	 */
	public static boolean isNumeric(String str)  
	{  
		try  
		{  
			Integer.parseInt(str);  
		}
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}
	
	/*
	 * inner product of two vectors
	 */
	public static double innerProdcut(double[] vector1, double[] vector2){
		double result = 0.0;
		if(vector1.length != vector2.length){
		}
		else{
			for(int i=0; i<vector1.length; i++){
				result += vector1[i]*vector2[i];
			}
			
		}
		return result;	
	}

}
