import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


public class FileManager {
	public static void main(String[] args){
		try {
			Record[]  r = readFile("classification//glass_train.txt");
			normalizeRecords(r);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Record[] readFile(String fileName) throws IOException{
		File file = new File(fileName);
		Scanner scanner = new Scanner(file);
		boolean trainOrNot = fileName.contains("train");
		//read file
		int NumOfSamples = scanner.nextInt();
		int NumOfAttributes = scanner.nextInt();
		int LabelOrNot = scanner.nextInt();
		scanner.nextLine();
		
		assert LabelOrNot == 1 : "No classLabel";
		
		Record[] records = new Record[NumOfSamples];
		int index = 0;
		while(scanner.hasNext()){
			double[] attributes = new double[NumOfAttributes];
			int classLabel = -1;
			
			for(int i = 0; i < NumOfAttributes; i ++){
				attributes[i] = scanner.nextDouble();
			}
			
			classLabel = (int) scanner.nextDouble();
			assert classLabel != -1 : "Reading class label is wrong!";
			
			if(trainOrNot)
				records[index] = new TrainRecord(attributes, classLabel);
			else
				records[index] = new TestRecord(attributes, classLabel);
			index ++;
		}
		
		return records;
	}
	
	// normalize the training records and return the normalization scheme for later use
	public static double[][] normalizeRecords(Record[] records){
		int numOfRecords = records.length;
		int numOfAttributes = records[0].attributes.length;
		double[][] scheme= new double[2][numOfAttributes]; //scheme[0] includes maxValue for each attribute, and scheme[1] includes minValue for each attributes
		
		//Read max and min values for each attribute
		double max, min; 
		for(int i = 0 ; i < numOfAttributes; i ++){
			max = records[0].attributes[i];
			min = max;
			
			for(int j = 1; j < numOfRecords; j ++){
				double temp = records[j].attributes[i];
				if( max < temp)
					max = temp;
				if( min > temp)
					min = temp;
			}
			
			scheme[0][i] = max;
			scheme[1][i] = min;
		}
		
		//Start to normalize the input records
		for(int m = 0 ; m < numOfAttributes; m ++){
			max = scheme[0][m];
			min = scheme[1][m];
			
			for(int n = 0; n < numOfRecords; n ++){
				double temp = records[n].attributes[m];
				records[n].attributes[m] = (temp - min) / (max - min);
			}
		}
		
		return scheme;
	}
	
	//Write File
	
}
