//This class implements Metric interface and is used to calculate EuclideanDistance
public class EuclideanDistance implements Metric {

	@Override
	//L2
	public double getDistance(Record s, Record e) {
		assert s.attributes.length == e.attributes.length : "s and e are different types of records!";
		int numOfAttributes = s.attributes.length;
		double sum2 = 0;
		
		for(int i = 0; i < numOfAttributes; i ++){
			sum2 += Math.pow(s.attributes[i] - e.attributes[i], 2);
		}
		
		return Math.sqrt(sum2);
	}

}
