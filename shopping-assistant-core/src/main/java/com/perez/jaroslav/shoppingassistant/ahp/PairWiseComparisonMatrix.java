package com.perez.jaroslav.shoppingassistant.ahp;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import com.perez.jaroslav.shoppingassistant.ahp.adt.Round;

import java.io.Serializable;


public class PairWiseComparisonMatrix implements Serializable, Cloneable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // ATTRIBUTES
    public static double EXTREMELY = 9.0;
    public static double BETWEEN_EXTREMELY_AND_VERY_STRONGLY = 8.0;
    public static double VERY_STRONGLY = 7.0;
    public static double BETWEEN_VERY_VERY_STRONGLY_AND_STRONGLY = 6.0;
    public static double STRONGLY = 5.0;
    public static double BETWEEN_STRONGLY_AND_SLIGHTLY = 4.0;
    public static double SLIGHTLY = 3.0;
    public static double BETWEEN_SLIGHTLY_EQUALLY = 2.0;
    public static double EQUALLY = 1.0;

    private Matrix matrix;
    private Matrix weights;
    private int size;
    private boolean ll;

    /**
     * Set the value of the pairwise comparison aij between w_i and w_j
     *
     * @param i     index of the dominant activity w_i
     * @param j     index of the dominated activity w_j
     * @param value of w_i/w_j
     * @throws IllegalArgumentException
     */
    public void set(int i, int j, double value) {
        if (i >= getSize() && j >= getSize())
            throw new IllegalArgumentException("index of a single element should be like 0<=i,j<size");
        if (i == j)
            matrix.set(i,j,value);
        else {
            matrix.set(i, j, value);
            if (value == 0)
                matrix.set(j, i, 0);
            else
                matrix.set(j, i, 1.0 / value);
        }
    }

    /**
     * Get the value of the pairwise comparison between aij=w_i
     *
     * @param i index of the dominant activity W_i
     * @param j index of the dominated activity W_j
     * @return value of W_i/W_j
     * @throws IllegalArgumentException "Out of bounded..."
     */
    public double get(int i, int j) {
        if (i >= getSize() && j >= getSize())
            throw new IllegalArgumentException("index of a single element should be like 0<=i,j<size");
        return matrix.get(i, j);
    }

    /**
     * Gets the value of matrix
     *
     * @return the value of matrix
     */
    public Matrix getMatrix() {
        return this.matrix;
    }

    /**
     * Sets the value of matrix
     *
     * @param argMatrix Value to assign to this.matrix
     */
    public void setA(Matrix argMatrix) {
        this.matrix = argMatrix;
    }

    /**
     * Gets the value of size
     *
     * @return the value of size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Sets the value of size
     *
     * @param argSize Value to assign to this.size
     */
    public void setSize(int argSize) {
        this.size = argSize;
    }


    /**
     * Creates a new  <code>PairwiseComparisonMatrix</code> instance.
     */
    public PairWiseComparisonMatrix(int size, boolean isll) {
        this.size = size;
        this.ll = isll;
        InitMatrix();
    }

    /**
     * Init a new  <code>Matrix</code> instance.
     */
    public void InitMatrix() {
        Matrix matrix2 = new Matrix(size, size, 1.0);
        if (matrix != null) {
            // copy the matrix
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                for (int j = 0; j < matrix.getColumnDimension(); j++) {
                    matrix2.set(i, j, matrix.get(i, j));
                }
            }
        }
        matrix = matrix2;
    }

    /**
     * Print the  <code>Matrix</code>
     */
    public void print() {
        matrix.print(matrix.getRowDimension(), 4);
    }


    /**
     * Get the value of the InconsistencyRatio
     *
     * @return double
     */
    public double getInconsistencyIndex() {
        //n!=1
        if (getSize() <= 1) return 0.0;
        return (getMaxEigenValue() - getSize()) / (getSize() - 1.0);
    }

    public void printWeights() {

        for (int i = 0; i < this.weights.getRowDimension(); i++) {
            System.out.println(i + " " + Round.round(this.weights.get(i, 0), 4));
        }
    }


    public double getRandomInconsistency() {
        switch (size) {
            case 0:
                return 0.00;
            case 1:
                return 0.00;
            case 2:
                return 0.00;
            case 3:
                return 0.58;
            case 4:
                return 0.90;
            case 5:
                return 1.12;
            case 6:
                return 1.24;
            case 7:
                return 1.32;
            case 8:
                return 1.41;
            case 9:
                return 1.45;
            case 10:
                return 1.49;
            default:
                return 1.5; //take care
        }

    }

    /**
     * Get the value of the InconsistencyRatio
     *
     * @return double
     */
    public double getInconsistencyRatio() {
        if (getSize() <= 2) return 0.0;
        return getInconsistencyIndex() / getRandomInconsistency();
    }


    /**
     * Check the consistency of the <code>PairwiseComparisonMatrix</code>
     *
     * @return boolean value
     */
    public boolean isConsistency() {
        if (getInconsistencyRatio() <= 0.1) return true;
        else return false;
    }


    /**
     * Get the value of the <code>max_eigen_value</code> of the matrix A.
     *
     * @return the max_eigen_value of the matrix A
     */

    public double getMaxEigenValue() {
        EigenvalueDecomposition Eig = new EigenvalueDecomposition(matrix);
        double[] values = Eig.getRealEigenvalues();
        double max = 0.0;

        for (int i = 0; i < this.size; i++) {
            if (values[i] >= max) max = values[i];
        }

        //System.out.println("The max eigenvalue : " + max);
        return max;

    }


    /**
     * Get the vector of weights of the <code>PairwiseComparisonMatrix</code>.
     *
     * @return Matrix
     */
    public void calcWeights() {
        // copy the matrix
        Matrix squared = matrix.copy();

        //System.out.println("matrix:\n");
        //squared.print(matrix.getRowDimension(), 4);

        Matrix eigenvector = new Matrix(squared.getRowDimension(), 1);

        //System.out.println("eigenvector:\n");
        //eigenvector.print(eigenvector.getRowDimension(), 4);

        Matrix prevEigenvector = new Matrix(eigenvector.getRowDimension(), eigenvector.getColumnDimension(), 1.0);

        //System.out.println("prev eigenvector:\n");
        //eigenvector.print(prevEigenvector.getRowDimension(), 4);
        double min = 0, max = 0;
        while (!equal(eigenvector, prevEigenvector)) {
            double totalSum = 0;
            // first, save a copy of the eigenvector for comparison
            prevEigenvector = eigenvector.copy();

            //System.out.println("prevEigenvector:");
            //prevEigenvector.print(prevEigenvector.getRowDimension(), 4);

            // step 1:  square the matrix
            squared = squared.times(squared);

            //System.out.println("squared:");
            //squared.print(squared.getRowDimension(), 4);

            // step 2:  compute eigenvector
            // first sum the rows
            for (int i = 0; i < squared.getRowDimension(); i++) {
                double sum = 0;
                for (int j = 0; j < squared.getColumnDimension(); j++) {
                    sum = sum + squared.get(i, j);
                }
                eigenvector.set(i, 0, sum);
                totalSum = totalSum + sum;
            }

            //System.out.println("sum rows:");
            //eigenvector.print(eigenvector.getRowDimension(), 4);
            //System.out.println("\nTotal sum: " + totalSum + "\n");

            // normalize
            min = 1000000000;
            max = 0;
            for (int i = 0; i < eigenvector.getRowDimension(); i++) {
                double norm = eigenvector.get(i, 0) / totalSum;
                if (norm < min) {
                    min = norm;
                }
                if (norm > max) {
                    max = norm;
                }
                eigenvector.set(i, 0, norm);
            }
            //System.out.println("normalized:");
            //eigenvector.print(eigenvector.getRowDimension(), 4);

            //System.out.println("prev eigenvector:\n");
            //eigenvector.print(prevEigenvector.getRowDimension(), 4);
            //System.out.println("eigenvector normalized:\n");
            //eigenvector.print(eigenvector.getRowDimension(), 4);
            //System.out.println("total sum is " + totalSum);

        }

        // for alternatives only scale from 0-1
        //System.out.println("min is " + min + " and max is " + max);
        if (ll) {
            for (int i = 0; i < eigenvector.getRowDimension(); i++) {
                double val = eigenvector.get(i, 0);
                //double scale = (val - min)/(max-min);
                //System.out.println("scale is: " + scale);
                eigenvector.set(i, 0, (val - min) / (max - min));
            }
        }

        this.weights = eigenvector;

        //return eigenvector;


        //Matrix Ab = new Matrix(getSize(), getSize());

        //Matrix W = new Matrix (getSize(), 1, 1.0);
    /*
    double sum=0.00;
    // normalization
    for(int j=0;j < getSize();j++){
      sum=0.00;
      for (int i=0;i<getSize();i++){
    	  sum+=matrix.get(i,j);
      }
      for (int i=0;i<getSize();i++){
    	  try{Ab.set(i,j,matrix.get(i,j)/sum);}
    	  catch (ArrayIndexOutOfBoundsException e) { System.err.println("Error in setting Ab : ArrayIndexOutOfBoundsException"+e);}
      }
    }
    //System.out.println("Matrix is:\n");
    //Ab.print(getSize(), getSize());

    //sum on each line
    for(int i=0;i < getSize();i++){
      sum=0.00;
      for (int j=0;j<getSize();j++){
    	  sum+=matrix.get(i,j);
      }
      try{W.set(i,0,sum);}
      catch (ArrayIndexOutOfBoundsException e) { System.err.println("Error in setting W : ArrayIndexOutOfBoundsException"+e);}

    }
    //normalization vector
    sum=0.00;
    for (int i=0;i<getSize();i++){
      try{sum+=W.get(i,0);}
      catch (ArrayIndexOutOfBoundsException e) { System.err.println("Error in setting W : ArrayIndexOutOfBoundsException"+e);}
    }
    for (int i=0;i<getSize();i++){

      try{W.set(i,0,W.get(i,0)/sum);}
      catch (ArrayIndexOutOfBoundsException e) { System.err.println("Error in setting W : ArrayIndexOutOfBoundsException"+e);}
    }
    //System.out.println("normalization vector:\n");
    //W.print(getSize(), 4);
	*/

        //return W;
    }

    private boolean equal(Matrix a, Matrix b) {
        if (a.getRowDimension() != b.getRowDimension()) return false;
        if (a.getColumnDimension() != b.getColumnDimension()) return false;
        for (int i = 0; i < a.getRowDimension(); i++) {
            for (int j = 0; j < a.getColumnDimension(); j++) {
                if (Round.round(a.get(i, j), 4) != Round.round(b.get(i, j), 4)) return false;
            }
        }
        return true;
    }


    public double getWeight(int i) {
        //Matrix W=new Matrix(getSize(),1);
        //W=getWeight();
        //return W.get(i,0);
        double w = 0;
        try {
            w = weights.get(i, 0);
        } catch (NullPointerException e) {
        }
        return w;
    }

    public void setWeight(int i, double val) {
        if (weights != null) {
            weights.set(i, 0, val);
        }
    }


    /**
     * <code>toString</code> Returns a string representation of this PairwiseComparisonMatrix, containing the String representation of each weight.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
        String s = new String();
        s = "Matrix : \n";
        //matrix.print(matrix.getRowDimension(), 4);
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                s += " " + Round.round(get(i, j), 4) + " ";
            }
            s += "\n";
        }
        s += "Weights : \n";
        //weight.print(weight.getRowDimension(), 4);
        for (int i = 0; i < getSize(); i++)
            s += " " + Round.round(getWeight(i), 4) + " ";
        s = s + "\n";
        //s=s+"Inconsistency Ratio      : " +getInconsistencyRatio()+"\n";
        return s;
    }


    public void addElement() {
        setSize(getSize() + 1);
        InitMatrix();

    }


    public void delElement(int index) {
        Matrix B = new Matrix((size - 1), (size - 1), 1.0);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i < index && j < index) {
                    B.set(i, j, matrix.get(i, j));
                }
                if (i < index && index < j) {
                    B.set(i, j - 1, matrix.get(i, j));
                }
                if (index < i && j < index) {
                    B.set(i - 1, j, matrix.get(i, j));
                }
                if (index < i && index < j) {
                    //Systemerr.println("size : "+size+"\n");
                    //Systemerr.println("i : "+i+"   j : "+j+"\n");

                    B.set(i - 1, j - 1, matrix.get(i, j));
                }
            }
        }

        B = matrix;
        ;
        size--;
    }
    public void setHarkerMethod(){
        for (int i = 0; i < getSize(); i++) {
            int zeros=0;
            for (int j = 0; j < getSize(); j++) {
                if(get(i,j)==0)
                    zeros++;
            }
            set(i,i,1+zeros);
        }
    }
    public boolean isWeightsAllNans(){
        for (int i=0;i<size;i++)
            if(!Double.isNaN(getWeight(i)))
                return false;
        return true;
    }

    public static void main(String args[]) {

        PairWiseComparisonMatrix P = new PairWiseComparisonMatrix(4, false);
        P.set(0, 0, 2);
        P.set(0, 1, 7);
        P.set(0, 2, 8);
        P.set(0, 3, 0);

        P.set(1, 1, 2);
        P.set(1, 2, 0);
        P.set(1, 2, 8);

        P.set(2, 2, 2);
        P.set(2, 3, 7);

        P.set(3, 3, 2);

        System.out.println("Print the matrix: ");

        P.print();
        P.calcWeights();
        P.printWeights();


    }


}
