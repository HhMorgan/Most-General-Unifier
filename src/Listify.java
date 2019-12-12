import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Listify {

	// Splits arguments of a function/predicate.
	public static ArrayList<String> splitSp(String terms) {
		ArrayList<String> arguments = new ArrayList<String>();
		int parenthsis = 0;
		String currentTerm = "";
		for (int i = 0; i < terms.length(); i++) {
			char currentChar = terms.charAt(i);
			if (currentChar == '(') {
				parenthsis++;
				currentTerm += currentChar;
			} else {
				if (currentChar == ')') {
					parenthsis--;
					currentTerm += currentChar;
				} else {
					if (currentChar == ',' && parenthsis == 0) {
						arguments.add(currentTerm);
						currentTerm = "";
					} else {
						currentTerm += currentChar;
					}
				}
			}
		}
		arguments.add(currentTerm);
		return arguments;
	}

	// Decomposes a logical statement into different terms. The terms are structured
	// in a tree form where each statement has children if it was a function or
	// predicate.
	public static Node decomposeStatement(String statement) {
		String pattern = "(\\w+)(\\([a-zA-Z0-9\\(\\)]+([,][a-zA-Z0-9\\(\\)]+)*\\))*";

		Pattern r = Pattern.compile(pattern);

		Matcher m = r.matcher(statement);
		if (m.find()) {
			Node parent = new Node(statement, m.group(1));
			String term = m.group(2);
			if (term != null) {
				parent.setType('T');
				term = term.substring(1, m.group(2).length() - 1);
				ArrayList<String> arugements = splitSp(term);
				for (String s : arugements) {
					Node child = decomposeStatement(s);
					parent.getChildren().add(child);
				}
			} else {
				String firstCharacter = parent.getPartition().charAt(0) + "";
				boolean hasLowercase = !firstCharacter.equals(firstCharacter.toUpperCase());
				if (hasLowercase) {
					parent.setType('V');
				} else {
					parent.setType('C');
				}
			}
			return parent;
		} else {
			return null;
		}
	}

	// Simple Listify function that implements the same behavior of =.. operator in
	// prolog.
	public static ArrayList<String> Listify(String statement) {
		ArrayList<String> arguments = new ArrayList<String>();
		String pattern = "(\\w+)(\\([a-zA-Z0-9\\(\\)]+([,][a-zA-Z0-9\\(\\)]+)*\\))*";
		Pattern r = Pattern.compile(pattern);

		Matcher m = r.matcher(statement);
		if (m.find()) {
			arguments.add(m.group(1));
			String term = m.group(2);
			if (term != null) {
				term = term.substring(1, m.group(2).length() - 1);
				ArrayList<String> arugements = splitSp(term);
				for (String s : arugements) {
					arguments.add(s);
				}
			}
		} else {
			System.out.println("NO MATCH");
		}
		return arguments;
	}

	public static boolean occurCheck(Node e1, Node e2) {
		if (e2.getType() == 'T') {
			ArrayList<Node> childrenE2 = e2.getChildren();
			for (Node node : childrenE2) {
				if (node.getType() == 'V' && node.equals(e1)) {
					return true;
				}
			}
		} else {
			if (e1.getTerm().contains(e2.getTerm()) || e2.getTerm().contains(e1.getTerm())) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Unified> updateMGU(ArrayList<Unified> mgu) {
		ArrayList<Unified> updatedMGU = new ArrayList<Unified>();
		for (int i = 0; i < mgu.size(); i++) {
			Unified u1 = mgu.get(i);
			for (int j = 0; j < mgu.size(); j++) {
				Unified u2 = mgu.get(j);
				if (u1.getNodeSubst().getType() == 'T') {
					ArrayList<Node> childrenE2 = u1.getNodeSubst().getChildren();
					for (Node node : childrenE2) {
						if (node.getType() == 'V' && node.equals(u2.getNode())) {
							if (occurCheck(u2.getNodeSubst(), node)) {
								return null;
							}
							if (occurCheck(node, u2.getNodeSubst())) {
								return null;
							}
							ArrayList<Node> modifiedE2Children = (ArrayList<Node>) childrenE2.clone();
							int index = modifiedE2Children.indexOf(node);
							modifiedE2Children.remove(index);
							modifiedE2Children.add(index, u2.getNodeSubst());
							u1.getNodeSubst().setChildren(modifiedE2Children);
							u1.getNodeSubst().setTerm(u1.getNodeSubst().beutifyTerm());
							break;
						}
					}
				}
				if (u1.getNode().equals(u2.getNodeSubst())) {
					if (occurCheck(u2.getNodeSubst(),u1.getNodeSubst())) {
						return null;
					}
					u2.setStatementSubst(u1.getNodeSubst());
				}
			}
			updatedMGU.add(u1);
		}
		return updatedMGU;
	}

	public static Node subst(ArrayList<Unified> mgu, Node e) {
		if (containsTerm(mgu, e)) {
			return mgu.get(indexOfTerm(mgu, e)).getNodeSubst();
		} else {
			return e;
		}
	}

	public static ArrayList<Unified> unifyVar(Node e1, Node e2, ArrayList<Unified> mgu) {
		if (containsTerm(mgu, e1)) {
			Node nodeSubst = mgu.get(indexOfTerm(mgu, e1)).getNodeSubst();
			if (!nodeSubst.equals(e2)) {
				return unifyHelper(nodeSubst, e2, mgu);
			} else {
				return new ArrayList<Unified>();
			}
		}
		Node t = subst(mgu, e2);
		if (occurCheck(e1, t)) {
			return null;
		}
		ArrayList<Unified> currMGU = new ArrayList<Unified>();
		currMGU.add(new Unified(e1, t));
		return currMGU;
	}

	public static ArrayList<Unified> unify(Node e1, Node e2) {
		return unifyHelper(e1, e2, new ArrayList<Unified>());
	}

	public static ArrayList<Unified> unifyHelper(Node e1, Node e2, ArrayList<Unified> mgu) {
//		System.out.println("==========================");
//		System.out.println(e1.beutifyTerm());
//		System.out.println(e2.beutifyTerm());
//		System.out.println("==========================");
		if (mgu == null) {
			return null;
		}
		if (e1.equals(e2)) {
			return mgu;
		}
		if (e1.getType() == 'V') {
			return unifyVar(e1, e2, mgu);
		}
		if (e2.getType() == 'V') {
			return unifyVar(e2, e1, mgu);
		}
		if (e1.getType() == 'C' || e2.getType() == 'C') {
			return null;
		}
		if (e1.getChildren().size() != e2.getChildren().size()) {
			return null;
		}
		ArrayList<Unified> currMGU = new ArrayList<Unified>();
		ArrayList<Unified> concatMGU = new ArrayList<Unified>();
		for (int i = 0; i < e1.getChildren().size(); i++) {
			concatMGU.addAll(currMGU);
			concatMGU.addAll(mgu);
			currMGU.addAll(unifyHelper(e1.getChildren().get(i), e2.getChildren().get(i), concatMGU));

//			System.out.println("CURR : " + currMGU);
//			System.out.println("MGU : " + mgu);
		}
		mgu.addAll(currMGU);
//		System.out.println("SEC : " + mgu);
		return updateMGU(currMGU);
	}

	public static boolean containsTerm(ArrayList<Unified> arr, Node e1) {
		for (Unified unified : arr) {
			if (unified.equals(e1)) {
				return true;
			}
		}
		return false;
	}

	public static int indexOfTerm(ArrayList<Unified> arr, Node e1) {
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).equals(e1)) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		String line1 = "p(x,g(x),g(f(A)))";
		String line2 = "p(f(u),v,v)";
//		String line1 = "p(A, y, f(y))";
//		String line2 = "p(z,z, u)";
//		String line1 = "f(x, g(x), x)";// Occurrence check
//		String line2 = "f(g(u), g(g(z)),z)";
//		String line1 = "g(f(y),f(A))";
//		String line2 = "g(x,y)";
//		String line1 = "P(A, y, u)";
//		String line2 = "P(x, F(x, u), G(z, B))";
//		String line1 = "P(x, x, z)";
//		String line2 = "P(F(A, A), y, y)";
//		String line1 = "P(x, F(y, z), B)";// Occurrence check
//		String line2 = "P(G(A, y), F(z, G(A, x)), B)";
//		String line1 = "G(y, y)";
//		String line2 = "G(A, B)";
//		String line1 = "P(F(x, A), G(z, y))";
//		String line2 = "P(F(G(A, B), z), x)";
//		String line1 = "P(F(x, A), G(y, y), z)";
//		String line2 = "P(F(G(A, B), z), x, A)";
//		String line1 = "P(x, H(x))";// Occurrence check
//		String line2 = "P(H(z), z)";
//		String line1 = "P(y,x,H(x))";
//		String line2 = "P(z,G(A,B),z)";
//		String line1 = "Q(y, G(A, B)),";
//		String line2 = "Q(G(x,x), y)";
//		String line1 = "Older(Father(y), y)";
//		String line2 = "Older(Father(x), John)";
//		String line1 = "Knows(Father (y), y)";
//		String line2 = "Knows(x, x)";
		Node e1 = decomposeStatement(line1.replaceAll("\\s+", ""));
		Node e2 = decomposeStatement(line2.replaceAll("\\s+", ""));
		System.out.println(e1.beutifyTerm());
		System.out.println(e2.beutifyTerm());
		try {
			ArrayList<Unified> mgu = unify(e1, e2);
			System.out.println(Arrays.toString(mgu.toArray()));
		} catch (Exception e) {
			System.out.println("Unification Error");
		}

	}

}
