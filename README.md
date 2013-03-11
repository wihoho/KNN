KNN
===
This is a course assignment, and we are supposed to implement the basic K-Nearest Neighbor algorithm. Below are the basic classes deisgned in the initial phase:	

- **FileManager**  
	in charge of file operations
	- **ReadFile**: read training files and test files
	- **ProcessData**: normalize and standaize the data
	- **WriteFile**: output the predicted labels
- **Metric**  
interface for defining different measurement methods
	- **CosineSimilarity**
	- **L1Distance**
	- **EuclideanDistance**
- **Record**  
abstract class which contains attributes and class label
	- **TrainRecord**: + distance
	- **TestRecord**: + predictedLabel
- **knn**  
the main class implementing the KNN algorithm
	- For each TestRecord, use a *k-size* container (like a heap) to maintain k TrainRecords which are the nearest to that Testrecord while going through all TrainRecords. (No need to store all the distances which is a kind of wasting memory)
	- During the classification phase, weigh the vote according to distance and assign the class lable with the largest weight.
	- Whether there is need to think about outlier???

Questions:

1. When normalizing data, do we need to consider TrainingData and TestData together or seperately?   
For different datasets, the max and min values might be different. As a result, the normalization results for TrainData and TestData are not unified.

2. During KNN, is there a need to set a threshold to test outliers?  
	Should be No since there are so many different datasets. We may nesure that all test cases have valid class labels.                      