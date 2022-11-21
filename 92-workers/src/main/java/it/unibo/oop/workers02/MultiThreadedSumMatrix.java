package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a class to calculate the sum of elements of a matrix.
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * Builds a {@code MultiThreadedSumMatrix}.
     * 
     * @param nthread is the number of threads performing the sum
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nrow;
        private double res;

        /**
         * 
         * @param matrix is the matrix to sum
         * @param startpos is the initial row for this worker
         * @param nrow is the number of rows to use for this worker
         */
        Worker(final double[][] matrix, final int startpos, final int nrow) {
            super();
            this.matrix = matrix.clone();
            this.startpos = startpos;
            this.nrow = nrow;
        }

        @Override
        public void run() {
            System.out.println("Working from row " + startpos + " to row " + (startpos + nrow - 1)); // NOPMD
            res = 0;
            for (int i = startpos; i < startpos + nrow && i < matrix.length; i++) {
                for (final double d : matrix[i]) {
                    res += d;
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @param matrix
     * @return the sum of the elements of the matrix
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        for (final Worker w : workers) {
            w.start();
        }
        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
}
