FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

RUN curl -L get.web3j.io | sh && . ~/.web3j/source.sh

WORKDIR /app