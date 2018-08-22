package Part2;

public abstract class Node {

	public abstract String getAttribute();
	public abstract Node getLeft();
	public abstract Node getRight();
	public abstract String getCategory();
	public abstract double getProbability();
	public abstract void report(String indent);
}

