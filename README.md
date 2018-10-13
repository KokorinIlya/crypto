# crypto
My implementation of some cryptographic algorithms

## Mercle tree

Data structure, that stores some fixed number of documents. Allows users to get document by index, and to get proof, returned document is stored by requested index. It doesn't allow to remove, add or change stored documents.

Class *MercleTree* stores documents, lets user to retreive the document by index and get proof of existence of the document by the index.

Class *MercleTreeVerifier* verifies froofs, aquired from Mercle tree.

[Mercle tree](src/main/scala/ru/ifmo/rain/kokorin/mercle_tree/)

