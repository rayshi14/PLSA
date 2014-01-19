package plsa;

import java.util.ArrayList;

import data.DataProcessor;


public class IPLSA {

	private double[][] p_w_z; //probability p(w|z)
	private double[][] p_z_d; //probability p(z|d)
	private int[][] n_dw; //occurrence of word w in doku d
	private double[][] p_w_d; //probability p(d,w)
	private int Z; // number of concepts
	private int D; // number of documents
	private int W; // number of words
	private double[][][] np_z_dw; // n(d,w) * probability p(z|w,d)
	private int iterNum;
	private ArrayList<Integer> pos;
	private final double THRESHOLD = 1e-6;
	private double[][] old_p_w_z;
	private double alpha;


	public IPLSA(int[][] frequencies, int[] position,  int numConcept, int iterNum, double[][] old_p_w_z){
		this.iterNum = iterNum;
		Z = numConcept;
		D = frequencies.length;
		W = frequencies[0].length;
		n_dw = new int[D][W];
		n_dw = frequencies;
		for(int i=0; i<position.length; i++){
			pos.add(position[i]);
		}
		this.old_p_w_z = old_p_w_z;
		p_w_d = new double[D][W];
		p_w_z = new double[W][Z];
		p_z_d = new double[Z][D];
		np_z_dw = new double[Z][D][W];
		alpha = 0.5;

	}
	
	public double[][] getNewTopicsWordProb(){
		int newWord = 0;
		for(int i=0; i<pos.size(); i++){
			if(pos.get(i) >= old_p_w_z.length) newWord++;
		}
		double[][] new_p_w_z = new double[W+newWord][Z];
		for(int w=0; w<W; w++){
			for(int z=0; z<Z; z++){
				if(pos.contains(w)){
					new_p_w_z[w][z] = p_w_z[pos.indexOf(w)][z];
				}
				else{
					new_p_w_z[w][z] = old_p_w_z[w][z];
				}
			}
		}
		
		return new_p_w_z;
		
	}

	public double[][] getNewDocumentsTopicsProb(){
		return p_z_d;
	}
	
	private void initialize(){
		// p(d,w)
		for(int d=0; d<D; d++){
			for(int w=0; w<W; w++){
				if(n_dw[d][w] != 0){
					p_w_d[d][w] = Math.random();
				}
				else{
					p_w_d[d][w] = 0;
				}
			}
		}

		// p(d|z), size: Z x D
		for(int z=0; z<Z; z++){
			for (int d = 0; d < D; d++)
			{
				p_z_d[z][d] = Math.random();
			}
		}

		// p(w|z), size: Z x W
		for (int z = 0; z < Z; z++)
		{
			for (int w = 0; w < W; w++)
			{	
				p_w_z[w][z] = Math.random();
			}
		}

		// n(d,w) * p(z|d,w)
		for (int z=0; z<Z; z++){
			for(int d=0; d<D; d++){
				for(int w=0; w<W; w++){
					if(n_dw[d][w] != 0){
						np_z_dw[z][d][w] = Math.random();
					}
					else{
						np_z_dw[z][d][w] = 0;
					}
				}
			}
		}
	}

	public void train(){
		double logLike, oldLike;
		initialize();
		// E-step
		eStep();
		//dataProcessor.vectorPrintOut(p_d_z[0]);
		// M-step
		logLike = mStep();
		//dataProcessor.vectorPrintOut(p_d_z[0]);
		for (int m = 0; m < iterNum; m++)
		{	
			// E-step
			eStep();
			//dataProcessor.vectorPrintOut(p_d_z[0]);
			oldLike = logLike;
			// M-step
			logLike = mStep();
			//			System.out.println(logLike);
			if(Math.abs(logLike - oldLike)/Math.abs(oldLike) < THRESHOLD){
				break;
			}
		}
	}

	private void eStep(){
		for (int d = 0; d < D; d++)
		{
			for (int w = 0; w < W; w++)
			{
				if(n_dw[d][w]!=0){
					for (int z = 0; z < Z; z++)
					{
						np_z_dw[z][d][w] = p_z_d[z][d] * p_w_z[w][z] * n_dw[d][w] / p_w_d[d][w];
					}
				}
			}
		}
	}

	private double mStep(){

		double logLike = 0.0;
		// p(z|d)
		for (int d = 0; d < D; d++)
		{
			double norm = 0.0;
			for (int z = 0; z < Z; z++)
			{
				p_z_d[z][d] = DataProcessor.vectorSum(np_z_dw[z][d]);
				norm += p_z_d[z][d];
			}
			for(int z=0; z<Z; z++){
				p_z_d[z][d] /= norm;
			}
		}

		// p(w|z)
		for (int z = 0; z < Z; z++)
		{
			double norm = 0.0;
			for (int w = 0; w < W; w++)
			{
				double sum = 0.0;

				for (int d = 0; d < D; d++)
				{
					sum += np_z_dw[z][d][w];
				}
				if(pos.get(w) < old_p_w_z.length){
					sum += alpha * old_p_w_z[pos.get(w)][z];
				}
				p_w_z[w][z] = sum;
				norm += sum;
			}

			// normalization
			for (int w = 0; w < W; w++)
			{
				p_w_z[w][z] /= norm;
			}
		}

		//p(w|d) and likelihood
		for (int d = 0; d < D; d++)
		{
			for (int w = 0; w < W; w++)
			{
				if(n_dw[d][w] !=0){
					p_w_d[d][w] = 0.0;
					for(int z=0; z<Z; z++){
						p_w_d[d][w] = p_w_d[d][w] + p_w_z[w][z] * p_z_d[z][d];
					}
					logLike = logLike + n_dw[d][w]*Math.log(p_w_d[d][w]);
				}
			}
		}
		return logLike;

	}


	public double[][] getWordsProbBasedOnTopics(){
		return p_w_z;
	}

	public double[][] getTopicsProbBasedOnDocs(){
		return p_z_d;
	}


}
