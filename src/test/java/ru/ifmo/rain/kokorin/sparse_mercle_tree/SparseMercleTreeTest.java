package ru.ifmo.rain.kokorin.sparse_mercle_tree;

import org.junit.Test;

import static org.junit.Assert.*;

public class SparseMercleTreeTest {
    @Test
    public void correctProofFullDocs() {
        int[] inds = new int[]{0, 1, 2, 3};
        byte[][] docs = new byte[][]{
                new byte[]{1, 2, 3, 4, 6, 1, 2},
                new byte[]{1, 3, 3, 7},
                new byte[]{4, 2},
                new byte[]{2, 4, 6, 0, 1}
        };
        SparseMercleTree tree = new SparseMercleTree(TestUtils.getMap(inds, docs), 2);
        SparseMurcleTreeVerifier verifier = new SparseMurcleTreeVerifier(tree.digest(), 2);

        byte[] document = tree.getDocument(3);
        byte[][] proof = tree.getProof(3);
        assertTrue(verifier.verify(3, document, proof));
    }

    @Test
    public void inCorrectProofFullDocs() {
        int[] inds = new int[]{0, 1, 2, 3};
        byte[][] docs = new byte[][]{
                new byte[]{1, 2, 3, 4, 6, 1, 2},
                new byte[]{1, 3, 3, 7},
                new byte[]{4, 2},
                new byte[]{2, 4, 6, 0, 1}
        };
        SparseMercleTree tree = new SparseMercleTree(TestUtils.getMap(inds, docs), 2);
        SparseMurcleTreeVerifier verifier = new SparseMurcleTreeVerifier(tree.digest(), 2);

        byte[] document = tree.getDocument(3);
        byte[][] proof = tree.getProof(2);
        assertFalse(verifier.verify(3, document, proof));
    }

    @Test
    public void correctProofNotFullDocs() {
        int[] inds = new int[]{0, 2, 4, 7};
        byte[][] docs = new byte[][]{
                new byte[]{1, 2, 3, 4, 6, 1, 2},
                new byte[]{1, 3, 3, 7},
                new byte[]{4, 2},
                new byte[]{2, 4, 6, 0, 1}
        };
        SparseMercleTree tree = new SparseMercleTree(TestUtils.getMap(inds, docs), 3);
        SparseMurcleTreeVerifier verifier = new SparseMurcleTreeVerifier(tree.digest(), 3);

        byte[] document = tree.getDocument(2);
        byte[][] proof = tree.getProof(2);
        assertTrue(verifier.verify(2, document, proof));
    }

    @Test
    public void inCorrectProofNotFullDocs() {
        int[] inds = new int[]{0, 2, 4, 7};
        byte[][] docs = new byte[][]{
                new byte[]{1, 2, 3, 4, 6, 1, 2},
                new byte[]{1, 3, 3, 7},
                new byte[]{4, 2},
                new byte[]{2, 4, 6, 0, 1}
        };
        SparseMercleTree tree = new SparseMercleTree(TestUtils.getMap(inds, docs), 3);
        SparseMurcleTreeVerifier verifier = new SparseMurcleTreeVerifier(tree.digest(), 3);

        byte[] document = tree.getDocument(4);
        byte[][] proof = tree.getProof(2);
        assertFalse(verifier.verify(2, document, proof));
    }

    @Test
    public void correctProofForNull() {
        int[] inds = new int[]{0, 2, 4, 7, 9};
        byte[][] docs = new byte[][]{
                new byte[]{1, 2, 3, 4, 6, 1, 2},
                new byte[]{1, 3, 3, 7},
                new byte[]{4, 2},
                new byte[]{2, 4, 6, 0, 1},
                new byte[]{2, 55, 6, 1, 3}
        };
        SparseMercleTree tree = new SparseMercleTree(TestUtils.getMap(inds, docs), 4);
        SparseMurcleTreeVerifier verifier = new SparseMurcleTreeVerifier(tree.digest(), 4);

        byte[] document = tree.getDocument(5);
        byte[][] proof = tree.getProof(5);
        assertTrue(verifier.verify(5, document, proof));
    }

    @Test
    public void inCorrectProofForNull() {
        int[] inds = new int[]{0, 2, 4, 7, 9};
        byte[][] docs = new byte[][]{
                new byte[]{1, 2, 3, 4, 6, 1, 2},
                new byte[]{1, 3, 3, 7},
                new byte[]{4, 2},
                new byte[]{2, 4, 6, 0, 1},
                new byte[]{2, 55, 6, 1, 3}
        };
        SparseMercleTree tree = new SparseMercleTree(TestUtils.getMap(inds, docs), 4);
        SparseMurcleTreeVerifier verifier = new SparseMurcleTreeVerifier(tree.digest(), 4);

        byte[] document = tree.getDocument(5);
        byte[][] proof = tree.getProof(8);
        assertFalse(verifier.verify(5, document, proof));
    }
}
