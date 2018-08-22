package Part2;

public class LeafNode extends Node{

	private double probability;
	private String category;

	public LeafNode(double prob, String cat) {
		this.probability = prob;
		this.category = cat;
	}

	@Override
	public String getAttribute() {
		return "";
	}

	@Override
	public Node getLeft() {
		return null;
	}

	@Override
	public Node getRight() {
		return null;
	}

	public double getProbability() {
		return probability;
	}

	public String getCategory() {
		return category;
	}
	
	public void report(String indent){
	
		System.out.format("%sClass %s, prob=%4.2f\n",
		indent, category, probability);
	}
}
