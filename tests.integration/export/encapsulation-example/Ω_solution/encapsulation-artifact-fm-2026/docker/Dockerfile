FROM eclipse-temurin:21
WORKDIR /mnt/enc
RUN apt-get update --yes && \
    apt-get install --yes --no-install-recommends \
    libgomp1 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*
ENTRYPOINT [ "/bin/bash", "-l", "-c" ]
