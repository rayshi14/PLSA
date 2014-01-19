package plsa;

import data.DataProcessor;


public class TemPLSA {

	private double[][] p_w_z; //probability p(w|z)
	private double[][] p_z_d; //probability p(z|d)
	private int[][] n_dw; //occurrence of word w in doku d
	private double[][] p_w_d; //probability p(d,w)
	private int Z; // number of concepts
	private int D; // number of documents
	private int W; // number of words
	private double[][][] np_z_dw; // n(d,w) * probability p(z|w,d)
	private int iterNum;
	private final double THRESHOLD = 1e-6;
	private double beta;
	private double n;
	
	
	public TemPLSA(int[][] frequencies, int numConcept, int iterNum){
		this.iterNum = iterNum;
		Z = numConcept;
		D = frequencies.length;
		W = frequencies[0].length;
		n_dw = new int[D][W];
		n_dw = frequencies;
		p_w_d = new double[D][W];
		p_w_z = new double[W][Z];
		p_z_d = new double[Z][D];
		np_z_dw = new double[Z][D][W];
		beta = 1.0;
		n = 0.8;
	}

	public void beginIPLSA(String newDocument){
		
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
		double beta = this.beta;
		initialize();
		// E-step
		eStep(beta);
		//dataProcessor.vectorPrintOut(p_d_z[0]);
		// M-step
		logLike = mStep(beta);
		//dataProcessor.vectorPrintOut(p_d_z[0]);
		
		for (int m = 0; m < 1; m++)
		{	
			beta *= this.n;
			System.out.println("beta:" + String.valueOf(beta));
			while(true){
				// E-step
				eStep(beta);
				//dataProcessor.vectorPrintOut(p_d_z[0]);
				oldLike = logLike;
				// M-step
				logLike = mStep(beta);
				System.out.println(logLike);
				if(Math.abs(logLike - oldLike)/Math.abs(oldLike) < THRESHOLD){
					break;
				}
			}
		}
	}

	private void eStep(double beta){
		for (int d = 0; d < D; d++)
		{
			for (int w = 0; w < W; w++)
			{
				if(n_dw[d][w]!=0){
					for (int z = 0; z < Z; z++)
					{
						np_z_dw[z][d][w] = Math.pow((p_z_d[z][d] * p_w_z[w][z]), beta) * n_dw[d][w] / p_w_d[d][w];
					}
				}
			}
		}
	}

	private double mStep(double beta){

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
						p_w_d[d][w] = p_w_d[d][w] + Math.pow(p_w_z[w][z] * p_z_d[z][d], beta);
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
