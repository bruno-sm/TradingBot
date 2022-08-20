## Trading Bot

An experimental trading bot to operate on decentralized exchanges (currently PancakeSwap only).

The deep learning model used behind the scenes can be found [here](https://dagshub.com/bru.1bruno/TradingModel).

### Usage

Create three files behind the secrets folder to store your sensitive data:
 - **secrets/public_key.txt:** Write down the public key of your wallet
 - **secrets/private_key.txt:** Write down the private key of your wallet
 - **secrets/web3_provider_url.txt:** Write down the url of your web3 provider including the api key if needed (e.g. https://bsc.getblock.io/mainnet/?api_key=0el806ij-4r2k-10s3-ms85-b6p1tmn51h2k)

Then just do `docker-compose up --build`.

Now you should have access to a chronograf instance at http://localhost:8888.

By default the bot will simulate the transactions. If you want to perform real operations set the `SIMULATION` enviroment variable to `false` in the docker-compose.yml file.