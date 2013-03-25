//compared with Record, add another attribute - distance which is used to
//store the distance between testRecord and the current trainRecord

public class TrainRecord extends Record {
	double distance;
	
	TrainRecord(double[] attributes, int classLabel) {
		super(attributes, classLabel);
	}
}
