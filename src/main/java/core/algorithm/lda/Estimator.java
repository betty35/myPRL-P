package core.algorithm.lda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Estimator {
	
	// output model
	protected Model trnModel;
	LDAOption option;
	
	
	public boolean init(LDAOption option){
		this.option = option;
		trnModel = new Model();
		
		if (option.est){
			if (!trnModel.initNewModel(option))
				return false;
			trnModel.data.localDict.writeWordMap();
		}
		else if (option.estc){
			if (!trnModel.initEstimatedModel(option))
				return false;
		}
		
		return true;
	}
	
	public void estimate(){
		
		System.out.println("Sampling " + trnModel.niters + " iteration!");
		int lastIter = trnModel.liter;
		for (trnModel.liter = lastIter + 1; trnModel.liter < trnModel.niters + lastIter; trnModel.liter++){
			System.out.println("Iteration " + trnModel.liter + " ...");
			// for all z_i
			for (int m = 0; m < trnModel.M; m++){				
				for (int n = 0; n < trnModel.data.docs[m].length; n++){
					// z_i = z[m][n]
					// sample from p(z_i|z_-i, w)
					int topic = sampling(m, n);
					trnModel.z[m].set(n, topic);
				}// end for each word
			}// end for each document
			
			if (option.savestep > 0){
				if (trnModel.liter % option.savestep == 0){
					System.out.println("Saving the model at iteration " + trnModel.liter + " ...");
					computeTheta();
					computePhi();
					trnModel.saveModel("model-" + LDAUtils.zeroPad(trnModel.liter, 5),option.swID);
				}
			}
		}// end iterations		
		
		System.out.println("Gibbs sampling completed!\n");
		System.out.println("Saving the final model!\n");
		
		computeTheta();
		computePhi();
		trnModel.liter--;
		trnModel.saveModel("model-final",option.swID);
	}
	
	/**
	 * Do sampling
	 * @param m document number
	 * @param n word number
	 * @return topic id
	 */
	public int sampling(int m, int n){
		// remove z_i from the count variable
		int topic = trnModel.z[m].get(n);
		int w = trnModel.data.docs[m].words[n];
		
		trnModel.nw[w][topic] -= 1;
		trnModel.nd[m][topic] -= 1;
		trnModel.nwsum[topic] -= 1;
		trnModel.ndsum[m] -= 1;
		
		double Vbeta = trnModel.V * trnModel.beta;
		double Kalpha = trnModel.K * trnModel.alpha;
		
		//do multinominal sampling via cumulative method
		for (int k = 0; k < trnModel.K; k++){
			
			double condition=1;
			/*if(trnModel.liter>800){
			String[] terms=getTermsOfTheTopic(k);
			double sim=getDistanceToTheCluster(w,terms);
			condition=sim>0.06?1:0.2;}*/
			trnModel.p[k] = condition*(trnModel.nw[w][k] + trnModel.beta)/(trnModel.nwsum[k] + Vbeta) *
					(trnModel.nd[m][k] + trnModel.alpha)/(trnModel.ndsum[m] + Kalpha);
		}
		
		// cumulate multinomial parameters
		for (int k = 1; k < trnModel.K; k++){
			trnModel.p[k] += trnModel.p[k - 1];
		}
		
		// scaled sample because of unnormalized p[]
		double u = Math.random() * trnModel.p[trnModel.K - 1];
		
		for (topic = 0; topic < trnModel.K; topic++){
			if (trnModel.p[topic] > u) //sample topic w.r.t distribution p
				break;
		}
		
		// add newly estimated z_i to count variables
		trnModel.nw[w][topic] += 1;
		trnModel.nd[m][topic] += 1;
		trnModel.nwsum[topic] += 1;
		trnModel.ndsum[m] += 1;
		
 		return topic;
	}
	
	public void computeTheta(){
		for (int m = 0; m < trnModel.M; m++){
			for (int k = 0; k < trnModel.K; k++){
				trnModel.theta[m][k] = (trnModel.nd[m][k] + trnModel.alpha) / (trnModel.ndsum[m] + trnModel.K * trnModel.alpha);
			}
		}
	}
	
	public void computePhi(){
		for (int k = 0; k < trnModel.K; k++){
			for (int w = 0; w < trnModel.V; w++){
				trnModel.phi[k][w] = (trnModel.nw[w][k] + trnModel.beta) / (trnModel.nwsum[k] + trnModel.V * trnModel.beta);
			}
		}
	}
	
	public String[] getTermsOfTheTopic(int topicID)
	{
		int k=topicID;
		String[] terms=new String[trnModel.twords];
		List<Pair> wordsProbsList = new ArrayList<Pair>(); 
		for (int w = 0; w < trnModel.V; w++)
		{
			Pair p = new Pair(w, trnModel.phi[k][w], false);
			wordsProbsList.add(p);
		}
		Collections.sort(wordsProbsList);
		
		for (int i = 0; i < trnModel.twords; i++)
		{
			if (trnModel.data.localDict.contains((Integer)wordsProbsList.get(i).first))
			{
				String word = trnModel.data.localDict.getWord((Integer)wordsProbsList.get(i).first);
				terms[i]=word;
			}
		}
		return terms;
	}
	
	/*public Double getDistanceToTheCluster(int wordID,String[] terms)
	{
		String word=trnModel.data.localDict.getWord(wordID);
		double sim=0;
		int count=0;
		double temp=0;
		for(int i=0;i<terms.length;i++)
		{
			temp=WordSimilarity.simWord(word,terms[i]);
			if(temp!=0)
			{
				sim+=temp;
				count++;
			}
		}
		if(count==0) return 1.0;
		else return sim/count;
	}*/
}
