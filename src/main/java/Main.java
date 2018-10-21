import avl.LeafData;
import avl.LeafNode;
import avl.Proof;
import com.avl.tree.AVLTree;
import com.avl.tree.Pair;

public class Main {

    public static void main(String[] args) throws Exception {
        AVLTree avlTree = new AVLTree();
        byte[] b = new byte[]{1};
        avlTree.add(new LeafNode(10, null, null, new LeafData(b), null));
//        avlTree.add(new LeafNode(15, null, null, new LeafData(b), null));
//        avlTree.add(new LeafNode(20, null, null, new LeafData(b), null));
//        avlTree.add(new LeafNode(22, null, null, new LeafData(b), null));
        Pair<Proof, LeafData> pair = avlTree.find(10);
        System.out.println(avlTree);
        System.out.println(pair.getFirst());
    }
}
