
public class Unified {
	private Node n1;
	private Node n2;

	public Unified(Node n1, Node n2) {
		this.n1 = n1;
		this.n2 = n2;
	}

	public Node getNode() {
		return n1;
	}

	public Node getNodeSubst() {
		return n2;
	}

	public void setStatement(Node n1) {
		this.n1 = n1;
	}

	public void setStatementSubst(Node n2) {
		this.n2 = n2;
	}

	public boolean equals(Object o) {
		Node node = (Node) o;
		if (this.n1.equals(node)) {
			return true;
		}
		return false;
	}

	public String toString() {
		return n1.getTerm() + " => " + n2.getTerm();
	}
}
