package avl;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AVLTreeTester {

    @Test
    public void singleAdditionCorrectGetCorrectVerifyReqest() throws Exception {
        AVLTree avlTree = new AVLTree();
        byte[] b = new byte[]{2, 4, 6, 0, 1};
        avlTree.add(10, b);
        TreeResponse<Proof, LeafData, LeafNode> pair = avlTree.find(10);
        AVLVerifier verifier = new AVLVerifier(new Digest(avlTree.getRootHash().getData()));
        assertTrue(verifier.verifySearch(10, pair.getSecond(), pair.getThird(), pair.getFirst()));
    }

    @Test
    public void singleAdditionCorrectGetIncorrectVerifyRequest() throws Exception {
        AVLTree avlTree = new AVLTree();
        byte[] b = new byte[]{2, 4, 6, 0, 1};
        avlTree.add(10, b);
        TreeResponse<Proof, LeafData, LeafNode> pair = avlTree.find(10);
        AVLVerifier verifier = new AVLVerifier(new Digest(avlTree.getRootHash().getData()));
        assertFalse(verifier.verifySearch(15, pair.getSecond(), pair.getThird(), pair.getFirst()));
    }

    @Test
    public void multipleAdditionCorrectGetCorrectVerifyReqest() throws Exception {
        AVLTree avlTree = new AVLTree();
        byte[] b = new byte[]{2, 4, 6, 0, 1};
        avlTree.add(10, b);
        avlTree.add(15, b);
        avlTree.add(20, b);
        avlTree.add(22, b);
        TreeResponse<Proof, LeafData, LeafNode> pair = avlTree.find(15);
        AVLVerifier verifier = new AVLVerifier(new Digest(avlTree.getRootHash().getData()));
        assertTrue(verifier.verifySearch(15, pair.getSecond(), pair.getThird(), pair.getFirst()));
    }

    @Test
    public void multipleAdditionCorrectGetInCorrectVerifyReqest() throws Exception {
        AVLTree avlTree = new AVLTree();
        byte[] b = new byte[]{2, 4, 6, 0, 1};
        avlTree.add(10, b);
        avlTree.add(15, b);
        avlTree.add(20, b);
        avlTree.add(22, b);
        TreeResponse<Proof, LeafData, LeafNode> pair = avlTree.find(15);
        AVLVerifier verifier = new AVLVerifier(new Digest(avlTree.getRootHash().getData()));
        assertFalse(verifier.verifySearch(22, pair.getSecond(), pair.getThird(), pair.getFirst()));
    }

    @Test
    public void simpleAdditionVerification() throws Exception {
        AVLTree avlTree = new AVLTree();
        AVLVerifier verifier = new AVLVerifier(new Digest(avlTree.getRootHash().getData()));
        byte[] b = new byte[]{2, 4, 6, 0, 1};
        TreeResponse<Proof, LeafData, LeafNode> pair = avlTree.add(10, b);
        Digest digest = new Digest(avlTree.getRootHash().getData());
        assertTrue(verifier.verifyChange(10, pair.getSecond(), pair.getThird(), pair.getFirst(), digest));
    }

    @Test
    public void incorrectSimpleAdditionVerification() throws Exception {
        AVLTree avlTree = new AVLTree();
        AVLVerifier verifier = new AVLVerifier(new Digest(avlTree.getRootHash().getData()));
        byte[] b = new byte[]{2, 4, 6, 0, 1};
        TreeResponse<Proof, LeafData, LeafNode> pair = avlTree.add(10, b);
        Digest digest = new Digest(avlTree.getRootHash().getData());
        assertFalse(verifier.verifyChange(15, pair.getSecond(), pair.getThird(), pair.getFirst(), digest));
    }

    @Test
    public void multipleAdditionVerification() throws Exception {
        AVLTree avlTree = new AVLTree();
        AVLVerifier verifier = new AVLVerifier(new Digest(avlTree.getRootHash().getData()));
        byte[] b = new byte[]{2, 4, 6, 0, 1};
        TreeResponse<Proof, LeafData, LeafNode> pair = avlTree.add(10, b);
        Digest digest = new Digest(avlTree.getRootHash().getData());
        assertTrue(verifier.verifyChange(10, pair.getSecond(), pair.getThird(), pair.getFirst(), digest));

        TreeResponse<Proof, LeafData, LeafNode> p2 = avlTree.add(15, b);
        Digest digest2 = new Digest(avlTree.getRootHash().getData());
        assertTrue(verifier.verifyChange(15, p2.getSecond(), p2.getThird(), p2.getFirst(), digest2));
    }
}
