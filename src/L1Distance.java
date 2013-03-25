//L1Distance

public class L1Distance implements Metric {

	@Override
	//L1
	public double getDistance(Record s, Record e) {
		assert s.attributes.length == e.attributes.length : "s and e are different types of records!";
		int numOfAttributes = s.attributes.length;
		double sum1 = 0;
		
		for(int i = 0; i < numOfAttributes; i ++){
			sum1 += Math.abs(s.attributes[i] - e.attributes[i]);
		}
		
		return sum1;
	}

}
