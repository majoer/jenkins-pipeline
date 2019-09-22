FROM jenkins/ssh-slave

ENV NODE_VERSION 10

RUN apt-get update && apt-get install -y -q --no-install-recommends \
        apt-transport-https \
        build-essential \
        ca-certificates \
        curl \
        git \
        libssl-dev \
        wget

RUN wget -qO- https://raw.githubusercontent.com/nvm-sh/nvm/v0.34.0/install.sh | bash
RUN /bin/bash -c "source ~/.nvm/nvm.sh && nvm install $NODE_VERSION && nvm use --delete-prefix $NODE_VERSION"
