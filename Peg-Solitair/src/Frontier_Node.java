// A node of the frontier. Frontier is kept as a double-linked list,
// for efficiency reasons for the breadth-first search algorithm.
public class Frontier_Node {
	
	Tree_Node n;			// pointer to a search-tree node
	Frontier_Node previous;	// pointer to the previous frontier node
	Frontier_Node next;		// pointer to the next frontier node

}
