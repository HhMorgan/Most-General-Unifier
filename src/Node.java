import java.util.ArrayList;
import java.util.Arrays;

public class Node {
	private String term;
	private String partition;
	private ArrayList<Node> children;
	private char type;
	private boolean isNegative;

	public Node(String term, String partition) {
		this.term = term;
		this.partition = partition;
		this.children = new ArrayList<Node>();
		this.isNegative = isNegative;
	}

	public Node(String term, char type) {
		this.term = term;
		this.partition = term;
		this.type = type;
		this.children = new ArrayList<Node>();
		this.isNegative = isNegative;
	}

	public String getTerm() {
		return this.term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getPartition() {
		return this.partition;
	}

	public ArrayList<Node> getChildren() {
		return this.children;
	}

	public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}

	public char getType() {
		return this.type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public boolean getIsNegative() {
		return this.isNegative;
	}

	public void setIsNegative(boolean isNegative) {
		this.isNegative = isNegative;
	}

	public boolean equals(Object o) {
		Node nodeOther = (Node) o;
		return this.term.equals(nodeOther.getTerm());
	}

	public String beutifyTerm() {
		String str = this.partition;
		if (this.type == 'T') {
			str += "(";
		}
		for (int i = 0; i < this.children.size(); i++) {
			Node child = this.children.get(i);
			if (child.type == 'T') {
				if (i < this.children.size() - 1) {
					str += child.beutifyTerm() + ",";
				} else {
					str += child.beutifyTerm();
				}
			} else {
				if (child.type == 'C' || child.type == 'V') {
					if (i < this.children.size() - 1) {
						str += child.partition + ",";
					} else {
						str += child.partition;
					}
				}
			}
		}
		if (this.type == 'T') {
			str += ")";
		}
		return str;
	}

	public String toStringDetails() {
		return this.beutifyTerm();
	}

	public String toString() {
		return this.type + ":" + this.partition + "=>" + Arrays.toString(this.children.toArray());
	}
}
