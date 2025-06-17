FROM quay.io/keycloak/keycloak:26.2.5-0
USER root
RUN microdnf install -y curl && microdnf clean all
USER keycloak
CMD ["start-dev", "--import-realm"]