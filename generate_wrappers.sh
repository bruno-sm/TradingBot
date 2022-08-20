#!/bin/bash

web3j generate solidity -a abis/PancakeFactory.abi -o src/main/java/contractwrappers -p contractwrappers -c PancakeFactory
web3j generate solidity -a abis/PancakePair.abi -o src/main/java/contractwrappers -p contractwrappers -c PancakePair
web3j generate solidity -a abis/PancakeRouter.abi -o src/main/java/contractwrappers -p contractwrappers -c PancakeRouter