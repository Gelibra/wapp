FROM alpine:3.10.3

USER root

RUN apk add --no-cache openjdk11 nodejs npm bash

COPY docker-entrypoint.sh /usr/local/bin/
RUN ln -s /usr/local/bin/docker-entrypoint.sh / # backwards compat

ENV GELIBRA_APP=gelibra-0.0.1-SNAPSHOT.jar

COPY target/$GELIBRA_APP /opt/

#api libra
COPY api-libra /opt/
WORKDIR opt/api-libra/
RUN npm install

EXPOSE 8080
EXPOSE 3000

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["/bin/bash"]
