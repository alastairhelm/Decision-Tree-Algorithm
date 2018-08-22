package Part2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DecisionTreeMain {

	private ArrayList<String> categoryNames;
	private ArrayList<String> attNames;
	private List<Instance> allInstances;
	int numCategories;
	int numAtts;
	Integer majorityCat;

	double correct = 0;
	double total = 0;


	public DecisionTreeMain() {}

	/**
	 * Build a decision tree form a training set.
	 * */
	private Node buildTree(List<Instance> instances, ArrayList<String> att) {
		ArrayList<String> attributes = new ArrayList<String>();
		for(String s : att) {
			attributes.add(s);
		}
		if(instances.isEmpty()) {
			return new LeafNode(getProbability(majorityCat, allInstances), categoryNames.get(majorityCat));
		}
		else if(isPure(instances)) {
			Instance i = instances.get(0);
			int cat = i.getCategory();
			return new LeafNode(1, categoryNames.get(cat));

		}
		else if(attributes.isEmpty()) {
			Integer maj = getMajorityCat(instances);
			return new LeafNode(getProbability(maj, instances), categoryNames.get(maj));
		}
		List<Instance> bestTrue = null;
		List<Instance> bestFalse = null;
		int bestAtt = 0;
		double bestAverageImpurity = Double.MAX_VALUE;
		for(int i = 0; i < attributes.size(); i ++) {
			int index = attNames.indexOf(attributes.get(i));
			List<Instance> trues = getTrues(instances, index);
			List<Instance> falses = getFalse(instances, index);
			double trueImpurity = getImpurity(trues);
			double falseImpurity = getImpurity(falses);
			double averageImpurity = ((double)trues.size() / (double)instances.size()) * trueImpurity + ((double)falses.size() / (double)instances.size()) * falseImpurity;
			if(i == 0 || averageImpurity <= bestAverageImpurity) {
				bestTrue = trues;
				bestFalse = falses;
				bestAverageImpurity = averageImpurity;
				bestAtt = i;
			}
		}
		String best = attributes.remove(bestAtt);
		TreeNode next = new TreeNode(best, buildTree(bestTrue, attributes), buildTree(bestFalse, attributes));
		return next;
	}

	private void printTree(Node n) {
		n.report("");
	}

	/**
	 * traverse the tree
	 * */
	private void traverseTree(Node root, Instance in) {
		if(root instanceof TreeNode) {
			String att = root.getAttribute();
			int index = 0;
			for(int i = 0; i < attNames.size(); i ++) {
				if(attNames.get(i).equals(att)) {
					index = i;
				}
			}
			if(in.getAtt(index)) {
				System.out.print(att + ":" + in.getAtt(index) + " --> ");
				traverseTree(root.getLeft(), in);
			}
			else if(! in.getAtt(index)) {
				System.out.print(att + ":" + in.getAtt(index) + " --> ");
				traverseTree(root.getRight(), in);
			}
		}
		else if(root instanceof LeafNode) {
			System.out.print(root.getCategory() + "  " + root.getProbability() + "\n" + "Should be: " + in.getCategory() + "\n");
			if(in.getCategory() == categoryNames.indexOf(root.getCategory())) {
				correct ++;
			}
		}
	}

	/**
	 * get all the instances for which a specific attribute is true;
	 * */
	private List<Instance> getTrues(List<Instance> l, int attIndex){
		List<Instance> trues = new ArrayList<Instance>();
		for(Instance i: l) {
			if(i.getAtt(attIndex)) {
				trues.add(i);
			}
		}
		return trues;
	}

	/**
	 * get all the instances for which a specific instance is false
	 * */
	private List<Instance> getFalse(List<Instance> l, int attIndex){
		List<Instance> falses = new ArrayList<Instance>();
		for(Instance i: l) {
			if(! i.getAtt(attIndex)) {
				falses.add(i);
			}
		}
		return falses;
	}

	/**
	 * Check to see if the current nodes instances are pure
	 * */
	private static boolean isPure(List<Instance> instances) {
		int currentCat = instances.get(0).getCategory();
		for(int i = 0; i < instances.size(); i ++) {
			int cat = instances.get(i).getCategory();
			if(cat != currentCat) {return false;}
		}
		return true;
	}

	/**
	 * get the purity of the instances in the current set, only works for binary trees.
	 * */
	private double getImpurity(List<Instance> instances) {
		if(instances.isEmpty()) {return 0;}
		double m = 0;
		double n = 0;
		for(Instance i : instances) {
			if(i.getCategory() == 0) {
				m = m + 1.0;
			}
			if(i.getCategory() == 1) {
				n = n + 1.0;
			}
		}
		double twoMN = 2 * m * n;
		double mNSQR = Math.pow(m + n, 2);
		return twoMN / mNSQR;
	}

	/**
	 * Get the majority category of instances at the current point.
	 * */
	private int getMajorityCat(List<Instance> instances) {
		Integer[] catNums = new Integer[categoryNames.size()];
		for(int i = 0; i < catNums.length; i ++) {
			catNums[i] = 0;
		}
		for(Instance i : instances) {
			catNums[i.getCategory()] ++;
		}
		int max = 0;
		int maxCat = 0;
		for(int i = 0; i < catNums.length;i ++) {
			if(catNums[i] > max) {
				max = catNums[i];
				maxCat = i;
			}
		}
		return maxCat;
	}

	private double getProbability(int cat, List<Instance> l) {
		double numOf = 0.0;
		for(Instance i : l) {
			if(i.getCategory() == cat) {
				numOf ++;
			}
		}
		return numOf / l.size();
	}

	public static void main(String[] args) {
		DecisionTreeMain tree = new DecisionTreeMain();
		tree.readDataFile("COMP307_ASSN1_Part2/part2/hepatitis-training.dat");
		tree.majorityCat = tree.getMajorityCat(tree.allInstances);
		Node root = tree.buildTree(tree.allInstances, tree.attNames);
		tree.readDataFile("COMP307_ASSN1_Part2/part2/hepatitis-test.dat");
		tree.printTree(root);
		for(Instance i : tree.allInstances) {
			System.out.println("Instance: ");
			tree.traverseTree(root, i);
			tree.total ++;
		}
		System.out.println(tree.correct + " / " + tree.total + " = " + tree.correct / tree.total);
	}

	private void readDataFile(String fname){
	    /**
	     * format of names file:
	     * names of categories, separated by spaces
	     * names of attributes
	     * category followed by true's and false's for each instance
	     */
	    System.out.println("Reading data from file "+fname);
	    try {
	      Scanner din = new Scanner(new File(fname));

	      categoryNames = new ArrayList<String>();
	      for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) categoryNames.add(s.next());
	      numCategories=categoryNames.size();
	      System.out.println(numCategories +" categories");

	      attNames = new ArrayList<String>();
	      for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) attNames.add(s.next());
	      numAtts = attNames.size();
	      System.out.println(numAtts +" attributes");

	      allInstances = readInstances(din);
	      din.close();
	    }
	   catch (IOException e) {
	     throw new RuntimeException("Data File caused IO exception");
	   }
	  }


	  private List<Instance> readInstances(Scanner din){
	    /* instance = classname and space separated attribute values */
	    List<Instance> instances = new ArrayList<Instance>();
	    while (din.hasNext()){
	      Scanner line = new Scanner(din.nextLine());
	      instances.add(new Instance(categoryNames.indexOf(line.next()),line));
	    }
	    System.out.println("Read " + instances.size()+" instances");
	    return instances;
	  }



	  private class Instance {

	    private int category;
	    private List<Boolean> vals;

	    public Instance(int cat, Scanner s){
	      category = cat;
	      vals = new ArrayList<Boolean>();
	      while (s.hasNextBoolean())vals.add(s.nextBoolean());
	    }

	    public boolean getAtt(int index){
	      return vals.get(index);
	    }

	    public int getCategory(){
	      return category;
	    }

	    public String toString(){
	      StringBuilder ans = new StringBuilder(categoryNames.get(category));
	      ans.append(" ");
	      for (Boolean val : vals)
		ans.append(val?"true  ":"false ");
	      return ans.toString();
	    }

	  }
}
