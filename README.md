# crypto
My implementation of some cryptographic algorithms

## Mercle tree

Data structure, that stores some fixed number of documents. Allows users to get document by index, and to get proof, returned document is stored by requested index. It doesn't allow to remove, add or change stored documents.

Class *MercleTree* stores documents, lets user to retreive the document by index and get proof of existence of the document by the index.

Class *MercleTreeVerifier* verifies froofs, aquired from Mercle tree.

[Mercle tree](src/main/scala/ru/ifmo/rain/kokorin/mercle_tree/)

## Sparse Mercle tree

This data structure is identical to ordinary Mercle tree, but it has one difference: it allows some nodes(both leaves and internal nodes) to hold no data (to be bull). If both children of some node are null, then this node is also null. This optimization can reduce size of the tree, if some documents do not exist. Only non-null nodes are built by algorithm.

[Sparse Mercle tree](src/main/scala/ru/ifmo/rain/kokorin/sparse_mercle_tree/)

