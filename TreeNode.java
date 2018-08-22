package Part2;

public class TreeNode extends Node{

	private String attName;
	private Node left;
	private Node right;

	public TreeNode(String att, Node l, Node r) {
		this.attName = att;
		this.left = l;
		this.right = r;
	}

	public String getAttribute() {
		return attName;
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}

	@Override
	public String getCategory() {
		return null;
	}

	public double getProbability() {
		return 0.0;
	}
	
	public void report(String indent){
		System.out.format("%s%s = True:\n",
		indent, attName);
		left.report(indent+" ");
		System.out.format("%s%s = False:\n",
		indent, attName);
		right.report(indent+"---->");
		}
}
