import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class knn {
	
	public static void main(String[] args){	
		System.out.println("iris");
		knn("classification\\iris_train.txt","classification\\iris_test.txt",1,2);
		System.out.println();

		
		System.out.println("glass");
		knn("classification\\glass_train.txt","classification\\glass_test.txt",1,0);
		System.out.println();
		
		System.out.println("vowel");
		knn("classification\\vowel_train.txt","classification\\vowel_test.txt",3,2);
		System.out.println();
		
		System.out.println("vehicle");
		knn("classification\\vehicle_train.txt","classification\\vehicle_test.txt",3,1);
		System.out.println();
		
		System.out.println("letter");
		knn("classification\\letter_train.txt","classification\\letter_test.txt",3,0);
		System.out.println();
		
		System.out.println("DNA");
		knn("classification\\dna_train.txt","classification\\dna_test.txt",5,2);
		System.out.println();
	}
	
	public static void knn(String trainingFile, String testFile, int K, int metricType){
		//get the current time
		final long startTime = System.currentTimeMillis();
		
		// make sure the input arguments are legal
		if(K <= 0){
			System.out.println("K should be larger than 0!");
			return;
		}
		
		// metricType should be within [0,2];
		if(metricType > 2 || metricType <0){
			System.out.println("metricType is not within the range [0,2]. Please try again later");
			return;
		}
		
		//TrainingFile and testFile should be the same group
		String trainGroup = extractGroupName(trainingFile);
		String testGroup = extractGroupName(testFile);
		
		if(!trainGroup.equals(testGroup)){
			System.out.println("trainingFile and testFile are illegal!");
			return;
		}
		
		
		try {
			//read trainingSet and testingSet
			TrainRecord[] trainingSet =  FileManager.readTrainFile(trainingFile);
			TestRecord[] testingSet =  FileManager.readTestFile(testFile);
			
			//determine the type of metric according to metricType
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
			
			//test those TestRecords one by one
			int numOfTestingRecord = testingSet.length;
			for(int i = 0; i < numOfTestingRecord; i ++){
				TrainRecord[] neighbors = findKNearestNeighbors(trainingSet, testingSet[i], K, metric);
				int classLabel = classify(neighbors);
				testingSet[i].predictedLabel = classLabel; //assign the predicted label to TestRecord
			}
			
			//calculate the accuracy
			int correctPrediction = 0;
			for(int j = 0; j < numOfTestingRecord; j ++){
				if(testingSet[j].predictedLabel == testingSet[j].classLabel)
					correctPrediction ++;
			}
			
			//Output a file containing predicted labels for TestRecords
			String predictPath = FileManager.outputFile(testingSet, trainingFile);
			System.out.println("The prediction file is stored in "+predictPath);
			System.out.println("The accuracy is "+((double)correctPrediction / numOfTestingRecord)*100+"%");
			
			//print the total execution time
			final long endTime = System.currentTimeMillis();
			System.out.println("Total excution time: "+(endTime - startTime) / (double)1000 +" seconds.");
		
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Find K nearest neighbors of testRecord within trainingSet 
	static TrainRecord[] findKNearestNeighbors(TrainRecord[] trainingSet, TestRecord testRecord,int K, Metric metric){
		int NumOfTrainingSet = trainingSet.length;
		assert K <= NumOfTrainingSet : "K is lager than the length of trainingSet!";
		
		//Update KNN: take the case when testRecord has multiple neighbors with the same distance into consideration
		//Solution: Update the size of container holding the neighbors
		TrainRecord[] neighbors = new TrainRecord[K];
		
		//initialization, put the first K trainRecords into the above arrayList
		int index;
		for(index = 0; index < K; index++){
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			neighbors[index] = trainingSet[index];
		}
		
		//go through the remaining records in the trainingSet to find K nearest neighbors
		for(index = K; index < NumOfTrainingSet; index ++){
			trainingSet[index].distance = metric.getDistance(trainingSet[index], testRecord);
			
			//get the index of the neighbor with the largest distance to testRecord
			int maxIndex = 0;
			for(int i = 1; i < K; i ++){
				if(neighbors[i].distance > neighbors[maxIndex].distance)
					maxIndex = i;
			}
			
			//add the current trainingSet[index] into neighbors if applicable
			if(neighbors[maxIndex].distance > trainingSet[index].distance)
				neighbors[maxIndex] = trainingSet[index];
		}
		
		return neighbors;
	}
	
	// Get the class label by using neighbors
	static int classify(TrainRecord[] neighbors){
		//construct a HashMap to store <classLabel, weight>
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		int num = neighbors.length;
		
		for(int index = 0;index < num; index ++){
			TrainRecord temp = neighbors[index];
			int key = temp.classLabel;
		
			//if this classLabel does not exist in the HashMap, put <key, 1/(temp.distance)> into the HashMap
			if(!map.containsKey(key))
				map.put(key, 1 / temp.distance);
			
			//else, update the HashMap by adding the weight associating with that key
			else{
				double value = map.get(key);
				value += 1 / temp.distance;
				map.put(key, value);
			}
		}	
		
		//Find the most likely label
		double maxSimilarity = 0;
		int returnLabel = -1;
		Set<Integer> labelSet = map.keySet();
		Iterator<Integer> it = labelSet.iterator();
		
		//go through the HashMap by using keys 
		//and find the key with the highest weights 
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
	
	static String extractGroupName(String filePath){
		StringBuilder groupName = new StringBuilder();
		for(int i = 15; i < filePath.length(); i ++){
			if(filePath.charAt(i) != '_')
				groupName.append(filePath.charAt(i));
			else
				break;
		}
		
		return groupName.toString();
	}
}
