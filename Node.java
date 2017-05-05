
public class Node {
	
	private int item;
	private int frequency;
	private Node right;
	private Node left;
	
	public Node(int item, int frequency, Node left, Node right) {
		
		this.item = item;
		this.frequency = frequency;
		this.left = left;
		this.right = right;
		
	}
	
	public int getItem() {
		return item;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public Node getLeftChild() {
		return left;
	}
	
	public Node getRightChild() {
		return right;
	}
	
	public boolean isLeaf() {
		return (getLeftChild() == null) && (getRightChild() == null);
	}
	
}