package ru.ifmo.rain.kokorin.mercle_tree;

import org.junit.Test;
import scala.collection.immutable.List;

import static org.junit.Assert.*;

public class MercleTreeTest {
    @Test
    public void correctProofCorrectSize() {
        Document[] docs = {
                new Document(new byte[]{1, 2, 3, 4, 6, 1, 2}),
                new Document(new byte[]{1, 3, 3, 7}),
                new Document(new byte[]{4, 2}),
                new Document(new byte[]{2, 4, 6, 0, 1})
        };
        MercleTree tree = new MercleTree(docs);
        MercleTreeVerifier verifier = new MercleTreeVerifier(tree.digest(), docs.length);

        Document document = tree.getDocument(3);
        List<ProofElement> proof = tree.getProof(3);
        assertTrue(verifier.verify(3, document, proof));
    }

    @Test
    public void incorrectProofCorrectSize() {
        Document[] docs = {
                new Document(new byte[]{1, 2, 3, 4, 6, 1, 2}),
                new Document(new byte[]{1, 3, 3, 7}),
                new Document(new byte[]{4, 2}),
                new Document(new byte[]{2, 4, 6, 0, 1})
        };
        MercleTree tree = new MercleTree(docs);
        MercleTreeVerifier verifier = new MercleTreeVerifier(tree.digest(), docs.length);

        Document document = tree.getDocument(3);
        List<ProofElement> proof = tree.getProof(2);
        assertFalse(verifier.verify(3, document, proof));
    }

    @Test
    public void correctProofIncorrectSize() {
        Document[] docs = {
                new Document(new byte[]{1, 2, 3, 4, 6, 1, 2}),
                new Document(new byte[]{1, 3, 3, 7}),
                new Document(new byte[]{4, 2}),
                new Document(new byte[]{2, 4, 6, 0, 1}),
                new Document(new byte[]{2, 4, 1, 8, 9, 0, 7, 6}),
                new Document(new byte[]{2, 4, 1, 2, 2, 5}),
                new Document(new byte[]{2}),
                new Document(new byte[]{1, 78, 0, 0})
        };
        MercleTree tree = new MercleTree(docs);
        MercleTreeVerifier verifier = new MercleTreeVerifier(tree.digest(), docs.length);

        Document document = tree.getDocument(5);
        List<ProofElement> proof = tree.getProof(5);
        assertTrue(verifier.verify(5, document, proof));
    }

    @Test
    public void incorrectProofIncorrectSize() {
        Document[] docs = {
                new Document(new byte[]{1, 2, 3, 4, 6, 1, 2}),
                new Document(new byte[]{1, 3, 3, 7}),
                new Document(new byte[]{4, 2}),
                new Document(new byte[]{2, 4, 6, 0, 1}),
                new Document(new byte[]{2, 4, 1, 8, 9, 0, 7, 6}),
                new Document(new byte[]{2, 4, 1, 2, 2, 5}),
                new Document(new byte[]{2}),
                new Document(new byte[]{1, 78, 0, 0})
        };
        MercleTree tree = new MercleTree(docs);
        MercleTreeVerifier verifier = new MercleTreeVerifier(tree.digest(), docs.length);

        Document document = tree.getDocument(2);
        List<ProofElement> proof = tree.getProof(5);
        assertFalse(verifier.verify(5, document, proof));
    }

    @Test
    public void incorrectDoc() {
        Document[] docs = {
                new Document(new byte[]{1, 2, 3, 4, 6, 1, 2}),
                new Document(new byte[]{1, 3, 3, 7}),
                new Document(new byte[]{4, 2}),
                new Document(new byte[]{2, 4, 6, 0, 1}),
                new Document(new byte[]{2, 4, 1, 8, 9, 0, 7, 6}),
                new Document(new byte[]{2, 4, 1, 2, 2, 5}),
                new Document(new byte[]{2}),
                new Document(new byte[]{1, 78, 0, 0})
        };
        MercleTree tree = new MercleTree(docs);
        MercleTreeVerifier verifier = new MercleTreeVerifier(tree.digest(), docs.length);

        Document document = tree.getDocument(5);
        List<ProofElement> proof = tree.getProof(5);
        assertFalse(verifier.verify(5, new Document(new byte[]{1, 2, 3}), proof));
    }

    @Test
    public void incorrectIndex() {
        Document[] docs = {
                new Document(new byte[]{1, 2, 3, 4, 6, 1, 2}),
                new Document(new byte[]{1, 3, 3, 7}),
                new Document(new byte[]{4, 2}),
                new Document(new byte[]{2, 4, 6, 0, 1}),
                new Document(new byte[]{2, 4, 1, 8, 9, 0, 7, 6}),
                new Document(new byte[]{2, 4, 1, 2, 2, 5}),
                new Document(new byte[]{2}),
                new Document(new byte[]{1, 78, 0, 0})
        };
        MercleTree tree = new MercleTree(docs);
        MercleTreeVerifier verifier = new MercleTreeVerifier(tree.digest(), docs.length);

        List<ProofElement> proof = tree.getProof(5);
        assertFalse(verifier.verify(100, new Document(new byte[]{1, 2, 3}), proof));
    }
}
