import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class knn {
	public static void main(String[] args){
//		knn("classification\\glass_train.txt","classification\\glass_test.txt", 1, 0, false);
//		knn("classification\\glass_train.txt","classification\\glass_test.txt", 6, 1,false);
//		knn("classification\\glass_train.txt","classification\\glass_test.txt", 10, 2,false);
//		knn("classification\\glass_train.txt","classification\\glass_test.txt", 20, 2,false);
//		System.out.println();
//		knn("classification\\glass_train.txt","classification\\glass_test.txt", 1, 0, true);
//		knn("classification\\glass_train.txt","classification\\glass_test.txt", 6, 1,true);
//		knn("classification\\glass_train.txt","classification\\glass_test.txt", 10, 2,true);
//		knn("classification\\glass_train.txt","classification\\glass_test.txt", 20, 2,true);
//		System.out.println();
//		knn("classification\\dna_train.txt","classification\\dna_test.txt", 4, 0,false);
//		knn("classification\\dna_train.txt","classification\\dna_test.txt", 4, 1,false);
//		knn("classification\\dna_train.txt","classification\\dna_test.txt", 4, 2,false);
//		knn("classification\\dna_train.txt","classification\\dna_test.txt", 15, 2,false);
//		System.out.println();
//		knn("classification\\dna_train.txt","classification\\dna_test.txt", 4, 0);
//		knn("classification\\dna_train.txt","classification\\dna_test.txt", 4, 1);
//		knn("classification\\dna_train.txt","classification\\dna_test.txt", 4, 2);
		knn("classification\\dna_train.txt","classification\\dna_test.txt", 15, 2);
	}
	
	public static void knn(String traningFile, String testFile, int K, int metricType){
		try {
			TrainRecord[] trainingSet =  FileManager.readTrainFile(traningFile);
			TestRecord[] testingSet =  FileManager.readTestFile(testFile);
			
			Metric metric;
			if(metricType == 0)
				metric = new CosineSimilarity();
			else if(metricType == 1)
				metric = new L1Distance();
			else if (metricType == 2)
				metric = new EuclideanDistance();
			else{
				System.out.println("The entered metric_type is wrong!");
				return;
			}
			int numOfTestingRecord = testingSet.length;
			for(int i = 0; i < numOfTestingRecord; i ++){
				TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, testingSet[i], K, metric);
				int classLabel = classify(neighbors);
				testingSet[i].predictedLabel = classLabel;
			}
			
			int correctPrediction = 0;
			for(int j = 0; j < numOfTestingRecord; j ++){
				if(testingSet[j].predictedLabel == testingSet[j].classLabel)
					correctPrediction ++;
			}
			
			System.out.println("The accuracy is "+(double)correctPrediction / numOfTestingRecord);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Find K nearest neighbors of testRecord within trainingSet 
	static TrainRecord[] findKNearestNeighbors(TrainRecord[] trainingSet, TestRecord testRecord,int K, Metric metric){
		int NumOfTrainingSet = trainingSet.length;
		assert K <= NumOfTrainingSet : "K is lager than the length of trainingSet!";
		
		//initialization
		TrainRecord[] neighbors = new TrainRecord[K];
		int index;
		for(index = 0; index < K; index ++){
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			neighbors[index] = trainingSet[index];
		}
		
		//go through the remaining records in the trainingSet to find K nearest neighbors
		for(index = K; index < NumOfTrainingSet; index ++){
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			
			int maxIndex = 0;
			for(int i = 1; i < K; i ++){
				if(neighbors[i].distance > neighbors[maxIndex].distance)
					maxIndex = i;
			}
			
			if(neighbors[maxIndex].distance > trainingSet[index].distance)
				neighbors[maxIndex] = trainingSet[index];
		}
		
		return neighbors;
	}
	
	// Get the class label by using neighbors
	static int classify(TrainRecord[] neighbors){
		HashMap<Integer, Double> map = new HashMap();
		int num = neighbors.length;
		
		for(int index = 0;index < num; index ++){
			TrainRecord temp = neighbors[index];
			int key = temp.classLabel;
			if(!map.containsKey(key))
				map.put(key, 1 / temp.distance);
			else{
				double value = map.get(key);
				value += 1 / temp.distance;
				map.put(key, value);
			}
		}	
		
		//Find the most likely label
		double maxSimilarity = 0;
		int returnLabel = -1;
		Set labelSet = map.keySet();
		Iterator<Integer> it = labelSet.iterator();
		while(it.hasNext()){
			int label = it.next();
			double value = map.get(label);
			if(value > maxSimilarity){
				maxSimilarity = value;
				returnLabel = label;
			}
		}
		
		return returnLabel;
	}
}
